package com.example.cdap_nutritiontracking;


import android.icu.text.SimpleDateFormat;

import java.util.Date;

public class FilterModel{
   public String sFromDate;
   public String sToDate;
   public Date startDate;
   public Date endDate;
   public FilterModel(String sFromDate,String sToDate){
     this.sFromDate=sFromDate;
     this.sToDate=sToDate;
     try{
       this.startDate=new SimpleDateFormat("dd/MM/yyyy").parse(sFromDate);
       this.endDate=new SimpleDateFormat("dd/MM/yyyy").parse(sToDate);
     }catch(Exception e){
       e.printStackTrace();
     }
   }
}