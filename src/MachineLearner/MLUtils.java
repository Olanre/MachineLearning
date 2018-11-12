package MachineLearner;

import Processor.DataFormat;
import Processor.DataProcessor;
import Processor.DataReader;

import javax.xml.crypto.Data;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by olanre on 2018-11-02.
 */
public class MLUtils {

    public static DataReader trimIndexesFromAttributes(ArrayList indexesToKeep, DataReader Attributes){
        ArrayList<String> newData = new ArrayList<>();
        DataReader newReader;

        newData =  (ArrayList) Attributes.getDataAtIndexes(indexesToKeep).clone();
        DataProcessor.DataType dt = Attributes.getType();
        newReader = new DataFormat(newData, dt);

        return newReader;

    }

    public static ArrayList<DataReader> getReadersFromIndexes(ArrayList indexesToKeep, ArrayList<DataReader> Attributes){
        ArrayList<DataReader> newAttributes = new ArrayList<>();
        for(int i = 0; i < Attributes.size(); i++){
            DataReader newAttribute = MLUtils.trimIndexesFromAttributes( indexesToKeep,  Attributes.get(i));
            newAttributes.add(i, newAttribute);
        }

        return newAttributes;

    }

    public static ArrayList<String> getDataAtIndex(ArrayList<DataReader> Attributes, int index){
        ArrayList<String> Rows = new ArrayList<>();
        String data;

        for(int i = 0; i < Attributes.size(); i++){
            ArrayList<String> dataArr = Attributes.get(i).getData();
            data = dataArr.get(index);
            Rows.add(i, data);
        }
        return Rows;
    }



    public static HashMap<String, ArrayList> getRowsinList(HashMap<String, ArrayList> instanceSpace, DataReader targetAttribute){
        Iterator space = instanceSpace.entrySet().iterator();
        ArrayList<Integer> indexes = new ArrayList<>();

        while (space.hasNext()) {
            HashMap.Entry entry = (HashMap.Entry) space.next();
            String key = (String) entry.getKey();
            ArrayList<Integer> value = (ArrayList<Integer>) entry.getValue();
            indexes.addAll(value);
        }

        return targetAttribute.getHashAtIndexes(indexes);

    }

    public static HashMap<String, ArrayList> getAllFeaturesMap(ArrayList<DataReader> Attributes){
        HashMap<String, ArrayList> featureMap = new HashMap<>();
        HashMap<String, ArrayList> transientMap = new HashMap<>();

        for( int i = 0; i < Attributes.size(); i++){
            transientMap = Attributes.get(i).getUniqueCounts();
            featureMap.putAll(transientMap);
        }

        return featureMap;
    }

    public ArrayList<String> normalizeWeight(DataReader dr){
        ArrayList<Double> data = dr.getASDouble();
        ArrayList<String> result = new ArrayList<>();
        double weightedVal;
        double max = Collections.max(data);
        double min = Collections.min(data);
        double range = max - min;

        for(int i = 0; i < data.size(); i++){
            weightedVal = (data.get(i) - min)/range;
            result.set(i, Double.toString(weightedVal) );
        }

        return result;
    }

    public ArrayList<String> sparification(DataReader dr){
        ArrayList<String> data = dr.getUniqueElements();

        Collections.sort(data);


        ArrayList<String> result = new ArrayList<>();

        for(int i = 0; i < data.size(); i++){

        }

        return result;
    }


    public DataReader preProcess(DataReader dr){
        //Use Sparsification on non-numerics
        ArrayList<String> data = dr.getData();
        ArrayList<String> newData;

        if(dr.getType() == DataProcessor.DataType.DOUBLE) {
            newData = normalizeWeight(dr);
            dr = new DataFormat(newData,DataProcessor.DataType.DOUBLE );
        }else {

            for (int i = 0; i < data.size(); i++) {

            }
        }
        return dr;
    }


}
