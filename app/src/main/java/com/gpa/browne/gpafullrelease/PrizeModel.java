package com.gpa.browne.gpafullrelease;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by B00075549 on 2/16/2018.
 */

public class PrizeModel {
    private Context context;
    private String prizes[];
    int balance;
    Calendar calToday;

    public PrizeModel(Context context){
        this.context = context;
        //firstDayOfThisWeek = getFirstDayOfThisWeek();
        prizes = new String[]{"","","","0"};
        balance = 0;
        detectSettings();

    }

    public void detectSettings() {
        File myMainDir = context.getDir("prizes", Context.MODE_PRIVATE);

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
                if(!prizes[3].isEmpty()) {
                    try {
                        //****************************debug balance**********************
                        balance = Integer.valueOf(prizes[3]);
                       //balance = Integer.valueOf("250");
                        //prizes[3] = "250";

                    } catch (Exception e) {
                        Log.i("INFO", "Error converting balance from string to int");
                    }
                }
            } catch (Exception e) {
                Log.i("INFO", "Unable to retrieve settings.");
                Log.i("INFO", e.getMessage());
                e.printStackTrace();
            }
        } else {
            Log.i("INFO","Prizes.txt not detected, default prizes generated");
            savePrizes(prizes);
        }
    }

    public String[] getprizes(){
        return prizes;
    }


    public void savePrizes(String[] prizes) {
        // add-write text into file

        String prizesData = "";
        prizesData = prizes[0] + "@" + prizes[1] + "@" +prizes[2] + "@" + prizes[3] ;

        try {
            File myMainDir = context.getDir("prizes", Context.MODE_PRIVATE);

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

    public String getPrizeLog(){
        // add-write text into file
        String rawPrizeLog ="";

        File myMainDir = context.getDir("prizeLog", Context.MODE_PRIVATE);

        File myFinalDir = new File(myMainDir, "prizeLog.txt");

        if (myFinalDir.exists()){
            Log.i("INFO","prizeLog.txt detected");
            Log.i("INFO", "Path: "+ myFinalDir.getAbsolutePath());
            Log.i("INFO", "File name: " + myFinalDir.getName());

            //parse prizes
            //reading text from file
            try {
                Log.i("INFO", "Retrieving prizeLog...");
                FileInputStream fis = new FileInputStream(new File(myFinalDir.getAbsolutePath()));
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                Log.i("INFO", "prizeLog Retrieved.");
                Log.i("INFO", "File contents: " + sb.toString());
                fis.close();
                rawPrizeLog = sb.toString();
                Log.i("INFO", rawPrizeLog);


            } catch (Exception e) {
                Log.i("INFO", "Unable to retrieve settings.");
                Log.i("INFO", e.getMessage());
                e.printStackTrace();
            }
        } else {
            Log.i("INFO","Prizes.txt not detected, default prizes generated");
        }
        return rawPrizeLog;
    }

    public void savePrizeLog(String prize){
        // add-write text into file
        String rawPrizeLog ="";

        File myMainDir = context.getDir("prizeLog", Context.MODE_PRIVATE);

        File myFinalDir = new File(myMainDir, "prizeLog.txt");

        if (myFinalDir.exists()){
            Log.i("INFO","prizeLog.txt detected");
            Log.i("INFO", "Path: "+ myFinalDir.getAbsolutePath());
            Log.i("INFO", "File name: " + myFinalDir.getName());

            //parse prizes
            //reading text from file
            try {
                Log.i("INFO", "Retrieving prizeLog...");
                FileInputStream fis = new FileInputStream(new File(myFinalDir.getAbsolutePath()));
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                Log.i("INFO", "prizeLog Retrieved.");
                Log.i("INFO", "File contents: " + sb.toString());
                fis.close();
                rawPrizeLog = sb.toString();
                Log.i("INFO", rawPrizeLog);


            } catch (Exception e) {
                Log.i("INFO", "Unable to retrieve settings.");
                Log.i("INFO", e.getMessage());
                e.printStackTrace();
            }
        } else {
            Log.i("INFO","Prizes.txt not detected, default prizes generated");
            savePrizes(prizes);
        }

        calToday = Calendar.getInstance();

        if(TextUtils.isEmpty(rawPrizeLog)){
            rawPrizeLog = calToday.getTime().toString() + "%" + prize;
        } else {
            rawPrizeLog = rawPrizeLog + "@" + calToday.getTime().toString() + "%" + prize;
        }

        try {

            FileOutputStream out = new FileOutputStream(myFinalDir, false); //Use the stream as usual to write into the file
            OutputStreamWriter outputWriter = new OutputStreamWriter(out);
            outputWriter.write(rawPrizeLog);
            outputWriter.close();
            Log.i("INFO", "PrizeLog Saved");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addXP(int xp) {
        int x;
        x = Integer.valueOf(prizes[3]);
        x = x + xp;
        prizes[3] = String.valueOf(x);
        savePrizes(prizes);
    }
}
