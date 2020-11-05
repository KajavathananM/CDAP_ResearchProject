package com.example.cdap_nutritiontracking;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

//Android MpChart Libraries
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;


//Chaquopy Libraries
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.util.ArrayList;
import java.util.List;

public class OverallNutritionActivity extends AppCompatActivity {
    TextView mealType;
    TextView mealDate;
    TextView ovCalories;

    TableLayout filters;
    TableRow row;

    String selectedDate=" ";
    String currentDate=" ";
    String mType=" ";
    double total=0;
    double tempTotal=0;

    
    public ArrayList<Meal> mealList=new ArrayList<Meal>();
    public ArrayList<Boolean> isFilteredList=new ArrayList<Boolean>();

    public String labels[];

    PieChart pieChart;
    HorizontalBarChart VHChart;
    HorizontalBarChart MHChart;
    int []colors=new int[]{Color.BLUE,Color.RED,Color.MAGENTA};

    /*int []hColors=new int[]{Color.BLUE,Color.RED,Color.MAGENTA,Color.CYAN,Color.GREEN,
    Color.GRAY,Color.BLACK,Color.DKGRAY};*/

    Python python;
    PyObject pythonFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     setContentView(R.layout.activity_loadsplashscreen); 
     if (! Python.isStarted()) {
           Python.start(new AndroidPlatform(this));
      }  
     new Handler().postDelayed(new Runnable(){
      @Override
      public void run() { 
        setContentView(R.layout.activity_overall_nutrition);

        mealType=(TextView)findViewById(R.id.mealType);
        mealDate=(TextView)findViewById(R.id.mealDate);
        ovCalories=(TextView)findViewById(R.id.ovCalories);
        filters=(TableLayout)findViewById(R.id.filters);
        pieChart = findViewById(R.id.piechart);
        VHChart= findViewById(R.id.VHChart);
        MHChart=findViewById(R.id.MHChart);
        

         
         python = Python.getInstance();
         pythonFile = python.getModule("CSVLoader");

         if(getIntent().getExtras() != null) {
             labels= getLabels(); 
             setMealDate();
             setMealType();
             loadFilters();
             populateFilters(mealList,filters);
             populatePieChart();
             populateVHChart();
             populateMHChart();
         }
      }
     },150);   
    }
    public void setMealDate(){
                if (getIntent().hasExtra("selectedDate")) {
                    selectedDate =getIntent().getStringExtra("selectedDate");
                    mealDate.setText(selectedDate);

                }
                else if (getIntent().hasExtra("currentDate")){
                    currentDate =getIntent().getStringExtra("currentDate");
                    mealDate.setText(currentDate);
                } 
    }
    public void setMealType(){
               if (getIntent().hasExtra("breakfast")) {
                        mType =getIntent().getStringExtra("breakfast");
                        mealType.setText(mType);
                }
                else if (getIntent().hasExtra("lunch")) {
                        mType =getIntent().getStringExtra("lunch");
                        mealType.setText(mType);
                }
                else if (getIntent().hasExtra("dinner")) {
                        mType =getIntent().getStringExtra("dinner");
                        mealType.setText(mType);
                }
    }
    public void loadFilters(){
        if (getIntent().hasExtra("breakfastList")) {
                mealList = (ArrayList<Meal>) getIntent().getSerializableExtra("breakfastList");
               
        }
        else if (getIntent().hasExtra("lunchList")) {
                mealList = (ArrayList<Meal>) getIntent().getSerializableExtra("lunchList");
        }
        else if (getIntent().hasExtra("dinnerList")) {
                mealList = (ArrayList<Meal>) getIntent().getSerializableExtra("dinnerList");
        }
    }
    //Create Filters based on Meal list into a table layout
    public void populateFilters(final ArrayList<Meal> list, final TableLayout filters){
        for(final Meal elem:list){
            tempTotal+=Double.parseDouble(elem.calories);
            total+=Double.parseDouble(elem.calories);
            row=new TableRow(OverallNutritionActivity.this);

            TableLayout.LayoutParams tableRowParams=
                new TableLayout.LayoutParams
                (TableLayout.LayoutParams.FILL_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);

            int leftMargin=10;
            int topMargin=9;
            int rightMargin=12;
            int bottomMargin=9;

            tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
            row.setLayoutParams(tableRowParams);
            filters.setStretchAllColumns(true);

             final Button filter=new Button(OverallNutritionActivity.this);
             filter.setId(list.indexOf(elem));
             filter.setText(elem.foodName);
             filter.setTextSize(8);
             filter.setTextColor(Color.parseColor("#FFFFFF"));
             filter.setBackgroundColor(Color.parseColor("#32CD32"));


             isFilteredList.add(false);
             filter.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                   // On and off control of filered buttons
                    if(isFilteredList.get(list.indexOf(elem)) ==true){
                        isFilteredList.set(list.indexOf(elem), false);
                        filter.setTextColor(Color.parseColor("#FFFFFF"));
                        filter.setBackgroundColor(Color.parseColor("#32CD32"));
                        total+=Double.parseDouble(elem.calories);
                        //Setting boundary of total calories between 0 and total
                        if(total>tempTotal){
                            total=tempTotal;
                        }
                        ovCalories.setText(String.valueOf(total));
                    }else if(isFilteredList.get(list.indexOf(elem)) ==false){
                        isFilteredList.set(list.indexOf(elem), true);
                        filter.setTextColor(Color.parseColor("#000000"));
                        filter.setBackgroundColor(Color.parseColor("#DCDCDC"));
                        total-=Double.parseDouble(elem.calories);
                        if(total<0){
                            total=0;
                        }
                        ovCalories.setText(String.valueOf(total));
                    }
              }
            });

                
                row.addView(filter);
            
            
            filters.addView(row);
        }
        ovCalories.setText(String.valueOf(total));
    }
    public String[] getLabels(){
        labels=new String[mealList.size()];
        for(int i=0;i<mealList.size();i++){
            labels[i]=(mealList.get(i).foodName).toString();
        }
        return labels;
    }
    private ArrayList getThreeNutrients(ArrayList<PieEntry> nComposition){
        // nComposition.add(new PieEntry((float) 70.22,"Carboyhydrate"));
        // nComposition.add(new PieEntry((float) 7.63,"Protein"));
        // nComposition.add(new PieEntry((float) 22.16,"Fat"));    
        //float oVcarb=Float.parseFloat(String.valueOf(pythonFile.callAttr("retrieveData",(Object)labels)));
         List<PyObject> nValues=(pythonFile.callAttr("retrieveNutrientsData",(Object)labels)).asList();
         float oVcarb=Float.parseFloat(String.valueOf(nValues.get(0)));
         float oVFat=Float.parseFloat(String.valueOf(nValues.get(1)));
         float oVProtein=Float.parseFloat(String.valueOf(nValues.get(2)));

        nComposition.add(new PieEntry(oVcarb,"Carboyhydrate"));
        nComposition.add(new PieEntry(oVFat,"Protein"));
        nComposition.add(new PieEntry(oVProtein,"Fat"));
        return nComposition;
    }
    //Create Piechart
    public void populatePieChart(){
             
        ArrayList<PieEntry> nComposition = new ArrayList<>();
        
    
        //PieDataSet pDataset=new PieDataSet(nComposition,"");
        PieDataSet pDataset=new PieDataSet( getThreeNutrients(nComposition),"");
        pDataset.setColors(colors);

        PieData pieData=new PieData(pDataset); 
        pieData.setValueTextSize(10f); 
        pieData.setValueTextColor(Color.WHITE);
        pieChart.setData(pieData);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(20f);
        
        pieChart.setUsePercentValues(true);
        //pieChart.setCenterText("% Weight of 3 Nutrient classes");
        //pieChart.setCenterTextSize(10);
        pieChart.setHoleRadius(0);
        pieChart.setTransparentCircleRadius(0);
        //pieChart.setMaxAngle(80);

        //pieChart.setDrawSliceText(false); // To remove slice text
        pieChart.setDrawMarkers(false); // To remove markers when click
        pieChart.setDrawEntryLabels(false); // To remove labels from piece of pie
        pieChart.getDescription().setEnabled(false); //

        Legend l = pieChart.getLegend(); // get legend of pie
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER); // set vertical alignment for legend
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT); // set horizontal alignment for legend
        l.setOrientation(Legend.LegendOrientation.VERTICAL); // set orientation for legend
        l.setDrawInside(false); // set if legend should be drawn inside or not

        pieChart.invalidate();
    }
    //Create Horizontal Barchart
    public void populateVHChart(){
        BarDataSet barDataSet = new BarDataSet(getVitaminsData(), "");
        barDataSet.setBarBorderWidth(0.9f);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData barData = new BarData(barDataSet);
        XAxis xAxis = VHChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        final String[] vitamins = new String[]{"Vitamin A", "Vitamin B6", "Vitamin B12", "Vitamin C",
         "Vitamin D", "Vitamin E", "Vitamin K"};
        IndexAxisValueFormatter formatter = new IndexAxisValueFormatter(vitamins);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);
        VHChart.setData(barData);
        VHChart.setFitBars(true);
        //VHChart.animateXY(5000, 5000);


        
        VHChart.setDrawMarkers(false); // To remove markers when click
        VHChart.getDescription().setEnabled(false); //

        Legend l = VHChart.getLegend(); // get legend of Barchart
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER); // set vertical alignment for legend
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT); // set horizontal alignment for legend
        l.setOrientation(Legend.LegendOrientation.VERTICAL); // set orientation for legend
        l.setDrawInside(false); // set if legend should be drawn inside or not
        VHChart.invalidate();

    }
    private ArrayList getVitaminsData(){
        ArrayList<BarEntry> entries = new ArrayList<>();
        List<PyObject> vitValues=(pythonFile.callAttr("retrieveVitaminsData",(Object)labels)).asList();
        float oVitA=Float.parseFloat(String.valueOf(vitValues.get(0)));
        float oVitB6=Float.parseFloat(String.valueOf(vitValues.get(1)));
        float oVitB12=Float.parseFloat(String.valueOf(vitValues.get(2)));
        float oVitC=Float.parseFloat(String.valueOf(vitValues.get(3)));
        float oVitD=Float.parseFloat(String.valueOf(vitValues.get(4)));
        float oVitE=Float.parseFloat(String.valueOf(vitValues.get(5)));
        float oVitK=Float.parseFloat(String.valueOf(vitValues.get(6)));
      
        entries.add(new BarEntry(0f, oVitA));
        entries.add(new BarEntry(1f, oVitB6));
        entries.add(new BarEntry(2f, oVitB12));
        entries.add(new BarEntry(3f, oVitC));
        entries.add(new BarEntry(4f, oVitD));
        entries.add(new BarEntry(5f, oVitE));
        entries.add(new BarEntry(6f,oVitK));
        return entries;
    }
    //Create Horizontal Barchart
     public void populateMHChart(){
        BarDataSet barDataSet = new BarDataSet(getMineralsData(), "");
        barDataSet.setBarBorderWidth(0.9f);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData barData = new BarData(barDataSet);
        XAxis xAxis = MHChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis yAxis = MHChart.getAxisLeft();
        yAxis.setGranularity(1f);
        yAxis.setLabelCount(8);
        final String[] minerals = new String[]{"Fluoride", "Calcium", "Sodium", "Potassium",
         "Iron", "Phosporus", "Magnesium","Zinc"};
        IndexAxisValueFormatter formatter = new IndexAxisValueFormatter(minerals);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);
        MHChart.setData(barData);
        MHChart.setFitBars(true);
        //MHChart.animateXY(5000, 5000);


        
        MHChart.setDrawMarkers(false); // To remove markers when click
        MHChart.getDescription().setEnabled(false); //

        Legend l = MHChart.getLegend(); // get legend of Barchart
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER); // set vertical alignment for legend
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT); // set horizontal alignment for legend
        l.setOrientation(Legend.LegendOrientation.VERTICAL); // set orientation for legend
        l.setDrawInside(false); // set if legend should be drawn inside or not
        
        MHChart.setDragEnabled(true); // on by default
        //MHChart.setVisibleXRange(8); // sets the viewport to show 3 bars
        MHChart.invalidate();

    }
    private ArrayList getMineralsData(){
        ArrayList<BarEntry> entries = new ArrayList<>();
        List<PyObject> mineralValues=(pythonFile.callAttr("retrieveMineralsData",(Object)labels)).asList();
        float fluoride=Float.parseFloat(String.valueOf(mineralValues.get(0)));
        float calcium=Float.parseFloat(String.valueOf(mineralValues.get(1)));
        float sodium=Float.parseFloat(String.valueOf(mineralValues.get(2)));
        float potassium=Float.parseFloat(String.valueOf(mineralValues.get(3)));
        float phosporus=Float.parseFloat(String.valueOf(mineralValues.get(4)));
        float iron=Float.parseFloat(String.valueOf(mineralValues.get(5)));
        float magnesium=Float.parseFloat(String.valueOf(mineralValues.get(6)));
        float zinc=Float.parseFloat(String.valueOf(mineralValues.get(7)));
        entries.add(new BarEntry(0f, fluoride));
        entries.add(new BarEntry(1f, calcium));
        entries.add(new BarEntry(2f, sodium));
        entries.add(new BarEntry(3f, potassium));
        entries.add(new BarEntry(4f, phosporus));
        entries.add(new BarEntry(5f, iron));
        entries.add(new BarEntry(6f, magnesium));
        entries.add(new BarEntry(7f, zinc));
        return entries;
    }
    
}
