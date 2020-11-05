package com.example.cdap_nutritiontracking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemDatesActivity extends AppCompatActivity {
    private ItemCardArrayAdapter itemCardArrayAdapter;
    private ListView listView;
    private TextView foodLabel;
    private ImageView foodPic;

      private Food food;
      private ArrayList<Dates> item_dates;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_dates);
        
         foodLabel=(TextView)findViewById(R.id.foodLabel);
         foodPic=(ImageView)findViewById(R.id.foodPic);
         
         if(getIntent().getExtras() != null) {
            food = (Food) getIntent().getSerializableExtra("food_item");
            item_dates=food.getDates();
         }
         foodLabel.setText(food.getFoodName());
         Picasso.get().load(food.getFoodURL()).into(foodPic);

        listView = (ListView) findViewById(R.id.card_listView);

        listView.addHeaderView(new View(this));
        listView.addFooterView(new View(this));

        itemCardArrayAdapter = new ItemCardArrayAdapter(getApplicationContext(), R.layout.list_itemdates_card);

        for (int i = 0; i < item_dates.size(); i++) {
            Card card = new Card(item_dates.get(i));
            itemCardArrayAdapter.add(card);
        }
        listView.setAdapter(itemCardArrayAdapter);
    }
}
