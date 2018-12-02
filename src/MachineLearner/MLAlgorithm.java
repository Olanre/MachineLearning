package MachineLearner;

import Log.Logger;
import Processor.DataReader;

import java.util.ArrayList;

/**
 * Created by olanre on 2018-10-22.
 */
public interface MLAlgorithm {

    public void learnData(ArrayList<DataReader> drs, DataReader goal);

    public void buildModel();

    public String getAlgorithmName();

    public ArrayList<String> ClassifySet(ArrayList<DataReader> example_data);

    public String Classify( ArrayList<String> cols);

    public Logger getLog();

    public void setLog(Logger log);

}
