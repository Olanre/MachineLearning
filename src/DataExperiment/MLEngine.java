package DataExperiment;

import MachineLearner.MLAlgorithm;
import MachineLearner.MLUtils;
import Processor.DataProcessor;
import Processor.DataFormat;
import Processor.DataReader;
import java.util.ArrayList;
import java.util.HashMap;

import Log.Logger;

/**
 * Created by olanre on 2018-10-22.
 */
public class MLEngine {

    public String filename;
    public int v_fold;
    public int t_fold;
    public int target;
    Logger log;
    public DataProcessor processor;
    public ArrayList<DataProcessor> processors;
    private HashMap<DataProcessor, MLAlgorithm> ProcessorToAlgo = new HashMap<>();

    public MLAlgorithm algorithm;

    public MLEngine(MLAlgorithm alg, ArrayList<DataProcessor> processors, int verification_fold, int test_fold){
        this.algorithm = alg;
        this.processors = processors;
        this.v_fold = verification_fold;
        this.t_fold = test_fold;
        this.log = new Logger("Main-Engine", Logger.LogLevel.DEBUG, "MLEngine");

    }


    public MLEngine(MLAlgorithm alg, DataProcessor processor, int verification_fold, int test_fold){
        this.algorithm = alg;
        this.processor = processor;
        this.v_fold = verification_fold;
        this.t_fold = test_fold;
        this.log = new Logger("Main-Engine", Logger.LogLevel.DEBUG, "MLEngine");
        ProcessorToAlgo.put(this.processor, this.algorithm);

    }



    public void buildClassifications(){
        String msg;
        for(int i = 0; i < processors.size(); i++) {
            this.processor = this.processors.get(i);
            msg = String.format(" Currently about to build classification models  for the processor at index: %s with filename: %s", String.valueOf(i), processor.getFileName());
            log.Log("buildClassifications", msg);
            buildClassification();
        }
        this.processor = this.processors.get(0);
    }


    public void buildClassification (){
        String msg = "<< Entering buildClassication for MlEngine";
        log.Log("buildClassication", msg);
        int target = this.processor.getTargetCol();

        this.processor.processData();
        ArrayList<DataReader> test = this.processor.getTrainingData();
        DataReader goal = test.get(target);
        test.remove(goal);
        this.algorithm.learnData(test, goal);
        ProcessorToAlgo.put(this.processor, this.algorithm);

        msg = ">> Leaving buildClassication for MlEngine";
        log.Log("buildClassication", msg);

    }

    public void runVerifications(){
        String msg;
        for(int i = 0; i < processors.size(); i++) {
            this.processor = this.processors.get(i);
            msg = String.format(" Currently about to run verification  for the processor at index: %s with filename: %s", String.valueOf(i), processor.getFileName());
            log.Log("runVerification", msg);
            runVerification();
        }
        this.processor = this.processors.get(0);

    }

    public void runVerification(){
        ArrayList<DataReader> verificationData, colData;
        ArrayList<String> verificationResult, expectedResult;
        int target = this.processor.getTargetCol();
        int correctClassified = 0;
        int error = 0;
        double accuracy, avgAccuracy = 0.0;

        String msg = "<< Entering runVerification for MlEngine";
        log.Log("runVerification", msg);

        msg = String.format("About to perform verification runs on algorithm: %s \n" , this.algorithm.getAlgorithmName());
        log.Log("runVerification", msg);
        MLAlgorithm algo = ProcessorToAlgo.get(this.processor);


        for (int i = 0; i < this.v_fold; i++) {
            verificationData = this.processor.getVerificationData();
            colData = MLUtils.RowToColMajor(verificationData);
            expectedResult = colData.get(target).getData();
            colData.remove(target);
            verificationData = MLUtils.ColtoRowMajor(colData);
            verificationResult = algo.ClassifySet(verificationData);
            accuracy  = 0.0;
            correctClassified = 0;
            error = 0;
            msg = String.format("About to perform classify examples of size: %s \n" , String.valueOf(expectedResult.size()));
            log.Log("runVerification", msg);
            for (int j = 0; j < expectedResult.size(); j++) {
                String classified = verificationResult.get(j);
                String expected = expectedResult.get(j);
                if (classified.equals(expected)) {
                    correctClassified++;
                } else {
                    error++;
                }
                msg = String.format("The algorithm %s to classified example as: %s. Actual value is: %s ", algo.getAlgorithmName(), classified, expected);
                //log.Log("runVerification", msg);

            }
            accuracy = (Double.valueOf(correctClassified) / Double.valueOf(expectedResult.size())) * 100.0;

            avgAccuracy += accuracy;
            msg = String.format("End of run for verification: %s. The accuracy reported by algorithm: %s on file: %s  is: %s ",String.valueOf(i), algo.getAlgorithmName(), this.processor.getFileName(), String.valueOf(accuracy));
            log.Log("runVerification", msg);

        }
        avgAccuracy = avgAccuracy / Double.valueOf(v_fold);
        msg = String.format("End of runs. The  average accuracy reported by algorithm: %s on file: %s  is: %s  \n\n\n", algo.getAlgorithmName(), this.processor.getFileName(), String.valueOf(avgAccuracy));
        log.Log("runVerification", msg);

        msg = ">> Leaving runVerification for MlEngine";
        log.Log("runVerification", msg);


    }

    public void runTests(){
        String msg;
        for(int i = 0; i < processors.size(); i++) {
            this.processor = this.processors.get(i);
            msg = String.format(" Currently about to run tests  on the processor at index: %s with filename: %s", String.valueOf(i), processor.getFileName());
            log.Log("runTests", msg);
            runTest();
        }
        this.processor = this.processors.get(0);

    }

    public void runTest(){
        ArrayList<DataReader> testData, colData;
        ArrayList<String> testResult, expectedResult;
        int target = this.processor.getTargetCol();
        int correctClassified = 0;
        int error = 0;
        double accuracy, avgAccuracy = 0.0;

        String msg = "<< Entering runTest for MlEngine";
        log.Log("runTest", msg);

        MLAlgorithm algo = ProcessorToAlgo.get(this.processor);


        for (int i =0; i < this.t_fold; i++) {

            testData = this.processor.getTestData();
            colData = MLUtils.RowToColMajor(testData);
            expectedResult = colData.get(target).getData();
            colData.remove(target);
            testData = MLUtils.ColtoRowMajor(colData);
            testResult = algo.ClassifySet(testData);

            msg = String.format("About to run tests on algorithm: %s \n", algo.getAlgorithmName());
            log.Log("runTest", msg);
            accuracy  = 0.0;
            correctClassified = 0;
            error = 0;

            msg = String.format("About to perform classify examples of size: %s \n" , String.valueOf(expectedResult.size()));
            log.Log("runTest", msg);
            for (int j = 0; j < expectedResult.size(); j++) {
                String classified = testResult.get(j);
                String expected = expectedResult.get(j);
                if (classified.equals(expected)) {
                    correctClassified++;
                } else {
                    error++;
                }
                msg = String.format("The algorithm %s to classified example as: %s. Actual value is: %s ", algo.getAlgorithmName(), classified, expected);
                //log.Log("runTest", msg);
            }
            accuracy = (Double.valueOf(correctClassified) / Double.valueOf(expectedResult.size())) * 100.0;

            avgAccuracy += accuracy;
            msg = String.format("End of run for test: %s. The accuracy reported by algorithm: %s on file: %s  is: %s",  String.valueOf(i), algo.getAlgorithmName(), this.processor.getFileName(),String.valueOf(accuracy));
            log.Log("runTest", msg );
        }
        avgAccuracy = avgAccuracy / Double.valueOf(t_fold);

        msg = String.format("End of runs. The  average accuracy reported by algorithm: %s on file: %s  is: %s \n\n\n", algo.getAlgorithmName(), this.processor.getFileName(), String.valueOf(avgAccuracy));
        log.Log("runTest", msg);

    }

    public void getReport(){

    }




}
