package DataExperiment;


import MachineLearner.ID3Learner;
import MachineLearner.MLAlgorithm;
import MachineLearner.MLUtils;
import Processor.DataProcessor;
import Processor.DataFormat;
import Processor.DataReader;
import java.util.ArrayList;
import Log.Logger;
/**
 * Created by olanre on 2018-11-27.
 */
public class Experiment {


    public static void main(String[] args) {
        String[] files = {"breast-cancer-wisconsin.data", "car.data", "ecoli.data", "letter-recognition.data", "mushroom.data"};

        DataProcessor dp = new DataProcessor("car.data", ",", false, 6);
        MLAlgorithm ID3 = new ID3Learner(0);
        MLEngine engine = new MLEngine(ID3, dp, 5, 2);
        engine.runVerification();
        engine.runTest();


    }


}
