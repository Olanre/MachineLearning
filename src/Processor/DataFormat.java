package Processor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;



/**
 * Created by olanre on 2018-10-22.
 */
public class DataFormat implements  DataReader{
    private ArrayList<String> Data;
    private DataProcessor.DataType type;


    public DataFormat(ArrayList<String> newData, DataProcessor.DataType newtype){
        this.Data = newData;
        this.type = newtype;
    }

    public DataFormat(DataFormat newData){
        this.Data = (ArrayList) newData.getData().clone();
        this.type = newData.type;
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

    public void printData(){
        //TODO
    }

    public ArrayList<String> getUniqueElements(){
        ArrayList<String> list = new ArrayList<>();
        String str;

        for(int i = 0; i < this.Data.size(); i ++){
            str = this.Data.get(i);

            if (!list.contains(str)) {

                list.add(i, str);
            }else{

                continue;
            }
        }

        return list;
    }

    public String findMode(ArrayList<String> Arr){
        HashMap<String,Integer> hashMap = new HashMap<String,Integer>();
        String str;
        String mode = Arr.get(0);
        int max  = 1;


        for(int i = 0; i < Arr.size(); i ++){
            str = Arr.get(i);

            if (hashMap.get(str) == null) {

                hashMap.put(str, 1);
            }else{

                int count = hashMap.get(str);
                count++;
                hashMap.put(str, count);

                if(count > max) {
                    max  = count;
                    mode = str;
                }
            }
        }

        return mode;
    }


    public HashMap<String, ArrayList> getUniqueCounts(){
        HashMap<String,ArrayList> hashMap = new HashMap<>();
        String str;
        ArrayList<Integer> index = new ArrayList<Integer>();
        for(int i = 0; i < this.Data.size(); i ++){
            str = this.Data.get(i);


            if (hashMap.get(str) == null) {
                index = new ArrayList<Integer>();
                index.add(i);
                hashMap.put(str, index);
            }else{

                index = hashMap.get(str);
                index.add(i);
                hashMap.put(str, index);

            }

        }

        return hashMap;
    }

    public HashMap<String, ArrayList> getHashAtIndexes(ArrayList<Integer> arr){
        HashMap<String,ArrayList> hashMap = new HashMap<String, ArrayList>();
        String str;
        int pos;
        ArrayList<Integer> index = new ArrayList<>();
        ;

        for(int i = 0; i < arr.size(); i ++ ){
            pos = arr.get(i);
            str = this.Data.get(pos);

            if (hashMap.get(str) == null) {
                index = new ArrayList<Integer>();
                index.add(pos);
                hashMap.put(str, index);
            }else{

                index = hashMap.get(str);
                index.add(pos);
                hashMap.put(str, index);

            }

        }

        return hashMap;
    }

    public ArrayList<String> getDataAtIndexes(ArrayList<Integer> arr){
        HashMap<String,ArrayList> hashMap = new HashMap<String, ArrayList>();
        String str;
        int pos;
        ArrayList<String> index = new ArrayList<>();
        ;

        for(int i = 0; i < arr.size(); i ++ ){
            pos = arr.get(i);
            str = this.Data.get(pos);


            index.add(str);


        }

        return index;
    }




    public ArrayList<Integer> getASInteger(){
        ArrayList<Integer> new_arr = new ArrayList<Integer>();
        String Value;
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
        String Value;
        for(int i = 0; i < this.Data.size(); i ++ ) {
            Value = this.Data.get(i);
            try {
                new_arr.add(Double.parseDouble(Value.trim()));
            } catch(NumberFormatException nfe) {

            }
        }
        return new_arr;
    }



}

