package MachineLearner;

import Processor.DataFormat;
import Processor.DataProcessor;
import Processor.DataReader;
import Util.Util;
import Log.Logger;

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

    public static  Logger initLogger(){
        Logger log =  new Logger("MLUtils", Logger.LogLevel.INFO, "MLUtils");
        return log;
    }

    public static DataReader trimIndexesFromAttributes(ArrayList indexesToKeep, DataReader Attribute){
        ArrayList<String> newData = new ArrayList<>();
        DataReader newReader;

        newData =  (ArrayList) Attribute.getDataAtIndexes(indexesToKeep).clone();
        DataProcessor.DataType dt = Attribute.getType();
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

    public static double getMaxElement(DataReader dr) {
        ArrayList<Double> data = dr.getASDouble();
        double max = Collections.max(data);

        return max;
    }

    public static double getMinElement(DataReader dr) {
        ArrayList<Double> data = dr.getASDouble();
        double min = Collections.min(data);

        return min;
    }




    public static ArrayList<String> normalizeWeight(DataReader dr){
        ArrayList<Double> data = dr.getASDouble();
        ArrayList<String> result = new ArrayList<>();
        double weightedVal;
        double max = getMaxElement(dr);
        double min = getMinElement(dr);
        double range = max - min;

        String msg;
        Logger log = initLogger();

        msg = "<< Entering normalizeWeight for MlUtils";
        log.Log("normalizeWeight", msg);


        msg = String.format("The max number in our Reader: %s. The minimum number is: %s. The range is: %s", max, min, range);
        log.Log("normalizeWeight", msg);

        for(int i = 0; i < data.size(); i++){
            weightedVal = (data.get(i) - min)/range;
            result.add(i, Double.toString(weightedVal) );
            msg = String.format("Adding new weightedValue of: %s into our Data Object at index: %s for the Reader", weightedVal, String.valueOf(i));
            log.Log("normalizeWeight", msg);
        }
        msg = ">> Leaving normalizeWeight for MlUtils";
        log.Log("normalizeWeight", msg);
        return result;
    }

    public static ArrayList<String> sparification(DataReader dr){
        ArrayList<String> result = new ArrayList<>();

        for(int i = 0; i < dr.getData().size(); i++){
            String val = dr.getData().get(i);
            int hash = Util.createStringHash(val);
            result.add(i, Integer.toString(hash));
        }
        return result;
    }


    public static DataReader preProcess(DataReader dr){
        //Use Sparsification on non-numerics
        String msg;
        Logger log = initLogger();

        msg = "<< Entering preProcess for MlUtils";
        log.Log("preProcess", msg);
        ArrayList<String> data = dr.getData();
        ArrayList<String> newData;

        if(dr.getType() == DataProcessor.DataType.DOUBLE) {
            msg = "Data Type is Double so we will be normalizing the weights";
            log.Log("preProcess", msg);
            newData = normalizeWeight(dr);

            dr = new DataFormat(newData,DataProcessor.DataType.DOUBLE );
            dr.setLog(log);
            log.Log("preProcess", dr.printData());

        }else {

            msg = "Data Type is String so we will be sparsifying the words";
            log.Log("preProcess", msg);

            newData = sparification( dr);
            dr = new DataFormat(newData,DataProcessor.DataType.STRING );
            dr.setLog(log);
            log.Log("preProcess", dr.printData());

        }
        msg = ">> Leaving ColtoRowMajor for MlUtils";
        log.Log("ColtoRowMajor", msg);
        return dr;
    }

    public static ArrayList<DataReader> ColtoRowMajor(ArrayList<DataReader> DataSet){

        Logger log = initLogger();
        String msg;
        msg = "<< Entering preProcess for MlUtils";
        log.Log("preProcess", msg);

        //must convert from column major to row major
        String temp_data;
        ArrayList<DataReader> row_major = new ArrayList<>();
        int row_list = DataSet.get(0).getData().size();

        for (int i = 0; i < row_list; i++){

            ArrayList<String> temp = new ArrayList<String>();
            for(int j = 0; j < DataSet.size(); j++){

                DataReader df = DataSet.get(j);
                //DataReader df = new DataFormat( DataSet.get(j));
                temp_data = df.getData().get(i);
                msg = String.format("Adding new column: %s at index: %s in row: %s: " , temp_data, String.valueOf(j), String.valueOf(i) );
                log.Log("ColtoRowMajor", msg);
                temp.add(j, temp_data);

            }

            DataReader dr = new DataFormat(temp, DataProcessor.DataType.STRING);
            dr.setLog(log);
            log.Log("ColtoRowMajor", dr.printData());
            row_major.add(i, dr);
            msg = String.format("Adding new row in data at index %s", String.valueOf(i));
            log.Log("ColtoRowMajor", msg);

        }

        msg = ">> Leaving ColtoRowMajor for MlUtils";
        log.Log("ColtoRowMajor", msg);

        return row_major;
    }


    public static ArrayList<DataReader> RowToColMajor(ArrayList<DataReader> DataSet){


        return ColtoRowMajor( DataSet);
    }







}
