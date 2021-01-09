package com.example.cdap_nutritiontracking;


import android.util.Log;

import java.util.ArrayList;

//This Service is used to get foodnames and reasons from python side.Then we link them at java side
public class MapService{
        private ArrayList<Food> avoidFoods=new ArrayList<Food>();
        public MapService(){}


        public void MapAvoidedFoods(String foodNames,String avoidReasons){
            String[] foodNamesArr = foodNames.split(",");
            Log.d("Arrlength1",String.valueOf(foodNamesArr.length));
            avoidReasons=avoidReasons.substring(1,avoidReasons.length()-1);
            String[] avoidReasonsArr = avoidReasons.split(",");
            Log.d("Arrlength2",String.valueOf(avoidReasonsArr.length));

            ArrayList<String> arr=new ArrayList<String>();
            for(String label:foodNamesArr){
                for(String reason:avoidReasonsArr){
                      if(reason.contains(label)){
                        reason=reason.substring(1,reason.length()-1);
                        if(reason.contains("'")){
                            reason=reason.substring(1,reason.length()); 
                        }
                        arr.add(reason);
                      }  
                }

                avoidFoods.add(new Food(label,arr.toArray(new String[0])));
                arr.clear();
            }
        }
        public ArrayList<Food> getAvoidedList(){
            return avoidFoods;
        }
}