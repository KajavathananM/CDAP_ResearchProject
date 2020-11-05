
package com.example.cdap_nutritiontracking;

import android.graphics.Bitmap;



public class Card {
    private Food food;
    private Dates date;
    public Card(Food food) {
        this.food = food;
    }
    public Card(Dates date) {
        this.date = date;
    }
   

    public Food getFood(){
        return food;
    }
    public Dates getDate(){
        return date;
    }
}
