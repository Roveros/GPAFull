package com.gpa.browne.gpafull;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class GoalsActivity extends AppCompatActivity {

    private TextView tvSetDate;
    private String date, time, topic;
    private GPAGoalModel model;
    private EditText etGoalDaily, etGoalWeekly;
    private CheckBox chbxSubjectComplete;

    //etGoalDaily, etGoalWeekly, tvSetDate, chbxSubjectComplete

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //this places a back button on the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        tvSetDate = (TextView) findViewById(R.id.tvSetDate);
        etGoalDaily = (EditText) findViewById(R.id.etGoalDaily);
        etGoalWeekly = (EditText) findViewById(R.id.etGoalWeekly);
        chbxSubjectComplete = (CheckBox) findViewById(R.id.chbxSubjectComplete);
        chbxSubjectComplete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(tvSetDate.getText().toString().equals("DD/MM/YYYY HH:MM")){
                    chbxSubjectComplete.setChecked(false);
                    Toast.makeText(GoalsActivity.this, "You cannot set topic as complete without a deadline date", Toast.LENGTH_SHORT).show();
                }
            }
        });

        date = "DD/MM/YY";
        time = "HH:MM";

        Intent intent = getIntent();
        topic = intent.getStringExtra("topic");
        model = new GPAGoalModel(getApplicationContext(), topic);
        //date@1@8@10/03/2018@11:55@0
        String settings[] = model.getGoals();
        etGoalDaily.setText(settings[1]);
        etGoalWeekly.setText(settings[2]);
        if((settings[4].charAt(3) == ':')){
            settings[4] = settings[4] + "0";
        }
        tvSetDate.setText(settings[3] + " " + settings[4]);
        if(settings[5].equals("1")){
            chbxSubjectComplete.setChecked(true);
        } else {
            chbxSubjectComplete.setChecked(false);
        }
    }

    //This override is needed to allow for a back button on the toolbar for this activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            //Save non-default settings or use explcit save? Save then toast?
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onHelpClick(View view) {
        Toast.makeText(getApplicationContext(), "Set your Daily/Weekly goals here to start earning Goal badges!",Toast.LENGTH_LONG).show();
    }

    //init goals to saved settings

    public void onSaveClick(View view){
        String dailyGoal, weeklyGoal, deadlineDate, deadlineTime, completed;
        dailyGoal = etGoalDaily.getText().toString();
        weeklyGoal = etGoalWeekly.getText().toString();
        //Log.i("INFO", tvSetDate)
        String dateTime[] = tvSetDate.getText().toString().split("\\s+");
        Log.i("INFO", Arrays.toString(dateTime));
        deadlineDate = dateTime[0];
        deadlineTime = dateTime[1];

        if (chbxSubjectComplete.isChecked()){
            completed = "1";
        } else {
            completed = "0";
        }

        model.saveSettings(dailyGoal, weeklyGoal, deadlineDate, deadlineTime, completed);
        Toast.makeText(getApplicationContext(), "Saving ... ",Toast.LENGTH_SHORT).show();
        finish();

    }

    public void onClickShowTimePicker(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");

    }

    public void onClickShowDatePicker(View view) {
        onClickShowTimePicker(view);
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void setDateDisplay(String date){
       this.date = date;
       tvSetDate.setText(this.date + " " + time);
    }

    public void setTimeDisplay(String time){
        this.time = time;
        if((time.charAt(3) == ':')){
            time = time + "0";
        }
        tvSetDate.setText(date + " " + this.time);
    }

    public void onBet(View view) {
        //Put up the Yes/No message box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("24 Hour Bet")
                .setMessage("Would you like to bet 100 XP that this topic will be completed 24 hours before its deadline? \nPrize: 200 XP")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Yes button clicked, do something
                        Toast.makeText(GoalsActivity.this, "Yes button pressed",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)						//Do nothing on no
                .show();
    }


    //set daily goal
    //set weekly goal
    //set deadline date
    //set topic complete


}
