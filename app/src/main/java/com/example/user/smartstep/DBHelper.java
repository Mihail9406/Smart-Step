package com.example.user.smartstep;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;



public class DBHelper extends SQLiteOpenHelper { //custom SQLiteOpenHelper class
    //creating a few constant strings to be used later on
    public final static String DATABASE_NAME = "Users.db";
    public final static String TABLE_NAME = "user_table";
    //public final static String COL_2 = "GoalSteps";
    public final static String COL_1 = "Username";
    public final static String COL_3 = "Password";
    public final static String COL_4 = "Email";
    public final static String COL_5 = "Age";
    public final static String COL_6 = "Sex";
    public final static String COL_7 = "Height";
    public final static String COL_8 = "Weight";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 7);
    }

    //creating the tables in which the registered users are stored
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (Username TEXT PRIMARY KEY,GoalSteps INTEGER DEFAULT 10000,Password TEXT,Email TEXT,Age INTEGER,Sex TEXT,Height INTEGER,Weight INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //Used when signing up, populates user_table
    public boolean insertData(String username,String password,String email,int age,String sex,int height,int weight){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,username);
        contentValues.put(COL_3,password);
        contentValues.put(COL_4,email);
        contentValues.put(COL_5,age);
        contentValues.put(COL_6,sex);
        contentValues.put(COL_7,height);
        contentValues.put(COL_8,weight);
        long output = db.insert(TABLE_NAME,null,contentValues);
        db.close();
        if(output == -1)
            return false;
        else
            return true;
    }

    //Search through the user_table to find the user's password based on username.
    //If the username given as s parameter is not found in the database, return "not found"
    //If the user exists, return his password
    public String loginSearch(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String querry = "select Username, Password from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(querry, null);
        String uname, pass;
        pass = "not found";
        if(cursor.moveToFirst()){
            do{
                uname = cursor.getString(0);

                if(uname.equals(username)){
                    pass = cursor.getString(1);
                    break;
                }
            }
            while(cursor.moveToNext());
        }
        db.close();
        return pass;
    }

    //used when signing up to check if the username is taken(existing in the database)
    public boolean isAvailable(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        String querry = "select Username from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(querry, null);
        String uname;
        boolean result = true;
        if(cursor.moveToFirst()){
            do{
                uname = cursor.getString(0);
                if(uname.equals(username)){
                    result = false;
                    break;
                }
            }
            while(cursor.moveToNext());
        }
        db.close();
        return result;
    }

    //method used for retrieving user's data from the database for further manipulation(based on username)
    //returns an ArrayList with data specified in the querry
    public ArrayList getUserData(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        String querry = "select Username, GoalSteps, Weight, Height, Age, Sex from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(querry, null);
        String uname,gender;
        int steps,weight,height,age;
        ArrayList userValues = new ArrayList();
        if(cursor.moveToFirst()){
            do{
                uname = cursor.getString(0);
                if(uname.equals(username)){
                    steps = cursor.getInt(1);
                    weight = cursor.getInt(2);
                    height = cursor.getInt(3);
                    age = cursor.getInt(4);
                    gender = cursor.getString(5);
                    userValues.add(steps);
                    userValues.add(weight);
                    userValues.add(height);
                    userValues.add(age);
                    userValues.add(gender);
                    break;
                }
            }
            while(cursor.moveToNext());
        }
        db.close();
        return userValues;
    }

    //used in the settings activity when the user changes his information
    public void updateData(String username,int goal,int weight,int height,int age){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("GoalSteps",goal);
        contentValues.put("Weight",weight);
        contentValues.put("Height",height);
        contentValues.put("Age",age);
        db.update(TABLE_NAME, contentValues, "Username = ?", new String[]{username});
        db.close();
    }

}
