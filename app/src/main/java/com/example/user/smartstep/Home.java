package com.example.user.smartstep;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Home extends AppCompatActivity implements View.OnClickListener {
    //Home activity/initial activity(this is the login page)
    DBHelper theDB;
    String username;
    protected void onCreate(Bundle savedInstanceState) {

        //creating the required buttons for this activity
        Button regBut;
        Button loginBut;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        theDB = new DBHelper(this);

        loginBut = (Button) this.findViewById(R.id.loginBut);
        loginBut.setOnClickListener(this);
        regBut = (Button) this.findViewById(R.id.regBut);
        regBut.setOnClickListener(this);
    }

    public void onClick(View v) {
        Intent in;
        //two if statements checking which button is pressed
        if (v.getId() == R.id.regBut) {//if sign up button is pressed go to signUp activity

            in = new Intent(this, signUp.class);
            startActivity(in);

        }
        if (v.getId() == R.id.loginBut) {//if login button is pressed perform the following

            EditText name = (EditText) findViewById(R.id.userText);
            EditText pass = (EditText) findViewById(R.id.passText);
            //take the values from the EditText fields,save the username and password
            username = name.getText().toString();
            String password = pass.getText().toString();

            //save the username in shared preferences to be used in other activities
            SharedPreferences pref = getSharedPreferences("userInfo", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("username",name.getText().toString());
            editor.apply();

            //creating a string checkPass to hold the user's password based on the username entered
            //if the username does not exist in the database - checkPass = "not found"
            String checkPass = theDB.loginSearch(username);
            //comparing the password entered to the password stored in the database
            //if it matches the user successfully logs in and is directed to the Main activity
            if (password.equals(checkPass)) {
                in = new Intent(this, Main.class);
                startActivity(in);
                this.finish();
            }
            //if the password does not match, an appropriate Toast is displayed
            else{
                Toast fail = Toast.makeText(Home.this, "Username and Password do not match!", Toast.LENGTH_SHORT);
                fail.show();
            }
        }
    }

    //Overriding the function of the device back button so that it closes the application when pressed
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
