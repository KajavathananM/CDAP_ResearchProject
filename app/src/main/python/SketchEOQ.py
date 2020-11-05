
#from tkinter import Tk,Label,Canvas,Entry,Button,Frame,ttk

#Libraries for plotting graph and loading dataset
import pandas as pd
import matplotlib.pyplot as plt
#from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
import numpy as np
from numpy import *
from io import BytesIO
import io
#EOQData=pd.read_csv('F:\\NutritionImageClassifierCDAP\\'+'EOQGraph.csv')
#print(EOQData.head())

#df=pd.DataFrame(EOQData)
#print(df)
'''

def graph(formula, x_range,color):  
    x = np.array(x_range)  
    y = eval(formula)
    plt.plot(x, y,color) 
'''   
    
'''
plt.ylabel('Cost')
plt.xlabel('Quantity')
graph('1/x', range(1, 100),'r')
#graph('x', range(1, 100),'b')
#graph('x+(1/x)', range(1, 100),'g')
plt.show()    
'''


'''
q = linspace(0, 10, 100)
#x = np.array(range(1,100))

a = eval('q')
b = eval('(1/q)+2')
c = eval('q+(1/q)')
plt.plot(q, a, 'r') # plotting t, a separately 
plt.plot(q, b, 'b') # plotting t, b separately 
plt.plot(q, c, 'g') # plotting t, c separately 
plt.show()
'''

#Make eoq graph for three costs
def sketchEOQGraph():
   unitPrice=240
   #d=320
   d=2500
   #k=180
   k=150
   #q = linspace(0, 160, 1000)
   q = linspace(100, 1000, 1000)
  
   #Formulas for three costs in EOQ graph
   oCost=k*(d/q)
   h=3
   hCost=h*(q/2)
   tCost=oCost+hCost


   a = eval('oCost')
   b = eval('hCost')
   c = eval('tCost')

   bio=io.BytesIO()
   plt.figure('EOQ')
   plt.ylabel('Cost')
   plt.xlabel('Quantity')
   plt.plot(q, a, 'r',label="Ordering Cost") # plotting Ordering Cost 
   plt.plot(q, b, 'b',label="Holding Cost") # plotting Holding Cost 
   plt.plot(q, c, 'g',label="Total Cost") # plotting total Cost
   plt.legend(loc="upper right")
   plt.show()
   plt.savefig(bio,format="png")
   eoqGraph=bio.getvalue()
   plt.clf()
   return eoqGraph

#sketchEOQGraph()