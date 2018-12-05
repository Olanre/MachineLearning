package MachineLearner;

import DecisionTree.DecisionTree;
import Log.Logger;
import Processor.DataFormat;
import Processor.DataReader;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import DecisionTree.Node;
import DecisionTree.DecisionNode;
import Util.Util;

import javax.xml.crypto.Data;


/**
 * Created by olanre on 2018-10-23.
 */
public class ID3Learner implements DecisionTree, MLAlgorithm {

    private ArrayList<DataReader> Attributes;
    private DataReader GoalAttribute;
    private double Entropy;
    private double Remainder;
    private double Gain;
    private double maxGain;
    private double randomRatio = 0.0;
    private int maxDepth, currentDepth;
    public static final String AlgorithmName = "ID3";
    Logger log;


    private Node tree;



    public ID3Learner(double  randomRatio){
        this.randomRatio = randomRatio;
    }



    public void learnData(ArrayList<DataReader> drs, DataReader goal){
        this.Attributes = drs;
        this.GoalAttribute = goal;
        initLoggers();
        buildModel();

    }
    public void initLoggers(){

        this.log = new Logger(this.AlgorithmName, Logger.LogLevel.INFO, this.AlgorithmName);
        this.GoalAttribute.setLog(this.log);
        for(DataReader Attribute: this.Attributes){
            Attribute.setLog(this.log);
        }
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

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }


    public Node getTree() {
        return this.tree;
    }

    public void setTree(Node tree) {
        this.tree = tree;
    }


    public void buildModel(){
        ArrayList<Integer> copyIndexes = new ArrayList<>();
        for(int i = 0; i < this.Attributes.get(0).getData().size(); i++){
            copyIndexes.add(i);
        }

        ArrayList<DataReader> copyAttributes = MLUtils.getReadersFromIndexes(copyIndexes, this.Attributes);

        DataReader copyGoal = new DataFormat(this.GoalAttribute);
        //copyAttributes.remove(0);
        setMaxDepth(10);
        this.tree = buildTree(copyAttributes, copyGoal, 0);
        String msg= "The final maximum depth was:  " + this.currentDepth;
        log.Log("buildTree", msg);

    }

    public boolean allExamplesPositive(DataReader targetAttributes){
        return MLUtils.getRowsinList(targetAttributes.getUniqueCounts(), targetAttributes).size() == 1;
    }

    public boolean depthReached(int currentDepth){
        if (this.maxDepth == 0 ) return false;
        if(currentDepth >  this.maxDepth) {
            return true;
        }else{
            return false;
        }

    }

    public Node buildTree(ArrayList<DataReader> currentAttributes, DataReader targetAttribute){
       return  buildTree( currentAttributes, targetAttribute,  0);
    }


    public Node buildTree(ArrayList<DataReader> currentAttributes, DataReader targetAttribute, int currentDepth){
        DecisionNode decision_data =  new DecisionNode();
        ArrayList<Integer> indices;
        int dataSize = currentAttributes.size();
        targetAttribute.setLog(this.log);
        String msg;
        Node root  = new Node();
        msg= "Our target attribute is currently:  " + targetAttribute.printData();
        log.Log("buildTree", msg);
        this.currentDepth = currentDepth;
        if( allExamplesPositive(targetAttribute) || currentAttributes.isEmpty() || depthReached(currentDepth) || dataSize == 1 ){
            msg= "All examples is target are either positive or the current Attributes array is empty or the max depth has been reached";
            log.Log("buildTree", msg);
            String value = targetAttribute.findMode(targetAttribute.getData());
            decision_data =  new DecisionNode(targetAttribute, value);
            root.setData(decision_data);
            msg= "Value to be returned is :  " + value;
            log.Log("buildTree", msg);

        }else{
            if(this.randomRatio != 0.0 && dataSize > 10){
                Double randomS = Math.floor(Double.valueOf(dataSize) * Double.valueOf(this.randomRatio));
                int randomSize = randomS.intValue();
                if( randomSize < dataSize){
                    // if(randomSize < 3) randomSize = 3;
                    indices = Util.getRandomInts(0, dataSize, randomSize  );
                    currentAttributes = MLUtils.getReadersAtIndex(indices, currentAttributes);
                }

            }


            DataReader bestAttribute = getBestAttribute(currentAttributes, targetAttribute);
            bestAttribute.setLog(this.log);
            msg= "New best attribute is:  " + bestAttribute.printData();
            log.Log("buildTree", msg);
            HashMap<String, ArrayList> attributeValues =  bestAttribute.getUniqueCounts();
            Iterator space = attributeValues.entrySet().iterator();
            String parent_mode = targetAttribute.findMode(targetAttribute.getUniqueElements());
            msg= "Parent mode is currently:  " + parent_mode;
            log.Log("buildTree", msg);
            int col = bestAttribute.getColumnNumber();
            //currentAttributes.remove(col);

            root.setColumn(col);

            while (space.hasNext()) {

                HashMap.Entry entry = (HashMap.Entry) space.next();
                String key = (String)entry.getKey();
                Node child = new Node();
                ArrayList<Integer> indexes = (ArrayList<Integer>) entry.getValue();

                ArrayList<String> targetSpace =  targetAttribute.getDataAtIndexes(indexes);

                if( targetSpace.size() == 1 ){
                    msg= "No more splits to be made as target no values exist for target of size: " + targetSpace.size();
                    log.Log("buildTree", msg);
                    DecisionNode child_data = new DecisionNode(targetAttribute, targetSpace.get(0));
                    child.setData(child_data);
                    child.setBranch(key);

                    root.addChild(child);

                    msg = "Current depth is updated to: " + currentDepth;
                    log.Log("buildTree", msg);

                }else{
                    msg = "Current master Attributes List size is: " + this.Attributes.size();
                    log.Log("buildTree", msg);
                    currentAttributes.remove(bestAttribute);

                    ArrayList<DataReader> newCurrentAttributes = MLUtils.getReadersFromIndexes(indexes, currentAttributes);
                    DataReader newTargetAttribute = MLUtils.trimIndexesFromAttributes(indexes, targetAttribute);
                    msg = "Current depth is updated to: " + currentDepth;
                    log.Log("buildTree", msg);
                    child = buildTree(newCurrentAttributes, newTargetAttribute, currentDepth + 1);
                    child.setBranch(key);
                    root.addChild(child);
                }

            }


        }
        return root;


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
        return Classify(cols, this.tree);
    }


    public String Classify( ArrayList<String> cols, Node tree){
        String classification = "";
        int index;
        if(tree.getChildren().size() == 0  && tree.getData() != null) {
            classification = tree.getData().getLabel();
            return classification;
        }else{
            index = tree.getColumn();

            ArrayList<Node> children = tree.getChildren();
            for(int i = 0; i < children.size() ; i ++){
                tree = children.get(i);
                if( cols.get(index).equals(tree.getBranch())){
                    //cols.remove(index);
                    classification = Classify(cols, tree);
                    break;
                }

            }
        }

        if( classification.equals("") && tree.getData() != null && tree.getChildren().size() == 0) {
            classification = tree.getData().getLabel();
        }


        return classification;
    }


    public double CalculateRemainder(DataReader dr){
        double distribution;
        HashMap<String, ArrayList> instanceSpace =  dr.getUniqueCounts();
        int data_size = dr.getData().size();
        double relative_remainder, remainder = 0.0;
        Iterator space = instanceSpace.entrySet().iterator();

        while (space.hasNext()) {
            HashMap.Entry entry = (HashMap.Entry) space.next();
            String key = (String)entry.getKey();
            ArrayList<Integer> value = (ArrayList<Integer>) entry.getValue();
            distribution = Double.valueOf(value.size()) / Double.valueOf(data_size);

            HashMap<String, ArrayList> goal_vals = this.GoalAttribute.getHashAtIndexes(value);
            relative_remainder = calculateEntropy(goal_vals, this.GoalAttribute.getData().size());

            remainder = distribution * relative_remainder;

            remainder += remainder;

            //System.out.println("Key = " + key + ", Value = " + value);
        }
        return remainder;
    }

    public double CalculateGain(DataReader dr){
        double gain = 0.0;
        double remainder  =CalculateRemainder(dr);
        double entropy = calculateEntropy(MLUtils.getRowsinList(dr.getUniqueCounts(), this.GoalAttribute), dr.getData().size());
        //double splitRatio = CalculateSplitRatio(MLUtils.getRowsinList(dr.getUniqueCounts(), this.GoalAttribute), dr.getData().size());

        gain = (entropy - remainder);
                //gain = gain / splitRatio;

        return gain;
    }

    //public double CalculateSplitRatio(DataReader dr, ){
    public double CalculateSplitRatio(HashMap<String, ArrayList> instanceClass, int totalInstances){
        double t = 0.0;
        double distribution, logVal;

        for (ArrayList value : instanceClass.values()) {
            distribution = Double.valueOf(value.size()) / Double.valueOf(totalInstances);
            logVal = Math.log(distribution)/Math.log(2);
            t += ( distribution * logVal) ;

        }

        return (-1 * t);
    }


    public double calculateEntropy(HashMap<String, ArrayList> instanceClass, int totalInstances){
        double t = 0.0;
        double distribution, logVal;
        //int totalInstances = 0;

        /**for (ArrayList value : instanceClass.values()) {
            totalInstances += value.size();

        }*/

        for (ArrayList value : instanceClass.values()) {
            distribution = Double.valueOf(value.size()) / Double.valueOf(totalInstances);
            logVal = Math.log(distribution)/Math.log(2);
            t = -1 * ( distribution * logVal) + t;

        }

        return t;
    }





    public DataReader getBestAttribute(ArrayList<DataReader> Attributes, DataReader GoalAttribute){
        //this.Attributes = Attributes;
        //this.GoalAttribute = GoalAttribute;
        double gain, maxGain = -20000.0;
        int indexOfBest = 0;
        if (Attributes.size()== 0) return null;

        DataReader bestAttribute = null;

        for(int i = 0 ; i < Attributes.size(); i++){
            if(Attributes.get(i) == null) continue;
            //find best attribute
            gain = CalculateGain(Attributes.get(i));
            if (gain > maxGain){
                maxGain = gain;
                indexOfBest = i;
                bestAttribute = Attributes.get(i);
            }
            if (gain == 1) break;

        }


        return bestAttribute;

    }



}
