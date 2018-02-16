package com.gpa.browne.gpafull;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;

public class PrizesActivity extends AppCompatActivity {
    EditText etEasyPrize, etMediumPrize, etHardPrize;
    TextView tvBalance;
    String[] prizes;
    int balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prizes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //this places a back button on the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        etEasyPrize = findViewById(R.id.etEasyPrize);
        etMediumPrize = findViewById(R.id.etMediumPrize);
        etHardPrize = findViewById(R.id.etHardPrize);
        tvBalance = findViewById(R.id.tvBalance);

        prizes = new String[]{"","","","0"};
        balance = 0;

        initializeActivity();

    }
    //This override is neede to allow for a back button on the toolbar for this activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            //GPAConfigModel model = new GPAConfigModel(getApplicationContext());
            //model.saveSettings(config[0], config[1] , config[2]);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSetClick(View view) {
        if(!TextUtils.isEmpty(etEasyPrize.getText().toString())){
            prizes[0] = etEasyPrize.getText().toString();
        }
        if(!TextUtils.isEmpty(etMediumPrize.getText())){
            prizes[1] = etMediumPrize.getText().toString();
        }
        if(!TextUtils.isEmpty(etHardPrize.getText())){
            prizes[2] = etHardPrize.getText().toString();
        }

        savePrizes();

    }

    public void onClaimClick(View view) {
        //dialog box here plx
    }

    public void onHelpClick(View view) {
        Toast.makeText(this, "Set your 100, 150 and 200 XP prizes and when you have enough XP CLAIM THEM!", Toast.LENGTH_LONG).show();
    }

    public void initializeActivity(){
        //read/create prizes.txt
        //load values for editetexts
        //create an alert dialogue for claiming prizes if they are not empty
        //get xp balance
        detectSettings();


    }

    private void detectSettings() {
        File myMainDir = getApplicationContext().getDir("prizes", Context.MODE_PRIVATE);

        File myFinalDir = new File(myMainDir, "prizes.txt");

        if (myFinalDir.exists()){
            Log.i("INFO","Prizes.txt detected");
            Log.i("INFO", "Path: "+ myFinalDir.getAbsolutePath());
            Log.i("INFO", "File name: " + myFinalDir.getName());

            //parse prizes
            //reading text from file
            try {
                Log.i("INFO", "Retrieving prizes...");
                FileInputStream fis = new FileInputStream(new File(myFinalDir.getAbsolutePath()));
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                Log.i("INFO", "Prizes Retrieved.");
                Log.i("INFO", "File contents: " + sb.toString());
                fis.close();
                prizes = sb.toString().split("@");
                Log.i("INFO", Arrays.toString(prizes));
                if(!prizes[0].isEmpty()){
                    etEasyPrize.setText(prizes[0]);
                }
                if(!prizes[1].isEmpty()){
                    etMediumPrize.setText(prizes[1]);
                }
                if(!prizes[2].isEmpty()){
                    etHardPrize.setText(prizes[2]);
                }
                if(!prizes[3].isEmpty()) {
                    try {
                        //balance = Integer.valueOf(prizes[3]);
                        balance = Integer.valueOf("250");
                        tvBalance.setText("Current Balance: " + balance + " XP");
                    } catch (Exception e) {
                        Log.i("INFO", "Error converting balance from string to int");
                        tvBalance.setText("Current Balance: " + balance + " XP");
                    }
                }
            } catch (Exception e) {
                Log.i("INFO", "Unable to retrieve settings.");
                Log.i("INFO", e.getMessage());
                e.printStackTrace();
            }
        } else {
            Log.i("INFO","Prizes.txt not detected, default prizes generated");
            savePrizes();
        }
    }
/*
    // add-write text into file
    String settings = "";
    settings = "shortBreakLength:"+shortBreakLength+"@longBreakLength:"+longBreakLength+"@pomLength:"+pomLength+"";
        Log.i("INFO", "Saving new settings ...");

        try {
        File myMainDir = context.getDir("settings", Context.MODE_PRIVATE);

        myMainDir.mkdir();

        File myFinalDir = new File(myMainDir, "settings.txt");

        FileOutputStream out = new FileOutputStream(myFinalDir, false); //Use the stream as usual to write into the file
        OutputStreamWriter outputWriter = new OutputStreamWriter(out);
        outputWriter.write(settings);
        outputWriter.close();

        Log.i("INFO",settings);

    } catch (Exception e) {
        Log.i("INFO", "Failed to save new settings");
        e.printStackTrace();
    }
}*/

    public void savePrizes(){
        // add-write text into file

        String prizesData = "";
        prizesData = prizes[0] + "@" + prizes[1] + "@" +prizes[2] + "@" + prizes[3] ;

        try {
            File myMainDir = getApplicationContext().getDir("prizes", Context.MODE_PRIVATE);

            myMainDir.mkdir();

            File myFinalDir = new File(myMainDir, "prizes.txt");

            FileOutputStream out = new FileOutputStream(myFinalDir, false); //Use the stream as usual to write into the file
            OutputStreamWriter outputWriter = new OutputStreamWriter(out);
            outputWriter.write(prizesData);
            outputWriter.close();
            Log.i("INFO", "Prizes Saved");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //set low medium and high rewards
    //record if earned for that week or not

}
