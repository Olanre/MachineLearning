package Processor;

import Log.Logger;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by olanre on 2018-10-23.
 */
public interface DataReader {

    public DataProcessor.DataType getType();

    public ArrayList<String> getData();

    public String printData();

    public void setLog(Logger log);

    public HashMap<String, ArrayList> getUniqueCounts();

    public ArrayList<String> getUniqueElements();

    public HashMap<String, ArrayList> getHashAtIndexes(ArrayList<Integer> arr);

    public String findMode(ArrayList<String> Arr);

    public ArrayList<String> getDataAtIndexes(ArrayList<Integer> arr);

    public ArrayList<Double> getASDouble();


    }
