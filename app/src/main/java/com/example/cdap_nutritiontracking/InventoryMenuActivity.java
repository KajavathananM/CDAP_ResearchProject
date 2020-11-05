package com.example.cdap_nutritiontracking;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;




public class InventoryMenuActivity extends AppCompatActivity {
     Button viewKitchenInventory;
     Button viewInventoryReport;

     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_menu_activity);

      viewKitchenInventory=(Button)findViewById(R.id.v1); 
      viewInventoryReport=(Button)findViewById(R.id.v2);   

      viewKitchenInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(InventoryMenuActivity.this,KitchenInventory.class);
                startActivity(i);
            }
        });

       viewInventoryReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(InventoryMenuActivity.this,InventoryReportActivity.class);
                startActivity(i);
            }
        });
    }
 
  
}
