package MachineLearner;

import Processor.DataFormat;
import Processor.DataProcessor;
import Processor.DataReader;

import javax.xml.crypto.Data;
import java.lang.reflect.Array;
import java.util.ArrayList;
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


}
