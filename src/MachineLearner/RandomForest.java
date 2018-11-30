package MachineLearner;

import DecisionTree.Node;
import Processor.DataReader;
import Processor.DataFormat;
import Util.Util;
import java.util.ArrayList;

/**
 * Created by olanre on 2018-11-02.
 */
public class RandomForest  extends Bagger implements MLAlgorithm{

    private ArrayList<DataReader> Attributes;
    private DataReader GoalAttribute;
    private ArrayList<MLAlgorithm> WeakLearners;
    public static final String AlgorithmName = "RandomForest";


    int bags = 50;
    int sampleRatio = 10;
    int splitRatio = 3;

    public RandomForest(int  splitRatio, int sampleRatio){

        super( sampleRatio);
        this.splitRatio = splitRatio;
    }

    public String getAlgorithmName() {
        return AlgorithmName;
    }


    public MLAlgorithm getNewWeakLearner(Bag bag ){

        MLAlgorithm weakLearner = new ID3Learner( this.splitRatio  );
        weakLearner.learnData(bag.getAttributes(), bag.getGoalAttribute());

        return weakLearner;
    }





}
