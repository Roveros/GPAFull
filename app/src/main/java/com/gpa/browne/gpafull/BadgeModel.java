package com.gpa.browne.gpafull;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by B00075549 on 1/17/2018.
 */

public class BadgeModel {
    private Context context;
    private ArrayList <String[]> badgesList = new ArrayList <>();
    private String badges[];
    private String firstDayOfThisWeek, topic;
    private ArrayList <String[]> weekList = new ArrayList <>();
    private int today;
    Calendar cal, calToday;
    static final int READ_BLOCK_SIZE = 100;

    public BadgeModel(Context context, String topic){
        this.topic=topic;
        this.context = context;
        firstDayOfThisWeek = getFirstDayOfThisWeek();
        badges = new String[]{"0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0"};
        getBadgeData();

    }

    //When persisting the model for badges is checked and updated then new data is appended
    //badgeNumber is the new badge earned to be persisted
    public int persist (){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = dateFormat.format(cal.getTime()) + ".txt";


        //give the array the correct week starting date
        badges[0] = firstDayOfThisWeek;

        //create the text string from the array data
        String badgePersistString = "";
        for (String value: badges) {
            badgePersistString += value + ".";
        }
        //remove the last ':' from the string
        badgePersistString = badgePersistString.substring(0, badgePersistString.length()-1);

        Log.i("INFO","Persisting badge data as " + fileName);
        try {
            File myMainDir = context.getDir("badges", Context.MODE_PRIVATE);
            File mySubDir = new File(myMainDir, topic);
            mySubDir.mkdir();
            File myFinalDir = new File(mySubDir, fileName);

            FileOutputStream out = new FileOutputStream(myFinalDir, false); //Use the stream as usual to write into the file
            OutputStreamWriter outputWriter = new OutputStreamWriter(out);
            outputWriter.write(badgePersistString);
            outputWriter.close();
            Log.i("INFO",fileName + " saved.");

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("INFO","Error saving " + fileName);
        }

        return 0;
    }

    public void showBadgeDialogue(String badgeNumber){
        //Code for displaying an image in a alert dialogue
        ImageView image = new ImageView(context);

        switch (badgeNumber) {
            case "b1":
                image.setImageResource(R.drawable.b1);
                break;
            case "b2":
                image.setImageResource(R.drawable.b2);
                break;
            case "b3":
                image.setImageResource(R.drawable.b3);
                break;
            case "b4":
                image.setImageResource(R.drawable.b4);
                break;
            case "b5":
                image.setImageResource(R.drawable.b5);
                break;
            case "b6":
                image.setImageResource(R.drawable.b6);
                break;
            case "b7":
                image.setImageResource(R.drawable.b7);
                break;
        }

        AlertDialog.Builder builder =
                new AlertDialog.Builder(context).
                        setPositiveButton("COLLECT BADGE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).
                        setView(image);
        builder.create().show();
    }

    //looks for .txt file with badge data for the given topic
    public int getBadgeData(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = dateFormat.format(cal.getTime());

        File myMainDir = context.getDir("badges", Context.MODE_PRIVATE);
        File mySubDir = new File(myMainDir, topic);

        if(!mySubDir.exists()){
            mySubDir.mkdir();
        }

        ArrayList<String> files = new ArrayList<String>(); //ArrayList cause you don't know how many files there is
        File[] filesInFolder = mySubDir.listFiles(); // This returns all the folders and files in your path
        for (File file : filesInFolder) { //For each of the entries do:
            if (!file.isDirectory()) { //check that it's not a dir
                files.add(new String(file.getName())); //push the filename as a string
            }
        }

        // for each file in the topic folder ...
        for (String file : files ) {

            File myFinalDir = new File(mySubDir, file);
            Log.i("INFO","Searching for "+ file +" ...");
            if (myFinalDir.exists()){
                Log.i("INFO","File detected.");
                Log.i("INFO", "Path: "+ myFinalDir.getAbsolutePath());
                Log.i("INFO", "File name: " + myFinalDir.getName());

                //parse values
                //reading text from file
                // data tokenised as:
                //@Date.b1.b2.b5.b1.b2.b5.b1.b2.b5.b1.b2.b5.b1.b2.b5.b1.b2.b5.b1.b2.b5.b3.b4.b6.b7
                try {
                    Log.i("INFO", "Attempting to retrieving badge data ...");
                    FileInputStream fis = new FileInputStream(new File(myFinalDir.getAbsolutePath()));
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader bufferedReader = new BufferedReader(isr);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }
                    Log.i("INFO", "Badge data retrieved successfully.");
                    Log.i("INFO", "File contents: " + sb.toString());
                    fis.close();
                    //split the data by week
                    String[] rawBadgeData = sb.toString().split("@");
                    Log.i("INFO", "Badges[] contents pre write: " + Arrays.toString(badges));

                    //the last iteration of this loop should contain the most recently saved data for this topic
                    //So badges[0] will have the most recent date in it
                    for (String badgeData: rawBadgeData) {
                        //split the data by variable
                        Log.i("INFO", "rawBadgeData - badgeData: " + badgeData);
                        badges = badgeData.split("\\.");
                        badgesList.add(badges);
                    }
                    Log.i("INFO", "Badges[] contents post write: " + Arrays.toString(badges));
                } catch (Exception e) {
                    Log.i("INFO", "Unable to retrieve badge data.");
                    Log.i("INFO", e.getMessage());
                    e.printStackTrace();
                }
            } else {
                Log.i("INFO","Badges.txt could not be detected. Generating default badges.txt");
                //create default data for this week.
                createDefaultBadges();
                badgesList.add(badges);
            }
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

        return cal.getTime().toString();
    }

    public void createDefaultBadges(){
        // add-write text into file

        //give the array the correct week starting date
        badges[0] = firstDayOfThisWeek;

        //create the text string from the array data
        String badgeDefaultString = "";
        for (String value: badges) {
            badgeDefaultString += value + ".";
        }
        //remove the last ':' from the string
        badgeDefaultString = badgeDefaultString.substring(0, badgeDefaultString.length()-1);

        Log.i("INFO","Generating badge.txt from: " + badgeDefaultString);
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String fileName = dateFormat.format(cal.getTime()) + ".txt";

            File myMainDir = context.getDir("badges", Context.MODE_PRIVATE);
            File mySubDir = new File(myMainDir, topic);
            mySubDir.mkdir();
            File myFinalDir = new File(mySubDir, fileName);

            FileOutputStream out = new FileOutputStream(myFinalDir, false); //Use the stream as usual to write into the file
            OutputStreamWriter outputWriter = new OutputStreamWriter(out);
            outputWriter.write(badgeDefaultString);
            outputWriter.close();
            Log.i("INFO","Badges.txt saved.");

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("INFO","Error saving badges.txt");
        }
    }

    //checks current array for badge
    public boolean hasBadge(String badgeNumber){

         if(badgeNumber.equals("b1")){
             return weekList.get(today)[0].equals("b1");
         } else if(badgeNumber.equals("b2")){
             return weekList.get(today)[1].equals("b2");
         } else if(badgeNumber.equals("b3")){
             return weekList.get(7)[0].equals("b3");
         } else if(badgeNumber.equals("b4")){
             return weekList.get(7)[1].equals("b4");
         } else if(badgeNumber.equals("b5")){
             return weekList.get(today)[2].equals("b5");
         } else if(badgeNumber.equals("b6")){
             return weekList.get(7)[2].equals("b6");
         } else if(badgeNumber.equals("b7")){
             return weekList.get(7)[3].equals("b7");
         }
        return false;
    }

    public void parseStringDatatoWeekList(){

        //make the data more accessable
        ////@Date:b1:b2:b5:b1:b2:b5:b1:b2:b5:b1:b2:b5:b1:b2:b5:b1:b2:b5:b1:b2:b5:b3:b4:b6:b7
        weekList = new ArrayList<>();
        String[] monday = {badges[1], badges[2], badges[3]};
        String[] tuesday = {badges[4], badges[5], badges[6]};
        String[] wednesday = {badges[7], badges[8], badges[9]};
        String[] thursday = {badges[10], badges[11], badges[12]};
        String[] friday = {badges[13], badges[14], badges[15]};
        String[] saturday = {badges[16], badges[17], badges[18]};
        String[] sunday = {badges[19], badges[20], badges[21]};
        String[] week = {badges[22], badges[23], badges[24], badges[25]};

        weekList.add(monday);       //0
        weekList.add(tuesday);      //1
        weekList.add(wednesday);    //2
        weekList.add(thursday);     //3
        weekList.add(friday);       //4
        weekList.add(saturday);     //5
        weekList.add(sunday);       //6
        weekList.add(week);         //7
    }

    public void parseWeekListToStringData(){
        //start i at 1, 0 = date
        int i = 1;
        badges[0] = firstDayOfThisWeek;
        //iterate through each array in the list
        for (String[] array: weekList) {
            Log.i("INFO", "Array "+ i +": " + Arrays.toString(array));
            //iterate trhough each value in the array
            for (int j = 0; j < array.length; j++) {
                badges[i] = array[j];
                i++;
            }
        }
        Log.i("INFO", "Parsed Week List To String Data:\n" + Arrays.toString(badges));
    }

    public void checkAllBadges(){

        Log.i("INFO", "Running checkAllBadges() - getBadgeData()");
        Log.i("INFO", "Context: " + context.toString());
       getBadgeData();

        Log.i("INFO", "First day of the week: " + firstDayOfThisWeek);
        Log.i("INFO", "Today: " + (today));
        Log.i("INFO", "Badges length: "+ badges.length);
        Log.i("INFO", "Badges contents: "+ Arrays.toString(badges));

        //if the date in badges[0] == last monday then this is this weeks badges
        if (!badges[0].equals(firstDayOfThisWeek)){
            Log.i("INFO","No records for week starting: " + badges[0] + ". Generating Default Data.");
            badges = new String[]{firstDayOfThisWeek,"0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0"};
        } else {
            Log.i("INFO","Records for the current week " + badges[0] + " found.");
        }

        //-----------------------------------DEBUG BADGES-----------------------------------//
/*        badges[13] = "b1";     //friday
        badges[4] = "b1";     //tuesday
        badges[7] = "b1";     //wednesday
        badges[10] = "b1";     //thursday

        badges[14] = "b2";     //friday
        badges[5] = "b2";     //tuesday
        badges[8] = "b2";     //wednesday
        badges[11] = "b2";     //thursday*/

        //badges[13] = "b1";     //friday
        //-----------------------------------DEBUG BADGES-----------------------------------//

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = dateFormat.format(cal.getTime()) + ".txt";

        File myMainDir = context.getDir("badges", Context.MODE_PRIVATE);
        File mySubDir = new File(myMainDir, topic);
        File myFinalDir = new File(mySubDir, fileName);

        if (!myFinalDir.exists()){
            createDefaultBadges();
        }

        //make the data more accessable using an arraylist for each day + week as a whole
        parseStringDatatoWeekList();

        checkBadge1();
        checkBadge2();
        checkBadge3();
        checkBadge4();
        checkBadge5();
        checkBadge6();
        checkBadge7();

        //update the badges array before persistane of any changes
        parseWeekListToStringData();
        persist();
    }

    //First pom of the day
    public int checkBadge1(){
        if(hasBadge("b1")){
            return 1;
        } else {
            //weeklist -> monday = [0]
            weekList.get(today)[0] = "b1";
            showBadgeDialogue("b1");
            return 1;
        }
    }

    //first pom set of the day
    public int checkBadge2(){
        if(hasBadge("b2")){
            return 1;
        } else {
            //read todays sessions and look for a 4 streak
            //reading text from file

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String fileName = dateFormat.format(calToday.getTime()) + ".txt";

                File myMainDir = context.getDir("logs", Context.MODE_PRIVATE);
                File mySubDir = new File(myMainDir, topic);
               // mySubDir.mkdir();
                File myFinalDir = new File(mySubDir, fileName);

                Log.i("INFO", "Retrieving logs and checking badge 2 for topic: " + topic);
                FileInputStream fileIn = new FileInputStream(new File(myFinalDir.getPath()));
                InputStreamReader InputRead = new InputStreamReader(fileIn);

                char[] inputBuffer = new char[READ_BLOCK_SIZE];
                String s = "";
                int charRead;

                while ((charRead = InputRead.read(inputBuffer)) > 0) {
                    // char to string conversion
                    String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                    s += readstring;
                }
                InputRead.close();
                Log.i("INFO", "File contents: " + s);
                String rawData[] = s.split("%"); //each array represents a session
                for (String data : rawData) {
                    int i = 0;
                    String tempArray[] = data.split("\\.");
                    for (String element : tempArray) {
                        if (element.equals("pom")){ i++; }
                    }
                    if (i >= 4){
                        weekList.get(today)[1] = "b2";
                        showBadgeDialogue("b2");
                        return 1;
                    }
                    fileIn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("INFO", "checkBadge2() failed");
            }
             return 0;
        }
    }

    //earned b1 5/7 days of the current week
    public int checkBadge3(){
        if(hasBadge("b3")){
            Log.i("INFO", "Found badge 3 for topic: " + topic);
            return 1;
        } else {
            //read todays sessions and look for a 4 streak
            //reading text from file

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String fileName = dateFormat.format(cal.getTime()) + ".txt";

                File myMainDir = context.getDir("badges", Context.MODE_PRIVATE);
                File mySubDir = new File(myMainDir, topic);
                File myFinalDir = new File(mySubDir, fileName);

                Log.i("INFO", "Retrieving badges and checking badge 3 for topic: " + topic);
                FileInputStream fileIn = new FileInputStream(new File(myFinalDir.getPath()));
                InputStreamReader InputRead = new InputStreamReader(fileIn);

                char[] inputBuffer = new char[READ_BLOCK_SIZE];
                String s = "";
                int charRead;

                while ((charRead = InputRead.read(inputBuffer)) > 0) {
                    // char to string conversion
                    String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                    s += readstring;
                }
                InputRead.close();
                Log.i("INFO", "File contents: " + s);
                String rawData[] = s.split("\\."); //each array represents a session

                int i = 0;
                for (String element : rawData) {
                    if (element.equals("b1")){ i++; }
                }

                //if there are 4 other recored b1's then this one makes 5, award badge
                if (i == 4){
                    if(!weekList.get(today).equals("b3")){
                        weekList.get(7)[0] = "b3";
                        showBadgeDialogue("b3");
                        return 1;
                    }
                }
                fileIn.close();

            } catch (Exception e) {
                e.printStackTrace();
                Log.i("INFO", "checkBadge3() failed");
            }
            return 0;
        }
    }

    public int checkBadge4(){
        if(hasBadge("b4")){
            Log.i("INFO", "b4");
            return 1;
        } else {
            //reading text from file
            //look for 4 "b2" badges. If this session gave the last b2 it will

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String fileName = dateFormat.format(cal.getTime()) + ".txt";

                File myMainDir = context.getDir("badges", Context.MODE_PRIVATE);
                File mySubDir = new File(myMainDir, topic);
                File myFinalDir = new File(mySubDir, fileName);

                Log.i("INFO", "Retrieving badges and checking badge 4 for topic: " + topic);
                FileInputStream fileIn = new FileInputStream(new File(myFinalDir.getPath()));
                InputStreamReader InputRead = new InputStreamReader(fileIn);

                char[] inputBuffer = new char[READ_BLOCK_SIZE];
                String s = "";
                int charRead;

                while ((charRead = InputRead.read(inputBuffer)) > 0) {
                    // char to string conversion
                    String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                    s += readstring;
                }
                InputRead.close();
                Log.i("INFO", "File contents: " + s);
                String rawData[] = s.split("\\."); //each array represents a session

                int i = 0;
                for (String element : rawData) {
                    if (element.equals("b2")){ i++; }
                }

                //if there are 4 other recored b1's then this one makes 5, award badge
                if (i == 4) {
                    if (weekList.get(today)[1].equals("b2")) {
                        weekList.get(7)[1] = "b4";
                        showBadgeDialogue("b4");
                        return 1;
                    }
                }
                fileIn.close();

            } catch (Exception e) {
                e.printStackTrace();
                Log.i("INFO", "checkBadge4() failed to read: " + topic);
            }
            return 0;
        }
    }

    public int checkBadge5(){
        if(!hasBadge("b5")){
            //get model to give goal data
            GPAGoalModel modelGoal = new GPAGoalModel(context, topic);
            String goals[] = modelGoal.getThisWeeksGoals();

            Log.i("INFO", "Goals from model: "+ Arrays.toString(goals));

            //if valid goal detected
            if ((Integer.valueOf(goals[1]) >= 1) && !hasBadge("b5")){
                Log.i("INFO", "Does not have badge 5. Goal is greater than 1 hour per day: " + goals[1]);

                //get number of minutes per goal
                int goalInMinutes = Integer.valueOf(goals[1]) * 60;
                int numOfPomsRequired;

                if((goalInMinutes % 25) == 0){
                    numOfPomsRequired = (goalInMinutes / 25);
                } else {
                    numOfPomsRequired = (goalInMinutes / 25) + 1;
                }

                Log.i("INFO", "Number of poms required: " + numOfPomsRequired);

                //Check todays logs and see how many "pom"
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String fileName = dateFormat.format(calToday.getTime()) + ".txt";

                    File myMainDir = context.getDir("logs", Context.MODE_PRIVATE);
                    File mySubDir = new File(myMainDir, topic);
                    // mySubDir.mkdir();
                    File myFinalDir = new File(mySubDir, fileName);

                    Log.i("INFO", "Retrieving logs and checking badge 5 for topic: " + topic);
                    FileInputStream fileIn = new FileInputStream(new File(myFinalDir.getPath()));
                    InputStreamReader InputRead = new InputStreamReader(fileIn);

                    char[] inputBuffer = new char[READ_BLOCK_SIZE];
                    String s = "";
                    int charRead;

                    while ((charRead = InputRead.read(inputBuffer)) > 0) {
                        // char to string conversion
                        String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                        s += readstring;
                    }
                    InputRead.close();
                    Log.i("INFO", "File contents: " + s);
                    String rawData[] = s.split("%"); //each array represents a session
                    //i is outside the foreach because i'm not looking for consecutive poms
                    int i = 0;
                    for (String data : rawData) {
                        String tempArray[] = data.split("\\.");
                        for (String element : tempArray) {
                            if (element.equals("pom")){ i++; }
                        }
                        if (i >= numOfPomsRequired){
                            weekList.get(today)[2] = "b5";
                            Log.i("INFO", "Badge 5 Earned");
                            showBadgeDialogue("b5");
                            return 1;
                        }
                        fileIn.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("INFO", "checkBadge5() failed");
                }
            }
        } else {
            Log.i("INFO", "Badge 5 Found.");
        }
        return 0;
    }

    public int checkBadge6(){
        //identify first day of this week. I need to get every file that is on or after that date. Count poms

        if(!hasBadge("b6")){
            //get model to give goal data
            GPAGoalModel modelGoal = new GPAGoalModel(context, topic);
            String goals[] = modelGoal.getThisWeeksGoals();

            Log.i("INFO", "Goals from model for badge 6: "+ Arrays.toString(goals));

            File myMainDir = context.getDir("logs", Context.MODE_PRIVATE);
            File mySubDir = new File(myMainDir, topic);

            if(!mySubDir.exists()){
                mySubDir.mkdir();
            }

            ArrayList<String> files = new ArrayList<String>(); //ArrayList cause you don't know how many files there is
            File[] filesInFolder = mySubDir.listFiles(); // This returns all the folders and files in your path
            for (File file : filesInFolder) { //For each of the entries do:
                Date tempDate =null;
                Calendar tempCal = cal;

                if (!file.isDirectory()) { //check that it's not a dir
                    //check date
                    //Date
                    Log.i("INFO", "File in directory: "+ file.getName());

                    try{
                        //remove the .txt from the end
                        String date = file.getName().substring(0, (file.getName().length() - 4));
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        tempDate = dateFormat.parse(date);
                        //create a temp date 50 seconds before start of week for checking for this weeks' files
                        tempCal.add(Calendar.SECOND, -50);
                        if(tempDate.after(tempCal.getTime())){
                            Log.i("INFO", "File " + tempDate.toString() + " after " +tempCal.getTime());
                            files.add(new String(file.getName())); //push the filename as a string
                        } else {
                            Log.i("INFO", "File " + tempDate.toString() + " not after " +tempCal.getTime());
                        }
                    } catch (Exception e){
                        Log.i("INFO", "Error comparing dates: " + tempDate.getTime() + " and " +tempCal.getTime());
                    }
                }
            }
            //if valid goal detected
            if ((Integer.valueOf(goals[2]) >= 4) && !hasBadge("b6") && !files.isEmpty()){
                Log.i("INFO", "Does not have badge 6. Goal is greater than 4 hour per day: " + goals[2]);

                //get number of minutes per goal
                int goalInMinutes = Integer.valueOf(goals[2]) * 60;
                int numOfPomsRequired;

                if((goalInMinutes % 25) == 0){
                    numOfPomsRequired = (goalInMinutes / 25);
                } else {
                    numOfPomsRequired = (goalInMinutes / 25) + 1;
                }

                Log.i("INFO", "Number of poms required: " + numOfPomsRequired);

                //Check todays logs and see how many "pom"
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    //String fileName = dateFormat.format(calToday.getTime()) + ".txt";

                    int i = 0;
                    //for each file, read log and count poms
                    for (String file : files) {

                        File myFinalDir = new File(mySubDir, file);

                        Log.i("INFO", "Retrieving logs and checking badge 6 for topic: " + topic);
                        FileInputStream fileIn = new FileInputStream(new File(myFinalDir.getPath()));
                        InputStreamReader InputRead = new InputStreamReader(fileIn);

                        char[] inputBuffer = new char[READ_BLOCK_SIZE];
                        String s = "";
                        int charRead;

                        while ((charRead = InputRead.read(inputBuffer)) > 0) {
                            // char to string conversion
                            String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                            s += readstring;
                        }
                        InputRead.close();
                        Log.i("INFO", "File contents: " + s);
                        String rawData[] = s.split("%"); //each array represents a session
                        //i is outside the foreach because i'm not looking for consecutive poms
                        for (String data : rawData) {
                            String tempArray[] = data.split("\\.");
                            for (String element : tempArray) {
                                if (element.equals("pom")){ i++; }
                            }
                            if (i >= numOfPomsRequired){
                                weekList.get(7)[2] = "b6";
                                Log.i("INFO", "Badge 6 Earned");
                                showBadgeDialogue("b6");
                                return 1;
                            }
                            fileIn.close();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("INFO", "checkBadge6() failed");
                }
            }
        }  else {
            Log.i("INFO", "Badge 6 Found.");
        }
        return 0;
    }

    public int checkBadge7(){
        //is the deadline completed checkbox checked
        //do they have the badge already?
        //is it 24 hours before the deadline date

        if(!hasBadge("b7")){
            //get model to give goal data
            GPAGoalModel modelGoal = new GPAGoalModel(context, topic);
            String goals[] = modelGoal.getThisWeeksGoals();

            Log.i("INFO", "Goals from model for badge 7: "+ Arrays.toString(goals));

            if(goals[5].equals("1")){
                Log.i("INFO", "Topic Completed");
                Log.i("INFO", "Date: " + goals[3] + ", Time: " + goals[4]);

                //check if date is dd/mm/yyy HH:SS,
                try{
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date tempDate = dateFormat.parse(goals[3] + " " + goals[4] + ":00");
                    Log.i("INFO", "Temp Date" + tempDate.toString());

                    Calendar requiredDate = Calendar.getInstance();
                    Calendar now = Calendar.getInstance();
                    requiredDate.setTime(tempDate);
                    requiredDate.add(Calendar.DAY_OF_YEAR, -1);
                    Log.i("INFO", "Required Completion Date: " + requiredDate.getTime().toString());

                    if (now.getTime().after(requiredDate.getTime())){
                        Log.i("INFO", "Deadline Passed, not eligible for badge 7");
                    } else {
                        Log.i("INFO", "Eligible for badge 7");
                        weekList.get(7)[3] = "b7";
                        Log.i("INFO", "Badge 7 Earned");
                        showBadgeDialogue("b7");
                        return 1;

                    }


                } catch (ParseException ex){
                    Log.i("INFO", "Parse Exception");
                    System.err.println(ex);
                }

            } else {
                Log.i("INFO", "Topic Not Completed");
            }
        }  else {
            Log.i("INFO", "Badge 7 Found.");
        }
        return 0;
    }
}
