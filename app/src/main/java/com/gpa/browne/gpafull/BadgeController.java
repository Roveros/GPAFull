package com.gpa.browne.gpafull;

import android.content.Context;
import android.widget.EditText;

/**
 * Created by B00075549 on 1/17/2018.
 */

public class BadgeController {
    private Context context;
    private BadgeModel model;
    private EditText etSessionTitle;

    public BadgeController(Context context, EditText etSessionTitle){
        this.context = context;
        this.etSessionTitle = etSessionTitle;
        model =  new BadgeModel(context, etSessionTitle.getText().toString());
    }


    public void checkAllBadges() {
        model = null;
        model = new BadgeModel(context, etSessionTitle.getText().toString());
        model.checkAllBadges();
       // model.persist();
    }
}
