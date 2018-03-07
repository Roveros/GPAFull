package com.gpa.browne.gpafull;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.Arrays;

public class PrizesActivity extends AppCompatActivity {
    EditText etEasyPrize, etMediumPrize, etHardPrize;
    TextView tvBalance;
    String[] prizes;
    int balance;
    PrizeModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prizes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //this places a back button on the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        model = new PrizeModel(getApplicationContext());

        etEasyPrize = findViewById(R.id.etEasyPrize);
        etMediumPrize = findViewById(R.id.etMediumPrize);
        etHardPrize = findViewById(R.id.etHardPrize);
        tvBalance = findViewById(R.id.tvBalance);

        prizes = new String[]{"","","","0"};
        balance = 0;

        detectSettings();

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

        model.savePrizes(prizes);
        finish();
    }

    public void onClaimClick(View view) {
        //dialog box here plx
        ArrayList<String> temp = new ArrayList<>();
        for (int i = 100; i < 400; i += 100) {
            if(!TextUtils.isEmpty(prizes[(i / 100) - 1])){
                temp.add(prizes[(i / 100) - 1]);
            }
        }

        if(temp.isEmpty()){
            Toast.makeText(this, "No prizes listed. Please set prizes for 100, 150 and 200 XP.", Toast.LENGTH_SHORT).show();
        } else {
            CharSequence choices[] = temp.toArray(new CharSequence[temp.size()]);
            //CharSequence choices[] = new CharSequence[] {prizes[0], prizes[1], prizes[2]};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Pick a Prize");
            builder.setItems(choices, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // the user clicked on colors[which]
                    int cost = 0;

                    switch (which){
                        case 0 : cost = 100;
                        break;
                        case 1 : cost = 150;
                        break;
                        case 2 : cost = 200;
                        break;
                    }

                    if(cost <= balance){
                        Log.i("INFO", "Redeemed Prize");
                        prizes[3] = String.valueOf((Integer.valueOf(prizes[3])) - cost);
                        balance = (Integer.valueOf(prizes[3]) - cost);
                        model.savePrizes(prizes);
                        detectSettings();
                        Toast.makeText(PrizesActivity.this, "You have redeemed: " + prizes[which], Toast.LENGTH_SHORT).show();

                        switch (cost){
                            case 100 : model.savePrizeLog("easy");
                                break;
                            case 150 : model.savePrizeLog("medium");
                                break;
                            case 200 : model.savePrizeLog("hard");
                                break;
                        }
                    } else {
                        Toast.makeText(PrizesActivity.this, "You do not have enough XP to redeem this prize.", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            builder.show();
        }
    }

    public void onHelpClick(View view) {
        Toast.makeText(this, "Set your 100, 150 and 200 XP prizes and when you have enough XP CLAIM THEM!", Toast.LENGTH_LONG).show();
    }

    private void detectSettings() {
        prizes = model.getprizes();
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
                //****************************debug balance**********************
                balance = Integer.valueOf(prizes[3]);
                //balance = Integer.valueOf("250");
                //prizes[3] = "250";
                tvBalance.setText("Current Balance: " + balance + " XP");
            } catch (Exception e) {
                Log.i("INFO", "Error converting balance from string to int");
                tvBalance.setText("Current Balance: " + balance + " XP");
            }
        }
    }
}
