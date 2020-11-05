package com.example.cdap_nutritiontracking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    //Button For Nutrition Tracking
    Button nt;
    //Button for Inventory Tracking
    Button it;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nt=(Button)findViewById(R.id.nt);
        it=(Button)findViewById(R.id.it);
        nt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent i=new Intent(MainActivity.this,CameraViewActivity.class);
                Intent i=new Intent(MainActivity.this,FoodDiaryViewActivity.class);
                startActivity(i);
            }
        });
        it.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,InventoryMenuActivity.class);
                startActivity(i);
            }
        });
    }
}
