package MachineLearner;

import DecisionTree.Node;
import Log.Logger;
import Processor.DataReader;
import Processor.DataFormat;
import Util.Util;
import java.util.ArrayList;

/**
 * Created by olanre on 2018-11-27.
 */
public class Bagger {

    private ArrayList<DataReader> Attributes;
    private DataReader GoalAttribute;
    private ArrayList<MLAlgorithm> WeakLearners;
    private String AlgorithmName = "Bagger";
    Logger log;


    int bags = 50;
    int sampleRatio;


    public Bagger( int sampleRatio){
        this.sampleRatio = sampleRatio;
    }


    public void learnData(ArrayList<DataReader> drs, DataReader goal){
        this.Attributes = drs;
        this.GoalAttribute = goal;
        initLoggers();
        buildModel();


    }


    public void initLoggers(){
        this.log = new Logger(this.AlgorithmName, Logger.LogLevel.INFO, this.AlgorithmName);
        this.GoalAttribute.setLog(this.log);
        for(DataReader Attribute: this.Attributes){
            Attribute.setLog(this.log);
        }
    }

    public void setAlgorithmName(String algorithmName) {
        AlgorithmName = algorithmName;
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public int getBags() {
        return bags;
    }

    public void setBags(int bags) {
        this.bags = bags;
    }

    public int getSampleRatio() {
        return sampleRatio;
    }

    public void setSampleRatio(int sampleRatio) {
        this.sampleRatio = sampleRatio;
    }

    public void buildBags(){
        int sampleSize;
        int dataSize;
        ArrayList<DataReader> AttributeBag = new ArrayList<>();
        ArrayList<Integer> indexes;

        dataSize = this.Attributes.get(0).getData().size();
        sampleSize = dataSize/ this.sampleRatio;
        this.WeakLearners = new ArrayList<>();
        for(int i = 0; i < bags; i++){
            indexes = Util.getRandomInts(0, dataSize - 1, sampleSize  );
            Bag bag = new Bag(indexes, this.Attributes, this.GoalAttribute);
            this.WeakLearners.add(i, getNewWeakLearner(bag));
        }

    }

    public MLAlgorithm getNewWeakLearner(Bag bag ){

        MLAlgorithm weakLearner = new ID3Learner(0);
        weakLearner.learnData(bag.getAttributes(), bag.getGoalAttribute()  );

        return weakLearner;
    }

    public ArrayList<String> ClassifySet(ArrayList<DataReader> example_data){
        ArrayList<String> result = new ArrayList<String>();
        DataReader row;

        for(int i = 0; i < example_data.size(); i ++){
            row = example_data.get(i);
            String classification = Classify(row.getData());
            result.add(classification);
        }

        return result;
    }

    public String Classify( ArrayList<String> cols ){
        ArrayList<String> bagResults = new ArrayList<>();
        for( int i = 0; i < this.WeakLearners.size(); i++){
            MLAlgorithm learner = this.WeakLearners.get(i);
            String classification = learner.Classify(cols);
            if(classification.equals("")) continue;

            bagResults.add( classification );
        }
        return Util.findMode(bagResults);

    }

    public void buildModel(){
        buildBags();
    }






}
