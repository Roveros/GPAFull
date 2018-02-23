package com.gpa.browne.gpafull;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TimerController timer;
    GPAConfigModel config;

    TextView tvTimerDisplay, tvCounterDisplay;
    EditText etSessionTitle;
    CoordinatorLayout layout;
    ProgressBar progressBar;

    String topics[];

    String prizeData = "";
    String prizeLog = "";
    String settings = "";

    String topicTitle = "";
    String badgeData = "";
    String goalData = "";
    String logData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init the GPAConfigModel
        config = new GPAConfigModel(this);

        //get timer display TextView from view
        tvTimerDisplay = findViewById(R.id.tvTimerDisplay);

        //get the counter display from the view
        tvCounterDisplay = findViewById(R.id.tvCounterDisplay);

        //get the session title from the view
        etSessionTitle = findViewById(R.id.etSessionTitle);

        //get the layout from the view. Will be used to edit background colour
        layout = findViewById(R.id.coordLayout);

        //get progress bar
        progressBar = findViewById(R.id.progressBar);

        //init the TimerModel and pass it the tvTimerDisplay TextView
        timer = new TimerController(MainActivity.this, layout, progressBar, config, tvTimerDisplay, tvCounterDisplay, etSessionTitle);

        topics = null;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
           // Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_LONG).show();
            config = new GPAConfigModel(this);
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra("shortBreakLength", config.getShortBreakLength());
            intent.putExtra("longBreakLength", config.getLongBreakLength());
            intent.putExtra("pomLength", config.getPomLength());
            startActivity(intent);
        } else if (id == R.id.action_exit) {
            onExitClick();
        } else if (id == R.id.action_goals) {
            if(!TextUtils.isEmpty(etSessionTitle.getText())){
                Intent intent = new Intent(this, GoalsActivity.class);
                intent.putExtra("topic", etSessionTitle.getText().toString());
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Cannot set goals without a session topic.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.action_prizes) {
            Intent intent = new Intent(this, PrizesActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_email) {
            sendEmail();
        } else if (id == R.id.action_select_topic) {
            displayTopics();
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayTopics() {
        File myMainDir = getDir("logs", Context.MODE_PRIVATE);
        File[] files = myMainDir.listFiles();
        
        if(files.length != 0){
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Choose Topic");

            final StringBuilder sb = new StringBuilder();
            for (File inFile : files) {
                if (inFile.isDirectory()) {
                    Log.i("INFO", inFile.getName());
                    sb.append(inFile.getName()+"\n");
                }
            }
            sb.append("Exit");
            final String[] types = sb.toString().split("\\n");
            topics = new String[types.length-1];
            for (int i = 0; i < types.length-1 ; i++) {
                topics[i] = types[i];
            }

            b.setItems(types, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                    if(!types[which].equals("Exit")){
                        etSessionTitle.setText(types[which]);
                    }

                }
            });
            b.show();
        } else {
            Toast.makeText(this, "No topics detected", Toast.LENGTH_SHORT).show();
        }
 
    }

    //Starts timer if not already started
    public void onStartClick(View view) {
        //config = new GPAConfigModel(this);
        //timer = new TimerController(MainActivity.this, layout, progressBar, config, tvTimerDisplay, tvCounterDisplay, etSessionTitle);
        timer.start("pom");
    }

    //Stops timer if timer has been started
    public void onStopClick(View view) {
        if (timer.isRunning()) {
            Log.i("Click Event", "onStopClick");
            timer.stop();
        }
    }

    //Exits tha application
    public void onExitClick() {
        if(timer.isRunning()){
            timer.stop();
        }
        finish();
        System.exit(0);
    }

    //Sends all text data for the session title
    protected void sendEmail() {

        Log.i("INFO", "Sending email ... ");

        String[] TO = {
                "b00075549@student.itb.ie"
        };
        String[] CC = {
                "robert.browne@student.itb.ie"
        };
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "All Data");

        //get prizedata ¦
        //get prizelog ¦
        //get settings |
        //foreach topic {
        //      get topic title ¦
        //      get all badge files, sort by creation date, add to list, add to string week 1 @ week 2 @ etc ¦
        //      get all goal files, sort by creation date, add to list, add to string week 1 @ week 2 @ etc ¦
        //      get all log files, sort by creation date, add to list, add to string day 1 @ day 2 @ etc |
        //remove final |

        prizeData = getPrizeData();
        prizeLog = getPrizeLog();
        settings = getSettings();

        if(TextUtils.isEmpty(prizeData)){
            prizeData = "NULL";
        }
        if(TextUtils.isEmpty(prizeLog)){
            prizeLog = "NULL";
        }
        if(TextUtils.isEmpty(settings)){
            settings = "NULL";
        }

        String generalData = prizeData + "¦" + prizeLog + "¦" + settings;
        Log.i("INFO-email", generalData);


        for (String topic : topics) {
            topicTitle = topic;
            if(TextUtils.isEmpty(badgeData)){
                badgeData = badgeData + getBadgeData(topic);
            } else {
                badgeData = badgeData + "@" + getBadgeData(topic);
            }
            goalData = getGoalData(topic);
            logData = getLogData(topic);
        }

        if(TextUtils.isEmpty(badgeData)){
            badgeData = "NULL";
        }
        if(TextUtils.isEmpty(goalData)){
            goalData = "NULL";
        }
        if(TextUtils.isEmpty(logData)){
            logData = "NULL";
        }


        String topicData = topicTitle + "¦" + badgeData + "¦" + goalData + "¦" + logData;
        Log.i("INFO-email", "TopicData: " + topicData);

        String allData = generalData + "|" + topicData;

        //concat all the files contents into a single string
        emailIntent.putExtra(Intent.EXTRA_TEXT, allData);


/*         //concat all the files contents into a single string
        try {
            for (File file: fileList) {
                stringBuilder.append(readFileToString(file.getPath()));
            }
            emailIntent.putExtra(Intent.EXTRA_TEXT, stringBuilder.toString());
        } catch (Exception e) {
            Log.e("ERROR!", "IOException converting files to string.");
        }*/

        //Start the email intent
        try {
            //startActivity(Intent.createChooser(emailIntent, "Choose an email provider"));
            //finish();
            Log.i("INFO", "Email sent ...");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this,
                    "No email client installed. Please download and install Microsoft Outlook", Toast.LENGTH_SHORT).show();
        }

    }

    //reads files and returns a string
    static String readFileToString(String path) throws IOException {
        InputStream is = new FileInputStream(path);
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));

        String line = buf.readLine();
        StringBuilder sb = new StringBuilder();

        while (line != null) {
            sb.append(line);
            line = buf.readLine();
        }

        String fileAsString = sb.toString();
        System.out.println("Contents : " + fileAsString);

        return fileAsString;
    }

    public String getPrizeData() {
        PrizeModel model = new PrizeModel(this);
        String prizeDataArray[] = model.getprizes();
        String prizeDataString = "";

        for (String variable: prizeDataArray) {
            if(variable.length() == 0){
                variable = "NULL";
            }
            if(TextUtils.isEmpty(prizeDataString)){
                prizeDataString = prizeDataString + variable;
            } else {
                prizeDataString = prizeDataString + "." + variable;
            }
        }
        
        //string should resemble X.X.X.X
        Log.i("INFO-Email", "Prize Data: " + prizeDataString);
        return prizeDataString;
    }

    public String getPrizeLog() {
        PrizeModel model = new PrizeModel(this);
        String prizeLogString = model.getPrizeLog();

        //
        Log.i("INFO-Email", "Prize Log: " + prizeLogString);
        return prizeLogString;

    }

    public String getSettings() {
        GPAConfigModel model = new GPAConfigModel(this);
        String settings = model.getSettings();

        Log.i("INFO-Email", "Settings: " + settings);
        return settings;
    }

    public String getBadgeData(String topic) {
        BadgeModel model = new BadgeModel(this, topic);
        ArrayList <String[]> allBadgeData = model.getAllBadgeData();
        ArrayList <Date> dates = new ArrayList<>();
        DateFormat df;
        for (String array[] : allBadgeData) {
           try {
               df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
               dates.add(df.parse(array[0]));
           } catch (ParseException e) {
               Log.i("INFO-getBadgeData", "Error formatting date");
            }
        }

        Boolean unsorted = true;
        while (unsorted) {
            unsorted = false;
            for (int i = 0; i < dates.size()-1; i++) {
                Date temp1 = dates.get(i);
                Date temp2 = dates.get(i+1);
                String tempArray1[] = allBadgeData.get(i);
                String tempArray2[] = allBadgeData.get(i+1);


                //if temp1 is after temp2, switch'em
                if(temp1.compareTo(temp2) == 1){
                    dates.set(i, temp2);
                    dates.set(i+1, temp1);
                    allBadgeData.set(i, tempArray2);
                    allBadgeData.set(i+1, tempArray1);
                    unsorted = true;
                    Log.i("INFO-getBadgeData", "Switching");
                }
            }
        }

        //dates and allBadgeData is now sorted by date

        String allDataString = "";
        //for each array in the list
        for (int i = 0; i < allBadgeData.size() ; i++) {
            String tempBadgeString = "";
            //for each variable in the array, append to string
            for (String variable : allBadgeData.get(i)) {
                if(TextUtils.isEmpty(tempBadgeString)){
                    tempBadgeString = tempBadgeString + variable;
                } else {
                    tempBadgeString = tempBadgeString + "." + variable;
                }
            }

            //take the string above and add it to the allDataString
            if(TextUtils.isEmpty(allDataString)){
                allDataString = allDataString + tempBadgeString;
            } else {
                allDataString = allDataString + "@" + tempBadgeString;
            }
        }

        Log.i("INFO-Email", "All Badge data for topic " + topic + ": " + allDataString);
        return allDataString;
    }

    public String getGoalData(String topic) {
        return goalData;
    }

    public String getLogData(String topic) {
        return logData;
    }


    public void onDebugClick(View view) {
//Code for displaying an image in a alert dialogue
/*        ImageView image = new ImageView(this);
        image.setImageResource(R.drawable.badge1);

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this).
                        setMessage("Badge Earned!").
                        setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).
                        setView(image);
        builder.create().show();*/
//****************************************************************************************************//
/*        //create default data for this week.
        // get today and clear time of day
        Calendar cal = Calendar.getInstance(new Locale("en","UK"));
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        // get start of this week
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        Log.i("INFO","Start of this week:       " + cal.getTime());
        Toast.makeText(this, "Start of this week:       " + cal.getTime(), Toast.LENGTH_SHORT).show();*/
    }
}