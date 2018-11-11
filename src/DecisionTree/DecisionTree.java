package DecisionTree;

import Processor.DataReader;

import java.util.ArrayList;

/**
 * Created by olanre on 2018-10-23.
 */
public interface DecisionTree {

    public double CalculateGain( DataReader dr);

    public DataReader getBestAttribute(ArrayList<DataReader> Attributes, DataReader GoalAttribute);

    public Node buildTree(ArrayList<DataReader> currentAttributes, DataReader targetAttribute);

    public String Classify( ArrayList<String> cols, Node tree);



}
