#!/usr/bin/env python
# # -*- coding: utf-8 -*-

from pyknow import *
import sys
import pandas as pd
import numpy as np
from os.path import dirname, join,abspath
from pathlib import Path

predictedLabels=["Apple","Banana","French Fries","Idly","Pizza","Rice with Dhal","Samosa"]
RecipeLabels=["Pizza","Idly","French Fries","Rice with Dhal"]

recommendList=[]
avoidanceList=[]




class NutrientsData(Fact):
    """Info about the Nutrition Details of food item"""
    pass
class PatientData(Fact):
    """Info about the Patient's health Details"""
    pass
class RecipeData(Fact):
    """Info about the Patient's health Details"""
    pass


class SuggestFoodEngine(KnowledgeEngine):
   
    #Suggest Food items below diabetic level or cholestrol level of a person
    @Rule(NutrientsData(Carbohydrate=MATCH.Carbohydrate,FoodName=MATCH.FoodName,Fat=MATCH.Fat),
          PatientData(sugar_lvl=MATCH.sugar_lvl,cholestrol_lvl=MATCH.cholestrol_lvl),
          AND(
                    TEST( lambda sugar_lvl, Carbohydrate: Carbohydrate<=sugar_lvl),
                    TEST( lambda cholestrol_lvl, Fat: Fat<=cholestrol_lvl)
          )
    )
    def SuggestLowCholestrolOrDiabetesFoods(self,FoodName):
           #print(FoodName+" is suggested since it is less risky for cholestrol.\n")
           if(FoodName not in recommendList):
                recommendList.append(FoodName) 

    #Avoid food items that exceed cholestrol level or diabetic level of a person
    @Rule(NutrientsData(Carbohydrate=MATCH.Carbohydrate,FoodName=MATCH.FoodName,Fat=MATCH.Fat),
          PatientData(sugar_lvl=MATCH.sugar_lvl,cholestrol_lvl=MATCH.cholestrol_lvl),
          OR(
               AND(
                    TEST( lambda sugar_lvl, Carbohydrate:Carbohydrate>sugar_lvl),
                    TEST( lambda cholestrol_lvl,Fat:Fat>cholestrol_lvl)  
               ),
               AND(
                    TEST( lambda sugar_lvl, Carbohydrate:Carbohydrate<=sugar_lvl),
                    TEST( lambda cholestrol_lvl,Fat:Fat>cholestrol_lvl)  
               ),
                AND(
                    TEST( lambda sugar_lvl, Carbohydrate:Carbohydrate>sugar_lvl),
                    TEST( lambda cholestrol_lvl,Fat:Fat<=cholestrol_lvl)  
               )
          )
    )
    def AvoidHighCholestrolOrDiabetesFoods(self,FoodName):
        #print("Please avoid "+FoodName+" that leads to cholestrol or diabetes.\n")
       if(FoodName in recommendList):
          recommendList.remove(FoodName)
       if(FoodName not in avoidanceList):
          avoidanceList.append(FoodName)
    
  
    #Trigger Liver disease alert vased on comparing user's Iron storage and Iron of food item
    @Rule(NutrientsData(Iron=MATCH.Iron,FoodName=MATCH.FoodName),
         PatientData(IronIntake=MATCH.IronIntake),
         TEST(lambda Iron, IronIntake: Iron>IronIntake)
    )
    def AvoidExcessIronFoods(self,FoodName):
         #print("Please avoid "+FoodName +" to prevent liver disease.\n")
         if(FoodName in recommendList):
           if(FoodName not in avoidanceList):
               recommendList.remove(FoodName)
               avoidanceList.append(FoodName) 
         if(FoodName not in avoidanceList):
              avoidanceList.append(FoodName)  
    
     
    #Suggest Protien rich food that is suitable for protein deficient
    @Rule(NutrientsData(Protein=MATCH.Protein,FoodName=MATCH.FoodName),
         PatientData(proteinDeficent=MATCH.proteinDeficent),
         AND(
            TEST(lambda proteinDeficent, proteinDeficient:True), 
            TEST(lambda Protein, P:Protein==2.69)    
         )
    )
    def SuggestProteinRichFoods(self,FoodName):
         #print(FoodName +" is suggested to you to gain protein.\n")
         if(FoodName  not in recommendList):
           if(FoodName  not in avoidanceList):
               recommendList.append(FoodName)
         if(FoodName not in avoidanceList):
              avoidanceList.append(FoodName)  

engine = SuggestFoodEngine()
engine.reset()

#1) Retrieving Nutrition Data nad Human's user data
#2) Perform comparison between two datas and check defficiency in Nutrient                              
for predictedLabel in predictedLabels:
  # Read Nutrition Composition for each food item
  nutrientcsvFile=predictedLabel+'.csv'
  nDatasetPath=join(dirname(__file__),nutrientcsvFile)
  nutritionData=pd.read_csv(nDatasetPath)
  c= nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Carbohydrate"].index,1].squeeze()
  f= nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Fat"].index,1].squeeze()
  p = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Protein"].index,1].squeeze()
  vitA = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Vitamin A"].index,1].squeeze()/((10**6)*3.33)
  vitB6 = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Vitamin B6"].index,1].squeeze()/(10**3)
  vitB12 = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Vitamin B12"].index,1].squeeze()/((10**6)*3.33)
  vitC = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Vitamin C"].index,1].squeeze()/(10**3)
  vitD=nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Vitamin D"].index,1].squeeze()/((10**6)*3.33)
  vitE=nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Vitamin E"].index,1].squeeze()/(10**3)
  vitK=nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Vitamin K"].index,1].squeeze()/((10**6)*3.33)
  fluoride = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Fluoride, F"].index,1].squeeze()/(10**6)
  calcium = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Calcium, Ca"].index,1].squeeze()/(10**3)
  sodium = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Sodium, Na"].index,1].squeeze()/(10**3)
  potassium = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Potassium, K"].index,1].squeeze()/(10**3)
  iron=nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Iron, Fe"].index,1].squeeze()/(10**3)
  phosphorus=nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Phosphorus, P"].index,1].squeeze()/(10**3)
  magnesium=nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Magnesium, Mg"].index,1].squeeze()/(10**3)
  zinc=nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Zinc, Zn"].index,1].squeeze()/(10**3)
  
  #Value of User's Patient Details
  cholestrolLvl=20
  sugarLvl=20
  IronAMT=5/(10**3)
  

  engine.declare(NutrientsData(
                          FoodName=predictedLabel,
                          Carbohydrate=c,
                          Fat=f,
                          Iron=iron,
                          Protein=p
                ),
                PatientData(
                           cholestrol_lvl=cholestrolLvl,
                           sugar_lvl=sugarLvl,
                           IronIntake=IronAMT,
                           proteinDeficent=True
                )
  )      
  engine.run() 
  

#Read ingredients from Food Item's dataset
ingredientscsvFile='Ingredients.csv'
iListDatasetPath=join(dirname(__file__),ingredientscsvFile)
iList=pd.read_csv(iListDatasetPath, usecols=RecipeLabels)
class AllergyEngine(KnowledgeEngine):
    #Check if there is an allergic ingredient in food and if the person is allergic
    @Rule(RecipeData(FoodName=MATCH.FoodName,ingredient=MATCH.ingredient),
          PatientData(allergyToCheese=MATCH.allergyToCheese),
          OR(
               AND(
                TEST(lambda allergyToTomato, ALT:allergyToTomato==True),
                TEST(lambda ingredient,item:ingredient=="Tomato")
               ),
               AND(
                TEST(lambda allergyToCheese, ALC:allergyToCheese==True),
                TEST(lambda ingredient,item:ingredient=="Cheese")
               )
          )
     )
    def AvoidAllergicFoodItems(self,FoodName):
            #print(FoodName+ " needs to be avoided due to cheese allergy.\n")
          if(FoodName in recommendList):
            recommendList.remove(FoodName)
          if(FoodName not in avoidanceList):
             avoidanceList.append(FoodName)

 
engine2=AllergyEngine() 
engine2.reset()
for label in RecipeLabels:
     #print(label)
     for item in iList[label]:
            #print(item)
            engine2.declare(
                    RecipeData(
                        FoodName=label,
                        ingredient=item
                    ),
                    PatientData(
                        allergyToTomato=True,
                        allergyToCheese=True
                    )  
            )     
            engine2.run() 
            


list1="Recommended Foods: "+ str(recommendList)
list2="Avoided Foods: "+ str(avoidanceList)

#Here we are returning two type of food List:Recommended foods to be eaten and high risk foods to be avoided
def returnList():
   return list1,list2

