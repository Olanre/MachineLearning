package Processor;

import Log.Logger;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;



/**
 * Created by olanre on 2018-10-22.
 */
public class DataFormat implements  DataReader{
    private ArrayList<String> Data;
    private DataProcessor.DataType type;
    Logger log;



    public DataFormat(ArrayList<String> newData, DataProcessor.DataType newtype){
        this.Data = newData;
        this.type = newtype;
    }

    public DataFormat(DataFormat newData){
        this.Data = (ArrayList) newData.getData().clone();
        this.type = newData.type;
    }

    public DataFormat(DataReader newData){
        this.Data = (ArrayList) newData.getData().clone();
        this.type = newData.getType();
    }


    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public DataFormat() {
        this.type = DataProcessor.DataType.STRING;
    }

    public void setDataType(DataProcessor.DataType newtype){
        this.type = newtype;
    }

    public DataProcessor.DataType getType(){
        return this.type;
    }

    public ArrayList<String> getData(){
        return this.Data;
    }

    public void addDataValue( String newValue){
        this.Data.add(newValue);
    }

    public String printData(){

        String msg = "<< Entering printData for Data Format";
        log.Log("printData", msg);
        StringBuilder sb=new StringBuilder("About to print out the contents of our Data Format with Data Type: " + type.name() + "\n\n");
        sb.append("Java");//now original string is changed
        for (int i = 0; i < Data.size(); i++) {
            msg = String.format("The value at index %s is %s \n" , String.valueOf(i), Data.get(i));
            sb.append(msg);//now original string is changed

        }
        msg = ">> Leaving printData for Data Format";
        log.Log("printData", msg);
        return sb.toString();


    }

    public ArrayList<String> getUniqueElements(){

        ArrayList<String> list = new ArrayList<>();
        String str, msg;
        msg = "<< Entering getUniqueElements for Data Format";
        //log.Log("getUniqueElements", msg);
        for(int i = 0; i < this.Data.size(); i ++){
            str = this.Data.get(i);

            if (!list.contains(str)) {
                //msg = String.format("Adding an element into the List at index: %s and value of: %s", String.valueOf(i), str);
                //log.Log("getUniqueElements", msg);
                list.add( str);

            }else{

                continue;
            }
        }
        msg = ">> Leaving getUniqueElements for Data Format";
        //log.Log("getUniqueElements", msg);

        return list;
    }

    public String findMode(ArrayList<String> Arr){
        HashMap<String,Integer> hashMap = new HashMap<String,Integer>();
        String str,msg;
        String mode = Arr.get(0);
        int max  = 1;
        msg = "<< Entering findMode for Data Format";
        log.Log("findMode", msg);

        for(int i = 0; i < Arr.size(); i ++){
            str = Arr.get(i);

            if (hashMap.get(str) == null) {
                msg = String.format("Adding a new element in our hashmap at index: %s and value of: %s", String.valueOf(i), str);
                log.Log("findMode", msg);

                hashMap.put(str, 1);
            }else{

                int count = hashMap.get(str);
                count++;
                hashMap.put(str, count);

                if(count > max) {
                    max  = count;
                    mode = str;
                    msg = String.format("We have a new mode value in ArrayList, number of occurances isi:  %s and value of: %s", String.valueOf(max), mode);
                    log.Log("findMode", msg);

                }
            }
        }
        msg = ">> Leaving findMode for Data Format";
        log.Log("findMode", msg);
        return mode;
    }


    public HashMap<String, ArrayList> getUniqueCounts(){
        HashMap<String,ArrayList> hashMap = new HashMap<>();
        String str, msg;
        ArrayList<Integer> index = new ArrayList<Integer>();

        msg = "<< Entering getUniqueCounts for Data Format";
        //log.Log("getUniqueCounts", msg);
        for(int i = 0; i < this.Data.size(); i ++){
            str = this.Data.get(i);


            if (hashMap.get(str) == null) {
                index = new ArrayList<Integer>();
                index.add(i);
                hashMap.put(str, index);
                //msg = String.format("Adding a new element in our HashMap. Key is : %s. New Index in ArrayList is : %s ", str, String.valueOf(i));
                //log.Log("getUniqueCounts", msg);
            }else{

                index = hashMap.get(str);
                index.add(i);
                hashMap.put(str, index);
                //msg = String.format("Adding a new value index %s in the Arraylist for key: %s in our HashMap.",  String.valueOf(i), str);
                //log.Log("getUniqueCounts", msg);
            }

        }
        msg = ">> Leaving getUniqueCounts for Data Format";
        //log.Log("getUniqueCounts", msg);

        return hashMap;
    }

    public HashMap<String, ArrayList> getHashAtIndexes(ArrayList<Integer> arr){
        HashMap<String,ArrayList> hashMap = new HashMap<String, ArrayList>();
        String str, msg;
        int pos;
        ArrayList<Integer> index = new ArrayList<>();
        msg = "<< Entering getHashAtIndexes for Data Format";
        log.Log("getHashAtIndexes", msg);

        for(int i = 0; i < arr.size(); i ++ ){
            pos = arr.get(i);
            str = this.Data.get(pos);

            if (hashMap.get(str) == null) {
                index = new ArrayList<Integer>();
                index.add(pos);
                hashMap.put(str, index);
                msg = String.format("Adding a new element in our HashMap. Key is : %s. New Index in ArrayList is : %s", str, String.valueOf(pos));
                log.Log("getHashAtIndexes", msg);
            }else{

                index = hashMap.get(str);
                index.add(pos);
                hashMap.put(str, index);
                msg = String.format("Adding a new value index %s in the Arraylist for key: %s in our HashMap.",  String.valueOf(pos), str);
                log.Log("getHashAtIndexes", msg);
            }
        }
        msg = ">> Leaving getHashAtIndexes for Data Format";
        log.Log("getHashAtIndexes", msg);

        return hashMap;
    }

    public ArrayList<String> getDataAtIndexes(ArrayList<Integer> arr){
        HashMap<String,ArrayList> hashMap = new HashMap<String, ArrayList>();
        String str, msg;
        int pos;
        ArrayList<String> index = new ArrayList<>();
        msg = "<< Entering getDataAtIndexes for Data Format";
        log.Log("getHashAtIndexes", msg);

        for(int i = 0; i < arr.size(); i ++ ){
            pos = arr.get(i);
            str = this.Data.get(pos);

            msg = String.format("Adding a new value index: %s and value: %s in the ArrayList.",  String.valueOf(pos), str);
            log.Log("getDataAtIndexes", msg);
            index.add(str);


        }
        msg = ">> Leaving getDataAtIndexes for Data Format";
        log.Log("getHashAtIndexes", msg);
        return index;
    }




    public ArrayList<Integer> getASInteger(){
        ArrayList<Integer> new_arr = new ArrayList<Integer>();
        String Value, msg;
        for(int i = 0; i < this.Data.size(); i ++ ) {
            Value = this.Data.get(i);
            try {
                new_arr.add(Integer.parseInt(Value.trim()));
            } catch(NumberFormatException nfe) {

            }
        }
        return new_arr;
    }

    public ArrayList<Double> getASDouble(){
        ArrayList<Double> new_arr = new ArrayList<Double>();
        ArrayList<Double> returnArr = new ArrayList<Double>();

        String Value, msg;
        msg = "<< Entering getASDouble for Data Format";
        log.Log("getASDouble", msg);
        for(int i = 0; i < this.Data.size(); i ++ ) {
            Value = this.Data.get(i);
            msg = String.format("About to attempt to parse %s as a double",  Value);
            log.Log("getASDouble", msg);
            try {
                new_arr.add(Double.parseDouble(Value.trim()));
            } catch(NumberFormatException nfe) {
                msg = String.format("Unable to parse %s as a double",  Value);
                log.Log("getASDouble", msg);
            }
        }
        double[] target = new double[new_arr.size()];
        for (int i = 0; i < target.length; i++) {
            target[i] = new_arr.get(i);                // java 1.5+ style (outboxing)
        }

        for(double d : target) returnArr.add(d);

        msg = ">> Leaving getASDouble for Data Format";
        log.Log("getASDouble", msg);
        return returnArr;
    }



}

