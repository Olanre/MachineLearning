package MachineLearner;

import DecisionTree.Node;
import Processor.DataReader;

import java.util.ArrayList;

/**
 * Created by olanre on 2018-11-27.
 */
public class Bag  {
    private int size ;
    private ArrayList<DataReader> Attributes;
    private DataReader GoalAttribute;



    public Bag(ArrayList<Integer> indexes, ArrayList<DataReader> Attributes, DataReader GoalAttribute   ) {

        this.Attributes = MLUtils.getReadersFromIndexes(indexes, Attributes);

        this.GoalAttribute = MLUtils.trimIndexesFromAttributes(indexes, GoalAttribute);

    }


    public ArrayList<DataReader> getAttributes() {
        return Attributes;
    }

    public void setAttributes(ArrayList<DataReader> attributes) {
        Attributes = attributes;
    }

    public DataReader getGoalAttribute() {
        return GoalAttribute;
    }

    public void setGoalAttribute(DataReader goalAttribute) {
        GoalAttribute = goalAttribute;
    }
}
