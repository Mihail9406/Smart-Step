package com.example.user.smartstep;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class Profile extends AppCompatActivity {
    //Profile activity(the user's information, stats overview)
    String username;
    TextView uname;
    DBHelper theDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        SharedPreferences pref = getSharedPreferences("userInfo", MODE_PRIVATE);
        username = pref.getString("username","");
        uname = (TextView)findViewById(R.id.textViewUsername);//creating the text view to hold the username
        uname.setText(username);//assigning the username to the text view

        //creating the required TextViews for this activity
        TextView steps = (TextView)findViewById(R.id.textViewSteps);
        TextView height = (TextView)findViewById(R.id.textViewHeight);
        TextView weight = (TextView) findViewById(R.id.textViewWeight);

        TextView age = (TextView)findViewById(R.id.textViewAge);
        TextView sex = (TextView)findViewById(R.id.textViewSex);
        TextView goalsReached = (TextView)findViewById(R.id.textViewReachGoalNum);
        TextView totalKM = (TextView)findViewById(R.id.textViewTotalKM);
        //TextView avgSteps = (TextView)findViewById(R.id.textViewAverageSteps);
        theDB = new DBHelper(this);
        ArrayList values = theDB.getUserData(username);//fetching user's data from the database based on username

        //assigning the required values to the TextViews
        steps.setText(values.get(0).toString());
        weight.setText(values.get(1).toString());
        height.setText(values.get(2).toString());
        age.setText(values.get(3).toString());
        sex.setText(values.get(4).toString());
        goalsReached.setText(String.valueOf(pref.getInt("goalsReached",0)));
        totalKM.setText(String.format("%.02f",pref.getFloat("totalKmWalked",0)));

      /*  //getting an array list with the user's steps if there are any(saved in steps_table)
        ArrayList<Integer> allSteps = new ArrayList<>();
        if(pref.getBoolean("hasStepsSaved",false)) {
            allSteps = theDB.getUsersSteps(username);
        }
        if(allSteps!=null){//if the array is not empty loop through it, calculata the user's average steps and assign the number in avgSteps
            double total = 0;
            double avg = 0;
            for(int i = 0; i<allSteps.size(); i++){
                total+= allSteps.get(i);
            }
            avg = total/allSteps.size();
            avgSteps.setText(String.valueOf(avg));
        }
        //if the allSteps is empty(user does not have any steps saved yet), assign the number of average steps to 0
        avgSteps.setText(String.valueOf(0)); */

    }
    //onClick methods for the navigation buttons
    public void goMain(View v){
        Intent in;
        in = new Intent(this, Main.class);
        startActivity(in);
    }

    public void goHome(View v){
        Intent in;
        in = new Intent(this, Home.class);
        startActivity(in);
        this.finish();
    }

    public void goProfile(View v){
        Intent in;
        in = new Intent(this, Profile.class);
        startActivity(in);
        this.finish();
    }

    public void goStat(View v){
        Intent in;
        in = new Intent(Profile.this, MapsActivity.class);
        startActivity(in);
    }

    public void goSettings(View v){
        Intent in;
        in = new Intent(this, Settings.class);
        startActivity(in);
    }

    //Disable back button of the device on which the app is running
    @Override
    public void onBackPressed() {

    }
}
