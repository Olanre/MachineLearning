package Processor; /**
 * Created by olanre on 2018-10-15.
 */


import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Collections;
import java.util.Scanner;
import java.util.HashMap;
import Log.Logger;
import Processor.DataReader;

import javax.xml.crypto.Data;

public class DataProcessor {

    private String fileName;
    private Logger log4US;
    private String fileDelimiter;
    private int TrainingRatio = 8;
    private int TestRatio = 2;
    private int CrossVerificationIndex = 0;

    private boolean missingColumns;
    private ArrayList<DataFormat> DataSet;
    private int TargetCol;


    public enum ScanOrientation {
        Vertical, Horizontal
    }

    public enum DataType{
        STRING, DOUBLE, INTEGER
    }



    public DataProcessor( String filename, String fileDelimiter, boolean missingColumns, int TargetCol){
        this.fileName = filename;
        this.missingColumns = missingColumns;
        this.TargetCol = TargetCol;
        this.fileDelimiter = fileDelimiter;

    }

    public void setTrainingRatio(int newRatio){
        this.TrainingRatio = newRatio;
    }

    public void setCrossVerificationIndex(int newIndex){
        this.CrossVerificationIndex = newIndex;
    }

    public int getTrainingRatio(){
        return this.TrainingRatio;
    }

    public void setTargetCol(int i){
        this.TargetCol = i;
    }

    public int getCrossVerificationIndex(){
        return this.CrossVerificationIndex;
    }

    public int getTargetCol(){
        return this.TargetCol;
    }



    public ArrayList<DataReader> getTrainingData(){
        int limit, size;
        String temp_data;
        DataType temp_type;
        ArrayList<DataReader> training_data = new ArrayList<>();

        for (int i = 0; i < this.DataSet.size(); i++){
            if (i == getTargetCol()) continue;
            DataFormat df = this.DataSet.get(i);
            size = df.getData().size();
            limit = size / this.TrainingRatio;
            limit = size - limit;
            ArrayList<String> temp = new ArrayList<String>();
            temp_type = df.getType();

            for(int j = 0; j < limit; j++){

                temp_data = df.getData().get(j);
                temp.add(j, temp_data);

            }

            DataReader dr = new DataFormat(temp, temp_type);
            training_data.add(i, dr);
        }
        return training_data;
    }

    public DataReader getTargetColumn(){

        DataReader target_col = new DataFormat();

        for (int i = 0; i < this.DataSet.size(); i++){
            if ( getTargetCol() == i) {
                DataFormat df = this.DataSet.get(i);


                target_col = new DataFormat(df.getData(), df.getType());
            }

        }
        return target_col;
    }


    public ArrayList<DataReader> getVerificationData(){

        int limit, size, i, row_list;
        String temp_data;
        ArrayList<DataReader> verification_data = new ArrayList<>();


        size = this.DataSet.get(0).getData().size();
        row_list = size / this.TrainingRatio;
        row_list = size - row_list;
        limit = row_list / 5;
        if(getCrossVerificationIndex() >= row_list + 1)  setCrossVerificationIndex(0);
        i = getCrossVerificationIndex();
        limit = limit + i;

        //must convert from column major to row major
        for (; i < limit; i++){

            ArrayList<String> temp = new ArrayList<String>();
            for(int j = 0; j < this.DataSet.size(); j++){
                if (j == getTargetCol()) continue;
                DataFormat df = this.DataSet.get(j);
                temp_data = df.getData().get(i);
                temp.add(j, temp_data);

            }
            if(getCrossVerificationIndex() >= row_list) {
                setCrossVerificationIndex(0);
                break;
            }
            DataReader dr = new DataFormat(temp, DataType.STRING);
            verification_data.add(i, dr);
            setCrossVerificationIndex(i);

        }
        return verification_data;

    }

    public ArrayList<DataReader> getTestData(){
        int size, row_list;
        String temp_data;
        ArrayList<DataReader> verification_data = new ArrayList<>();
        size = this.DataSet.get(0).getData().size();
        row_list = size / this.TestRatio;
        row_list = size - row_list;


        //must convert from column major to row major
        for (int i = 0; i < row_list; i++){

            ArrayList<String> temp = new ArrayList<String>();
            for(int j = 0; j < this.DataSet.size(); j++){
                if (j == getTargetCol()) continue;

                DataFormat df = this.DataSet.get(j);
                temp_data = df.getData().get(i);
                temp.add(j, temp_data);
            }

            DataReader dr = new DataFormat(temp, DataType.STRING);
            verification_data.add(i, dr);

        }
        return verification_data;
    }

    public ArrayList<DataFormat> AllData(){
        return this.DataSet;
    }

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException | NullPointerException exp) {
            return false;
        }
        return true;
    }

    public static boolean isInteger(String s, int radix) {
        Scanner sc = new Scanner(s.trim());

        if(!sc.hasNextInt(radix)) return false;
        sc.nextInt(radix);
        return !sc.hasNext();
    }

    public static boolean isDouble(String s) {
        Scanner sc = new Scanner(s.trim());
        if(!sc.hasNextDouble()) return false;
        sc.nextDouble();
        return !sc.hasNext();
    }


    public DataFormat setDataType(DataFormat col){
        boolean isInt = false;
        boolean isDouble = false;
        String sample;

        for(int i = 0; i < col.getData().size(); i++) {
            sample = col.getData().get(i);
            if (!isNumeric(sample)){
                col.setDataType(DataType.STRING);
                break;
            }
            if (isInteger(sample, 10)) isInt = true;

            if (isDouble(sample)) isDouble = true;
        }

        if( isInt) col.setDataType(DataType.INTEGER);
        if( isDouble) col.setDataType(DataType.DOUBLE);

        return col;

    }

    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.trim().isEmpty())
            return false;
        return true;
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






    public DataFormat FillWithMode(DataFormat col){
        ArrayList<String> checkArr = new ArrayList<String>();
        String mode, sample;

        for(int i = 0; i < col.getData().size(); i++) {
            sample = col.getData().get(i);
            if (isNullOrEmpty(sample)) continue;
            checkArr.add(sample);
        }

        mode = findMode(checkArr);

        for(int i = 0; i < col.getData().size(); i ++){
            sample = col.getData().get(i);
            if (isNullOrEmpty(sample)) col.getData().set(i, mode);
        }

        return col;

    }



    public void scanData(String fileName, String fileDelimiter, int targetCol) {
        String[] rows;
        int row_length;
        int col_length;
        DataFormat newCol;
        String val;
        String[][] file_array = new String[10][10];

        try {
            File file = new File(fileName);
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            row_length = lines.size();
            for (int i = 0; i < lines.size(); i++) {

                String line = lines.get(i);
                rows = line.split(fileDelimiter,-1);
                col_length = rows.length;
                file_array = new String[row_length][col_length];

                for (int j = 0; j < col_length; j++) {
                    file_array[i][j] = rows[j];
                }
            }

            //Process the data in column major format.
            for (int i = 0; i < file_array[0].length; i++){

                if (i == targetCol){
                    setTargetCol(i);
                }
                newCol = new DataFormat();


                for(int j = 0; j < file_array.length; j ++){

                    val =  file_array[j][i];

                    newCol.addDataValue( file_array[j][i]);


                }
                newCol = preProcess( newCol, i);

                this.DataSet.add(newCol);
            }


        } catch (Exception e) {
            System.out.println("Unable to read file " + e.getMessage());
        }

    }

    public DataFormat preProcess(DataFormat newCol, int colNum) {
        int discreteClasses = 10;
        int total_unique;


        total_unique = newCol.getUniqueCounts().size();
        if (total_unique == 0) return newCol;
        if( total_unique >= 100 && total_unique <= 500 )  discreteClasses = 25;
        if( total_unique > 500 && total_unique <= 1000 )  discreteClasses = 50;
        if( total_unique > 1000 )  discreteClasses = total_unique / 10;


        newCol = setDataType(newCol);

        newCol = FillWithMode(newCol);

        if(colNum != getTargetCol()) {
            newCol = descretization(discreteClasses, newCol);
        }

        return newCol;
    }





    public double[] getK_Bins(int discreteClasses, DataFormat col){
        int dataSize = col.getData().size();
        double[] k_bins = new double[discreteClasses];
        double max, min, range, incrementStep, incrValue;




        if(dataSize < 1)  return k_bins;

        if(col.getType() == DataType.DOUBLE){

            ArrayList<Double> columns = col.getASDouble();
            max = Collections.max(columns);
            min = Collections.max(columns);
            range = max - min;
            incrementStep = range / (double) discreteClasses;
            incrValue = min;

            for (int i = 0 ; i < discreteClasses; i ++){

                k_bins[i] = incrValue;
                incrValue = incrValue + incrementStep;

            }

        }

        return k_bins;

    }



    public DataFormat descretization(int discreteClasses, DataFormat col){
        int dataSize;
        ArrayList<Double> columns = col.getASDouble();


        if(col.getType() == DataType.STRING) return col;
        dataSize = col.getData().size();

        if( dataSize < 1 ) return col;

        double[] k_bins = getK_Bins(discreteClasses, col);

        for(int i = 0; i < columns.size(); i++) {

            for (int j = 0; j < k_bins.length; j ++){

                if( columns.get(i) >= k_bins[j]){
                    col.getData().set(i, String.valueOf(j));

                }else{
                    continue;
                }
            }

        }
        return col;

    }

}
