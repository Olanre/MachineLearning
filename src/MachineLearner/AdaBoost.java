package MachineLearner;

import Log.Logger;
import Processor.DataReader;
import DecisionTree.Node;
import java.util.ArrayList;
import Util.Util;
import java.util.HashMap;

import javax.xml.crypto.Data;


/**
 * Created by olanre on 2018-11-01.
 */
public class AdaBoost implements MLAlgorithm {

    double[] unitResult;
    ArrayList<Double> weights, alpha, error;
    int bins = 50;
    private ArrayList<DataReader> Attributes;
    private DataReader GoalAttribute;
    private ArrayList<Node> trees;
    private ArrayList<DataReader> TotalAttributes;
    private ArrayList<ID3Learner> weakLearners;
    public static final String AlgorithmName = "AdaBoost";

    Logger log;


    public void learnData(ArrayList<DataReader> drs, DataReader goal){
        this.Attributes = drs;
        this.GoalAttribute = goal;
        initLoggers();
        preInit();
        buildModel();
    }

    public void initLoggers(){
        this.log = new Logger(this.AlgorithmName, Logger.LogLevel.INFO, this.AlgorithmName);
        this.GoalAttribute.setLog(this.log);
        for(DataReader Attribute: this.Attributes){
            Attribute.setLog(this.log);
        }
    }

    public String getAlgorithmName() {
        return AlgorithmName;
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public int getBins() {
        return bins;
    }

    public void setBins(int bins) {
        this.bins = bins;
    }

    public void buildModel(){
        ArrayList<DataReader> DataBins;
        int limit, discreteClasses, badClassifiers ;
        int size = this.bins/4;
        double weightedError, noise, smallAlpha;


        discreteClasses = this.GoalAttribute.getUniqueElements().size();
        noise = 1 / discreteClasses;
        double multiClassConstant = Math.log(discreteClasses - 1);

        this.TotalAttributes = MLUtils.ColtoRowMajor(this.Attributes);
        limit  = this.TotalAttributes.size();
        badClassifiers = 0;

        for(int i  = 0; i < this.bins; i ++){
            ArrayList<Integer> randomIndexes = Util.getRandomInts(0, limit, size);

            DataBins = MLUtils.getReadersFromIndexes( randomIndexes, this.Attributes);
            DataReader newGoalAttribute = MLUtils.trimIndexesFromAttributes(randomIndexes, this.GoalAttribute);
            ID3Learner weakLearner = new ID3Learner(0  );
            weakLearner.setMaxDepth(1);
            weakLearner.learnData(DataBins, newGoalAttribute);
            Node T = weakLearner.getTree();
            this.weakLearners.set(i, weakLearner);
            weightedError = getWeightedSum(weakLearner, T);
            if ( 1 - weightedError  <= noise){
                // this is no better than a random guess to skip this classifier.
                i--;
                badClassifiers++;
                continue;

            } else {
                this.trees.add(i, T);
                this.error.add(i, weightedError);

                if (badClassifiers > 5 || weightedError == 0 ) {
                    break;
                }

            }
            smallAlpha = Math.log((1 - weightedError)/weightedError) + multiClassConstant ;
            this.alpha.set(i, smallAlpha);
            setUpdatedWeightedSum(weightedError, smallAlpha);

        }

    }

    public double getWeightedSum(ID3Learner weakLearner, Node T){
        int limit  = this.TotalAttributes.size();
        String ClassifiedResult, actualResult;
        ArrayList<String> tempVals, goalVals;
        DataReader DataBin;
        double weightedError = 0.0;
        double sum = 0.0;

        for(int j = 0; j < limit; j++){

            DataBin = this.TotalAttributes.get(j);
            tempVals = DataBin.getData();
            goalVals = this.GoalAttribute.getData();
            actualResult = goalVals.get(j);
            ClassifiedResult = weakLearner.Classify(tempVals, T);

            if(ClassifiedResult != actualResult) {
               weightedError = weightedError + this.weights.get(j);
               this.error.set(j, 1.0);
            }
            sum += weightedError;

        }
        weightedError = weightedError / sum;


        return weightedError;

    }

    public void setUpdatedWeightedSum(double oldWeight, double alpha){
        int limit  = this.TotalAttributes.size();

        double weightedError = 0.0;
        double sum = 0.0;
        for(int j = 0; j < limit; j++){

            if( this.error.get(j) == 1.0){
                weightedError = oldWeight * Math.exp(alpha);
                this.weights.set(j, weightedError);
            }
            sum += this.weights.get(j);

        }

    }



    public void preInit(){
        double weight  = 1 / this.TotalAttributes.size();
        int discreteClasses = this.GoalAttribute.getData().size();

        if((discreteClasses % 2) == 0 ){
            this.bins = (discreteClasses / 2) - 1;
        }else{
            this.bins = discreteClasses/ 2;
        }

        for(int i = 0 ; i < this.TotalAttributes.size(); i++){
            this.weights.add(i, weight);

        }

    }

    public ArrayList<String> ClassifySet(ArrayList<DataReader> example_data){
        ArrayList<String> result = new ArrayList<String>();
        DataReader row;

        for(int i = 0; i < example_data.size(); i ++){
            row = example_data.get(i);
            String classification = Classify(row.getData());
            result.add(classification);
        }

        return result;
    }

    public String Classify( ArrayList<String> cols){
        String classification = "";
        String ClassifiedResult;
        ArrayList<String> discreteClasses;

        int index;
        discreteClasses = this.GoalAttribute.getUniqueElements();
        HashMap<String, Double> adaResult = Util.createHashFromStrings(discreteClasses, 1.0);
        for(int i  = 0; i < this.bins; i ++) {
            ID3Learner weakLearner = this.weakLearners.get(i);
            Node T = weakLearner.getTree();
            ClassifiedResult = weakLearner.Classify(cols, T);
            double tempAlpha = adaResult.get(ClassifiedResult);
            tempAlpha *= this.alpha.get(i);
            adaResult.put(ClassifiedResult, tempAlpha);
        }

        classification = Util.findMax(adaResult);
        return classification;
    }

}
