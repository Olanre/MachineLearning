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
    ArrayList<Double> weights, alpha, error = new ArrayList<>();
    int bins = 50;
    private ArrayList<DataReader> Attributes;
    private DataReader GoalAttribute;
    private ArrayList<Node> trees;
    private ArrayList<DataReader> TotalAttributes;
    private ArrayList<ID3Learner> weakLearners;
    public static final String AlgorithmName = "AdaBoost";
    int sampleRatio = 10;


    Logger log;

    public AdaBoost( int sampleRatio){
        this.sampleRatio = sampleRatio;
    }


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
        limit  = this.TotalAttributes.size();
        int size =  limit/ this.sampleRatio;;

        double weightedError, noise, smallAlpha = 0.0;
        this.weakLearners = new ArrayList<>();
        this.alpha = new ArrayList<>();
        this.error = new ArrayList<>();
        this.trees  = new ArrayList<>();

        discreteClasses = this.GoalAttribute.getUniqueElements().size();
        noise = 1 / discreteClasses;
        double multiClassConstant = Math.log(discreteClasses - 1);

        limit  = this.TotalAttributes.size();
        badClassifiers = 0;

        for(int i  = 0; i < this.bins; i ++){
            ArrayList<Integer> randomIndexes = Util.getRandomInts(0, limit - 1, size);

            DataBins = MLUtils.getReadersFromIndexes( randomIndexes, this.Attributes);
            DataReader newGoalAttribute = MLUtils.trimIndexesFromAttributes(randomIndexes, this.GoalAttribute);
            ID3Learner weakLearner = new ID3Learner(0  );
            weakLearner.setMaxDepth(1);
            weakLearner.learnData(DataBins, newGoalAttribute);
            Node T = weakLearner.getTree();
            this.weakLearners.add( weakLearner);
            weightedError = getWeightedSum(weakLearner, T);
            this.trees.add( T);
            this.error.add( weightedError);
            smallAlpha = Math.log((1.0 - weightedError)/Math.max(1E-10,weightedError)) + multiClassConstant ;
            this.alpha.add( smallAlpha);
            setUpdatedWeightedSum(weightedError, smallAlpha);

            /**if ( 1 - weightedError  <= noise){
                // this is no better than a random guess!
                badClassifiers++;
                i--;

            } else {


            }*/

        }

    }

    public double getWeightedSum(ID3Learner weakLearner, Node T){
        int limit  = this.TotalAttributes.size();
        String ClassifiedResult, actualResult;
        ArrayList<String> tempVals, goalVals;
        DataReader DataBin;
        double weightedError = 0.0;
        double sum = 1.0;

        for(int j = 0; j < limit; j++){

            DataBin = this.TotalAttributes.get(j);
            tempVals = DataBin.getData();
            goalVals = this.GoalAttribute.getData();
            actualResult = goalVals.get(j);
            ClassifiedResult = weakLearner.Classify(tempVals, T);

            if(ClassifiedResult != actualResult) {
               weightedError = weightedError + this.weights.get(j);
               this.error.add( 1.0);
            }
            sum += weightedError;

        }
        weightedError = weightedError / sum;


        return weightedError;

    }

    public void setUpdatedWeightedSum(double oldWeight, double alpha){
        int limit  = this.TotalAttributes.size();

        double weightedError = 0.0;
        for(int j = 0; j < this.error.size(); j++){

            if( this.error.get(j) == 1.0){
                weightedError = oldWeight * Math.exp(alpha);
                this.weights.add( weightedError);
            }
        }

    }



    public void preInit(){

        this.weights = new ArrayList<>();

        this.TotalAttributes = MLUtils.ColtoRowMajor(this.Attributes);

        double weight  = 1.0 / Double.valueOf(this.TotalAttributes.size());
        /** int discreteClasses = this.GoalAttribute.getUniqueElements().size();

        if( discreteClasses  > this.bins ){
            this.bins = discreteClasses;
        }

        if( this.GoalAttribute.getData().size() < 500){
            this.bins = this.GoalAttribute.getData().size() / 2;
        }*/
        for(int i = 0 ; i < this.TotalAttributes.size(); i++){
            this.weights.add( weight);

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
        double tempAlpha = 0.0;
        discreteClasses = this.GoalAttribute.getUniqueElements();
        HashMap<String, Double> adaResult = Util.createHashFromStrings(discreteClasses, 1.0);
        for(int i  = 0; i < this.bins; i ++) {
            ID3Learner weakLearner = this.weakLearners.get(i);
            Node T = weakLearner.getTree();
            ClassifiedResult = weakLearner.Classify(cols, T);
            if(ClassifiedResult.equals("")) continue;
            tempAlpha = adaResult.get(ClassifiedResult);
            tempAlpha *= this.alpha.get(i);
            adaResult.put(ClassifiedResult, tempAlpha);
        }

        classification = Util.findMax(adaResult);
        return classification;
    }

}
