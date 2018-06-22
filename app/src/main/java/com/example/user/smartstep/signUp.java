package com.example.user.smartstep;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class signUp extends AppCompatActivity implements View.OnClickListener{
    //signUp activity(registration page) used to create a new account
    DBHelper theDB;
    EditText username,password,email,age,height,weight;
    Button signUpBut;
    Spinner gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//back button in the action bar to previous activity

        //creating the required objects in order to take user's information
        username = (EditText)findViewById(R.id.usernameField);
        password = (EditText)findViewById(R.id.passwordField);
        email = (EditText)findViewById(R.id.emailField);
        age = (EditText)findViewById(R.id.ageField);
        height = (EditText)findViewById(R.id.heightField);
        weight = (EditText)findViewById(R.id.weightField);
        gender = (Spinner)findViewById(R.id.genderList);

        theDB = new DBHelper(this);

        signUpBut = (Button)this.findViewById(R.id.signBut);
        signUpBut.setOnClickListener(this);
    }

    //when the sign up button is clicked
    public void onClick(View v){

        //check if any field was left empty, if so notify the user via Toast
        if(username.getText().toString().equals("")||password.getText().toString().equals("")||email.getText().toString().equals("")||age.getText().toString().equals("")||height.getText().toString().equals("")||weight.getText().toString().equals("")) {

            Toast.makeText(signUp.this, "All fields must be filled!", Toast.LENGTH_LONG).show();
        }
        else {

            //check if the entered username is available(not taken), the database does not allow duplicate usernames
            //if the username is available try to insert tha data
            if (theDB.isAvailable(username.getText().toString())) {
                boolean isInserted = theDB.insertData(username.getText().toString(), password.getText().toString(), email.getText().toString(), Integer.parseInt(age.getText().toString()), gender.getSelectedItem().toString(), Integer.parseInt(height.getText().toString()), Integer.parseInt(weight.getText().toString()));
                //if the data was inserted, notify with a Toast and take the user to the login screen
                if (isInserted) {
                    Toast.makeText(signUp.this, "Registered Successfully", Toast.LENGTH_LONG).show();
                    Intent in;
                    in = new Intent(this, Home.class);
                    startActivity(in);
                    this.finish();
                }
                //if the data insert failed, display a Toast
                else {
                    Toast.makeText(signUp.this, "Sign up FAILED", Toast.LENGTH_LONG).show();
                }
            }
            //if the username is taken, display a Toast to the user
            else {
                Toast.makeText(signUp.this, "This username is already taken", Toast.LENGTH_LONG).show();
            }
        }
    }

    //Disable back button of the device on which the app is running
    @Override
    public void onBackPressed() {

    }
}
