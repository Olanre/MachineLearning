package MachineLearner;

import Processor.DataReader;

import java.util.ArrayList;

/**
 * Created by olanre on 2018-10-22.
 */
public interface MLAlgorithm {

    public void learnData(ArrayList<DataReader> drs, DataReader goal);

    public void buildModel();

    public ArrayList<String> ClassifySet(ArrayList<DataReader> example_data);


}
