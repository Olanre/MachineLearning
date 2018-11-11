package MachineLearner;

import DecisionTree.Node;
import Processor.DataReader;
import Processor.DataFormat;
import Util.Util;
import java.util.ArrayList;

/**
 * Created by olanre on 2018-11-02.
 */
public class RandomForest implements MLAlgorithm{

    private ArrayList<DataReader> Attributes;
    private DataReader GoalAttribute;
    private ArrayList<Node> trees;

    int bags = 50;
    int sampleRatio = 10;
    int splitRatio = 3;

    public RandomForest(ArrayList<DataReader> Attributes, DataReader GoalAttribute,int  splitRatio){
        this.Attributes = Attributes;
        this.GoalAttribute = GoalAttribute;
        this.splitRatio = splitRatio;
    }


    public void learnData(ArrayList<DataReader> drs, DataReader goal){
        this.Attributes = drs;
        this.GoalAttribute = goal;

    }

    public int getBags() {
        return bags;
    }

    public void setBags(int bags) {
        this.bags = bags;
    }

    public int getSampleRatio() {
        return sampleRatio;
    }

    public void setSampleRatio(int sampleRatio) {
        this.sampleRatio = sampleRatio;
    }

    public void buildBags(){
        int sampleSize;
        int dataSize;
        ArrayList<DataReader> AttributeBag = new ArrayList<>();
        ArrayList<Integer> indexes;

        dataSize = this.GoalAttribute.getData().size();
        sampleSize = dataSize/ this.sampleRatio;
        this.trees = new ArrayList<>();
        for(int i = 0; i < bags; i++){
            indexes = Util.getRandomInts(0, dataSize, sampleSize  );
            this.trees.add(i, getNewBag(indexes));
        }

    }

    public Node getNewBag(ArrayList<Integer> indexes ){

        ArrayList<DataReader> AttributeBag = MLUtils.getReadersFromIndexes(indexes,this.Attributes);;
        DataReader newGoalAttribute = MLUtils.trimIndexesFromAttributes( indexes,  this.GoalAttribute);

        ID3Learner weakLearner = new ID3Learner(AttributeBag, newGoalAttribute, this.splitRatio  );
        weakLearner.buildModel();
        Node bag = weakLearner.getTree();


        return bag;
    }

    public ArrayList<String> ClassifySet(ArrayList<DataReader> example_data){
        ArrayList<String> result = new ArrayList<String>();
        DataReader row;

        for(int i = 0; i < example_data.size(); i ++){
            row = example_data.get(i);
            String classification = ClassifyBags(row.getData(), this.trees);
            result.add(classification);
        }

        return result;
    }

    public String ClassifyBags( ArrayList<String> cols, ArrayList<Node> trees){
        ArrayList<String> bagResults = new ArrayList<>();
        for( int i = 0; i < trees.size(); i++){
            bagResults.add( Classify( cols, trees.get(i)) );
        }
        return Util.findMode(bagResults);

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


    public void buildModel(){

    }

    public void preProcess(DataReader dr){

    }


}
