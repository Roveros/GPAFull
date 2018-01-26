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

        Log.i("INFO","Persisting badge data as " + fileName + ".txt");
        try {
            File myMainDir = context.getDir("badges", Context.MODE_PRIVATE);
            File mySubDir = new File(myMainDir, topic);
            mySubDir.mkdir();
            File myFinalDir = new File(mySubDir, fileName);

            FileOutputStream out = new FileOutputStream(myFinalDir, false); //Use the stream as usual to write into the file
            OutputStreamWriter outputWriter = new OutputStreamWriter(out);
            outputWriter.write(badgePersistString);
            outputWriter.close();
            Log.i("INFO",fileName + ".txt saved.");

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("INFO","Error saving " + fileName + ".txt");
        }

        return 0;
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
        String[] week = {badges[22], badges[22], badges[24], badges[25]};

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
    /*    badges[1] = "b1";     //monday
        badges[4] = "b1";     //tuesday
        badges[7] = "b1";     //wednesday
        badges[10] = "b1";     //thursday
        badges[13] = "b1";     //friday*/
        //-----------------------------------DEBUG BADGES-----------------------------------//

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
                String fileName = dateFormat.format(cal.getTime()) + ".txt";

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
                if (i >= 4){
                    weekList.get(7)[0] = "b3";
                    return 1;
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
        return 0;
    }

    public int checkBadge5(){
        return 0;
    }

    public int checkBadge6(){
        return 0;
    }

    public int checkBadge7(){
        return 0;
    }

    /**
     * Pom completes
     * Check for achievements
     * getBadgeData()
     * check b1 - b7, skip checks for badges already earned
     * add new badges where applicable
     * persist
     */

}
