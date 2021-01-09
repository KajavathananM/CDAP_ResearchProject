package com.example.cdap_nutritiontracking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
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
import org.opencv.core.CvType;


import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


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
    TextView productName;
    //Photo button to take photo
    ImageView btnPhoto;
    ImageView logo;
    View header;
    Bitmap image;
    Bitmap imageTemp;
    byte[] byteArray;

    Meal meal;
    List<Integer> xcordinates=new ArrayList<Integer>();
    List<Integer> ycordinates=new ArrayList<Integer>();

    Button reset;
    Button analyze;
    Button clear;
    Button crop;
    Canvas canvas;
    Paint paint;

    int image_w;
    int image_h;
    int imageView_w;
    int imageView_h;
    
    Uri image_uri;
    int w=0;
    int h=0;
    boolean isRotated=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);

      logo=findViewById(R.id.logo);
      productName=findViewById(R.id.textView2);
      imageView=findViewById(R.id.imageView);
      helper=findViewById(R.id.helper);    
      btnPhoto= (ImageView) findViewById(R.id.btn_photo);
      header= (View) findViewById(R.id.header);
      reset=(Button) findViewById(R.id.btn_reset);
      analyze=(Button)findViewById(R.id.btn_analyze);
      clear=(Button)findViewById(R.id.btn_clear);
      crop=(Button)findViewById(R.id.btn_crop);
       
      
      if(getIntent().getExtras() != null) {
            meal = (Meal) getIntent().getSerializableExtra("meal");
      }

      Log.d("BfImageViewWidth:",imageView.getMeasuredWidth() + "ImageViewHeight:  " + imageView.getMeasuredHeight()  );
      hideButtons();
      btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  ContentValues values = new ContentValues();
              //  values.put(MediaStore.Images.Media.TITLE, "New Picture");
              //  values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
              //  image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
               Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
               startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

      
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(image!=null){
                  finish();
                  startActivity(getIntent());
               }
            }
        });   
      
       
    }
     @Override
      protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CAMERA_REQUEST)
        {
                       
                 image=(Bitmap)data.getExtras().get("data");  
                 imageTemp=image.copy(image.getConfig(), true);

             
                 image_w = image.getWidth();
                 image_h = image.getHeight();
                 imageView_h=imageView.getMeasuredHeight();
                   
               
               
                 Log.d("AfImageViewWidth:",imageView.getMeasuredWidth() + "ImageViewHeight:  " + imageView.getMeasuredHeight()  );
                 Log.d("CapturedImageProp:", "Height = " + image_h + " Width = " + image_w );
               
                 //Make Landscape image in potrait
                 if(image_h<image_w){
                   Matrix matrix = new Matrix();
                   //  matrix.postRotate(90);
                   matrix.postRotate(270);
                   image= Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
                   imageTemp=image.copy(image.getConfig(), true);
                   image=image.copy(image.getConfig(), true);
                   isRotated=true;
                   
                   image_w = image.getWidth();
                   image_h = image.getHeight();
                   Log.d("RotatedImageWidth:",image_w + " RotatedImage Height:" +image_h );
                 }
 
                 showButtons();              
          
                 resetImageView();
                 btnPhoto.setVisibility(View.INVISIBLE);
                 btnPhoto.setEnabled(false);
                 helper.setVisibility(View.INVISIBLE);
                 logo.setVisibility(View.INVISIBLE);
                 header.setVisibility(View.INVISIBLE);
                 productName.setVisibility(View.INVISIBLE);
 
                 imageView.setImageBitmap(image);
                 canvas=new Canvas(image);
                 paint=new Paint();
                 paint.setStyle(Paint.Style.STROKE);
                 paint.setColor(Color.RED);
                 paint.setStrokeWidth(4);
                 paint.setAntiAlias(true);
                imageView.setOnTouchListener(new View.OnTouchListener()
                {                        
                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {  
                       int x = (int)event.getX()*image.getWidth()/1080;
                       int y = (int)event.getY()*image.getHeight()/2340;
                      

                       String coordinates="xCoordindate: "+String.valueOf(x)+" , yCoordinate: "+String.valueOf(y);
                       Toast toast=Toast.makeText(getApplicationContext(), coordinates, Toast.LENGTH_LONG);
                       if (event.getAction() == MotionEvent.ACTION_DOWN) {
                          toast.show();
                          Log.d("entersup","entersup");
                          if(xcordinates.size() < 2 && ycordinates.size() < 2) {
                                  xcordinates.add(x);
                                  ycordinates.add(y);
                          }
                         
                                                    
                       } 
                     
                
                       if(xcordinates.size()==2 && ycordinates.size()==2){
                          if(xcordinates.get(0)>xcordinates.get(1)){
                            w=xcordinates.get(0)-xcordinates.get(1);
                            h=ycordinates.get(0)-ycordinates.get(1);
                            int xTemp1=xcordinates.get(0);
                            int xTemp2=xcordinates.get(1);
                            xcordinates.set(0,xTemp2);
                            xcordinates.set(1,xTemp1);

                            int yTemp1=ycordinates.get(0);
                            int yTemp2=ycordinates.get(1);
                            ycordinates.set(0,yTemp2);
                            ycordinates.set(1,yTemp1);

                          }
                          showCropButton();
                          String xCords=String.valueOf(xcordinates.get(0))+" "+String.valueOf(xcordinates.get(1));
                          Log.d("xCords:",xCords);
                          String yCords=String.valueOf(ycordinates.get(0))+" "+String.valueOf(ycordinates.get(1));
                          Log.d("yCords:",yCords);              
                            // if (xcordinates.get(1)-xcordinates.get(0) < 1 || ycordinates.get(1)- ycordinates.get(0) < 1)
                            // {
                            //   Log.d("SelectRegionError","Selected Region is too short. Please Try Again");
                            //   return false;
                            // }
                            // Log.d("Execute","ExecuteTest");
                                               
                          canvas.drawRect(new android.graphics.Rect(
                            xcordinates.get(0), // Left
                            ycordinates.get(0), // Top
                            xcordinates.get(1), // Right
                            ycordinates.get(1)// Bottom
                          ),paint);
                          
                          Mat image_=new Mat();
                  

                          Utils.bitmapToMat(image, image_); 
                          // Imgproc.rectangle(image_,new Point(50,50), new Point(100,100), new Scalar(0,0,100),2);
                          // Imgproc.rectangle(image_,new Point(xcordinates.get(0),ycordinates.get(0)), new Point(xcordinates.get(1),ycordinates.get(1)), new Scalar(0,0,100),2);
                          // Log.d("ImageRowsF:",image_.rows() + "ImageColsF:" + image_.cols() );
                          Utils.matToBitmap(image_,image);
                       
                          Log.d("ImageRowsFr:",image_.rows() + "ImageColsF:" + image_.cols() );
                          
                          imageView.setImageBitmap(image); 
                          Log.d("ImgCroppedWidth: ",String.valueOf(imageView.getMaxWidth())+"ImgCroppedHeight: "+String.valueOf(imageView.getMaxHeight()));
                          
                        }    
                        return false;
                    }
                });
                reset.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                    w=0;
                    h=0;

                    hideAnalyzeButton();
                    helper.setVisibility(View.INVISIBLE);
                    logo.setVisibility(View.INVISIBLE);
                    productName.setVisibility(View.INVISIBLE);
                    header.setVisibility(View.INVISIBLE);

                    helper.setText("Take photo of the food to analyze Nutrient Contents");
                    resetImageView();
                    Log.d("TestRefresh","test");
                    image=imageTemp.copy(imageTemp.getConfig(), true);
                    canvas.setBitmap(image);
                    imageView.setImageBitmap(image);
                    xcordinates.clear();
                    ycordinates.clear();
                  }
                }); 
                crop.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                    hideCropButton();
                    showAnalyzeButton();
                    
                    helper.setVisibility(View.VISIBLE);
                    logo.setVisibility(View.VISIBLE);
                    header.setVisibility(View.VISIBLE);
                    productName.setVisibility(View.VISIBLE);

                    helper.setText("Press Analyze for Nutrition Analysis");
                    
                    imageView.getLayoutParams().width = imageView_w;
                    imageView.getLayoutParams().height = imageView_h;
                    // imageView.requestLayout();
                    // Log.d("CWidth:",canvas.getWidth()+"CHeight:"+canvas.getHeight());
                    // Log.d("BeforeCrop",String.valueOf(xcordinates.size()));
                    cropImage(imageTemp);
                  }
                }); 
                analyze.setOnClickListener(new View.OnClickListener() {
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
      public void cropImage(Bitmap image){
         Mat image_original= new Mat(); 
         Utils.bitmapToMat(image, image_original);
         Log.d("ImageRowsT:",image_original.rows() + " ImageColsT:" + image_original.cols() );
         Log.d("ImageWidth:",image.getWidth() + " ImageHeight:" + image.getHeight() );

        
        if(w==0){
          w=xcordinates.get(1)-xcordinates.get(0);
          h=ycordinates.get(1)-ycordinates.get(0);
        }
        
        Log.d("Xcordinate0: ",String.valueOf(xcordinates.get(0)) +",Ycoordinate0: "+String.valueOf(ycordinates.get(0))+",w:"+String.valueOf(w)+",h:"+String.valueOf(h));
        Rect rectCrop = new Rect(xcordinates.get(0),ycordinates.get(0), w, h);
        //  Mat image_cropped= image_original.submat(rectCrop);
         Mat image_cropped= new Mat(image_original, rectCrop);
         
        
        Log.d("ImageCropR",image_cropped.rows() + " ImageCropC:" + image_cropped.cols() );
        // Utils.matToBitmap(image_new,image);
        resizeImage(image,image_cropped);
        
        image_original.release();
        image_cropped.release();
     }
     public double restrictMaxPixels(double inputVal,double maxVal){
       double outputVal=inputVal*1.5;
       if(outputVal>maxVal){
          return 255;
       }
       return outputVal;
     }
     public int findResizedHeight(int resizedWidth){
      int resizedHeight=Integer.parseInt(String.valueOf(image.getWidth()/image.getWidth() * resizedWidth));
      Log.d("resizedHeight", String.valueOf(resizedHeight));
      return  resizedHeight;    
     }
   /*Cropped imaged get resized according to specified width and height*/
      public void resizeImage(Bitmap image,Mat image_cropped){
                        int resizedHeight=findResizedHeight(600); 

                        Mat image_aspect=new Mat();
                        Imgproc.resize(image_cropped,image_aspect,new Size(600,resizedHeight), 0, 0,Imgproc.INTER_CUBIC);
        
        
                        Mat image_new=new Mat(resizedHeight,600,CvType.CV_8UC4,new Scalar(0,0,0,255));
     
                         for (int i=0; i < image_aspect.height();i++)
                         {
                              for (int j=0; j<image_aspect.width();j++)
                              {
                                double Arr[]=image_aspect.get(i,j);
                                // Arr[0] = restrictMaxPixels(Arr[0],255);
                                // Arr[1] =restrictMaxPixels(Arr[1],255);
                                // Arr[2] =restrictMaxPixels(Arr[2],255);
                                image_new.put(i,j,Arr);

                               }
                          }
                     
                          // image_new=darkenImage(image_aspect);
                          // image_new=brightenImage(image_aspect);
                          Log.d("ImageRowsCR",image_new.rows() + " ImageColsCR:" + image_new.cols() );
                          Bitmap imageNew=Bitmap.createBitmap(image_new.cols(),image_new.rows(),Bitmap.Config.ARGB_8888);
                          Utils.matToBitmap(image_new,imageNew);
                          if(isRotated==true){
                            Matrix matrix = new Matrix();
                             matrix.postRotate(90);
                            imageNew= Bitmap.createBitmap(imageNew, 0, 0, imageNew.getWidth(), imageNew.getHeight(), matrix, true);
                            imageNew=imageNew.copy(image.getConfig(), true);
                          }
                          imageView.setImageBitmap(imageNew);
                        

                    

                          //Save image to gallery
                          saveToInternalStorage(imageNew);
                          //Bytes form is required to processed at python library
                          convertBitmapToBytes(imageNew);
                          image_aspect.release();
                          image_new.release();
                          imageNew=null;
       }
      //  /*Image gets darkened*/
       public Mat darkenImage(Mat image_res){
         Mat darkImg = new Mat(image_res.rows(), image_res.cols(), image_res.type()); 
  
         Imgproc.GaussianBlur(image_res, darkImg, new Size(0, 0), 10); 
         Core.addWeighted(image_res, 1.5, darkImg, -0.5, 0, darkImg);

         return darkImg;
       }
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

    public void resetImageView(){
        imageView.getLayoutParams().width = 1080;
        imageView.getLayoutParams().height = 2340;
        // imageView.requestLayout();
    }
    public void hideButtons(){
      reset.setVisibility(View.INVISIBLE);
      clear.setVisibility(View.INVISIBLE); 
      crop.setVisibility(View.INVISIBLE);  
      analyze.setVisibility(View.INVISIBLE);  

      reset.setEnabled(false); 
      clear.setEnabled(false); 
      crop.setEnabled(false); 
      analyze.setEnabled(false); 
      imageView.setEnabled(false);
    }
    public void showButtons(){
      reset.setVisibility(View.VISIBLE);
      clear.setVisibility(View.VISIBLE); 
      crop.setVisibility(View.INVISIBLE);  
      

      reset.setEnabled(true); 
      clear.setEnabled(true); 
      crop.setEnabled(false); 
      imageView.setEnabled(true);
    }
    public void showAnalyzeButton(){
      analyze.setEnabled(true); 
      analyze.setVisibility(View.VISIBLE);
      imageView.setEnabled(false);
    }
    public void showCropButton(){
      crop.setVisibility(View.VISIBLE); 
      crop.setEnabled(true); 
    }
    public void hideAnalyzeButton(){
      analyze.setEnabled(false); 
      crop.setEnabled(false); 
      analyze.setVisibility(View.INVISIBLE);
      imageView.setEnabled(true);
      
    }
    public void hideCropButton(){
      crop.setVisibility(View.INVISIBLE); 
      crop.setEnabled(false);
    }
}
