package com.example.cdap_nutritiontracking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cdap_nutritiontracking.Meal;

import java.util.ArrayList;
import java.util.UUID;

public class FoodDiaryService extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "SMKitchenDB.db";


    // Doctor table name
    private static final String TABLE_MEALS = "Meal";

    // Doctor Table Columns names
    private static final String KEY_mealId = "mealId";
    private static final String KEY_mealDate = "mealDate";
    private static final String KEY_mealType = "mealType";
    private static final String KEY_foodName = "foodName";
    private static final String KEY_calories = "calories";
    private static final String KEY_mainImage = "mainImage";
    private static final String KEY_vitamins = "vitamins";
    private static final String KEY_minerals = "minerals";



    private static final String[] COLUMNS1 = {KEY_mealId, KEY_mealDate, KEY_mealType, KEY_foodName,KEY_calories, KEY_mainImage,KEY_vitamins,KEY_minerals};

    public FoodDiaryService(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create Meal table
        String CREATE_MEAL_TABLE = "CREATE TABLE Meal ( " +
                "mealId  TEXT PRIMARY KEY , " +
                "mealDate TEXT, " +
                "mealType TEXT, " +
                "foodName TEXT, " +
                "calories TEXT, " +
                "mainImage BLOB, " +
                "vitamins BLOB, " +
                "minerals BLOB )";


        db.execSQL(CREATE_MEAL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS Doctor");
        // create all tables
        this.onCreate(db);
    }
    //---------------------------------------------------------------------




    public void addMeal(Meal meal) {

         UUID uuid = UUID.randomUUID();
         String mealId= uuid.toString();
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_mealId,mealId);
        values.put(KEY_mealDate, meal.date);
        values.put(KEY_mealType, meal.mealType);
        values.put(KEY_foodName, meal.foodName);
        values.put(KEY_calories, meal.calories);
        values.put(KEY_mainImage,meal.mainImage );
        values.put(KEY_vitamins,meal.vitamins);
        values.put(KEY_minerals,meal.minerals);

        // 3. insert record into meals table
        db.insert(TABLE_MEALS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }
    //Return Meal list based on food type
    public void getMealList(ArrayList<Meal> list,String mealType,String mealDate){
             
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Meal meal;
        Cursor cursor =db.query(TABLE_MEALS, // a. table
                        COLUMNS1, // b. column names
                        "mealDate = ? and mealType=?", // c. selections
                        new String[] { String.valueOf(mealDate),String.valueOf(mealType) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit
       if(cursor.getCount()>0){
            while (cursor.moveToNext()) {
            {
               meal=new Meal(cursor.getString(1),cursor.getString(2));
               meal.setFoodId(cursor.getString(0));
               meal.setFoodName(cursor.getString(3));
               meal.setCalories(cursor.getString(4));
               meal.setmainImage(cursor.getBlob(5));
               //To store bitmap
               meal.setVitamins(cursor.getBlob(6));
               meal.setMinerals(cursor.getBlob(7));

               list.add(meal);
           }
        }
       }else{
           return;
       }    
       cursor.close();

    }
    public void deleteMeal(Meal m) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete record of meal based on food id
        db.delete(TABLE_MEALS,
                KEY_mealId+" = ?",
                new String[] { String.valueOf(m.foodId) });

        // 3. close
        db.close();



    }

}