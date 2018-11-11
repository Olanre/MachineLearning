package MachineLearner;

import DecisionTree.Node;
import Processor.DataReader;

import java.util.ArrayList;

/**
 * Created by olanre on 2018-11-06.
 */
public class KNearestNeighbour  implements MLAlgorithm{

    double error, wts;
    double[] alpha;
    private ArrayList<DataReader> Attributes;
    // private DataReader GoalAttribute;

    public void learnData(ArrayList<DataReader> drs, DataReader goal){

    }

    public void buildModel(){

    }

    public void preProcess(DataReader dr){
        //Use Sparsification on non-numerics
    }

    public ArrayList<String> ClassifySet(ArrayList<DataReader> example_data){

        ArrayList<String> mylist = new ArrayList<>();
        return mylist;
    }

}
