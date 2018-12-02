package MachineLearner;

import Log.Logger;
import Processor.DataReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import Util.Util;

/**
 * Created by olanre on 2018-11-04.
 */
public class NaiveBayes  implements  MLAlgorithm{

    private ArrayList<DataReader> Attributes;
    private DataReader GoalAttribute;
    private HashMap<String, Double> priorProbability;
    private HashMap<String, Double> conditionProbability;
    public final String AlgorithmName = "NaiveBayes";
    HashMap<String, ArrayList> goalClasses = new HashMap<>();
    Logger log;


    public void learnData(ArrayList<DataReader> drs, DataReader goal){
        this.Attributes = drs;
        this.GoalAttribute = goal;
        initLoggers();
        buildModel();
    }

    public void initLoggers(){
        this.log = new Logger(this.AlgorithmName, Logger.LogLevel.DEBUG, this.AlgorithmName);
        this.GoalAttribute.setLog(this.log);
        for(DataReader Attribute: this.Attributes){
            Attribute.setLog(this.log);
        }
    }

    public void buildModel(){
        this.goalClasses = this.GoalAttribute.getUniqueCounts();
        buildPriorProbabilites();
        buildConditionProbabilities();

    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }



    public String getAlgorithmName() {
        return AlgorithmName;
    }

    public void buildPriorProbabilites(){

        Iterator space = this.goalClasses.entrySet().iterator();
        double priorProbability;

        this.priorProbability = new HashMap<>();
        while (space.hasNext()) {
            HashMap.Entry entry = (HashMap.Entry) space.next();
            String key = (String)entry.getKey();
            ArrayList<Integer> value = (ArrayList<Integer>) entry.getValue();
            priorProbability = Double.valueOf(value.size()) / Double.valueOf(this.GoalAttribute.getData().size()) ;
            this.priorProbability.put(key, priorProbability);
        }
    }

    public void buildConditionProbabilities(){

        HashMap<String, ArrayList> totalFeatures = MLUtils.getAllFeaturesMap(this.Attributes);
        //this.goalClasses = this.GoalAttribute.getUniqueCounts();
        Iterator space = totalFeatures.entrySet().iterator();
        ArrayList<Integer> transientIndexes;
        int m = this.GoalAttribute.getUniqueElements().size();
        double n =  Double.valueOf(this.GoalAttribute.getData().size());
        double Probability;

        this.conditionProbability = new HashMap<>();
        while (space.hasNext()) {
            HashMap.Entry entry = (HashMap.Entry) space.next();
            String featureKey = (String)entry.getKey();
            ArrayList<Integer> featureValue = (ArrayList<Integer>) entry.getValue();
            //transientIndexes = GoalAttribute.getDataAtIndexes(featureValue);
            Iterator goalSpace = this.goalClasses.entrySet().iterator();
            while (goalSpace.hasNext()) {
                HashMap.Entry goalEntry = (HashMap.Entry) goalSpace.next();
                String goalKey = (String)goalEntry.getKey();
                ArrayList<Integer> goalValue = (ArrayList<Integer>) goalEntry.getValue();
                String featureAndGoal =  featureKey + "|" + goalKey;
                transientIndexes = Util.intersection(featureValue, goalValue);
                //calculate using naive bayes estimator to avoid conditional probability of 0 using Laplace Smoothing
                Probability = (Double.valueOf(transientIndexes.size()) + 1.0) / (n + Double.valueOf(m));
                this.conditionProbability.put(featureAndGoal, Probability);
            }
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

    public Double getPosterior( String value, String target){
        int m = this.GoalAttribute.getUniqueElements().size();
        double n =  Double.valueOf(this.GoalAttribute.getData().size());
        String concat = value + "|" + target;
        if(this.conditionProbability.get(concat) == null){
            double LaplaceSmooth = 1.0/ (1.0 * Double.valueOf(m) + n);
            return LaplaceSmooth;

        }else{
            return this.conditionProbability.get(concat);

        }
    }

    public Double getPrior(String target){

        return this.priorProbability.get(target);
    }

    public String Classify( ArrayList<String> cols){
        String attrValue, targetValue;
        HashMap<String, Double> classification_set = new HashMap<>();
        ArrayList<String> possibleGoals = this.GoalAttribute.getUniqueElements();
        double posterior = 0.0;
        for(int j = 0 ; j < possibleGoals.size() ; j ++){
            targetValue = possibleGoals.get(j);
             posterior = getPrior(targetValue);
            double new_posterior = 1.0;
            for(int i = 0; i < cols.size(); i ++){
                attrValue = cols.get(i);
                new_posterior  = new_posterior * getPosterior( attrValue, targetValue);
            }
            posterior = posterior * new_posterior;

            classification_set.put(targetValue, posterior);

        }
        return Util.findMax(classification_set);

    }


}
