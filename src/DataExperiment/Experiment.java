package DataExperiment;


import MachineLearner.*;
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

        DataProcessor processor1 = new DataProcessor("/Users/olanre/Documents/CS6735/Code/test-resources/car.data.backup", ",", false, 6);
        DataProcessor processor2 = new DataProcessor("/Users/olanre/Documents/CS6735/Code/test-resources/breast-cancer-wisconsin.data.backup", ",", false, 10);
        DataProcessor processor3 = new DataProcessor("/Users/olanre/Documents/CS6735/Code/test-resources/ecoli.data.backup", "\\s+", false, 8);
        DataProcessor processor4 = new DataProcessor("/Users/olanre/Documents/CS6735/Code/test-resources/letter-recognition.data.backup", ",", false, 0);
        DataProcessor processor5 = new DataProcessor("/Users/olanre/Documents/CS6735/Code/test-resources/mushroom.data.backup", ",", false, 0);

        ArrayList<DataProcessor> processors = new ArrayList<>();
        processors.add(processor1);
        processors.add(processor2);
        processors.add(processor3);
        //processors.add(processor4);
        processors.add(processor5);


        ArrayList<MLAlgorithm> algorithms = new ArrayList<>();
        //algorithms.add(new ID3Learner(0));
        algorithms.add(new NaiveBayes());
        algorithms.add(new NaiveBayesBagging(5));
        //algorithms.add(new KNearestNeighbour());



        for(int i = 0; i < algorithms.size(); i++){
            for(int j = 0; j < processors.size(); j++){
                MLEngine engine = new MLEngine(algorithms.get(i), processors.get(j), 5, 10);
                engine.buildClassification();
                engine.runVerification();
                //engine.runTest();
            }

        }



    }


}
