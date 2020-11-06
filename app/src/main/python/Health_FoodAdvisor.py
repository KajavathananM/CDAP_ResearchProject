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

"""
This Rule Engine suggests Food by checking person's
cholestrol level and diabetic level in blood either normal,low 
or high scenarios and compare with carbohydrate and Fat intake in blood
"""
class SuggestFoodEngine(KnowledgeEngine):
   # Check if diabetic range is normal  but sugar level from food is below our normal blood level to recommend all carbohydrate enriched foods
    @Rule(
            AND(
                NutrientsData(FoodName=MATCH.FoodName),
                PatientData(raise_blood_sugar=MATCH.raise_blood_sugar,allowed_range_sugar=MATCH.allowed_range_sugar),
                PatientData(ent_sugarLvl=P(lambda ent_sugarLvl:ent_sugarLvl>70) & P(lambda ent_sugarLvl:ent_sugarLvl<140))
            )
           
    )
    def CheckIfPersonInNormalDiabeticRange(self,FoodName,allowed_range_sugar,raise_blood_sugar):
       recommendList.append(FoodName)
       
    # Check if diabetic range is above normal and avoid cerau
    @Rule(
            NutrientsData(FoodName=MATCH.FoodName),
            PatientData(ent_sugarLvl=P(lambda ent_sugarLvl:ent_sugarLvl>=140)& P(lambda ent_sugarLvl:ent_sugarLvl<200))
    )
    def CheckifJustAboveDiabeticRangeTrue(self,FoodName,allowed_range_sugar,raise_blood_sugar):
               if(allowed_range_sugar<raise_blood_sugar):
                 if(FoodName not in recommendList):
                     recommendList.append(FoodName)

    # Check if diabetic range is above normal and avoid cerau
    @Rule(
            NutrientsData(FoodName=MATCH.FoodName),
            PatientData(ent_sugarLvl=P(lambda ent_sugarLvl:ent_sugarLvl>=140)& P(lambda ent_sugarLvl:ent_sugarLvl<200))
    )
    def CheckifJustAboveDiabeticRangeFalse(self,FoodName,allowed_range_sugar,raise_blood_sugar):
               if(allowed_range_sugar>raise_blood_sugar):
                  avoidanceList.append(FoodName)
    
    # Check if cholestrol range is  normal
    @Rule(
       OR(
           AND(
            NutrientsData(FoodName=MATCH.FoodName,fatVal=MATCH.fatVal),
            NutrientsData(fatVal=P(lambda fatVal:fatVal>=12) & P(lambda fatVal:fatVal<=15)),
            PatientData(ent_cholestrolLvl=P(lambda ent_cholestrolLvl:ent_cholestrolLvl>200) & P(lambda ent_cholestrolLvl:ent_cholestrolLvl<230))
          ),
          AND(
            NutrientsData(FoodName=MATCH.FoodName,fatVal=MATCH.fatVal),
            NutrientsData(fatVal=P(lambda fatVal:fatVal>=12) & P(lambda fatVal:fatVal<=15)),
            PatientData(ent_cholestrolLvl=P(lambda ent_cholestrolLvl:ent_cholestrolLvl<=200))
          )
       )
    )
    def CheckIfPersonInNormalCholestrolRange(self,FoodName):
        if(FoodName not in recommendList and FoodName not in avoidanceList):
              recommendList.append(FoodName) 
    # Check if cholestrol range is above normal
    @Rule( 
       AND(
            AND(
                 NutrientsData(FoodName=MATCH.FoodName,fatVal=MATCH.fatVal),
                 NutrientsData(fatVal=P(lambda fatVal:fatVal>14))
            ),
            PatientData(ent_cholestrolLvl=P(lambda ent_cholestrolLvl:ent_cholestrolLvl>=230))
       )
    )
    def AvoidHighCholestrolFoods(self,FoodName):
            if(FoodName in recommendList):
              recommendList.remove(FoodName) 
            avoidanceList.append(FoodName)
 
   

    
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
  ent_sugarLvl=110
  CarbValue= (carb/100) * 175
  raise_blood_sugar = CarbValue * 3.5
  allowed_range_sugar = 200 - ent_sugarLvl
  #print("Raised Blood Sugar: "+str(raise_blood_sugar))
  #print("Allowed Range Sugar: "+str(allowed_range_sugar))
  
  #FatValue= (fat/100) * 175
  #print("Carb Value for "+predictedLabel+": "+str(CarbValue))
  #print("Fat Value for "+predictedLabel+": "+str(fat))
  ent_cholestrolLvl=280

 
  engine1.declare(NutrientsData(
                          FoodName=predictedLabel,
                          fatVal=fat,
                          carbVals=CarbValue
                ),
                PatientData(
                           ent_sugarLvl=ent_sugarLvl,
                           raise_blood_sugar=raise_blood_sugar,
                           allowed_range_sugar=allowed_range_sugar,
                           ent_cholestrolLvl=ent_cholestrolLvl
                )
  )      
  engine1.run() 
  engine1.reset()
  

#Read ingredients from Food Item's dataset
ingredientscsvFile='Ingredients.csv'
iListDatasetPath=join(dirname(__file__),ingredientscsvFile)
iList=pd.read_csv(iListDatasetPath, usecols=RecipeLabels)

#This Rule Engine checks if there is an allergic ingredient in food and if the person is allergic to it
class AllergyEngine(KnowledgeEngine):
    #Check if there is an allergic ingredient from the food item
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
          if(FoodName not in recommendList and FoodName not in avoidanceList):
             avoidanceList.append(FoodName)
          elif (FoodName in recommendList and FoodName not in avoidanceList):
              recommendList.remove(FoodName)
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
            engine2.reset()
           


list1="Recommended Foods: "+ str(recommendList)
list2="Avoided Foods: "+ str(avoidanceList)

print(list1)
print(list2)

#Here we are returning two type of food List:Recommended foods to be eaten and  foods to be avoided if they are risk to our health
def returnList():
   return list1,list2
