package com.example.cdap_nutritiontracking;

public class BSReport{
    private String foodName;
    private String orderDate;
    private String pdDate;
    private String rLevel;

    public BSReport(String foodName,String orderDate,String pdDate,String rLevel){
          this.foodName=foodName;
          this.orderDate=orderDate;
          this.pdDate=pdDate;
          this.rLevel=rLevel;
    }
    public String getFoodName(){
        return foodName;
    }
    public String getReorderLevel(){
        return rLevel;
    }
    public String getPurchaseDate(){
        return pdDate;
    }
    public String getOrderDate(){
        return orderDate;
    }
}