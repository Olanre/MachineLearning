package DataExperiment;

import MachineLearner.MLAlgorithm;
import MachineLearner.MLUtils;
import Processor.DataProcessor;
import Processor.DataFormat;
import Processor.DataReader;
import java.util.ArrayList;
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
    public MLAlgorithm algorithm;

    public MLEngine(MLAlgorithm alg, DataProcessor processor, int verification_fold, int test_fold){
        this.algorithm = alg;
        this.processor = processor;
        this.v_fold = verification_fold;
        this.t_fold = test_fold;
        this.log = new Logger("Main-Engine", "MLEngine");

    }

    public void buildClassication (){
        ArrayList<DataReader> test = this.processor.getTestData();
        DataReader target = this.processor.getTargetColumn();

        this.algorithm.learnData(test, target);


    }

    public void runVerification(){
        ArrayList<DataReader> verificationData, colData;
        ArrayList<String> verificationResult, expectedResult;
        int target = this.processor.getTargetCol();
        int correctClassified = 0;
        int error = 0;
        String msg = String.format("About to perform vertification runs on algorithm: %s \n" , this.algorithm.getAlgorithmName());
        log.Log("runVerification", msg);

        for (int i =0; i < this.v_fold; i++){
            verificationData = this.processor.getVerificationData();
            verificationResult = this.algorithm.ClassifySet(verificationData);
            colData = MLUtils.RowToColMajor(verificationData);
            expectedResult = colData.get(target).getData();

            for(int j = 0; j < expectedResult.size(); j++){
                String classified = verificationResult.get(j);
                String expected = expectedResult.get(j);
                if(classified == expected){
                    correctClassified++;
                }else{
                    error++;
                }
                msg = String.format("The algorithm %s to classified example as: %s. Actual value is: %s ", this.algorithm.getAlgorithmName(), classified, expected);
                log.Log("runVerification", msg );
            }
            msg = String.format("End of run for verification: %s \n\n\n" , String.valueOf(i));
            log.Log("runVerification", msg );


        }


    }

    public void runTest(){
        ArrayList<DataReader> testData, colData;
        ArrayList<String> testResult, expectedResult;
        int target = this.processor.getTargetCol();

        for (int i =0; i < this.t_fold; i++) {

            testData = this.processor.getVerificationData();
            testResult = this.algorithm.ClassifySet(testData);
            colData = MLUtils.RowToColMajor(testData);
            expectedResult = colData.get(target).getData();
            String msg = String.format("About to run tests on algorithm: %s \n", this.algorithm.getAlgorithmName());

            log.Log("runTest", msg);

            for (int j = 0; j < expectedResult.size(); j++) {
                String classified = testResult.get(j);
                String expected = expectedResult.get(j);
                msg = String.format("The algorithm %s to classified example as: %s. Actual value is: %s ", this.algorithm.getAlgorithmName(), classified, expected);
                log.Log("runTest", msg);
            }
            msg = String.format("End of test run: %s \n\n\n" , String.valueOf(i));
            log.Log("runTest", msg );
        }

    }

    public void getReport(){

    }




}
