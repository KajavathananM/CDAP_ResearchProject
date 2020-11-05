#Here I am load Nutritional values
import pandas as pd
import numpy as np
from os.path import dirname, join,abspath

threeNutrientsList= []
vitaminList = [] 
mineralList = [] 

csvFiles = [] 

totals1=np.reshape(3,1)
totals2=np.reshape(7,1)
totals3=np.reshape(8,1)

class ThreeNutrients:
    def __init__(self,carb,fat,protein):
        self.carb=carb
        self.fat=fat
        self.protein=protein

class Vitamins:
    def __init__(self,vitaminA,vitaminB6,vitaminB12,vitaminC,vitaminD,vitaminE,vitaminK):
        self.vitaminA=vitaminA
        self.vitaminB6=vitaminB6
        self.vitaminB12=vitaminB12
        self.vitaminC=vitaminC
        self.vitaminD=vitaminD
        self.vitaminE=vitaminE
        self.vitaminK=vitaminK

class Minerals:
    def __init__(self,fluoride,calcium,sodium,potassium,iron,phosphorus,magnesium,zinc):
        self.fluoride=fluoride
        self.calcium=calcium
        self.sodium=sodium
        self.potassium=potassium
        self.iron=iron
        self.phosphorus=phosphorus
        self.magnesium=magnesium
        self.zinc=zinc


def retrieveNutrientsData(foodList):
    for foodName in foodList: 
        fileName=foodName+'.csv'
        csvFiles.append(fileName) 
    loadThreeNutrientsList(csvFiles)
    totals1=calNutrientsTotal(threeNutrientsList)
    tC=totals1[0]
    tF=totals1[1]
    tP=totals1[2]
    return tC,tF,tP

def calNutrientsTotal(threeNutrientsList):
    totalCarb =0
    totalFat =0
    totalProtein =0
    for elem in threeNutrientsList:
       totalCarb+=elem.carb
       totalFat+=elem.fat
       totalProtein+=elem.protein
       totals1=[totalCarb,totalFat,totalProtein]
       totals1=np.array(totals1)
    return totals1


def loadThreeNutrientsList(csvFiles):
    for filename in csvFiles:
        nDatasetPath=join(dirname(__file__),filename)
        nutritionData=pd.read_csv(nDatasetPath)
        carbohyrate = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Carbohydrate"].index,1].squeeze()
        fat= nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Fat"].index,1].squeeze()
        protein = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Protein"].index,1].squeeze()
        
        threeNutrientsList.append(ThreeNutrients(carbohyrate,fat,protein))
    
def retrieveVitaminsData(foodList):
    for foodName in foodList: 
        fileName=foodName+'.csv'
        csvFiles.append(fileName) 
    loadVitaminsList(csvFiles)
    totals2=calVitaminsTotal(vitaminList)
    tVA=totals2[0]
    tVB6=totals2[1]
    tVB12=totals2[2]
    tVC=totals2[3]
    tVD=totals2[4]
    tVE=totals2[5]
    tVK=totals2[6]
    return tVA,tVB6,tVB12,tVC,tVD,tVE,tVK

def loadVitaminsList(csvFiles):
      for filename in csvFiles:
        nDatasetPath=join(dirname(__file__),filename)
        nutritionData=pd.read_csv(nDatasetPath)
        vitaminA = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Vitamin A"].index,1].squeeze()/((10**6)*3.33)
        vitaminB6 = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Vitamin B6"].index,1].squeeze()/(10**3)
        vitaminB12 = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Vitamin B12"].index,1].squeeze()/((10**6)*3.33)
        vitaminC = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Vitamin C"].index,1].squeeze()/(10**3)
        vitaminD=nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Vitamin D"].index,1].squeeze()/((10**6)*3.33)
        vitaminE=nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Vitamin E"].index,1].squeeze()/(10**3)
        vitaminK=nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Vitamin K"].index,1].squeeze()/((10**6)*3.33)
      vitaminList.append(Vitamins(vitaminA,vitaminB6,vitaminB12,vitaminC,vitaminD,vitaminE,vitaminK))
     
def calVitaminsTotal(vitaminList):
    totalVA =0
    totalVB6 =0
    totalVB12 =0
    totalVC =0
    totalVD =0
    totalVE =0
    totalVK =0
    for elem in vitaminList:
       totalVA+=elem.vitaminA
       totalVB6+=elem.vitaminB6
       totalVB12+=elem.vitaminB12
       totalVC+=elem.vitaminC
       totalVD+=elem.vitaminD 
       totalVE+=elem.vitaminE 
       totalVK+=elem.vitaminK 
       totals2=[totalVA,totalVB6,totalVB12,totalVC,totalVD,totalVE,totalVK]
       totals2=np.array(totals2)
    return totals2

def retrieveMineralsData(foodList):
    for foodName in foodList: 
        fileName=foodName+'.csv'
        csvFiles.append(fileName) 
    loadMineralsList(csvFiles)
    totals3=calMineralsTotal(mineralList)
    tf=totals3[0]
    tc=totals3[1]
    ts=totals3[2]
    tp=totals3[3]
    ti=totals3[4]
    tph=totals3[5]
    tm=totals3[6]
    tz=totals3[7]
    return tf,tc,ts,tp,ti,tph,tm,tz

def loadMineralsList(csvFiles):
  for filename in csvFiles:
    nDatasetPath=join(dirname(__file__),filename)
    nutritionData=pd.read_csv(nDatasetPath)
    fluoride = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Fluoride, F"].index,1].squeeze()/(10**6)
    calcium = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Calcium, Ca"].index,1].squeeze()/(10**3)
    sodium = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Sodium, Na"].index,1].squeeze()/(10**3)
    potassium = nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Potassium, K"].index,1].squeeze()/(10**3)
    iron=nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Iron, Fe"].index,1].squeeze()/(10**3)
    phosphorus=nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Phosphorus, P"].index,1].squeeze()/(10**3)
    magnesium=nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Magnesium, Mg"].index,1].squeeze()/(10**3)
    zinc=nutritionData.iloc[nutritionData.loc[nutritionData['Nutrient'] == "Zinc, Zn"].index,1].squeeze()/(10**3) 
  mineralList.append(Minerals(fluoride,calcium,sodium,potassium,iron,phosphorus,magnesium,zinc))

def calMineralsTotal(mineralList):
    totalfluoride =0
    totalcalcium =0
    totalsodium =0
    totalpotassium =0
    totaliron =0
    totalphosphorus =0
    totalmagnesium =0
    totalzinc =0
    for elem in mineralList:
       totalfluoride+=elem.fluoride
       totalcalcium+=elem.calcium
       totalsodium+=elem.sodium
       totalpotassium+=elem.potassium
       totaliron+=elem.iron 
       totalphosphorus+=elem.phosphorus 
       totalmagnesium+=elem.magnesium 
       totalzinc+=elem.zinc 
       totals3=[totalfluoride,totalcalcium,totalsodium,totalpotassium,totaliron,totalphosphorus,totalmagnesium,totalzinc]
       totals3=np.array(totals3)
    return totals3
  
#print(retrieveNutrientsData(['Apple','Banana']))
#print(retrieveVitaminsData(['Apple','Banana']))
print(retrieveMineralsData(['Apple','Banana']))

