package com.example.cdap_nutritiontracking;

import java.io.Serializable;

public class Meal implements Serializable
{

    public String foodId;
    public String date;
    public String mealType;
    public String foodName;
    public String calories;
    //These are BitmapValues
    public byte[] mainImage;
    public byte[] vitamins;
    public byte[] minerals;

        
    public Meal(String date,String mealType){
        this.date=date;
        this.mealType=mealType;
    }
    public void setFoodId(String foodId){
        this.foodId=foodId;
    }
    public void setFoodName(String foodName){
        this.foodName=foodName;
    }
    public void setCalories(String calories){
        this.calories=calories;
    }
    public void setmainImage(byte[] mainImage){
        this.mainImage=mainImage;
    }
    public void setVitamins(byte[] vitamins){
        this.vitamins=vitamins;
    }
    public void setMinerals(byte[] minerals){
        this.minerals=minerals;
    }

    public void setFood(String foodName,String calories,byte[] mainImage,
                        byte[] vitamins,byte[] minerals){                  
        setFoodName(foodName);
        setCalories(calories);
        setmainImage(mainImage);
        setVitamins(vitamins);
        setMinerals(minerals);
    }

}