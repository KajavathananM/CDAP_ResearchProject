package com.example.cdap_nutritiontracking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class CameraViewActivity extends AppCompatActivity {
    static{
      System.loadLibrary("opencv_java4");
    }   
  
    private static String TAG="CameraViewActivity";
    static
    {
      if(OpenCVLoader.initDebug())
      {
          Log.d("KAJATEST1", "OpenCv is connected sucessfully");
      }else{
           Log.d("KAJATEST2", "OpenCv is not working.");
      }
    }



    public static final int CAMERA_REQUEST=1888;
    //Image is rendered from camera and modified from Image processing
    ImageView imageView;
    //Instruction for the home user
    TextView helper;
    //Photo button to take photo
    ImageView btnPhoto;
    Bitmap image;
    byte[] byteArray;

    Meal meal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);

      
      imageView=findViewById(R.id.imageView);
      helper=findViewById(R.id.helper);    
      btnPhoto= (ImageView) findViewById(R.id.btn_photo);

        if(getIntent().getExtras() != null) {
            meal = (Meal) getIntent().getSerializableExtra("meal");
         }
       
       btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
        
            
       
    }
     @Override
      protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CAMERA_REQUEST)
        {
             
              image=(Bitmap)data.getExtras().get("data");           
              

             //For Testing Purpose 
              int image_w = image.getWidth();
              int image_h = image.getHeight();
              
              
              resizeImage(image,image_w,image_h);
              //cropImage(image);
              
              Log.d("Captured Image Prop: ", "Height = " + image_h + " Width = " + image_w );
            
              
                     
              helper.setText("Click on the photo to view nutrient composition of the food item");
              imageView.setOnClickListener(new View.OnClickListener() {
                 @Override
                public void onClick(View v) {
                    Intent i=new Intent(CameraViewActivity.this,NutrientViewActivity.class);
                    i.putExtra("byteArray",byteArray);
                    i.putExtra("meal",(Meal)meal);
                    startActivity(i);
                    CameraViewActivity.this.finish();
                }
             });
            
        }

      }
       //Image Processing with OpenCV
      /*Forms a rectangle of width 100px and height 100px and crops the image
        starting from (0,0)
      */
      public Mat cropImage(Bitmap image){
         Mat image_original= new Mat(); 
         Utils.bitmapToMat(image, image_original);
         Log.d("ImageRowsT:",image_original.rows() + "ImageColsT:" + image_original.cols() );
         Rect rectCrop = new Rect(0,0, 115, 115);
         //Mat image_cropped= image_original.submat(rectCrop);
         Mat image_cropped= new Mat(image_original, rectCrop);
         
        //Each Row of Image matrix has pixels multiplied by 1.5 to improve brightness
        for (int i=1; i < image_cropped.height();i++)
        {
            for (int j=1; j<image_cropped.width();j++)
            {
                double Arr[]=image_cropped.get(i,j);
                Arr[0] = restrictMaxPixels(Arr[0],255);
                Arr[1] =restrictMaxPixels(Arr[1],255);
                Arr[2] =restrictMaxPixels(Arr[2],255);
                image_cropped.put(i,j,Arr);
            }
        }
         return image_cropped;   
     }
     public double restrictMaxPixels(double inputVal,double maxVal){
       double outputVal=inputVal*1.5;
       if(outputVal>maxVal){
          return 255;
       }
       return outputVal;
     }
   /*Cropped imaged get resizied according to specified width and height*/
      public void  resizeImage(Bitmap image,int image_w,int image_h){
                    Mat image_cropped = new Mat();
                    Mat image_resized = new Mat();
                   
                    //Convert bitmap to Mat   
                    Utils.bitmapToMat(image, image_cropped);
                    
                    
                    //Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_BGR2GRAY);
                    image_cropped=cropImage(image);
                    Log.d("ImageRowsB:",image_cropped.rows() + "ImageColsB:" + image_cropped.cols() );
                     
                    //Resize Image
                    Imgproc.resize(image_cropped,image_resized,new Size(600,600), 0, 0,Imgproc.INTER_CUBIC);
                    //Imgproc.resize(imageMat,image_res,new Size(400,400),0.5,0.5,Imgproc.INTER_AREA);
                    //Imgproc.rectangle(image_res,new Point(0,0), new Point(384,400), new Scalar(150,150,0),2); 
                    
                    
                     //image_resized=brightenImage(image_resized); 
                     Log.d("ImageRowsK:",image_resized.rows() + "ImageColsK:" + image_resized.cols() );  
                     image=Bitmap.createBitmap(image_resized.cols(), image_resized.rows(), Bitmap.Config.ARGB_8888);
                    //Image processed bitmap is assigned the image view of our interface                   
                     Utils.matToBitmap(image_resized,image);
                    
                     
                     imageView.setImageBitmap(image);

                    

                    //Save image to gallery
                    saveToInternalStorage(image);
                    convertBitmapToBytes(image);
                    //To check the resized image height and width
                     image_w = image.getWidth();
                     image_h = image.getHeight();
                     Log.d("Resize Image Prop: ", "Height = " + image_h + " Width = " + image_w );
       }
      //  /*Image gets darkened*/
      //  public Mat darkenImage(Mat image_res){
      //    Mat darkImg = new Mat(image_res.rows(), image_res.cols(), image_res.type()); 
  
      //    Imgproc.GaussianBlur(image_res, darkImg, new Size(0, 0), 10); 
      //    Core.addWeighted(image_res, 1.5, darkImg, -0.5, 0, darkImg);

      //    return darkImg;
      //  }
        /*Image gets brighten*/
       public Mat brightenImage(Mat image_res){
         Mat brightImg = new Mat(image_res.rows(), image_res.cols(), image_res.type()); 
         image_res.convertTo(brightImg, -1, 1, 50); 

         return brightImg;
       }
       //Translate Bitmap into array of bytes to pass to python code
       public void convertBitmapToBytes(Bitmap image){
              ByteArrayOutputStream stream = new ByteArrayOutputStream();
              image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
              byteArray = stream.toByteArray();
       }
       //Save Image to the external storage of the Project
       private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
         // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        Log.d("KajaTEST3", String.valueOf(directory));
        // Create imageDir
        File mypath=new File(directory,"test_img.jpg");

        FileOutputStream fos = null;
        try {           
            fos = new FileOutputStream(mypath);
       // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
              e.printStackTrace();
        } finally {
            try {
              fos.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
        } 
        return directory.getAbsolutePath();
    }
}
