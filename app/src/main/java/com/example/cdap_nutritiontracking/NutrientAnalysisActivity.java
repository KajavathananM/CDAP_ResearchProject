package com.example.cdap_nutritiontracking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class NutrientAnalysisActivity extends AppCompatActivity {
    TextView foodName;
    TextView calorieValue;
    ImageView caloriesView;
    ImageView vitaminsView;
    ImageView mineralsView;
    String predictedLabel;

    byte[] byteArray;
    Meal meal;

    byte[] imageData1;
    byte[] imageData2;
    byte[] imageData3;

    Bitmap bitmap1;
    Bitmap bitmap2;
    Bitmap bitmap3;

    FoodDiaryService fdService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        byteArray = i.getByteArrayExtra("byteArray");
        if(i.getExtras() != null) {
            meal = (Meal) i.getSerializableExtra("elem");
        }

                   setContentView(R.layout.activity_nutrient_view);
                   foodName=(TextView)findViewById(R.id.FoodName);
                   calorieValue=(TextView)findViewById(R.id.calValue);
                   caloriesView=findViewById(R.id.caloriesView);
                   vitaminsView=findViewById(R.id.vitaminsView);
                   mineralsView=findViewById(R.id.mineralsView);

                   
                       foodName.setText(meal.foodName);
                       calorieValue.setText(meal.calories);
                       initalizeImages(meal);
                      

                       Bitmap bitmap = BitmapFactory.decodeByteArray(imageData1, 0, imageData1.length);
                       caloriesView.setImageBitmap(bitmap1); 
                       vitaminsView.setImageBitmap(bitmap2);
                       mineralsView.setImageBitmap(bitmap3);                      
                        
                   
                   
                  
            }
            //Initialize Image views with Bitmap
            public void initalizeImages(Meal meal){

                imageData1= meal.mainImage;
                imageData2=meal.vitamins;
                imageData3=meal.minerals;

                bitmap1 = BitmapFactory.decodeByteArray(imageData1, 0, imageData1.length);
                bitmap2 = BitmapFactory.decodeByteArray(imageData2, 0, imageData2.length);
                bitmap3 = BitmapFactory.decodeByteArray(imageData3, 0, imageData3.length);

            }
            

}
  

