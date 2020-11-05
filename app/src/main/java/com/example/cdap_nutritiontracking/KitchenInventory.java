package com.example.cdap_nutritiontracking;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.View;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.List;

public class KitchenInventory extends AppCompatActivity {

    private static final String TAG = "KitchenInventory";
    private CardArrayAdapter cardArrayAdapter;
    private Card card;
    private ListView listView;

    private ArrayList<Food> foodList;
    FoodService foodService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        foodService=new FoodService();
        //foodList=foodService.getFoodList();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen_inventory);
        listView = (ListView) findViewById(R.id.card_listView);

        listView.addHeaderView(new View(this));
        listView.addFooterView(new View(this));

        cardArrayAdapter = new CardArrayAdapter(getApplicationContext(), R.layout.list_item_card);
        foodService.makeList(cardArrayAdapter,card,listView,this);
        //foodService.makeGroceryList(this);
        //cardArrayAdapter.notifyDataSetChanged();  
    }
}
