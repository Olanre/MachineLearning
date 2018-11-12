package MachineLearner;

import DecisionTree.Node;
import Processor.DataProcessor;
import Processor.DataReader;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by olanre on 2018-11-06.
 */
public class KNearestNeighbour  implements MLAlgorithm{

    double error, wts;
    int k;
    double[] alpha;
    private ArrayList<DataReader> Attributes;
    // private DataReader GoalAttribute;

    public void learnData(ArrayList<DataReader> drs, DataReader goal){

    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public double Calculate_Minkowski_Distance(ArrayList<Double> attributesVals, ArrayList<Double> sampleValues, int p){
        double Distance, Summation = 0.0;
        double powerResult, subtractResult;
        if(attributesVals.size() != sampleValues.size())return 0;

        for(int i = 0; i < attributesVals.size(); i ++){
            subtractResult = Math.abs(attributesVals.get(i) - sampleValues.get(i));
            powerResult = Math.pow(subtractResult, p);
            Summation += powerResult;
        }
        Distance = Math.sqrt(Summation);

        return Distance;
    }



    public void buildModel(){

    }


    public ArrayList<String> ClassifySet(ArrayList<DataReader> example_data){

        ArrayList<String> result = new ArrayList<>();
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

        return classification;
    }



}
