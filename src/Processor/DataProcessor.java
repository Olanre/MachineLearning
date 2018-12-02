package Processor; /**
 * Created by olanre on 2018-10-15.
 */


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.nio.charset.StandardCharsets;

import Log.Logger;
import MachineLearner.MLUtils;
import Processor.DataReader;
import Util.Util;

import javax.xml.crypto.Data;

public class DataProcessor {

    private String fileName;
    private Logger log4US;
    private String fileDelimiter;
    private int TrainingRatio = 8;
    private int TestRatio = 2;
    private int CrossVerificationIndex = 0;
    Logger log;
    private boolean missingColumns;
    private ArrayList<DataFormat> DataSet;
    private ArrayList<DataReader> testData, trainingData, verificationDate;
    private int TargetCol;



    public enum ScanOrientation {
        Vertical, Horizontal
    }

    public enum DataType{
        STRING, DOUBLE
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public DataProcessor(String filename, String fileDelimiter, boolean missingColumns, int TargetCol){
        this.fileName = filename;
        this.missingColumns = missingColumns;
        this.TargetCol = TargetCol;
        this.fileDelimiter = fileDelimiter;
        this.log = new Logger("DataProcessor", Logger.LogLevel.INFO,"DataProcessor");
        this.DataSet = new ArrayList<>();


    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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



    public void setTrainingAndTest(){
        Double limit;
        int size;
        String temp_data;
        DataType temp_type;
        ArrayList<DataReader> training_data = new ArrayList<>();
        String msg = "About to set training and Test Data for DataProcessor";
        log.Log("setTrainingAndTest", msg);
        DataFormat df = this.DataSet.get(0);
        size = df.getData().size();
        msg = " total size of the rows is " + size;
        log.Log("getTrainingData", msg);
        limit = size * (this.TrainingRatio/10.0);
        msg = "size of the limited number of rows to be retrieved is:  " + limit;
        log.Log("getTrainingData", msg);
        ArrayList<Integer> indexes = Util. getRandomInts(0, size, limit.intValue());
        for (int i = 0; i < this.DataSet.size(); i++){

            DataReader dr = new DataFormat(this.DataSet.get(i));
            training_data.add(dr);
        }

        trainingData = MLUtils.getReadersFromIndexes(indexes, training_data );


    }
    public ArrayList<DataReader> getTrainingData(){
        double limit, size;
        String temp_data;
        DataType temp_type;
        ArrayList<DataReader> training_data = new ArrayList<>();
        String msg = "About to get training Data for DataProcessor";
        log.Log("getTrainingData", msg);

        //int k = 0;

        for (int i = 0; i < this.DataSet.size(); i++){
            //if (i == getTargetCol()) continue;
            //k++;

            DataFormat df = this.DataSet.get(i);
            size = df.getData().size();
            msg = " total size of the rows is " + size;
            log.Log("getTrainingData", msg);
            limit = size * (this.TrainingRatio/10.0);
            msg = "size of the limited number of rows to be retrieved is:  " + limit;
            log.Log("getTrainingData", msg);
            ArrayList<String> temp = new ArrayList<String>();
            temp_type = df.getType();

            for(int j = 0; j < limit; j++){

                temp_data = df.getData().get(j);
                temp.add(j, temp_data);
                msg = String.format("Adding new row: %s at index: %s in column: %s: " , temp_data, String.valueOf(j), String.valueOf(i) );
                log.Log("getTrainingData", msg);

            }

            DataReader dr = new DataFormat(temp, temp_type);
            dr.setLog(this.log);
            log.Log("getTrainingData", dr.printData());

            training_data.add(  dr);
            msg = "Adding new column in data";
            log.Log("getTrainingData", msg);
        }
        return training_data;
    }

    public DataReader getTargetColumn(){

        DataReader target_col = new DataFormat();
        String msg = "<< Entering getTargetColumn for DataProcessor";
        log.Log("getTargetColumn", msg);

        for (int i = 0; i < this.DataSet.size(); i++){
            if ( getTargetCol() == i) {
                DataFormat df = this.DataSet.get(i);
                target_col = new DataFormat(df.getData(), df.getType());
                target_col.setLog(this.log);
                msg = "Retireved the targetColumn from the DataSet, printing out";
                log.Log("getTargetColumn", msg);
                log.Log("getTargetColumn", target_col.printData());
            }
        }

        msg = ">> Entering getTargetColumn for DataProcessor";
        log.Log("getTargetColumn", msg);
        return target_col;
    }


    public ArrayList<DataReader> getVerificationData(){

        double limit, size, row_list;
        int i;
        String temp_data;
        ArrayList<DataReader> verification_data = new ArrayList<>();
        String msg = "<< Entering getVerificationData for DataProcessor";
        log.Log("getVerificationData", msg);

        size = this.DataSet.get(0).getData().size();
        row_list = size * (this.TrainingRatio/10.0);
        limit = row_list / 5;
        msg = String.format("Noted that the size of the total training data set is :%s out of a possible: %s values. Amount used for this verification Run is %s ", String.valueOf(row_list), size, String.valueOf(limit));
        log.Log("getVerificationData", msg);

        if(getCrossVerificationIndex() + 1 >= row_list)  setCrossVerificationIndex(0);
        i = getCrossVerificationIndex() + 1;

        limit = limit + i;
        msg = String.format("Current starting position for loop is: %s, limit is: %s ", String.valueOf(i), String.valueOf(limit));
        log.Log("getVerificationData", msg);
        //must convert from column major to row major
        for (; i < limit; i++){

            ArrayList<String> temp = new ArrayList<String>();
            for(int j = 0; j < this.DataSet.size(); j++){
                DataFormat df = this.DataSet.get(j);
                temp_data = df.getData().get(i);
                temp.add(j, temp_data);
                msg = String.format("Adding new column: %s at index: %s in row: %s: " , temp_data, String.valueOf(j), String.valueOf(i) );
                log.Log("getVerificationData", msg);

            }
            if(getCrossVerificationIndex() >= row_list) {
                setCrossVerificationIndex(0);
                msg = "At end of limit, resetting verification index to 0";
                log.Log("getVerificationData", msg);
                break;
            }
            DataReader dr = new DataFormat(temp, DataType.STRING);
            dr.setLog(this.log);
            log.Log("getVerificationData", dr.printData());
            verification_data.add( dr);
            setCrossVerificationIndex(i);
            msg = String.format("Adding new row in data at index %s", String.valueOf(i));
            log.Log("getTrainingData", msg);

        }
        msg = ">> Leaving getVerificationData for DataProcessor";
        log.Log("getVerificationData", msg);
        return verification_data;

    }

    public ArrayList<DataReader> getTestData(){
        Double  row_list;
        int size;
        String temp_data;
        ArrayList<DataReader> test_data = new ArrayList<>();
        size = this.DataSet.get(0).getData().size();
        row_list = size * (this.TestRatio/10.0);
        int i = size - row_list.intValue();
        String msg = "<< Entering getTestData for DataProcessor";
        log.Log("getVerificationData", msg);

        msg = String.format("Noted that the size of the total test data set is : %s. With starting position of %s ", String.valueOf(row_list), String.valueOf(i));
        log.Log("getTestData", msg);

        //must convert from column major to row major
        for (; i < size; i++){

            ArrayList<String> temp = new ArrayList<String>();
            for(int j = 0; j < this.DataSet.size(); j++){
                //if (j == getTargetCol()) continue;

                DataFormat df = this.DataSet.get(j);
                temp_data = df.getData().get(i);
                temp.add(j, temp_data);
                msg = String.format("Adding new column: %s at index: %s in row: %s: " , temp_data, String.valueOf(j), String.valueOf(i) );
                log.Log("getTestData", msg);
            }

            DataReader dr = new DataFormat(temp, DataType.STRING);
            dr.setLog(this.log);
            log.Log("getTestData", dr.printData());
            test_data.add( dr);
            msg = String.format("Adding new row in data at index %s", String.valueOf(i));
            log.Log("getTestData", msg);

        }
        msg = ">> Leaving getTestData for DataProcessor";
        log.Log("getTestData", msg);
        return test_data;
    }

    public ArrayList<DataFormat> AllData(){
        return this.DataSet;
    }


    public DataFormat setDataType(DataFormat col){
        boolean isDouble = false;
        String sample;
        col.setDataType(DataType.STRING);

        for(int i = 0; i < col.getData().size(); i++) {
            sample = col.getData().get(i);
            if (!Util.isNumeric(sample)){
                col.setDataType(DataType.STRING);
                isDouble = false;
                break;
            }

            if (Util.isDouble(sample)){
                isDouble = true;
            }
        }

        if( isDouble) col.setDataType(DataType.DOUBLE);

        return col;

    }


    public void processData() {
        String[] rows;
        int row_length;
        int col_length;
        DataFormat newCol;
        String val, msg;
        ArrayList<String> tmp_col = new ArrayList<>();
        File file;
        List<String> lines = new ArrayList<>();
        //String[][] file_array = new String[10][10];

        msg = "<< Entering processData for DataProcessor";
        log.Log("processData", msg);
        try {
            try{
                file = new File(this.fileName);
                lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

                System.out.println( this.fileName + " File Created in Project root directory");

            } catch (IOException e) {
                System.out.println("Unable to create to file " + fileName + " Due to exception: " + e.getMessage());
            }

            rows = lines.get(0).split(this.fileDelimiter,-1);

            String[][] file_array = new String[lines.size()][rows.length];

            for (int i = 0; i < lines.size(); i++) {

                String line = lines.get(i);
                rows = line.split(this.fileDelimiter,-1);

                col_length = rows.length;

                for (int j = 0; j < rows.length; j++) {
                    msg = String.format("We're currently looking at row: %s with values: %s", String.valueOf(i), rows[j]);
                    log.Log("processData", msg);

                    file_array[i][j] = rows[j];
                }
            }

            //Process the data in column major format.
            for (int i = 0; i < file_array[0].length; i++){

                tmp_col = new ArrayList<>();

                for(int j = 0; j < file_array.length; j ++){

                    val =  file_array[j][i];
                    tmp_col.add(j, val);

                }
                newCol = new DataFormat(tmp_col, DataType.STRING);
                newCol.setLog(this.log);
                newCol = preProcess( newCol, i);
                msg = String.format("Final Data Column after preprocessing is: %s", newCol.printData());
                log.Log("processData", msg);

                this.DataSet.add(newCol);
            }


        } catch (Exception e) {
            //System.out.println("Unable to read file " + e.getMessage());
            throw e;
        }

        msg = ">> Leaving processData for DataProcessor";
        log.Log("processData", msg);


    }

    public DataFormat preProcess(DataFormat newCol, int colNum) {
        int discreteClasses ;
        int total_unique;
        String msg = "<< Entering preProcess for DataProcessor";
        log.Log("preProcess", msg);

        //if(colNum == getTargetCol()) return newCol;
        total_unique = newCol.getUniqueCounts().size();
        discreteClasses = newCol.getData().size();
        if (total_unique == 0) return newCol;
        if( total_unique >= 200 && total_unique <= 500 )  discreteClasses = 50;
        if( total_unique > 500 && total_unique <= 1000 )  discreteClasses = 100;
        if( total_unique > 1000 && total_unique <= 5000 )  discreteClasses = 250;
        if( total_unique > 5000 )  discreteClasses = total_unique / 10;

        msg = String.format("We will be setting the number of discreteClasses to: %s, based on the number of unique values which is: %s ", String.valueOf(discreteClasses), String.valueOf(total_unique)) ;
        log.Log("preProcess", msg);
        newCol = setDataType(newCol);

        newCol = FillWithMode(newCol);

        if(colNum != getTargetCol() && (total_unique >= 200 )) {
            msg = "The column being evaluated is not the target column, so discretization will occur ";
            log.Log("preProcess", msg);
            newCol = descretization(discreteClasses, newCol);
        }
        msg = ">> Leaving preProcess for DataProcessor";
        log.Log("preProcess", msg);
        return newCol;
    }

    public DataFormat FillWithMode(DataFormat col){
        ArrayList<String> checkArr = new ArrayList<String>();
        String mode, sample;

        String msg = "<< Entering FillWithMode for DataProcessor";
        log.Log("FillWithMode", msg);

        for(int i = 0; i < col.getData().size(); i++) {
            sample = col.getData().get(i);
            if (Util.isNullOrEmpty(sample)) continue;
            if( sample.equals("?")){
                msg = "continue as the value is hidden";
                log.Log("FillWithMode", msg);
                continue;
            }
            checkArr.add(sample);
        }

        mode = Util.findMode(checkArr);
        msg = String.format("The mode returned for the Array is: %s out of: %s elements in the Array ", mode, String.valueOf(checkArr.size()));
        log.Log("FillWithMode", msg);

        for(int i = 0; i < col.getData().size(); i ++){
            sample = col.getData().get(i);
            if (Util.isNullOrEmpty(sample)){
                col.getData().set(i, mode);
                msg = String.format("Set to the mode, the value in index: %s as the value is null or blank", String.valueOf(i));
                log.Log("FillWithMode", msg);
            }
            if( sample.equals("?")){
                col.getData().set(i, mode);
                msg = String.format("Set to the mode, the value in index: %s as the value is hidden", String.valueOf(i));
                log.Log("FillWithMode", msg);
            }
        }

        msg = ">> Exiting FillWithMode for DataProcessor";
        log.Log("FillWithMode", msg);

        return col;

    }


    public double[] getK_Bins(int discreteClasses, DataFormat col){
        int dataSize = col.getData().size();
        double[] k_bins = new double[discreteClasses];
        double max, min, range, incrementStep, incrValue = 0;
        String msg = "<< Entering getK_Bins for DataProcessor";
        log.Log("getK_Bins", msg);

        if(dataSize < 1)  return k_bins;

        if(col.getType() == DataType.DOUBLE){
            msg = "Confirmed that the column type is a double, we will attempt to get Data Bins for discretization";
            log.Log("getK_Bins", msg);

            ArrayList<Double> columns = col.getASDouble();

            max =Collections.max(columns);
            min = Collections.min(columns);
            range = max - min;
            incrementStep = range /  discreteClasses;
            incrValue = min;

            msg = String.format("The max value in the Column is: %s. The minimum value is: %s. We will be using a range of: %s with  incremental steps of size: %s",
                    String.valueOf(max), String.valueOf(min), String.valueOf(range), String.valueOf(incrementStep));
            log.Log("getK_Bins", msg);

            for (int i = 0 ; i < discreteClasses; i ++){
                k_bins[i] = incrValue;
                incrValue = incrValue + incrementStep;
            }
        }
        msg = ">> Exiting getK_Bins for DataProcessor";
        log.Log("getK_Bins", msg);
        return k_bins;

    }

    public DataFormat descretization(int discreteClasses, DataFormat col){
        int dataSize;
        ArrayList<Double> columns = col.getASDouble();

        String msg = "<< Entering descretization for DataProcessor";
        log.Log("descretization", msg);
        if(col.getType() == DataType.STRING){
            msg = "Column is not applicable for discretization";
            log.Log("descretization", msg);
            return col;
        }
        dataSize = col.getData().size();

        if( dataSize < 1 ){
            msg = "Column  size is 0, notapplicable for discretization";
            log.Log("descretization", msg);
            return col;
        }

        double[] k_bins = getK_Bins(discreteClasses, col);
        msg = "K-bins array is:\n\n " + Arrays.toString(k_bins);
        log.Log("descretization", msg);


        for(int i = 0; i < columns.size(); i++) {
            double old_val = columns.get(i);

            for (int j = 0; j < k_bins.length; j ++){
                if( columns.get(i) >= k_bins[j]){
                    col.getData().set(i, String.valueOf(j));

                }else{
                    continue;
                }
            }
            msg = String.format("Data at column index: %s . Old value was: %s. New value is: %s after discretization ", String.valueOf(i), old_val, String.valueOf(col.getData().get(i)));
            log.Log("descretization", msg);

        }


        msg = ">> Exiting descretization for DataProcessor";
        log.Log("descretization", msg);
        return col;
    }

}
