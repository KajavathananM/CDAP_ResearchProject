package com.example.cdap_nutritiontracking;



import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Function;


public class FoodService{
   //Listeners to freuqently update mobile app's interface
   private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("foodList");
   private DatabaseReference bStock = FirebaseDatabase.getInstance().getReference("Report/BowerStockReport");
   boolean control=false; 
   boolean controlV=false; 
   ArrayList<Food> throwList=new ArrayList<Food>();
   ArrayList<Food> shopList=new ArrayList<Food>();
   SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
   Calendar c = Calendar.getInstance();
   String currentDate = sdf.format(c.getTime());

ArrayList<BSReport>reports=new ArrayList<BSReport>();
public void fetchData(DataSnapshot snap,CardArrayAdapter cardArrayAdapter,Card card,ListView listview,Context context)
{                            
                ArrayList<Dates> dates=new ArrayList<Dates>(); 
                String foodName = snap.child("foodName").getValue(String.class);
                String foodImageUrl =snap.child("foodImageUrl").getValue(String.class);
                String foodType=snap.child("foodType").getValue(String.class);
                int foodQuantity =Integer.parseInt(snap.child("foodQuantity").getValue(String.class)); 
                int tQty=0;
                String mfDate="";
                String expDate="";  

               
                for(int i=0;i<reports.size();i++){
                   if(reports.get(i).getFoodName().equals(foodName)){
                      /*Check if food quantity < reorder level.If less
                        reorder quantity is calculated and shopping list is sent
                      */
                      if(foodQuantity<Integer.parseInt(reports.get(i).getReorderLevel()) ){
                           Log.d("FoodQuantity", String.valueOf(foodQuantity));
                           int rQuantity= Integer.parseInt(reports.get(i).getReorderLevel())-foodQuantity;
                           Log.d("rQuantity", String.valueOf(rQuantity));
                           shopList.add(new Food(foodName,rQuantity));
                      }
                   }
                }
                for (DataSnapshot snapDate : snap.child("dates").getChildren()) {
                    mfDate=snapDate.child("mfDate").getValue(String.class);
                    expDate=snapDate.child("expDate").getValue(String.class);
                    if(!mfDate.equals("empty") && !expDate.equals("empty"))
                    { 
                       dates.add(new Dates(mfDate,expDate));
                       card=new Card(new Food(foodName,foodImageUrl,foodType,foodQuantity,dates));
                       try {
                           Date expiryDate = new SimpleDateFormat("dd/MM/yyyy").parse(expDate);
                           Date cDate= new SimpleDateFormat("dd/MM/yyyy").parse(currentDate);
                           /*Check if current date is greater than expiry date of dairy product.If it is greater,
                             Removal reminder message is triggered 
                            */
                           if(cDate.after(expiryDate)){
                              if(snapDate.getChildrenCount()==1)
                                 throwList.add(card.getFood());
                              else if(snapDate.getChildrenCount()>1){
                                     tQty+=1;
                              }
                              //Log.d("grocList",String.valueOf(grocList.get(0)));
                           }
                        } catch (ParseException e) {
                           e.printStackTrace();
                        }
                    }else{
                       card=new Card(new Food(foodName,foodImageUrl,foodType,foodQuantity));
                    }    
                }
                if(tQty>0){
                  if(foodType.equals("sweets")){
                      throwList.add(new Food(foodName,tQty,expDate));
                  }
                }
                cardArrayAdapter.add(card);
                listview.setAdapter(cardArrayAdapter);
                //makeGroceryList(context);
                Log.d("Dates",String.valueOf(dates)); 
                //Log.d("Food Name:",String.valueOf(foodName));
                //Log.d("Food ImageUrl: ",String.valueOf(foodImageUrl));
}

//Retrieve reorder level for each food item
public void fetchReorderLevels(DataSnapshot snap){
   String foodName = snap.child("itemName").getValue(String.class);
   String orderDate=snap.child("orderDate").getValue(String.class);
   String purchaseDate=snap.child("pdDate").getValue(String.class);
   String rLevel=snap.child("rLevel").getValue(String.class);
  
   Log.d("reorder",rLevel);
   reports.add(new BSReport(foodName,orderDate,purchaseDate,rLevel));
   Log.d("ReportSize", String.valueOf(reports.size()));
}
public void makeList(final CardArrayAdapter cardArrayAdapter, final Card card, final ListView listview, final Context context){
     bStock.addListenerForSingleValueEvent(new ValueEventListener() {
       @Override
       public void onDataChange(@NonNull DataSnapshot snapshot) {
         for (DataSnapshot snap : snapshot.getChildren()) {
            //Log.d("HelloKaja","test");
            fetchReorderLevels(snap);
         }
       }
       @Override
       public void onCancelled(DatabaseError databaseError) {

       }
    });
    mDatabase.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
         cardArrayAdapter.clearCardList();
         throwList.clear();
         listview.setAdapter(null);
         for (DataSnapshot snap : dataSnapshot.getChildren()) {
              fetchData(snap,cardArrayAdapter,card,listview,context);
         }
         
         
        //Log.d("grocList",String.valueOf(grocList.get(0)));
        //makeGroceryList(context);
          makeRemovalReminderList(context);
          makeShopList(context);

     }
      @Override
    public void onCancelled(DatabaseError databaseError) {
        // Getting Post failed, log a message
        Log.w("Firebase Not working", "loadPost:onCancelled", databaseError.toException());
    }

  });
     
}
 //Generate SMS message to remove food items
public void makeRemovalReminderList(Context context){
       if(control==false){
          makeRemoveReminderMsg(context);
          control=true;
       }
};
   // public void makeMsg(){
   //    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
   //    Calendar c = Calendar.getInstance();
   //    String currentDate = sdf.format(c.getTime());

   //    //String phoneNo="94766557526";
   //    String phoneNo="94766557526";
   //    String msg="Shopping List: ".concat(currentDate+"\n\n")
   //                .concat("Apple\t\t5 packs\n")
   //                .concat("Prima Bread\t\t2 packs\n")
   //                .concat("Banana\t\t200 g\n");
   //    //  String msg="Apple\t5 packs\n"
   //    //             .concat("Prima Bread\t2 packs\n")
   //    //             .concat("Banana\t200 g\n");
    
   //    SmsManager smsManager=SmsManager.getDefault();
   //    smsManager.sendTextMessage(phoneNo, null, msg, null, null);
   // } 

//Message construction for removing spoiled food items 
public void makeRemoveReminderMsg(Context context){
      //Mesage is sent to this phone number
      String phoneNo="94766557526";
      //Message Title
      String heading="Removal Reminder List: ".concat(currentDate+"\n\n") ; 
      String tableHead="Food Name"+"\t\tFood Quantity"+"\t\tExpiry Date";       
      ArrayList<String> tableRows=new ArrayList<String>();
      String msg=heading+"\n"+tableHead+"\n";
      //Generate removal list with food name,quantity and expiry date
      for(Food elem : throwList){
         tableRows.add(elem.getFoodName()+"\t\t\t\t\t\t\t\t\t"+String.valueOf(elem.getFoodQuantity())+"\t\t\t\t\t\t\t\t"+String.valueOf(elem.getExpDate())+"\n");       
         msg+=tableRows.get(throwList.indexOf(elem));
      }
      //Sending a message of characters>160 characters
      SmsManager smsManager=SmsManager.getDefault();
      ArrayList<String> shopList =smsManager.divideMessage(msg);
      ArrayList<PendingIntent> pendingIntents = new ArrayList<PendingIntent>(1);
      Intent sentIntent = new Intent("SENT");
      pendingIntents.add(PendingIntent.getBroadcast(context, 0, sentIntent, 0));
      smsManager.sendMultipartTextMessage(phoneNo, null, shopList, pendingIntents, null);
      
}
   //Generate SMS message to reorder food items
   public void makeShopList(Context context){
       if(controlV==false){
          makeShopListMsg(context);
          controlV=true;
       }
   };
   //Message Construction of reorder food items
   public void makeShopListMsg(Context context){
      //Mesage is sent to this phone number
      String phoneNo="94766557526";
      //Message Title
      String heading="Shopping List: ".concat(currentDate+"\n\n") ; 
      String tableHead="Food Name"+"\t\tReorder Quantity";       
      ArrayList<String> tableRows=new ArrayList<String>();
      String msg=heading+"\n"+tableHead+"\n";
      //Generate shopping list with food name and reorder Quantity 
      for(Food elem : shopList){
         tableRows.add(elem.getFoodName()+"\t\t\t\t\t\t\t\t\t"+String.valueOf(elem.getFoodQuantity())+"\n");
         msg+=tableRows.get(shopList.indexOf(elem));
         Log.d("shoplist101",msg);
      }
      SmsManager smsManager=SmsManager.getDefault();
      ArrayList<String> shopList =smsManager.divideMessage(msg);
      ArrayList<PendingIntent> pendingIntents = new ArrayList<PendingIntent>(1);
      Intent sentIntent = new Intent("SENT");
      pendingIntents.add(PendingIntent.getBroadcast(context, 0, sentIntent, 0));
      smsManager.sendMultipartTextMessage(phoneNo, null, shopList, pendingIntents, null);
      
   }
}