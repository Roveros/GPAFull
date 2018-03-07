package com.gpa.browne.gpafull;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Roveros on 31/10/2017.
 */

public class GPAGoalModel {

    private int today;
    private String topic, firstDayOfWeek;
    private Context context;
    Calendar cal, calToday;
    String goals[];
    private ArrayList <String[]> goalList = new ArrayList <>();
    private BadgeModel model;

    public GPAGoalModel(Context context, String topic){
        this.topic = topic;
        getFirstDayOfThisWeek();
        //goals[] = date.daily.weekly.deadlinedate.deadlinetime.completed.bet
        goals = new String[]{"","","","","","",""};
        model = new BadgeModel(context, topic);

        this.context = context;
        getGoalData();
    }

    //looks for .txt file with badge data for the given topic
    public int getGoalData(){

        //Empty the list
        goalList.clear();

        //get date of the this weeks first day (Monday)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = dateFormat.format(cal.getTime());

        //Identify the directory that goals data is saved in
        File myMainDir = context.getDir("goals", Context.MODE_PRIVATE);
        File mySubDir = new File(myMainDir, topic);

        //create it if it doesn't exist
        if(!mySubDir.exists()){
            mySubDir.mkdir();
        }

        //for each file in there add it tot the list of files
        ArrayList<String> files = new ArrayList<String>(); //ArrayList cause you don't know how many files there is
        File[] filesInFolder = mySubDir.listFiles(); // This returns all the folders and files in your path


        for (File file : filesInFolder) { //For each of the entries do:
            if (!file.isDirectory()) { //check that it's not a dir
                files.add(new String(file.getName())); //push the filename as a string
            }
        }

        //If there are files
        if(!files.isEmpty()){
            // for each file in the topic folder ...
            for (String file : files ) {

                File myFinalDir = new File(mySubDir, file);
                Log.i("INFO","Searching for "+ file +" ...");
                if (myFinalDir.exists()){
                    Log.i("INFO","File detected.");
                    Log.i("INFO", "Path: "+ myFinalDir.getAbsolutePath());
                    Log.i("INFO", "File name: " + myFinalDir.getName());

                    //data tokenised as:
                    //date.dailyGoal.weeklyGoal.DD/MM/YYYY.HH:MM.completed.bet
                    //date.1.8.10/03/2018.11:55.0.0
                    try {
                        Log.i("INFO", "Attempting to retrieving goal data ...");
                        FileInputStream fis = new FileInputStream(new File(myFinalDir.getAbsolutePath()));
                        InputStreamReader isr = new InputStreamReader(fis);
                        BufferedReader bufferedReader = new BufferedReader(isr);
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            sb.append(line);
                        }
                        Log.i("INFO", "Goal data retrieved successfully.");
                        Log.i("INFO", "File contents: " + sb.toString());
                        fis.close();
                        //split the data by week
                        String[] rawGoalData = sb.toString().split("\\.");
                        Log.i("INFO", "Goals[] contents pre write: " + Arrays.toString(goals));

                        //the last iteration of this loop should contain the most recently saved data for this topic
                        //So badges[0] will have the most recent date in it

                        //split the data by variable
                        Log.i("INFO", "rawGoalData: " + Arrays.toString(rawGoalData));
                        goals = rawGoalData;
                        goalList.add(goals);

                        Log.i("INFO", "Goals[] contents post write: " + Arrays.toString(goals));
                    } catch (Exception e) {
                        Log.i("INFO", "Unable to retrieve goal data.");
                        Log.i("INFO", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    Log.i("INFO","Goals data could not be detected. Generating default goals.txt");
                    //create default data for this week.
                    createDefaultGoals();
                    goalList.add(goals);
                }
            }
            //get this weeks data if it exists
            try {
                Log.i("INFO", "GoalList size: " + goalList.size());
                for (String goalData[] : goalList) {
                    Log.i("INFO", "Comparing goalData[0]: "+ goalData[0].toString() + " to first day: " + firstDayOfWeek );
                    if(goalData[0].equals(firstDayOfWeek)){
                        Log.i("INFO","Goal data containing date: "+ firstDayOfWeek);
                        goals = goalData;
                        Log.i("INFO", "goals[] contents: " + Arrays.toString(goals));
                    } else {
                        //Only set goals to default if it is not currently set to THIS weeks' monday
                        if(!goals[0].equals(firstDayOfWeek)){
                            goals = new String[]{firstDayOfWeek,"1","6","DD/MM/YYYY","HH:MM","0","0"};
                            saveSettings("1","6","DD/MM/YYYY","HH:MM","0","0");
                        }
                    }
                }
            } catch (Exception e){
                Log.i("INFO", "Error detecting first day of the week within goals[]");
            }
        } else {
            Log.i("INFO","Goals data could not be detected. Generating default goals.txt");
            //create default data for this week.
            createDefaultGoals();
            goalList.add(goals);
        }

        return 0;
    }

    public String[] getGoals(){
        getGoalData();
        return goals;
    }

    public void createDefaultGoals(){
        // add-write text into file

        //give the array the correct week starting date
        goals[0] = firstDayOfWeek;      //first day of this week
        goals[1] = "1";                 //daily hour goal
        goals[2] = "6";                 //weekly hour goal
        goals[3] = "DD/MM/YYYY";        //deadline date
        goals[4] = "HH:MM";             //deadline time
        goals[5] = "0";                 //completed 1 or 0
        goals[6] = "0";                 //24 hour b7 bet taken 1 or 0

        //create the text string from the array data
        String goalDefaultString = "";
        for (String value: goals) {
            goalDefaultString += value + ".";
        }
        //remove the last '.' from the string
        goalDefaultString = goalDefaultString.substring(0, goalDefaultString.length()-1);

        Log.i("INFO","Generating goals.txt from: " + goalDefaultString);
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String fileName = dateFormat.format(cal.getTime()) + ".txt";

            File myMainDir = context.getDir("goals", Context.MODE_PRIVATE);
            File mySubDir = new File(myMainDir, topic);
            mySubDir.mkdir();
            File myFinalDir = new File(mySubDir, fileName);

            FileOutputStream out = new FileOutputStream(myFinalDir, false); //Use the stream as usual to write into the file
            OutputStreamWriter outputWriter = new OutputStreamWriter(out);
            outputWriter.write(goalDefaultString);
            outputWriter.close();
            Log.i("INFO","Goals[] data saved.");

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("INFO","Error saving goals[] data.");
        }
    }

    public int saveSettings(String dailyGoal, String weeklyGoal, String deadlineDate, String deadlineTime, String completed, String bet){
        // add-write text into file
        //date.1.8.10/03/2018.11:55.0.0
        //get date of the this weeks first day (Monday)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = dateFormat.format(cal.getTime());
        //String date = "Mon Feb 5 00:00:00 GMT+00:00 2018";
        String date = getFirstDayOfThisWeek();
        String settings = "";
        settings = date + "." + dailyGoal + "." + weeklyGoal + "." + deadlineDate + "." + deadlineTime + "." + completed + "." + bet;
        Log.i("INFO", "Saving new goals data ...");

        try {
            //Identify the directory that goals data is saved in
            File myMainDir = context.getDir("goals", Context.MODE_PRIVATE);
            File mySubDir = new File(myMainDir, topic);

            //create it if it doesn't exist
            if (!mySubDir.exists()) {
                mySubDir.mkdir();
            }

            File myFinalDir = new File(mySubDir, fileName + ".txt");

            FileOutputStream out = new FileOutputStream(myFinalDir, false); //Use the stream as usual to write into the file
            OutputStreamWriter outputWriter = new OutputStreamWriter(out);
            outputWriter.write(settings);
            outputWriter.close();

            Log.i("INFO", settings + " persisted.");

            if (completed.equals("1") && bet.equals("1")) {
                model = new BadgeModel(context, topic);
                model.parseStringDatatoWeekList();
                //model.getBadgeData();
                int b7 = model.checkBadge7();
                model.parseWeekListToStringData();
                model.persist();

                if(b7 == 1){
                    return 1;
                }
                //Code for displaying an image in a alert dialogue
/*                ImageView image = new ImageView(context);
                image.setImageResource(R.drawable.b7);
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(context).
                                setPositiveButton("COLLECT BADGE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).
                                setView(image);
                builder.create().show();*/
            }
            return 0;
        } catch (Exception e) {
            Log.i("INFO", "Failed to save new goals[] data");
            e.printStackTrace();
        }
        return 0;
    }

    private String getFirstDayOfThisWeek(){
        // get today and clear time of day
        //must use uk local to get monday as first day instead of sunday
        cal = Calendar.getInstance(new Locale("en","UK"));
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        // get start of this week
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        calToday = Calendar.getInstance(new Locale("en","UK"));

        today = calToday.get(Calendar.DAY_OF_WEEK) - 2;
        firstDayOfWeek = cal.getTime().toString();
        return cal.getTime().toString();
    }

    public String[] getThisWeeksGoals() {
        getGoalData();
        return goals;
    }

    public ArrayList<String[]> getAllGoalData() {
        getGoalData();
        return goalList;
    }
}
