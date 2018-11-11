package Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by olanre on 2018-11-02.
 */
public class Util {

    public static ArrayList<Integer> getRandomInts(int min, int max, int count) {
         ArrayList<Integer> ranInts = new ArrayList<Integer>(count);


        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();

        for (int i = 0; i < count; i++) {
            ranInts.add(i, r.nextInt((max - min) + 1) + min);
        }
        return ranInts;
    }

    public static String findMode(ArrayList<String> Arr){
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

    public static <T> ArrayList<T> intersection(ArrayList<T> list1, ArrayList<T> list2) {
        ArrayList<T> list = new ArrayList<T>();

        for (T t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }


    public static String findMax(HashMap<String, Double> myMap){
        HashMap.Entry<String, Double> max = null;

        for(HashMap.Entry<String, Double> entry : myMap.entrySet()){
            if(max == null || entry.getValue() > max.getValue()){
                max = entry;
            }
        }
        return max.getKey();
    }


}
