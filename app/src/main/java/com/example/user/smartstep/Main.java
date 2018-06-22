package com.example.user.smartstep;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;


public class Main extends AppCompatActivity implements SensorEventListener{

    //Main activity(displaying number of steps walked, goal, calories burned, distance walked, Pie Chart, weight and bmi)
    DBHelper theDB;
    String username;
    SensorManager aSensorManager;
    Sensor countSensor;
    boolean running = false;
    TextView stepsWalked,calories,km;
    PieChart pieChart;
    float goal;
    float counter;
    PieDataSet set;
    PieData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences pref = getSharedPreferences("userInfo", MODE_PRIVATE);
        username = pref.getString("username","");

        //assigning the last recorded value of the counter in SharedPreference
        counter = getPrefCounter();

        TextView weight = (TextView)findViewById(R.id.textViewWeight);
        TextView bmi = (TextView)findViewById(R.id.textViewBMI);
        TextView goalSteps = (TextView)findViewById(R.id.textViewGoalSteps);
        stepsWalked = (TextView)findViewById(R.id.textViewStepsWalked);
        calories = (TextView)findViewById(R.id.textViewCal);
        km = (TextView)findViewById(R.id.textViewKM);
        theDB = new DBHelper(this);

        //assigning the sensor to be of type step counter
        aSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        countSensor = aSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        //assigning the last recorded values for steps, km and calories so that the fields are not empty until the sensor detects movement
        stepsWalked.setText(String.valueOf(Math.round(pref.getFloat("steps", 0))));
        km.setText(String.format("%.02f",pref.getFloat("currentKM",0)));
        calories.setText(pref.getString("calories",null));
        //fetching user's data based on username
        ArrayList values = theDB.getUserData(username);
       /* if(values.isEmpty()){
            goalSteps.setText("Something is TERRIBLY WRONG!");
        } */
       //assign the steps walked to the last recorded value of steps in shared pref
        weight.setText(values.get(1).toString());//assigning the user's weight to the TextView
        goalSteps.setText(values.get(0).toString());
        //calculating user's height(square m)
        double sqrHeight = (Double.parseDouble(values.get(2).toString()) / 100) * (Double.parseDouble(values.get(2).toString()) / 100);
        //calculating user's BMI(body mass index)
        double calculateBMI = Double.parseDouble(values.get(1).toString()) / sqrHeight;

        //formatting the bmi value for better viewing through the use of BigDecimal
        BigDecimal bd = new BigDecimal(calculateBMI);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        double result = bd.doubleValue();
        bmi.setText(Double.toString(result));//assigning the bmi value to the TextView

        goal = Float.parseFloat(values.get(0).toString());//assigning user's activity goal number
        //creating the list of pie entries required for the pie chart
        List<PieEntry> pieEntries = new ArrayList<>();
        //two entries, the goal(number of steps set by the user) and steps(current number of steps)
        //as the number of steps increase, the goal value decrease(simulating that the user is getting closer to his goal)
        pieEntries.add(new PieEntry(goal - pref.getFloat("steps",0),""));
        pieEntries.add(new PieEntry(pref.getFloat("steps",0),""));

        set = new PieDataSet(pieEntries,"");
        set.notifyDataSetChanged();
        //choosing the desired data set colors
        set.setColors(0xFF000000,Color.parseColor("#2196F3"));
        data = new PieData(set);
        data.setDrawValues(false);

        //creating the Pie Chart and customising it's appearance
        pieChart = (PieChart)findViewById(R.id.pieChart);
        pieChart.setHoleRadius(90f);
        pieChart.setHoleColor(Color.parseColor("#CCCCCC"));
        pieChart.getDescription().setEnabled(false);
        pieChart.setRotationEnabled(false);



        pieChart.setData(data);//setting the pie chart data with the pie data created above
        pieChart.invalidate();//drawing the pie chart
    }

    //onClick methods for the navigation buttons
    public void goMain(View v){
        Intent in;
        in = new Intent(this, Main.class);
        startActivity(in);
        this.finish();
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

    //Disable back button of the device on which the app is running
    @Override
    public void onBackPressed() {

    }

    //whenever the sensor detects a step(device is moved)
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(running) {

            SharedPreferences pref = getSharedPreferences("userInfo", MODE_PRIVATE);

            //When the counter reaches goal(goal steps set by the user),do the following and reset counter
            if(counter==goal){

                //Creating a notification simply saying that the activity goal has been reached
                NotificationCompat.Builder notBuilder =(NotificationCompat.Builder)new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.stat_notify_error)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                        .setContentTitle("Smart Step")
                        .setContentText("Congratulations, you have reached your goal!");
                notBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
                NotificationManagerCompat notManager = NotificationManagerCompat.from(this);
                notManager.notify(1, notBuilder.build());

                //increment goalsReached pref by 1(keep a record of the number of times the users has reached his goal
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("goalsReached",pref.getInt("goalsReached",0)+1);
                editor.putFloat("totalKmWalked",pref.getFloat("totalKmWalked",0)+pref.getFloat("currentKM",0));//saving current km walked to keep a record of total km walked
                editor.apply();
                //resetting the counter
                counter = 0;
            }

                SharedPreferences.Editor editor = pref.edit();
                //incrementing the counter by one every time the sensor detects a step
                counter = counter + 1;
                //saving the counter value in shared preferences
                editor.putFloat("steps", counter);
                editor.apply();
                //assigning the text value of the stepsWalked, km and calories TextViews here, so that they are updated as soon as a step is registered
                //(no need to refresh the activity)
                //NOTE! Because there is no sensor on the virtual device(emulator) these text views are blank
                stepsWalked.setText(String.valueOf(Math.round(pref.getFloat("steps", 0))));
                km.setText(calculateDistance(counter));
                calories.setText(calculateCalories(calculateDistance(counter)));

                //Using the same code as in the onCreate but with a new pie entry list in order to update/redraw the pie chart with every step detected
                List<PieEntry> pieEntries2 = new ArrayList<>();
                pieEntries2.add(new PieEntry(goal - pref.getFloat("steps",0),""));
                pieEntries2.add(new PieEntry(pref.getFloat("steps",0),""));
                set = new PieDataSet(pieEntries2,"");
                set.setColors(0xFF000000,Color.parseColor("#2196F3"));
                set.notifyDataSetChanged();
                data = new PieData(set);
                data.setDrawValues(false);
                data.notifyDataChanged();
                pieChart.setData(data);
                pieChart.notifyDataSetChanged();
                pieChart.invalidate();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        running = true;
        //if statement check if the device supports such sensor
        //NOTE!When running on virtual device this will always be null, it works on real phone
        if(countSensor!=null){
            aSensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        }
        else{
            Toast.makeText(Main.this, "Sensor not found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        running = false;
    }

    //method used for calculating the distance walked(in km) based on the number of steps
    public String calculateDistance(Float stepNum){
        String result = "";
        float km = stepNum/1320;
        //saving the current km walked in shared pref to be added(in total km walked) later on when the counter is reset
        SharedPreferences pref = getSharedPreferences("userInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat("currentKM",km);
        editor.apply();
        result = String.format("%.02f",km);//formatting for better viewing
        return result; //returning the km distance as a string to be used in TextView
    }

    //method for calculating the calories burned based on the user's weight and distance walked
    public String calculateCalories(String dist){
        ArrayList values = theDB.getUserData(username);
        double distance = Double.valueOf(dist);
        double weight = Double.valueOf(values.get(1).toString());
        double formula = (weight*3) * distance;
        String result = String.valueOf(Math.round(formula));
        SharedPreferences pref = getSharedPreferences("userInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("calories",result);
        editor.apply();
        return result;
    }

    //method used in the onCreate to assign the counter to the value stored in shared preferences
    public float getPrefCounter(){
        float result;
        SharedPreferences pref = getSharedPreferences("userInfo", MODE_PRIVATE);
        result = pref.getFloat("steps",0);
        return result;
    }
}
