package MachineLearner;

import Log.Logger;
import Processor.DataReader;

import java.util.ArrayList;

/**
 * Created by olanre on 2018-11-27.
 */
public class NaiveBayesBagging extends Bagger implements MLAlgorithm {

    private ArrayList<MLAlgorithm> WeakLearners;

    int sampleRatio = 10;
    public static final String AlgorithmName = "NaiveBayes-Bagging";


    public NaiveBayesBagging( int sampleRatio){
        super(sampleRatio);

    }

    public String getAlgorithmName() {
        return AlgorithmName;
    }

    public MLAlgorithm getNewWeakLearner(Bag bag ){

        MLAlgorithm weakLearner = new NaiveBayes( );
        weakLearner.learnData(bag.getAttributes(), bag.getGoalAttribute());
        weakLearner.buildModel();

        return weakLearner;
    }


}

