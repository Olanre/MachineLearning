package MachineLearner;

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


    public void learnData(ArrayList<DataReader> drs, DataReader goal){
        this.Attributes = drs;
        this.GoalAttribute = goal;
        buildModel();
    }

    public void buildModel(){
        buildPriorProbabilites();
        buildConditionProbabilities();

    }

    public String getAlgorithmName() {
        return AlgorithmName;
    }

    public void buildPriorProbabilites(){

        HashMap<String, ArrayList> goalClasses = this.GoalAttribute.getUniqueCounts();
        Iterator space = goalClasses.entrySet().iterator();
        double priorProbability;

        this.priorProbability.clear();
        while (space.hasNext()) {
            HashMap.Entry entry = (HashMap.Entry) space.next();
            String key = (String)entry.getKey();
            ArrayList<Integer> value = (ArrayList<Integer>) entry.getValue();
            priorProbability = value.size() / this.GoalAttribute.getData().size() ;
            this.priorProbability.put(key, priorProbability);
        }
    }

    public void buildConditionProbabilities(){

        HashMap<String, ArrayList> totalFeatures = MLUtils.getAllFeaturesMap(this.Attributes);
        HashMap<String, ArrayList> goalClasses = this.GoalAttribute.getUniqueCounts();
        Iterator space = totalFeatures.entrySet().iterator();
        ArrayList<Integer> transientIndexes;
        int m = this.GoalAttribute.getUniqueElements().size();

        double Probability;

        this.conditionProbability.clear();
        while (space.hasNext()) {
            HashMap.Entry entry = (HashMap.Entry) space.next();
            String featureKey = (String)entry.getKey();
            ArrayList<Integer> featureValue = (ArrayList<Integer>) entry.getValue();
            //transientIndexes = GoalAttribute.getDataAtIndexes(featureValue);
            Iterator goalSpace = goalClasses.entrySet().iterator();
            while (goalSpace.hasNext()) {
                HashMap.Entry goalEntry = (HashMap.Entry) space.next();
                String goalKey = (String)goalEntry.getKey();
                ArrayList<Integer> goalValue = (ArrayList<Integer>) goalEntry.getValue();
                String featureAndGoal =  featureKey + "|" + goalKey;
                transientIndexes = Util.intersection(featureValue, goalValue);
                //calculate using naive bayes estimator to avoid conditional probability of 0
                Probability = (transientIndexes.size() + 1) / (goalValue.size() + m);
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

        String concat = value + "|" + target;
        return this.conditionProbability.get(concat);
    }

    public Double getPrior(String target){

        return this.priorProbability.get(target);
    }

    public String Classify( ArrayList<String> cols){
        String attrValue, targetValue;
        HashMap<String, Double> classification_set = new HashMap<>();
        ArrayList<String> possibleGoals = this.GoalAttribute.getUniqueElements();

        for(int j = 0 ; j < possibleGoals.size() ; j ++){
            targetValue = possibleGoals.get(j);
            double posterior = getPrior(targetValue);

            for(int i = 0; i < cols.size(); i ++){
                attrValue = cols.get(i);
                posterior  = posterior * getPosterior( attrValue, targetValue);
            }

            classification_set.put(targetValue, posterior);

        }
        return Util.findMax(classification_set);

    }


}
