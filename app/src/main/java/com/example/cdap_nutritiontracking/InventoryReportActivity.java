package com.example.cdap_nutritiontracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.lang.Math;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class InventoryReportActivity extends AppCompatActivity {  
    public FilterModel filterSM=null;
    public FilterModel filterBS=null;
    private DatabaseReference stockMovement ;
    private DatabaseReference bowerStock ;
    TableLayout RTable;
    TableLayout BTable;
    TableLayout DTable;
    TableRow row;
    TextView smtitle;
    TextView bstitle; 
    // f1 and f2 opens popup window
    Button f1;
    Button f2;
    Button f3; 
    Button viewEOQGraph; 
     final Context context = this;
     String SfromDate=" ";
     String StoDate=" ";
     SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
     Calendar c = Calendar.getInstance();

    Date fstartDate;
    Date fendDate;  

    Python python;
    PyObject pythonFile;
    byte[] imageData;
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
                   python = Python.getInstance();
                   pythonFile = python.getModule("SketchEOQ");
                   setContentView(R.layout.activity_inventory_report);
         
                String stockMovementRef="Report/StockMovementReport";
                String bowerStockRef="Report/BowerStockReport";
                stockMovement = FirebaseDatabase.getInstance().getReference(stockMovementRef);
                bowerStock= FirebaseDatabase.getInstance().getReference(bowerStockRef);
                RTable=(TableLayout)findViewById(R.id.RTable);
                BTable=(TableLayout)findViewById(R.id.BTable);
                DTable=(TableLayout)findViewById(R.id.DTable);
                f1=(Button)findViewById(R.id.f1);
                f2=(Button)findViewById(R.id.f2);
                f3=(Button)findViewById(R.id.f3);
                viewEOQGraph=(Button)findViewById(R.id.viewEOQ);

                smtitle=(TextView)findViewById(R.id.title);
                bstitle=(TextView)findViewById(R.id.title2);;
                RTable.setVisibility(View.INVISIBLE);
                BTable.setVisibility(View.INVISIBLE);
                DTable.setVisibility(View.INVISIBLE);
                f1.setVisibility(View.INVISIBLE);
                f2.setVisibility(View.INVISIBLE);
                f3.setVisibility(View.INVISIBLE);
                viewEOQGraph.setVisibility(View.INVISIBLE);
                smtitle.setTextSize(14);
                smtitle.setGravity(Gravity.LEFT);
                smtitle.setText("Loading...");
                bstitle.setTextSize(14);
                bstitle.setGravity(Gravity.LEFT);
                bstitle.setText("Loading...");
                makeSmReportRows();
                makeBSReportRows();
          //Filtering records regarding start date and end date for Stock Movement Report
          f1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
			          final Dialog dialog = new Dialog(context);
			          dialog.setContentView(R.layout.activity_inventory__report__dialog);
			          dialog.setTitle("Inventory Report Dialog");
			
			          final TextView fromDate = (TextView) dialog.findViewById(R.id.fromLabel);
                fromDate.setText("Start Date");
                fromDate.setTextSize(16);
                fromDate.setTypeface(null, Typeface.BOLD);
			          final TextView toDate = (TextView) dialog.findViewById(R.id.toLabel);
                toDate.setText("End Date");
                toDate.setTextSize(16);
                toDate.setTypeface(null, Typeface.BOLD);
		      	    Button db1 = (Button) dialog.findViewById(R.id.db1);
                Button db2 = (Button) dialog.findViewById(R.id.db2);
            
           
                final DatePicker fromAns=(DatePicker)dialog.findViewById(R.id.fromAns);
                final DatePicker toAns=(DatePicker)dialog.findViewById(R.id.toAns);
			
              fromAns.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    //Creating dd/mm/yyyy format for datepicker selection
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
                    SfromDate = dd+"/"+mm+"/"+yyyy;              
                }
            });
            toAns.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    //Creating dd/mm/yyyy format for datepicker selection
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
                    StoDate = dd+"/"+mm+"/"+yyyy;              
                }
            });
			db1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
      //Filtering action occurs here for Stock Movement Report
      db2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
                  if (SfromDate == " ")
                 {
                    SfromDate = sdf.format(c.getTime());
                 }
                 if (StoDate == " ")
                 {
                      StoDate = sdf.format(c.getTime());
                 } 
                                
                 
                 filterSM=new FilterModel(SfromDate,StoDate);
                 Log.d("Start Date",filterSM.sFromDate);
                 Log.d("End Date",filterSM.sToDate);
                 makeSmReportRows();
				}
			});
			dialog.show();
            }
     });
     //Filtering records regarding Order date and Purchase date for Bowel Stock Report
      f2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
			          final Dialog dialog = new Dialog(context);
			          dialog.setContentView(R.layout.activity_inventory__report__dialog);
			          dialog.setTitle("Inventory Report Dialog");   
			          final TextView fromDate = (TextView) dialog.findViewById(R.id.fromLabel);
                fromDate.setText("Order Date");
                fromDate.setTextSize(16);
                fromDate.setTypeface(null, Typeface.BOLD);
			          final TextView toDate = (TextView) dialog.findViewById(R.id.toLabel);
                toDate.setText("Delivery Date");
                toDate.setTextSize(15);
                toDate.setTypeface(null, Typeface.BOLD);
		      	    Button db1 = (Button) dialog.findViewById(R.id.db1);
                Button db2 = (Button) dialog.findViewById(R.id.db2);
            
           
                final DatePicker fromAns=(DatePicker)dialog.findViewById(R.id.fromAns);
                final DatePicker toAns=(DatePicker)dialog.findViewById(R.id.toAns);
			
           fromAns.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
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
                    SfromDate = dd+"/"+mm+"/"+yyyy;              
                }
            });
            toAns.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
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
                    StoDate = dd+"/"+mm+"/"+yyyy;              
                }
            });
			db1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
      db2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
                  //If there are no filtration parameters set.Hence set to current date
                  if (SfromDate == " ")
                 {
                    SfromDate = sdf.format(c.getTime());
                 }
                 if (StoDate == " ")
                 {
                      StoDate = sdf.format(c.getTime());
                 }                                
                 filterBS=new FilterModel(SfromDate,StoDate);
                 Log.d("Order Date:",filterBS.sFromDate);
                 Log.d("Delivery Date:",filterBS.sToDate);
                 makeBSReportRows();
				}
			});
			dialog.show();
            }
    });
    //Removing the two filters mentioned above
    f3.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
              filterSM=null;  
              filterBS=null; 
              makeSmReportRows();
              makeBSReportRows();
				}
			});

       viewEOQGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
			          final Dialog dialog = new Dialog(context);
			          dialog.setContentView(R.layout.activity_eoqgraph);
			          dialog.setTitle("View EOQ Grapg Dialog");   
			          final TextView title = (TextView) dialog.findViewById(R.id.title);
                title.setText("EOQ Graph");
                title.setTextSize(16);
                title.setTypeface(null, Typeface.BOLD);
                ImageView eoqGraph = dialog.findViewById(R.id.eoqGraph);
		      	    Button close = (Button) dialog.findViewById(R.id.close);
                eoqGraph.setImageBitmap(makeEOQGraph());
                close.setOnClickListener(new View.OnClickListener() {
				            @Override
				            public void onClick(View v) {
					            dialog.dismiss();
				            }
			          });
                dialog.show();
            }
          
            });
            }
        },250);     
 
  }
  //Generate one table for Stock Movement Report
 public void populateSmData(String itemCode,String itemName,String measurementUnit,String unitCost,String startDate,String openingStock,String purchase,String consumption,String endDate,String closingStock){
           row=new TableRow(InventoryReportActivity.this);                
           row.setBackgroundColor(Color.WHITE);
            
             TextView ItemCode=new  TextView(InventoryReportActivity.this);
             ItemCode.setWidth(5);
             ItemCode.setTextSize(12);
             ItemCode.setText(" "+itemCode);
             TextView ItemName=new  TextView(InventoryReportActivity.this);
             ItemName.setWidth(5);
             ItemName.setTextSize(12);
             ItemName.setText(itemName);
             TextView MeasurementUnit=new  TextView(InventoryReportActivity.this);
             MeasurementUnit.setTextSize(12);
             MeasurementUnit.setText(measurementUnit);
             TextView UnitCost=new  TextView(InventoryReportActivity.this);
             UnitCost.setTextSize(12);
             UnitCost.setText(unitCost);
             TextView StartDate=new  TextView(InventoryReportActivity.this);
             StartDate.setTextSize(12);
             StartDate.setText(startDate);
             TextView OpeningStock=new  TextView(InventoryReportActivity.this);
             OpeningStock.setGravity(Gravity.CENTER);
             OpeningStock.setTextSize(12);
             OpeningStock.setText(openingStock);
             TextView Purchase=new  TextView(InventoryReportActivity.this);
             Purchase.setTextSize(10);
             Purchase.setText(purchase);
             TextView Consumption=new  TextView(InventoryReportActivity.this);
             Consumption.setTextSize(12);
             Consumption.setText(consumption);
             TextView EndDate=new  TextView(InventoryReportActivity.this);
             EndDate.setTextSize(12);
             EndDate.setText(endDate);
             TextView ClosingStock=new  TextView(InventoryReportActivity.this);
             ClosingStock.setGravity(Gravity.CENTER);
             ClosingStock.setTextSize(12);
             ClosingStock.setText(closingStock); 




             row.addView(ItemCode);
             row.addView(ItemName);
             row.addView(MeasurementUnit);
             row.addView(UnitCost);
             row.addView(StartDate);
             row.addView(OpeningStock);
             row.addView(Purchase);
             row.addView(Consumption);
             row.addView(EndDate);
             row.addView(ClosingStock);
             
            //RTable.setStretchAllColumns(true);
            RTable.setShrinkAllColumns(true); 
            RTable.addView(row);        
    }
   public void makeSmReportRows(){
    ValueEventListener ls=new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          for(int i = 1; i < RTable.getChildCount(); i = 1)
         {
            View child = RTable.getChildAt(0);
            if (child != null && child instanceof TableRow)
          {
            TableRow row = (TableRow) child;
            //row.removeAllViews();
            RTable.removeViewAt(i);
           }        
        }
       //Assign values for each table row
       if(dataSnapshot.hasChildren()){
           RTable.setVisibility(View.VISIBLE);
           f1.setVisibility(View.VISIBLE);
           f3.setVisibility(View.VISIBLE);
           smtitle.setTextSize(20);
           smtitle.setGravity(Gravity.CENTER);
           smtitle.setText("Stock Movement Report");
           int i=0;
           //Assign values to the table
           for (DataSnapshot snap : dataSnapshot.getChildren()) {
              String itemCode=snap.child("itemCode").getValue(String.class);
              String itemName=snap.child("itemName").getValue(String.class);
              String measurementUnit=snap.child("measurementUnit").getValue(String.class);
              String unitCost=snap.child("unitCost").getValue(String.class);
              String startDate=snap.child("startDate").getValue(String.class);
              String openingStock=snap.child("openingStock").getValue(String.class);
              String purchase=snap.child("purchase").getValue(String.class);
              String consumption=snap.child("consumption").getValue(String.class);
              String endDate=snap.child("endDate").getValue(String.class);
              
              //Calculation for Closing Stock
              int closeStock=Integer.parseInt(openingStock)+Integer.parseInt(purchase)-Integer.parseInt(consumption);
              snap.child("closingStock").getRef().setValue(String.valueOf(closeStock));
              String closingStock=snap.child("closingStock").getValue(String.class);
              

              if(filterSM==null)
                populateSmData(itemCode,itemName,measurementUnit,unitCost,startDate,openingStock,purchase,consumption,endDate,closingStock);
              else if(filterSM!=null){
                 try{
                  Date startDateD=new SimpleDateFormat("dd/MM/yyyy").parse(startDate);
                  Date endDateD=new SimpleDateFormat("dd/MM/yyyy").parse(endDate);
                  //Querying the table at front end based on filtration parameters
                  if(startDateD.after(filterSM.startDate)|| startDateD.compareTo(filterSM.startDate) == 0)
                  {
                    if(endDateD.before(filterSM.endDate)|| endDateD.compareTo(filterSM.endDate) == 0){
                      populateSmData(itemCode,itemName,measurementUnit,unitCost,startDate,openingStock,purchase,consumption,endDate,closingStock);
                    }
                  }else{
                    RTable.setVisibility(View.INVISIBLE);
                    smtitle.setTextSize(14);
                    smtitle.setGravity(Gravity.LEFT);
                    smtitle.setText("Results Not Found");
                  }

                 }catch(Exception e){
                   e.printStackTrace();
                 }
              
              }
          }
       }
     }
      @Override
    public void onCancelled(DatabaseError databaseError) {
        // Getting Post failed, log a message
        Log.w("Firebase Not working", "loadPost:onCancelled", databaseError.toException());
    }
  };
    //Making the page to be refreshed if filtration applied.
    stockMovement.removeEventListener(ls);
    stockMovement.addValueEventListener(ls);
     
 }
 // Generate table rows of one table in Bower Stock
  public void populateBSData(String itemCode,String itemName,String measurementUnit,String unitCost,String avgSpoilage,String avgConsumption,String bowerStock,String eoq,String rLevel){
           row=new TableRow(InventoryReportActivity.this);   
           TableLayout.LayoutParams tableRowParams=
                new TableLayout.LayoutParams
                (TableLayout.LayoutParams.FILL_PARENT,TableLayout.LayoutParams.WRAP_CONTENT,1.0f);             
           row.setBackgroundColor(Color.WHITE);
           row.setLayoutParams(tableRowParams); 
             TextView ItemCode=new  TextView(InventoryReportActivity.this);
             ItemCode.setWidth(5);
             ItemCode.setTextSize(12);
             ItemCode.setText(" "+itemCode);
             TextView ItemName=new  TextView(InventoryReportActivity.this);
             ItemName.setWidth(5);
             ItemName.setTextSize(12);
             ItemName.setText(itemName);
             TextView MeasurementUnit=new  TextView(InventoryReportActivity.this);
             MeasurementUnit.setGravity(Gravity.CENTER);
             MeasurementUnit.setTextSize(12);
             MeasurementUnit.setText(measurementUnit);
             TextView UnitCost=new  TextView(InventoryReportActivity.this);
             UnitCost.setTextSize(12);
             UnitCost.setText(unitCost);
             TextView AvgSpoilage=new  TextView(InventoryReportActivity.this);
             AvgSpoilage.setTextSize(12);
             AvgSpoilage.setText(avgSpoilage);
             TextView AvgConsumption=new  TextView(InventoryReportActivity.this);
             AvgConsumption.setTextSize(12);
             AvgConsumption.setText(avgConsumption);
             TextView BowerStock=new  TextView(InventoryReportActivity.this);
             BowerStock.setTextSize(12);
             BowerStock.setText(bowerStock);
             TextView Eoq=new TextView(InventoryReportActivity.this);
             Eoq.setTextSize(12);
             Eoq.setText(eoq);
             TextView RLevel=new  TextView(InventoryReportActivity.this);
             RLevel.setGravity(Gravity.CENTER);
             RLevel.setTextSize(12);
             RLevel.setText(rLevel);
             
            


             row.addView(ItemCode);
             row.addView(ItemName);
             row.addView(MeasurementUnit);
             row.addView(UnitCost);
             row.addView(AvgSpoilage);
             row.addView(AvgConsumption);
             row.addView(BowerStock);
             row.addView(Eoq);
             row.addView(RLevel);
                          
            BTable.setShrinkAllColumns(true); 
            BTable.addView(row);        
    }
  // Generate table rows of another table in Bower Stock
  public void populateDateRecords(String itemCode,String itemName,String pdDate,String orderDate,String leadTime){
           row=new TableRow(InventoryReportActivity.this);                
           row.setBackgroundColor(Color.WHITE);
            
             TextView ItemCode=new  TextView(InventoryReportActivity.this);
             ItemCode.setWidth(5);
             ItemCode.setTextSize(12);
             ItemCode.setText(" "+itemCode);
             TextView ItemName=new  TextView(InventoryReportActivity.this);
             ItemName.setWidth(5);
             ItemName.setTextSize(12);
             ItemName.setText(itemName);
             TextView PdDate=new  TextView(InventoryReportActivity.this);
             PdDate.setTextSize(12);
             PdDate.setText(pdDate);
             TextView OrderDate=new  TextView(InventoryReportActivity.this);
             OrderDate.setTextSize(12);
             OrderDate.setText(orderDate);
             TextView LeadTime=new  TextView(InventoryReportActivity.this);
             LeadTime.setTextSize(12);
             LeadTime.setText(leadTime);
          
             
            


             row.addView(ItemCode);
             row.addView(ItemName);
             row.addView(PdDate);
             row.addView(OrderDate);
             row.addView(LeadTime);
                                     
            //DTable.setShrinkAllColumns(true); 
            DTable.setStretchAllColumns(true);
            DTable.addView(row);        
    }
 public void makeBSReportRows(){
    ValueEventListener ls=new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        //Clear values in one table
          for(int i = 1; i < BTable.getChildCount(); i = 1)
         {
            View child = BTable.getChildAt(0);
            if (child != null && child instanceof TableRow)
          {
            TableRow row = (TableRow) child;
            //row.removeAllViews();
            BTable.removeViewAt(i);
           }        
        }

          //Clear values in other table
          for(int i = 1; i < DTable.getChildCount(); i = 1)
         {
            View child = DTable.getChildAt(0);
            if (child != null && child instanceof TableRow)
          {
            TableRow row = (TableRow) child;
            //row.removeAllViews();
            DTable.removeViewAt(i);
           }        
        }
        //Check if snapshot is not empty
       if(dataSnapshot.hasChildren()){
           f2.setVisibility(View.VISIBLE);
           BTable.setVisibility(View.VISIBLE);
           DTable.setVisibility(View.VISIBLE);
           viewEOQGraph.setVisibility(View.VISIBLE);
           bstitle.setTextSize(20);
           bstitle.setGravity(Gravity.CENTER);
           bstitle.setText("BowelStock Report");
           int i=0;
           //Assign values for each table row
           for (DataSnapshot snap : dataSnapshot.getChildren()) {
              String itemCode=snap.child("itemCode").getValue(String.class);
              String itemName=snap.child("itemName").getValue(String.class);
              String measurementUnit=snap.child("measurementUnit").getValue(String.class);
              String unitCost=snap.child("unitCost").getValue(String.class);
              String avgSpoilage=snap.child("avgSpoilage").getValue(String.class);
              String pdDate=snap.child("pdDate").getValue(String.class);
              String orderDate=snap.child("orderDate").getValue(String.class);
              
              String leadTime;
              int lTime;
              try{

                 //calculation for Leadtime
                 Date pdDateD=new SimpleDateFormat("dd/MM/yyyy").parse(pdDate);
                 Date orderDateD=new SimpleDateFormat("dd/MM/yyyy").parse(orderDate);
                 long  diff_time=pdDateD.getTime() - orderDateD.getTime(); 
                 lTime=Math.round(diff_time/ (1000 * 60 * 60 * 24)% 365) +1;
                 snap.child("leadTime").getRef().setValue(String.valueOf(lTime));
                 leadTime=snap.child("leadTime").getValue(String.class);
                 String avgConsumption=snap.child("avgConsumption").getValue(String.class);
                 String avgHold=snap.child("avgHold").getValue(String.class);
                 String avgOrdering=snap.child("avgOrdering").getValue(String.class);
             
                 //calculation for Bower Stock or Safety Stock
                 int bStock=Integer.parseInt(leadTime)*Integer.parseInt(avgConsumption);
                 snap.child("bowerStock").getRef().setValue(String.valueOf(bStock));
                 String bowerStock=snap.child("bowerStock").getValue(String.class);
             
                 //calculation for EOQ
                 int eoqAns= (int) Math.sqrt((2*Integer.parseInt(avgConsumption)*Integer.parseInt(avgOrdering))/Integer.parseInt(avgHold));
                 snap.child("eoq").getRef().setValue(String.valueOf(eoqAns));
                 String eoq=snap.child("eoq").getValue(String.class);

                 //calculation for Reorder Level
                 int rLevelAns=bStock;
                 snap.child("rLevel").getRef().setValue(String.valueOf(rLevelAns));
                 String rLevel=snap.child("rLevel").getValue(String.class);
            
                 //Without filter parameters
                 if(filterBS==null){
                   populateDateRecords(itemCode,itemName,pdDate,orderDate,leadTime);
                   populateBSData(itemCode,itemName,measurementUnit,unitCost,avgSpoilage,avgConsumption,bowerStock,eoq,rLevel);      
                 }
                 //With filter parameters
                  else if(filterBS!=null){
                  //Check if filtered paramters are within the range of the filtered parameters
                  if(orderDateD.after(filterBS.startDate)|| orderDateD.compareTo(filterBS.startDate) == 0)
                  {
                    if(pdDateD.before(filterBS.endDate)|| pdDateD.compareTo(filterBS.endDate) == 0){
                      populateDateRecords(itemCode,itemName,pdDate,orderDate,leadTime);
                      populateBSData(itemCode,itemName,measurementUnit,unitCost,avgSpoilage,avgConsumption,bowerStock,eoq,rLevel);
                    }
                  }else{
                    //If there are no results according to filtered parameters
                    DTable.setVisibility(View.INVISIBLE);
                    BTable.setVisibility(View.INVISIBLE);
                    bstitle.setTextSize(14);
                    bstitle.setGravity(Gravity.LEFT);
                    bstitle.setText("Results Not Found");
                  }             
              }
              }catch(Exception e){
                e.printStackTrace();
              }                          
         }
       }
     }
      @Override
    public void onCancelled(DatabaseError databaseError) {
        // Getting Post failed, log a message
        Log.w("Firebase Not working", "loadPost:onCancelled", databaseError.toException());
    }

  };
  //Making the page to be refreshed if filtration applied.
    bowerStock.removeEventListener(ls);
    bowerStock.addValueEventListener(ls);
     
 }
 //Generate EOQ Graph 
 public Bitmap makeEOQGraph(){
        imageData = pythonFile.callAttr("sketchEOQGraph").toJava(byte[].class);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        return bitmap;
 }
}
