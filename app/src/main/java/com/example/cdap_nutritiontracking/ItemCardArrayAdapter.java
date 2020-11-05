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

//Display list of mf dates and expired dates if quantity is greater than one.
public class ItemCardArrayAdapter  extends ArrayAdapter<Card> {
    private static final String TAG = "CardArrayAdapter";
    private List<Card> cardList = new ArrayList<Card>();
  

    static class CardViewHolder {
        TextView labelMfDate;
        TextView expDate;
        TextView mfDate;
        TextView labelExpDate;
    }

    public ItemCardArrayAdapter(Context context, int textViewResourceId) {
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
            row = inflater.inflate(R.layout.list_itemdates_card, parent, false);
            viewHolder = new CardViewHolder();
                        
            viewHolder.labelMfDate=(TextView) row.findViewById(R.id.labelMfDate);
            viewHolder.labelExpDate=(TextView) row.findViewById(R.id.labelExpDate);
            viewHolder.mfDate = (TextView) row.findViewById(R.id.mfDate);
            viewHolder.expDate = (TextView) row.findViewById(R.id.expDate);
            
            row.setTag(viewHolder);
        } else {
            viewHolder = (CardViewHolder)row.getTag();
        }
        Card card = getItem(position);
        Dates itemDate=card.getDate();
        viewHolder.mfDate.setText(itemDate.getMfDate());
        viewHolder.expDate.setText(itemDate.getExpDate());

        return row;
     
    }
}
