package com.example.cdap_nutritiontracking;


import java.io.Serializable;
import java.util.ArrayList;

class Dates implements Serializable
{
    private String mfDate;
    private String expDate;

    public Dates(String mfDate,String expDate){
        this.mfDate=mfDate;
        this.expDate=expDate;
    }
    //Daily product Properties
    public String getMfDate() {
        return mfDate;
    }
    public String getExpDate() {
        return expDate;
    }
}
public class Food implements Serializable {
    private String foodName;
    private String foodType;
    private String foodImageUrl;
    private int foodQuantity;
    private ArrayList<Dates> dates;
    private String expDate;

     public Food(String foodName,int foodQuantity){
        this.foodName = foodName;
        this.foodQuantity=foodQuantity;
    }
    public Food(String foodName,int foodQuantity,String expDate){
        this.foodName = foodName;
        this.foodQuantity=foodQuantity;
        this.expDate=expDate;
    }
    public Food(String foodName,String foodImageUrl,String foodType,int foodQuantity) {
        this.foodName = foodName;
        this.foodImageUrl=foodImageUrl;
        this.foodType=foodType;
        this.foodQuantity=foodQuantity;
    }

    public Food(String foodName,String foodImageUrl,String foodType,int foodQuantity,
    ArrayList<Dates> dates) {
        this.foodName = foodName;
        this.foodImageUrl=foodImageUrl;
        this.foodType=foodType;
        this.foodQuantity=foodQuantity;
        this.dates=dates;
    }
    
    //Common Properties
    public String getFoodName() {
        return foodName;
    }
    public String getFoodType() {
        return foodType;
    }
    public String getFoodURL() {
        return foodImageUrl;
    }
    public int getFoodQuantity() {
        return foodQuantity;
    }
    public ArrayList<Dates>  getDates() {
        return dates;
    }
    public String getExpDate(){
        return expDate;
    }
}
