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

#This Rule Engine suggests Food by checking person's cholestrol level and diabetic level in blood either normal,low or high and compare with carbohydrate and Fat intake
class SuggestFoodEngine(KnowledgeEngine): 
    @Rule(
        NutrientsData(FoodName=MATCH.FoodName,carbVal=MATCH.carbVal),
        PatientData(ent_sugarLvl=P(lambda ent_sugarLvl:ent_sugarLvl>70) & P(lambda ent_sugarLvl:ent_sugarLvl<140))
    )
    def CheckIfPersonInNormalDiabeticRange(self,FoodName,carbVal):
           #print("TEST1")
           raise_blood_sugar = carbVal * 3.5
           allowed_range_sugar = 200 - ent_sugarLvl
           if (allowed_range_sugar < raise_blood_sugar ):
                recommendList.append(FoodName)
    @Rule(
        NutrientsData(FoodName=MATCH.FoodName),
        PatientData(ent_sugarLvl=P(lambda ent_sugarLvl:ent_sugarLvl>=200))
    )
    def SuggestLowCarbFoods(self,FoodName):
      if(FoodName not in avoidanceList):
          recommendList.append(FoodName)

    @Rule(
        NutrientsData(FoodName=MATCH.FoodName),
        PatientData(ent_sugarLvl=P(lambda ent_sugarLvl:ent_sugarLvl<=70))
    )
    def SuggestHighCarbFoods(self,FoodName):
      if(FoodName not in avoidanceList):
              recommendList.append(FoodName)
    @Rule(
        NutrientsData(FoodName=MATCH.FoodName,fatVal=MATCH.fatVal),
        PatientData(ent_cholestrolLvl=P(lambda ent_cholestrolLvl:ent_cholestrolLvl>200) & P(lambda ent_cholestrolLvl:ent_cholestrolLvl<239))
    )
    def CheckIfPersonInNormalCholestrolcRange(self,FoodName,fatVal):
           #print("TEST1")
           raise_blood_cholestrol = fatVal * 3.5
           allowed_range_cholestrol = 240 - ent_cholestrolLvl
           if (allowed_range_cholestrol < raise_blood_cholestrol and Food not in recommendList):
                recommendList.append(FoodName)
    @Rule(
        NutrientsData(FoodName=MATCH.FoodName),
        PatientData(ent_cholestrolLvl=P(lambda ent_cholestrolLvl:ent_cholestrolLvl>240))
    )
    def SuggestHighCholestrolFoods(self,FoodName):
      if(FoodName not in avoidanceList and FoodName not in recommendList ):
              recommendList.append(FoodName)
    @Rule(
        NutrientsData(FoodName=MATCH.FoodName),
        PatientData(ent_cholestrolLvl=P(lambda ent_cholestrolLvl:ent_cholestrolLvl<200))
    )
    def SuggestLowCholestrolFoods(self,FoodName):
      if(FoodName not in avoidanceList and FoodName not in recommendList ):
         recommendList.append(FoodName)
   

    
engine1 = SuggestFoodEngine()
engine1.reset()
#1) Retrieving Nutrition Data nad Human's user data
#2) Perform comparison between two datas and check defficiency in Nutrient                              
for predictedLabel in predictedLabels:
  # Read Nutrition Composition for each food item
  nutrientcsvFile=predictedLabel+'.csv'
  nDatasetPath=join(dirname(__file__),nutrientcsvFile)
  nutritionData=pd.read_csv(nDatasetPath)
  carb= nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Carbohydrate"].index,1].squeeze()
  fat= nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Fat"].index,1].squeeze()
  protein = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Protein"].index,1].squeeze()
  
  
  #Value of User's Patient Details
  CarbValue= (carb/100) * 175
  FatValue= (fat/100) * 175
  print("Carb Value for "+predictedLabel+": "+str(CarbValue))
  print("Fat Value for "+predictedLabel+": "+str(FatValue))
  ent_cholestrolLvl=20
  ent_sugarLvl=100
   

  engine1.declare(NutrientsData(
                          FoodName=predictedLabel,
                          carbVal=CarbValue,
                          fatVal=FatValue
                ),
                PatientData(
                           ent_sugarLvl=ent_sugarLvl,
                           ent_cholestrolLvl=ent_cholestrolLvl
                )
  )      
  engine1.run() 
  

#Read ingredient names from Food Item's dataset
ingredientscsvFile='Ingredients.csv'
iListDatasetPath=join(dirname(__file__),ingredientscsvFile)
iList=pd.read_csv(iListDatasetPath, usecols=RecipeLabels)
#This Rule Engine checks if there is an allergic ingredient in food and if the person is allergic
class AllergyEngine(KnowledgeEngine):
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
            #break


#print("==============================================================================\n")
#print("Recommended Foods: "+ str(recommendList))
#print("Avoided Foods: "+ str(avoidanceList))
#print("================================================================================")

list1="Recommended Foods: "+ str(recommendList)
list2="Avoided Foods: "+ str(avoidanceList)

#Here we are returning two type of food List:Recommended foods to be eaten and foods to be avoided
def returnList():
   return list1,list2
