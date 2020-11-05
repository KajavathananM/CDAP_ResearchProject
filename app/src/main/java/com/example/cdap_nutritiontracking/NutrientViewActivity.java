package com.example.cdap_nutritiontracking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//Chaquopy Libraries
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;


public class NutrientViewActivity extends AppCompatActivity {
    Button home;
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
    FoodDiaryService fdService;

    Python python;
    PyObject pythonFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fdService=new FoodDiaryService(NutrientViewActivity.this);
        Intent i = getIntent();
        byteArray = i.getByteArrayExtra("byteArray");
        if(i.getExtras() != null) {
            meal = (Meal) i.getSerializableExtra("meal");
         }

        final Toast toast=Toast.makeText(getApplicationContext(), "Invalid Object, Please Try Again!",Toast.LENGTH_LONG);
        //Shows Loading screen until Keras classifies the image
        setContentView(R.layout.activity_loadsplashscreen);
        if (! Python.isStarted()) {
           Python.start(new AndroidPlatform(this));
        }
        
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() { 
                   python = Python.getInstance();
                   pythonFile = python.getModule("food_cnn_predict-backend");  
                   predictedLabel=getFoodName(byteArray);

                   setContentView(R.layout.activity_nutrient_view);
                   home=(Button)findViewById(R.id.home);
                   foodName=(TextView)findViewById(R.id.FoodName);
                   calorieValue=(TextView)findViewById(R.id.calValue);
                   caloriesView=findViewById(R.id.caloriesView);
                   vitaminsView=findViewById(R.id.vitaminsView);
                   mineralsView=findViewById(R.id.mineralsView);

                   
                   try{
                       foodName.setText(predictedLabel);
                       calorieValue.setText(getCalorieValue(predictedLabel));
                       caloriesView.setImageBitmap(getCalorieBitmap(predictedLabel));
                       vitaminsView.setImageBitmap(getVitaminsBitmap(predictedLabel));
                       mineralsView.setImageBitmap(getMineralsBitmap(predictedLabel));

                       meal.setFood(predictedLabel,getCalorieValue(predictedLabel),
                         imageData1,imageData2,
                         imageData3
                       );

                       fdService.addMeal(meal);
                       home.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i=new Intent(NutrientViewActivity.this,FoodDiaryViewActivity.class);
                                startActivity(i);
                                NutrientViewActivity.this.finish();
                            }
                       });
                    }catch(Exception e)
                    {
                        foodName.setText("");
                        calorieValue.setText("");
                        caloriesView.setImageBitmap(null);
                        vitaminsView.setImageBitmap(null);
                        mineralsView.setImageBitmap(null);

                        
                        Intent returnCameraView=new Intent(NutrientViewActivity.this,CameraViewActivity.class);
                        startActivity(returnCameraView);
                        NutrientViewActivity.this.finish();
                        toast.show();
                    }     
                   
                   
                  
            }
        },250);     

    }
    //This returns accurate predicted Label  according to Food Image
    private String getFoodName(byte[] byteArray){
        // Python python = Python.getInstance();
        // PyObject pythonFile = python.getModule("food_cnn_predict-backend");
        return pythonFile.callAttr("classifyFoodImage",byteArray).toString();
        
    }
    //This returns calorie value according predicted label
    private String getCalorieValue(String predictedLabel){
        // Python python = Python.getInstance();
        // PyObject pythonFile = python.getModule("food_cnn_predict-backend");
        return pythonFile.callAttr("loadCalorieValue",predictedLabel).toString();    
    }
    //This returns piechart of calories according predicted label
    private Bitmap getCalorieBitmap(String predictedLabel){
        // Python python = Python.getInstance();
        // PyObject pythonFile = python.getModule("food_cnn_predict-backend");
        imageData1 = pythonFile.callAttr("sketchPieChart",predictedLabel).toJava(byte[].class);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData1, 0, imageData1.length);
        return bitmap;
    }
    //This returns horizontal barchart of vitamins composition according predicted label
    private Bitmap getVitaminsBitmap(String predictedLabel){
        // Python python = Python.getInstance();
        // PyObject pythonFile = python.getModule("food_cnn_predict-backend");
        imageData2 = pythonFile.callAttr("sketchVitaminsHorizontalBarChart",predictedLabel).toJava(byte[].class);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData2, 0, imageData2.length);
        return bitmap;
    }
    //This returns horizontal barchart of minerals composition according predicted label
    private Bitmap getMineralsBitmap(String predictedLabel){
        // Python python = Python.getInstance();
        // PyObject pythonFile = python.getModule("food_cnn_predict-backend");
        imageData3 = pythonFile.callAttr("sketchMinearalsHorizontalBarChart",predictedLabel).toJava(byte[].class);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData3, 0, imageData3.length);
        return bitmap;
    }
}
