package com.gpa.browne.gpafull;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public class BadgesActivity extends AppCompatActivity {
    TextView tvB1Description, tvB2Description, tvB3Description, tvB4Description, tvB5Description, tvB6Description, tvB7Description;
    int badgeCount[] = {0,0,0,0,0,0,0};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badges);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //this places a back button on the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        tvB1Description = findViewById(R.id.tvB1Description);
        tvB2Description = findViewById(R.id.tvB2Description);
        tvB3Description = findViewById(R.id.tvB3Description);
        tvB4Description = findViewById(R.id.tvB4Description);
        tvB5Description = findViewById(R.id.tvB5Description);
        tvB6Description = findViewById(R.id.tvB6Description);
        tvB7Description = findViewById(R.id.tvB7Description);

        initBadgeInfo();

        String message = "A Good Start: ";
        tvB1Description.setText(message + badgeCount[0]);

        message = "You're All Set: ";
        tvB2Description.setText(message + badgeCount[1]);

        message = "Part-Timer: ";
        tvB3Description.setText(message + badgeCount[2]);

        message = "Full-Timer";
        tvB4Description.setText(message + badgeCount[3]);

        message = "One Day At A Time: ";
        tvB5Description.setText(message + badgeCount[4]);

        message = "A Good Work Week: ";
        tvB6Description.setText(message + badgeCount[5]);

        message = "With Time To Spare: ";
        tvB7Description.setText(message + badgeCount[6]);


    }

    private void initBadgeInfo() {
        //get dirs
        //get files
        //for each file
        //tokenize to array
        //loop array count badges
        //when finished print to tv
        String badges[] = new String[26];
        File myMainDir = getDir("badges", Context.MODE_PRIVATE);
        File[] files = myMainDir.listFiles();

        final StringBuilder sb = new StringBuilder();
        if(files.length != 0){
            for (File inFile : files) {
                if (inFile.isDirectory()) {
                    Log.i("INFO", inFile.getName());
                    sb.append(inFile.getName()+"\n");
                }
            }
            final String[] dirNames = sb.toString().split("\\n");

            for (String dirName : dirNames) {
                File mySubDir = new File(myMainDir, dirName);
                File[] dirFiles = mySubDir.listFiles();
                Log.i("INFO", "List of files in " + dirName + ": " + Arrays.toString(dirFiles));

                for (File file : dirFiles) {
                    File myFinalDir = file;
                    Log.i("INFO", "Searching for " + file + " ...");

                    //if the file exists
                    if (myFinalDir.exists()) {
                        Log.i("INFO", "File detected.");
                        Log.i("INFO", "Path: " + myFinalDir.getAbsolutePath());
                        Log.i("INFO", "File name: " + myFinalDir.getName());

                        //Data tokenised as:
                        //@Date.b1.b2.b5.b1.b2.b5.b1.b2.b5.b1.b2.b5.b1.b2.b5.b1.b2.b5.b1.b2.b5.b3.b4.b6.b7
                        //try to read the file contents to a string builder
                        try {
                            Log.i("INFO", "Attempting to retrieving badge data ...");
                            FileInputStream fis = new FileInputStream(new File(myFinalDir.getAbsolutePath()));
                            InputStreamReader isr = new InputStreamReader(fis);
                            BufferedReader bufferedReader = new BufferedReader(isr);
                            StringBuilder sb2 = new StringBuilder();
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                sb2.append(line);
                            }
                            Log.i("INFO", "Badge data retrieved successfully.");
                            Log.i("INFO", "File contents: " + sb2.toString());
                            fis.close();

                            //Data is split week by week by the @ token
                            String[] rawBadgeData = sb2.toString().split("@");


                            //this fills the badge array with the contents of that weeks array i.e
                            //Date.b1.b2.b5.b1.b2.b5.b1.b2.b5.b1.b2.b5.b1.b2.b5.b1.b2.b5.b1.b2.b5.b3.b4.b6.b7
                            //this array is added to an array list
                            for (String badgeData : rawBadgeData) {
                                Log.i("INFO", "Badges[] contents pre write: " + Arrays.toString(badges));
                                Log.i("INFO", "rawBadgeData - badgeData: " + badgeData);

                                //split the data by variable
                                badges = badgeData.split("\\.");
                                //badgesList.add(badges);
                                for (String badge : badges) {
                                    switch (badge) {
                                        case "b1": badgeCount[0]++;
                                            break;
                                        case "b2": badgeCount[1]++;
                                            break;
                                        case "b3": badgeCount[2]++;
                                            break;
                                        case "b4": badgeCount[3]++;
                                            break;
                                        case "b5": badgeCount[4]++;
                                            break;
                                        case "b6": badgeCount[5]++;
                                            break;
                                        case "b7": badgeCount[6]++;
                                            break;
                                    }
                                }
                                Log.i("INFO", "Badges[] contents post write: " + Arrays.toString(badges));
                            }
                        } catch (Exception e) {
                            Log.i("INFO", "Unable to retrieve badge data.");
                            Log.i("INFO", e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }

        }

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

    public void onB1Click(View view) {
        String message = "Awarded for completing your first work session of the day for a single topic";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void onB2Click(View view) {
        String message = "Awarded for completing your first set of 4 consecutive work sessions of the day for a single topic";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void onB3Click(View view) {
        String message = "Awarded for earning 5 \"A Good Start\" badges fora  single topic in one week";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void onB4Click(View view) {
        String message = "Awarded for earning 5 \"You're All Set\" badges for a single topic in one week";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void onB5Click(View view) {
        String message = "Awarded for reaching your daily app usage goals";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void onB6Click(View view) {
        String message = "Awarded for reaching your weekly app usage goals";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void onB7Click(View view) {
        String message = "Awarded for beating your deadline submission time by 24-hours or more for a single topic ";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
