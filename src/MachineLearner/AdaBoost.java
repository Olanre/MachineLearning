package MachineLearner;

import Processor.DataReader;
import DecisionTree.Node;
import java.util.ArrayList;

/**
 * Created by olanre on 2018-11-01.
 */
public class AdaBoost implements MLAlgorithm {

    double error, wts;
    double[] alpha;
    private ArrayList<DataReader> Attributes;
   // private DataReader GoalAttribute;
    private ArrayList<Node> trees;

    public void learnData(ArrayList<DataReader> drs, DataReader goal){

    }

    public void buildModel(){

    }

    public void preProcess(DataReader dr){

    }

    public ArrayList<String> ClassifySet(ArrayList<DataReader> example_data){

        ArrayList<String> mylist = new ArrayList<>();
        return mylist;
    }

}
