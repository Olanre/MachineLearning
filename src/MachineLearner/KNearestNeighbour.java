package MachineLearner;

import DecisionTree.Node;
import Log.Logger;
import Processor.DataProcessor;
import Processor.DataReader;
import Util.Util;

import java.util.*;

/**
 * Created by olanre on 2018-11-06.
 */
public class KNearestNeighbour  implements MLAlgorithm{

    private double error, wts;
    private int k = 4;
    double[] alpha;
    private ArrayList<DataReader> Attributes;
    private DataReader GoalAttribute;
    private ArrayList<DataReader> TotalAttributes;
    public static final String AlgorithmName = "KNN";
    Logger log;



    public void learnData(ArrayList<DataReader> drs, DataReader goal){
        this.Attributes = drs;
        this.GoalAttribute = goal;
        initLoggers();
        buildModel();


    }
    public void initLoggers(){
        this.log = new Logger(this.AlgorithmName, Logger.LogLevel.INFO, this.AlgorithmName);
        this.GoalAttribute.setLog(this.log);
        for(DataReader Attribute: this.Attributes){
            Attribute.setLog(this.log);
        }
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public String getAlgorithmName() {
        return AlgorithmName;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public double Calculate_Minkowski_Distance(ArrayList<Double> attributesVals, ArrayList<Double> testValues, int p){
        double Distance, Summation = 0.0;
        double powerResult, subtractResult;
        if(attributesVals.size() != testValues.size())return 0;

        for(int i = 0; i < attributesVals.size(); i ++){
            subtractResult = Math.abs(Double.valueOf(attributesVals.get(i)) - Double.valueOf(testValues.get(i)));
            powerResult = Math.pow(subtractResult, p);
            Summation += powerResult;
        }
        Distance = Math.sqrt(Summation);

        return Distance;
    }



    public void buildModel(){
        for(int i = 0 ; i < this.Attributes.size(); i++){
            DataReader tmp = this.Attributes.get(i);
            DataReader newReader = MLUtils.preProcess(tmp);
            this.Attributes.set(i, newReader);
            //this.TotalAttributes.set(i, newReader);

        }
        int discreteClasses = this.GoalAttribute.getUniqueElements().size();
        if( discreteClasses > 10) {
            if ((discreteClasses % 2) == 0) {
                this.k = (discreteClasses / 2) + 1;
            } else {
                this.k = discreteClasses / 2;
            }
        }

        this.TotalAttributes = MLUtils.ColtoRowMajor(this.Attributes);
    }


    public ArrayList<String> ClassifySet(ArrayList<DataReader> example_data){

        ArrayList<String> result = new ArrayList<>();
        DataReader row;

        for(int i = 0; i < example_data.size(); i ++){
            row = example_data.get(i);
            String classification = Classify(row.getData());
            result.add(classification);

        }

        return result;
    }

    public ArrayList<String> buildKClassifiers(ArrayList<Double> parsedCols){
        ArrayList<Double> RowData = new ArrayList<>();
        DataReader row;
        String msg;
        double distance;

        ArrayList<String> classifications = new ArrayList<>();

        HashMap<Double, String> ManhattanDistances = new HashMap<>();
        HashMap<Double, String> EuclideanDistances = new HashMap<>();
        HashMap<Double, String> HammingDistances = new HashMap<>();

        for(int i = 0; i < TotalAttributes.size(); i++){
            row = TotalAttributes.get(i);
            //row has already being preprocessed as double
            RowData = row.getASDouble();

            //this will get us the Hamming distance
            distance = Calculate_Minkowski_Distance(RowData, parsedCols, 0);
            HammingDistances.put( distance, String.valueOf(i));

            //this will get us the Manhattan distance
            distance = Calculate_Minkowski_Distance(RowData, parsedCols, 1);
            ManhattanDistances.put( distance, String.valueOf(i));

            //this will get us the Euclidean distance
            distance = Calculate_Minkowski_Distance(RowData, parsedCols, 2);
            EuclideanDistances.put( distance, String.valueOf(i));

        }

        List<Double> Hammingkeys = Util.getKeysFromMap(HammingDistances);
        msg = String.format("Keys are algorithm: %s \n", Hammingkeys.toString());
        log.Log("buildKClassifiers", msg);
        Collections.sort(Hammingkeys);
        if( Hammingkeys.size() > this.getK()){
            Hammingkeys = Hammingkeys.subList(0, this.getK());
        }
        classifications.addAll(Util.ValsFromHashAsArr( HammingDistances, Hammingkeys ));

        List<Double> Manhattankeys = Util.getKeysFromMap(ManhattanDistances);
        Collections.sort(Manhattankeys);
        if( Manhattankeys.size() > this.getK()){
            Manhattankeys = Manhattankeys.subList(0, this.getK());
        }
        Manhattankeys = Manhattankeys.subList(0, this.getK());
        classifications.addAll(Util.ValsFromHashAsArr( ManhattanDistances, Manhattankeys ));


        List<Double> Euclideankeys = Util.getKeysFromMap(EuclideanDistances);
        Collections.sort(Euclideankeys);
        if( Euclideankeys.size() > this.getK()){
            Euclideankeys = Euclideankeys.subList(0, this.getK());
        }
        Euclideankeys = Euclideankeys.subList(0, this.getK());
        classifications.addAll(Util.ValsFromHashAsArr( EuclideanDistances, Euclideankeys ));

        return classifications;

    }



    public ArrayList<Double> processColumns (ArrayList<String> cols){
        ArrayList<Double> newCol = new ArrayList<>();
        double max, min, range, weightedVal;

        for(int i = 0; i < cols.size(); i++){
            String val = cols.get(i);
            if(Util.isNumeric(val)){
                ArrayList<Double> data;

                double value = Double.valueOf(val);
                DataReader col = this.Attributes.get(i);
                max = MLUtils.getMaxElement(col);
                min = MLUtils.getMinElement(col);
                range = max - min;
                weightedVal = (value - min)/range;
                newCol.add(i, weightedVal);

            }else{
                int  hash = Util.createStringHash(val);
                newCol.add(i, Double.valueOf(hash));

            }
        }
        return newCol;
    }

    public String Classify( ArrayList<String> cols){
        String classification;

        ArrayList<Double> parsedCols = processColumns(cols);
        ArrayList<String> possibleClassifications = buildKClassifiers(parsedCols);
        String mode = Util.findMode(possibleClassifications);
        classification = GoalAttribute.getData().get( Integer.parseInt(mode));

        return classification;
    }




}
