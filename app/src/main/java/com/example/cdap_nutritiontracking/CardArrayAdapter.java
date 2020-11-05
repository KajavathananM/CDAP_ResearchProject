package com.example.cdap_nutritiontracking;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class CardArrayAdapter  extends ArrayAdapter<Card> {
    private static final String TAG = "CardArrayAdapter";
    public List<Card> cardList = new ArrayList<Card>();
  

    static class CardViewHolder {
        TextView foodName;
        ImageView photo;
        TextView foodQuantity;
        TextView unit;

        TextView labelMfDate;
        TextView expDate;
        TextView mfDate;
        TextView labelExpDate;
        Button btnDates;
    }

    public CardArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public void add(Card object) {
        cardList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return this.cardList.size();
    }

    @Override
    public Card getItem(int index) {
        return this.cardList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CardViewHolder viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_item_card, parent, false);
            viewHolder = new CardViewHolder();
            viewHolder.foodName = (TextView) row.findViewById(R.id.foodName);
            viewHolder.photo=(ImageView) row.findViewById(R.id.foodPic);
            viewHolder.foodQuantity = (TextView) row.findViewById(R.id.quantity);
            viewHolder.unit = (TextView) row.findViewById(R.id.unit);
            
            viewHolder.labelMfDate=(TextView) row.findViewById(R.id.labelMfDate);
            viewHolder.labelExpDate=(TextView) row.findViewById(R.id.labelExpDate);
            viewHolder.mfDate = (TextView) row.findViewById(R.id.mfDate);
            viewHolder.expDate = (TextView) row.findViewById(R.id.expDate);
            viewHolder.btnDates=(Button) row.findViewById(R.id.btnDates);
            row.setTag(viewHolder);
        } else {
            viewHolder = (CardViewHolder)row.getTag();
        }
        Card card = getItem(position);
        final Food food=card.getFood();
        viewHolder.foodName.setText(food.getFoodName());
        viewHolder.foodQuantity.setText(String.valueOf(food.getFoodQuantity()));
        // Display unit type as pack as well as show mf date and exp date for food type sweets
        if(food.getFoodType().equals("sweets")){
                if(food.getFoodQuantity() == 1){
                    viewHolder.labelMfDate.setVisibility(View.VISIBLE);
                    viewHolder.labelExpDate.setVisibility(View.VISIBLE);
                    viewHolder.mfDate.setVisibility(View.VISIBLE);
                    viewHolder.expDate.setVisibility(View.VISIBLE);
                    viewHolder.btnDates.setVisibility(View.INVISIBLE);

                    viewHolder.mfDate.setText(food.getDates().get(0).getMfDate());
                    viewHolder.expDate.setText(food.getDates().get(0).getExpDate());
                    viewHolder.unit.setText(" pack");
                }else if(food.getFoodQuantity() > 1){
                   viewHolder.btnDates.setVisibility(View.VISIBLE);
                   final ArrayList<Dates> dates=food.getDates();
                   viewHolder.btnDates.setOnClickListener(new View.OnClickListener() {
                        @Override
                                public void onClick(View view) {
                                    Intent i=new Intent(getContext(),ItemDatesActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.putExtra("food_item",food);
                                    getContext().startActivity(i);
                                }
                   });
                   
                   viewHolder.labelMfDate.setVisibility(View.INVISIBLE);
                   viewHolder.labelExpDate.setVisibility(View.INVISIBLE);
                   viewHolder.mfDate.setVisibility(View.INVISIBLE);
                   viewHolder.expDate.setVisibility(View.INVISIBLE);
                   viewHolder.unit.setText(" packs");
                }
        // Display unit type as grams as well as hide mf date and exp date for food type sweets
        }else if(food.getFoodType().equals("Vegetable")|| food.getFoodType().equals("Fruit")){
                viewHolder.btnDates.setVisibility(View.INVISIBLE);
                viewHolder.labelMfDate.setVisibility(View.INVISIBLE);
                viewHolder.labelExpDate.setVisibility(View.INVISIBLE);
                viewHolder.mfDate.setVisibility(View.INVISIBLE);
                viewHolder.expDate.setVisibility(View.INVISIBLE);
                viewHolder.unit.setText(" grams");
        }
        Picasso.get().load(food.getFoodURL()).into(viewHolder.photo);
        return row;
     
    }
    public void clearCardList(){
        cardList.clear();
    }
}
