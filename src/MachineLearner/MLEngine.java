package MachineLearner;

import MachineLearner.MLAlgorithm;
import Processor.DataProcessor;

/**
 * Created by olanre on 2018-10-22.
 */
public class MLEngine {

    public String filename;
    public int v_fold;
    public int t_fold;
    public int target;
    public DataProcessor processor;
    public MLAlgorithm algorithm;

    public MLEngine(MLAlgorithm alg, DataProcessor processor, int verification_fold, int test_fold, int target_attribute){
        this.algorithm = alg;
        this.processor = processor;
        this.v_fold = verification_fold;
        this.t_fold = test_fold;
        this.target = target_attribute;

    }

    public void buildClassication (){


    }

    public void runVerification(){

    }

    public void runTest(){

    }

    public void getReport(){

    }




}
