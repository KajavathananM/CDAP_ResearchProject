package com.example.cdap_nutritiontracking;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//Chaquopy Libraries
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
public class FoodDiaryViewActivity extends AppCompatActivity {
    Button sf;
   
    Button b1;
    Button b2;
    Button b3;

    Button ov1;
    Button ov2;
    Button ov3;

    String selectedDate=" ";
    String currentDate=" ";
    TableLayout BItemsDetail;
    TableLayout LItemsDetail;
    TableLayout DItemsDetail;

    final Context context = this;
    
    DatePicker  mealCalendar;


    FoodDiaryService   fdService;
    public ArrayList<Meal> breakfastList=new ArrayList<Meal>();
    public ArrayList<Meal> lunchList=new ArrayList<Meal>();
    public ArrayList<Meal> dinnerList=new ArrayList<Meal>();

    Meal meal;
    TableRow row;

    Python python;
    PyObject pythonFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meals_dateview);

        sf=(Button)findViewById(R.id.SF);

        b1=(Button)findViewById(R.id.b1);
        b2=(Button)findViewById(R.id.b2);
        b3=(Button)findViewById(R.id.b3);

        ov1=(Button)findViewById(R.id.ov1);
        ov2=(Button)findViewById(R.id.ov2);
        ov3=(Button)findViewById(R.id.ov3);


        BItemsDetail=(TableLayout)findViewById(R.id.BItemsDetail);
        LItemsDetail=(TableLayout)findViewById(R.id.LItemsDetail);
        DItemsDetail=(TableLayout)findViewById(R.id.DItemsDetail);

        //bFoodName=(TextView)findViewById(R.id.bFoodName);
        
        mealCalendar = (DatePicker)findViewById(R.id.mealCalendar);
        
        fdService=new FoodDiaryService(FoodDiaryViewActivity.this);
            
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        currentDate = sdf.format(c.getTime());
        fdService.getMealList(breakfastList,"Breakfast",currentDate);
        fdService.getMealList(lunchList,"Lunch",currentDate);
        fdService.getMealList(dinnerList,"Dinner",currentDate);
        displayMealItems();
        

        sf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (! Python.isStarted()) {
                   Python.start(new AndroidPlatform(context));
               }
               python = Python.getInstance();
               pythonFile = python.getModule("Health_FoodAdvisor");
               List<PyObject> foodList=(pythonFile.callAttr("returnList")).asList();
               String list1=String.valueOf(foodList.get(0));
               String list2=String.valueOf(foodList.get(1));  
			   final Dialog dialog = new Dialog(context);
			   dialog.setContentView(R.layout.activity_healthfoodadvisor);
			   dialog.setTitle("Food Suggestor Dialog");   
			   final TextView title = (TextView) dialog.findViewById(R.id.title);
               final TextView recommendFoods = (TextView) dialog.findViewById(R.id.rFoods);
               final TextView avoidFoods = (TextView) dialog.findViewById(R.id.avoidFoods);

                title.setText("Health Suggestion of Food Items to take for your meals");
                title.setTextSize(16);
                title.setTypeface(null, Typeface.BOLD);

                recommendFoods.setText(list1);
                avoidFoods.setText(list2);
                Button close = (Button) dialog.findViewById(R.id.close);
                close.setOnClickListener(new View.OnClickListener() {
				            @Override
				            public void onClick(View v) {
					            dialog.dismiss();
				            }
			          });
                dialog.show();
            }
          
        });
        mealCalendar.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
         @Override
         public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
              String yyyy=String.valueOf(year);
              String mm=String.valueOf(monthOfYear+1);
              if(Integer.parseInt(mm)>=1 && Integer.parseInt(mm)<=9)
              {
                  mm="0"+mm;
              }
              String dd=String.valueOf(dayOfMonth);
              if(Integer.parseInt(dd)>=1 && Integer.parseInt(dd)<=9)
              {
                  dd="0"+dd;
              }
              selectedDate = dd+"/"+mm+"/"+yyyy;

              clearMealItems();
              fdService.getMealList(breakfastList,"Breakfast",selectedDate);
              fdService.getMealList(lunchList,"Lunch",selectedDate);
              fdService.getMealList(dinnerList,"Dinner",selectedDate);
             
              displayMealItems();
              
         }
      });
       
       


        //Breakfast
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Meal meal;
                if(!selectedDate.equals(" ")){
                    meal=new Meal(selectedDate,"Breakfast");
                }else{
                    meal=new Meal(currentDate,"Breakfast");
                }
                Intent i=new Intent(FoodDiaryViewActivity.this,CameraViewActivity.class);
                i.putExtra("meal", (Meal) meal);
                startActivity(i);
                FoodDiaryViewActivity.this.finish();
            }
        });
        ov1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(FoodDiaryViewActivity.this,OverallNutritionActivity.class);
                i.putExtra("breakfast","Breakfast");
                if(!selectedDate.equals(" ")){
                     i.putExtra("selectedDate",selectedDate);    
                }else{
                     i.putExtra("currentDate",currentDate);
                }
                i.putExtra("breakfastList",breakfastList);
                startActivity(i);
            }
        });
        //Lunch
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Meal meal;
                if(!selectedDate.equals(" ")){
                    meal=new Meal(selectedDate,"Lunch");
                }else{
                    meal=new Meal(currentDate,"Lunch");
                }
                
                Intent i=new Intent(FoodDiaryViewActivity.this,CameraViewActivity.class);
                i.putExtra("meal", (Meal) meal);
                startActivity(i);
                FoodDiaryViewActivity.this.finish();
            }
        });
        ov2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(FoodDiaryViewActivity.this,OverallNutritionActivity.class);
                i.putExtra("lunch","Lunch");
                if(!selectedDate.equals(" ")){
                     i.putExtra("selectedDate",selectedDate);    
                }else{
                     i.putExtra("currentDate",currentDate);
                }
                i.putExtra("lunchList",lunchList);
                startActivity(i);

            }
        });
        //Dinner
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Meal meal;
                if(!selectedDate.equals(" ")){
                   meal=new Meal(selectedDate,"Dinner");     
                }else{
                     meal=new Meal(currentDate,"Dinner");
                }
                Intent i=new Intent(FoodDiaryViewActivity.this,CameraViewActivity.class);
                i.putExtra("meal", (Meal) meal);
                startActivity(i);
                FoodDiaryViewActivity.this.finish();
            }
        });
        ov3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(FoodDiaryViewActivity.this,OverallNutritionActivity.class);
                i.putExtra("dinner","Dinner");
                if(!selectedDate.equals(" ")){
                     i.putExtra("selectedDate",selectedDate);    
                }else{
                     i.putExtra("currentDate",currentDate);
                }
                i.putExtra("dinnerList",dinnerList);
                startActivity(i);

            }
        });
    }
    public void clearMealItems(){

        clearTable(breakfastList,BItemsDetail);
        clearTable(lunchList,LItemsDetail);
        clearTable(dinnerList,DItemsDetail);

        breakfastList.clear();
        lunchList.clear();
        dinnerList.clear();
    }
    public void displayMealItems(){
               if(breakfastList.isEmpty()){ 
                  ov1.setVisibility(View.INVISIBLE);
                  BItemsDetail.setVisibility(View.INVISIBLE);
               }else{
                  ov1.setVisibility(View.VISIBLE);
                  BItemsDetail.setVisibility(View.VISIBLE);
                  populateTable(breakfastList,BItemsDetail);
               }
               if(lunchList.isEmpty()){
                  ov2.setVisibility(View.INVISIBLE);
                  LItemsDetail.setVisibility(View.INVISIBLE);
               }else{
                  ov2.setVisibility(View.VISIBLE);
                  LItemsDetail.setVisibility(View.VISIBLE);
                  populateTable(lunchList,LItemsDetail);
               }
               if(dinnerList.isEmpty()){
                 ov3.setVisibility(View.INVISIBLE);
                 DItemsDetail.setVisibility(View.INVISIBLE);
               }else{
                 ov3.setVisibility(View.VISIBLE);
                 DItemsDetail.setVisibility(View.VISIBLE);
                 populateTable(dinnerList,DItemsDetail);
               }
    }
    public void populateTable(final ArrayList<Meal> list, final TableLayout table){
        for(final Meal elem:list){
            row=new TableRow(FoodDiaryViewActivity.this);

            TableLayout.LayoutParams tableRowParams=
                new TableLayout.LayoutParams
                (TableLayout.LayoutParams.FILL_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);

            int leftMargin=10;
            int topMargin=9;
            int rightMargin=9;
            int bottomMargin=9;

            tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
            row.setLayoutParams(tableRowParams);
            table.setStretchAllColumns(true);
            
            TextView foodName = new  TextView(FoodDiaryViewActivity.this);
            foodName.setTextSize(15);
            foodName.setTextColor(Color.parseColor("#FFFFFF"));
            foodName.setText(elem.foodName);
            row.addView(foodName);

            Button NA=new Button(FoodDiaryViewActivity.this);
            NA.setText("Nutrition Analysis");
            NA.setLayoutParams(new TableRow.LayoutParams(90, 100));
            NA.setTextSize(8);
            NA.setBackgroundColor(Color.parseColor("#FFEB3B"));
            NA.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View view) {
                    Intent i=new Intent(FoodDiaryViewActivity.this,NutrientAnalysisActivity.class);
                    i.putExtra("elem",(Meal)elem);
                    startActivity(i);
                }
           });
           row.addView(NA);
          
           Button delete=new Button(FoodDiaryViewActivity.this);
           delete.setBackgroundResource(R.drawable.trash);
           delete.setLayoutParams(new TableRow.LayoutParams(12,52));
           delete.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View view) {
                   fdService.deleteMeal(elem);
                   list.remove(elem);
                   table.removeView(row);
                   showOVN();
                   
                }
           });
           row.addView(delete);

           

           table.addView(row);
        }
    }
    public void showOVN(){
        if(breakfastList.isEmpty()){ 
                  ov1.setVisibility(View.INVISIBLE);   
         }else{
            ov1.setVisibility(View.VISIBLE);
         }
         if(lunchList.isEmpty()){
             ov2.setVisibility(View.INVISIBLE);
         }else{
             ov2.setVisibility(View.VISIBLE);
         }
         if(dinnerList.isEmpty()){
             ov3.setVisibility(View.INVISIBLE);
         }else{
             ov3.setVisibility(View.VISIBLE);
         }
    }
    public void clearTable(ArrayList<Meal> list,TableLayout table){
        for(int i = 0; i < table.getChildCount(); i = 0)
      {
        View child = table.getChildAt(0);
        if (child != null && child instanceof TableRow)
        {
            TableRow row = (TableRow) child;
            row.removeAllViews();
            table.removeViewAt(i);
        }        
      }
    }
       
}

