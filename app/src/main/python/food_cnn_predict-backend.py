
#Tensorflow Keras Image Classification 

import numpy as np
from keras.preprocessing.image import ImageDataGenerator, load_img, img_to_array
from keras.models import Sequential, load_model
from os.path import dirname, join,abspath
from pathlib import Path
#load model
img_width, img_height = 128, 128
model_path=join(dirname(__file__), 'model.h5')
model_weights_path=join(dirname(__file__), 'weights.h5')


model = load_model(model_path)
model.load_weights(model_weights_path)



#Prediction on a new picture
from keras.preprocessing import image as image_utils

from PIL import Image
import requests
from io import BytesIO

#Libraries for plotting graph and loading dataset
import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import io
import cv2




#Generate Piechart for the predicted Label
def sketchPieChart(predictedLabel):
    bio=io.BytesIO()
    csvFile=predictedLabel+'.csv'
    nDatasetPath=join(dirname(__file__),csvFile)
    nutritionData=pd.read_csv(nDatasetPath)
    calories=nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Calories"].index,1].squeeze()
    plt.style.use('ggplot')
    carbohyrate = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Carbohydrate"].index,1].squeeze()
    fat= nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Fat"].index,1].squeeze()
    protein = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Protein"].index,1].squeeze()
    weights = [carbohyrate,fat,protein]
    label = ['Carbohydrate', 'Fat', 'Protein']
    plt.figure('View Calories')
    plt.title('Weight for 3 Nutrient Classes',size=12)
    plt.pie(weights, labels=label,pctdistance=0.8,autopct='%.2f %%')
    plt.savefig(bio,format="png")
    piechart=bio.getvalue()
    plt.clf()
    return piechart
    
#Generate Vitamins horizontal barchart for the predicted Label
def sketchVitaminsHorizontalBarChart(predictedLabel):
    bio=io.BytesIO()
    csvFile=predictedLabel+'.csv'
    nDatasetPath=join(dirname(__file__),csvFile)
    nutritionData=pd.read_csv(nDatasetPath)
    vitaminA = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Vitamin A"].index,1].squeeze()/((10**6)*3.33)
    vitaminB6 = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Vitamin B6"].index,1].squeeze()/(10**3)
    vitaminB12 = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Vitamin B12"].index,1].squeeze()/((10**6)*3.33)
    vitaminC = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Vitamin C"].index,1].squeeze()/(10**3)
    vitaminD=nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Vitamin D"].index,1].squeeze()/((10**6)*3.33)
    vitaminE=nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Vitamin E"].index,1].squeeze()/(10**3)
    vitaminK=nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Vitamin K"].index,1].squeeze()/((10**6)*3.33)

    labels = ('Vitamin A','Vitamin B6','Vitamin B12', 'Vitamin C','Vitamin D','Vitamin E','Vitamin K')
    y_pos = np.arange(len(labels))
    values = [vitaminA,vitaminB6,vitaminB12,vitaminC,vitaminD,vitaminE,vitaminK]
  
   
    figure=plt.figure('View Vitamins')
    plt.barh(y_pos,values, align='center', alpha=0.5,color='#FF4500')
    plt.yticks(y_pos, labels)
    plt.xlabel('Weight g')
    plt.title('Vitamins')
    plt.savefig(bio,format="png")
    hBarchartV=bio.getvalue()
    plt.clf()
    return hBarchartV
#Generate Minerals horizontal barchart for the predicted Label   
def sketchMinearalsHorizontalBarChart(predictedLabel):
    bio=io.BytesIO()
    csvFile=predictedLabel+'.csv'
    nDatasetPath=join(dirname(__file__),csvFile)
    nutritionData=pd.read_csv(nDatasetPath)
    fluoride = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Fluoride, F"].index,1].squeeze()/(10**6)
    calcium = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Calcium, Ca"].index,1].squeeze()/(10**3)
    sodium = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Sodium, Na"].index,1].squeeze()/(10**3)
    potassium = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Potassium, K"].index,1].squeeze()/(10**3)
    iron=nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Iron, Fe"].index,1].squeeze()/(10**3)
    phosphorus=nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Phosphorus, P"].index,1].squeeze()/(10**3)
    magnesium=nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Magnesium, Mg"].index,1].squeeze()/(10**3)
    zinc=nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Zinc, Zn"].index,1].squeeze()/(10**3)

    labels = ('Fluoride','Calcium','Sodium','Potassium','Iron','Phosphorus','Magnesium','Zinc')
    y_pos = np.arange(len(labels))
    values = [fluoride,calcium,sodium,potassium,iron,phosphorus,magnesium,zinc]
  
    width=0.4
    plt.figure('View Minerals')
    plt.barh(y_pos,values,width,align='center', alpha=0.5,color='#FF4500')
    plt.yticks(y_pos, labels)
    plt.xlabel('Weight g')
    plt.title('Minerals')
    plt.show()
    plt.savefig(bio,format="png")
    hBarchartM=bio.getvalue()
    plt.clf()
    return hBarchartM

#Get the color pixels for Rice 
def getRiceColorPixel(imgC):
    hsv_frame = cv2.cvtColor(imgC, cv2.COLOR_BGR2HSV)
    low_rice = np.array([0, 0, 150])
    high_rice = np.array([255,55, 255])
    rice_mask = cv2.inRange(hsv_frame, low_rice, high_rice)
    riceColor = cv2.bitwise_and(imgC, imgC, mask=rice_mask)
    return cv2.countNonZero(rice_mask)

#Get the color pixels for dhal     
def getDhalColorPixel(imgC):
    hsv_frame = cv2.cvtColor(imgC, cv2.COLOR_BGR2HSV)
    low_dhal = np.array([20, 100, 100])
    high_dhal = np.array([30, 255, 255])
    dhal_mask = cv2.inRange(hsv_frame, low_dhal, high_dhal)
    dhalColor = cv2.bitwise_and(imgC, imgC, mask=dhal_mask)
    return cv2.countNonZero(dhal_mask)
    

#Get the color pixels for Sambal          
def getSambalColorPixel(imgC):
    hsv_frame = cv2.cvtColor(imgC, cv2.COLOR_BGR2HSV)
    low_Sambal = np.array([10, 100, 20])
    high_Sambal = np.array([25, 255, 200])
    sambal_mask = cv2.inRange(hsv_frame, low_Sambal, high_Sambal)
    sambalColor = cv2.bitwise_and(imgC, imgC, mask=sambal_mask) 
    return cv2.countNonZero(sambal_mask)  

#Get the color pixels for Banana          
def getBananaColorPixel(imgC):
    hsv_frame = cv2.cvtColor(imgC, cv2.COLOR_BGR2HSV)
    low_banana = np.array([20,0,0])
    high_banana = np.array([50,255,255])
    banana_mask = cv2.inRange(hsv_frame,low_banana, high_banana)
    bananaColor = cv2.bitwise_and(imgC, imgC, mask=banana_mask)
    return cv2.countNonZero(banana_mask)  

def erodeImage(imgGray,img):
  ret, otsu = cv2.threshold(imgGray, 0, 255, 
  cv2.THRESH_BINARY | cv2.THRESH_OTSU)
  ret2, triangle = cv2.threshold(imgGray, 0, 255, 
  cv2.THRESH_BINARY | cv2.THRESH_TRIANGLE)
  kernels = np.ones((5, 5), np.uint8)
  img_erode = cv2.erode(img, kernels, iterations=5)
  return img_erode
def detectBlobs(imgByteArr):
  img_stream = BytesIO(imgByteArr)
  imgC = cv2.imdecode(np.fromstring(img_stream.read(), np.uint8), 1)
  imgC=cropImageFromRect(imgC,0,0,(imgC.shape[1]-2),(imgC.shape[0]-2))


  imgGray = cv2.cvtColor(imgC, cv2.COLOR_BGR2GRAY)
  imgC=erodeImage(imgGray,imgC)
  # Set up the detector with default parameters.
  detector = cv2.SimpleBlobDetector_create()
  # Detect blobs.
  keypoints = detector.detect(imgC)
  # Draw detected blobs as red circles.
  # cv2.DRAW_MATCHES_FLAGS_DRAW_RICH_KEYPOINTS ensures the size of the circle corresponds to the size of blob
  blobs = cv2.drawKeypoints(imgC, keypoints, np.array([]), (255, 0, 0),cv2.DRAW_MATCHES_FLAGS_DRAW_RICH_KEYPOINTS) 
  # Show keypoints
  return len(keypoints)

#Through the percentage of greyness or Dhal color,ensuring there is no misclassifcation of Rice Dhal with other 
#6 food items or Unknown objects
def getColorFromImage(imgByteArr):
    img_stream = BytesIO(imgByteArr)
    imgC = cv2.imdecode(np.fromstring(img_stream.read(), np.uint8), 1)
    total_pixels = imgC.shape[0]*imgC.shape[1]
    imgC=cropImageFromRect(imgC,0,0,(imgC.shape[1]-2),(imgC.shape[0]-2))
       
    img_pix = 0
    for i in range(len(imgC)):
        for  j in range(len(imgC[i])):
            pixel_value = imgC[i,j]
            #print(pixel_value)
            img_pix += 1
                     
            
           
    percent_riceColor=(getRiceColorPixel(imgC)/img_pix) * 100
    percent_DhalColor=(getDhalColorPixel(imgC)/img_pix) * 100
    percent_SambalColor=(getSambalColorPixel(imgC)/img_pix) * 100
    percent_BananaColor=(getBananaColorPixel(imgC)/total_pixels) * 100
    

    # print("Percentage of Rice Color: "+str(percent_riceColor))
    # print("Percentage of Dhal Color: "+str(percent_DhalColor))
    # print("Percentage of Sambal Color: "+str(percent_SambalColor))

    return percent_riceColor,percent_DhalColor,percent_SambalColor,percent_BananaColor

#Food item recognition from Photo taken from mobile camera
def classifyFoodImage(byteArray): 
    imgByteArr=bytes(byteArray)
    #image_path=join(dirname(__file__), 'apple4.jpg')
    #test_image = Image.open(image_path)
    #image_path=join(dirname(__file__), 'profile.jpg')
    #test_image = Image.open(image_path)
    
    #This is where Keras Model does image recognition on food Image 'test_image'
    test_image = Image.open(BytesIO(imgByteArr))
    test_image = test_image.resize((128,128),3)
    test_image = image_utils.img_to_array(test_image)/255
    test_image = np.expand_dims(test_image, axis=0)
    
    #Predicts the accurate class of food image with respect to training and testing image datasets
    result = model.predict_proba(test_image)
    
    verifyColorFromImage=getColorFromImage(imgByteArr)
    

    predictedLabel = 'Unknown' 
    if result[0][0]==np.max(result) and result[0][0]>0.7:   
        predictedLabel = 'Apple'    
    if result[0][4]>=0.9:
       predictedLabel = 'Pizza'

    elif result[0][1]==np.max(result) or result[0][1]>=0.3 and verifyColorFromImage[3]>5:
        predictedLabel = 'Banana'
    elif result[0][2]==np.max(result) and  result[0][2]>=0.6:  
        predictedLabel = 'French Fries'
   
    elif verifyColorFromImage[0]>=2 and verifyColorFromImage[0]<=60 and verifyColorFromImage[1]<=60 and detectBlobs(imgByteArr)>0:
        predictedLabel = 'Rice with Dhal'  
    elif verifyColorFromImage[0]>=7 and verifyColorFromImage[2]>1 or result[0][3]>=0.8 and verifyColorFromImage[0]>=7:        
             predictedLabel = 'Idly'
    elif result[0][6]==np.max(result) and result[0][6]>0.7:
        predictedLabel = 'Samosa'   
    
    return predictedLabel

#Load the respective calorie value based on the predicted label
def loadCalorieValue(predictedLabel):
    csvFile=predictedLabel+'.csv'
    nDatasetPath=join(dirname(__file__),csvFile)
    nutritionData=pd.read_csv(nDatasetPath)
    calories=nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Calories"].index,1].squeeze()
    return calories

# Grabcut method that replaces background from  the image with black color and leaves only the object
def cropImageFromRect(imgC,ix,iy,x,y):
    imgCV=imgC.copy()

    #Width and height of the selected region
    # width=(x-ix)
    # height=(y-iy)
    #Top left point
    # print(str(ix)+" "+str(iy))
    # #Bottom Right Point
    # print(str(x)+" "+str(y))
    w=x-ix
    h=y-iy
     
    mask=np.zeros(imgCV.shape[:2],np.uint8) 
    bgModel=np.zeros((1,65),np.float64)
    #This the extracted object is located
    fgModel=np.zeros((1,65),np.float64)
    #Selected Region is constructed
    # rect = (0,0,width,height)
    rect = (ix,iy,w,h)
    
    #Here we are converting Background of the image as black to extract the object
    cv2.grabCut(imgCV,mask,rect,bgModel,fgModel,7,cv2.GC_INIT_WITH_RECT)
    mask2=np.where((mask==2)|(mask==0),0,1).astype('uint8')
    imgCV = imgCV*mask2[:,:,np.newaxis]
    return imgCV
    