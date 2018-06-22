package com.example.user.smartstep;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Settings extends AppCompatActivity implements View.OnClickListener {
   //Settings activity(the users is able to update/change his personal information)
    Spinner activityGoal;
    Button update;
    EditText weight,height,age;
    DBHelper theDB;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//back button in the action bar to previous activity
        //creating the needed objects for this activity
        activityGoal = (Spinner)findViewById(R.id.spinnerSteps);
        update = (Button)findViewById(R.id.buttonUpdate);
        weight = (EditText)findViewById(R.id.editTextWeight);
        height = (EditText)findViewById(R.id.editTextHeight);
        age = (EditText)findViewById(R.id.editTextAge);
        theDB = new DBHelper(this);
        SharedPreferences pref = getSharedPreferences("userInfo", MODE_PRIVATE);
        username = pref.getString("username","");//getting the username from the shared preferences
        update.setOnClickListener(this);
    }
    //when the update button is clicked
    public void onClick(View v) {

        //check if any field was left empty(without this check the database querry can crash the app)
        //if there is an empty field notify the user via a Toast
        if(weight.getText().toString().equals("")||height.getText().toString().equals("")||age.getText().toString().equals("")){
            Toast.makeText(Settings.this, "All fields must be filled!", Toast.LENGTH_LONG).show();
        }
        //if every field has a value call the updateData method on the database and pass as parameters the values in those fields
        //and display a Toast for successful update
        else{
            theDB.updateData(username,Integer.parseInt(activityGoal.getSelectedItem().toString()),
                    Integer.parseInt(weight.getText().toString()),Integer.parseInt(height.getText().toString()),
                    Integer.parseInt(age.getText().toString()));
            Toast.makeText(Settings.this, "Update successful!", Toast.LENGTH_LONG).show();
        }
    }

    //Disable back button of the device on which the app is running
    @Override
    public void onBackPressed() {

    }
}
