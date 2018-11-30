package MachineLearner;

import DecisionTree.DecisionTree;
import Processor.DataReader;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import DecisionTree.Node;
import DecisionTree.DecisionNode;
import Util.Util;


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
    private int randomRatio = 0;
    private int maxDepth;
    public static final String AlgorithmName = "ID3";


    private Node tree;



    public ID3Learner(int  randomRatio){
        this.randomRatio = randomRatio;
    }



    public void learnData(ArrayList<DataReader> Attributes, DataReader GoalAttribute){
        buildModel();


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

        this.tree = buildTree(this.Attributes, this.GoalAttribute, 0);

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
        Node root  = new Node();
        if( allExamplesPositive(targetAttribute) || currentAttributes.isEmpty() || depthReached(currentDepth) ){
            String value = targetAttribute.findMode(targetAttribute.getUniqueElements());
            decision_data =  new DecisionNode(targetAttribute, value);
            root.setData(decision_data);

        }else{
            if(this.randomRatio != 0){
                int randomSize = dataSize / this.randomRatio;
                // if(randomSize < 3) randomSize = 3;
                indices = Util.getRandomInts(0, dataSize, randomSize  );
                currentAttributes = MLUtils.getReadersFromIndexes(indices, currentAttributes);
            }


            DataReader bestAttribute = getBestAttribute(currentAttributes, targetAttribute);

            HashMap<String, ArrayList> attributeValues =  bestAttribute.getUniqueCounts();
            Iterator space = attributeValues.entrySet().iterator();
            String parent_mode = targetAttribute.findMode(targetAttribute.getUniqueElements());

            while (space.hasNext()) {

                HashMap.Entry entry = (HashMap.Entry) space.next();
                String key = (String)entry.getKey();
                Node child = new Node();
                ArrayList<Integer> indexes = (ArrayList<Integer>) entry.getValue();

                ArrayList<String> targetSpace =  targetAttribute.getDataAtIndexes(indexes);

                if(targetSpace.isEmpty()){
                    DecisionNode child_data = new DecisionNode(targetAttribute, parent_mode);
                    child.setData(child_data);
                    child.setBranch(key);
                    root.addChild(child);
                }else{
                    currentAttributes.remove(bestAttribute);
                    ArrayList<DataReader> newCurrentAttributes = MLUtils.getReadersFromIndexes(indexes,this.Attributes);

                    DataReader newTargetAttribute = MLUtils.trimIndexesFromAttributes(indexes, targetAttribute);
                     currentDepth += 1;
                    child = buildTree(newCurrentAttributes, newTargetAttribute, currentDepth);
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
        if(tree.getChildren().size() == 0 ) {
            return tree.getData().getLabel();
        }else{
            ArrayList<Node> children = tree.getChildren();
            for(int i = 0; i < children.size() ; i ++){
                cols.contains(children.get(i).getBranch());
                tree = children.get(i);
                index = cols.indexOf(children.get(i).getBranch());
                cols.remove(index);
                classification = Classify(cols, tree);
                break;
            }
        }




        return classification;
    }


    public double CalculateRemainder(DataReader dr){
        double distribution;
        HashMap<String, ArrayList> instanceSpace =  dr.getUniqueCounts();
        int data_size = dr.getData().size();
        double relative_remainder, remainder = 0;
        Iterator space = instanceSpace.entrySet().iterator();

        while (space.hasNext()) {
            HashMap.Entry entry = (HashMap.Entry) space.next();
            String key = (String)entry.getKey();
            ArrayList<Integer> value = (ArrayList<Integer>) entry.getValue();
            distribution = value.size() / data_size;

            HashMap<String, ArrayList> goal_vals = this.GoalAttribute.getHashAtIndexes(value);
            relative_remainder = calculateEntropy(goal_vals);

            remainder = distribution * relative_remainder;

            remainder += remainder;

            System.out.println("Key = " + key + ", Value = " + value);
        }
        return remainder;
    }

    public double CalculateGain(DataReader dr){
        double gain = 0.0;

        gain = calculateEntropy(MLUtils.getRowsinList(dr.getUniqueCounts(), this.GoalAttribute)) - CalculateRemainder(dr);

        return gain;
    }


    public double calculateEntropy(HashMap<String, ArrayList> instanceClass){
        double t = 0.0;
        double distribution, logVal;
        int totalInstances = 0;

        for (ArrayList value : instanceClass.values()) {
            totalInstances += value.size();

        }

        for (ArrayList value : instanceClass.values()) {
            distribution = value.size() / totalInstances;
            logVal = Math.log(distribution)/Math.log(2);
            t = - ( distribution * logVal) + t;

        }

        return t;
    }





    public DataReader getBestAttribute(ArrayList<DataReader> Attributes, DataReader GoalAttribute){
        this.Attributes = Attributes;
        this.GoalAttribute = GoalAttribute;
        double gain, maxGain = -20;
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
