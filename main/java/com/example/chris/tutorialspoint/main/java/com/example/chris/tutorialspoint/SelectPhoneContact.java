package com.example.chris.tutorialspoint;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import static android.R.attr.value;

/**
 * Created by Chris on 23/06/2017.
 */

public class SelectPhoneContact {

    String phone;

    public String getPhone() {return phone;}

    public void setPhone(String phone) {
        this.phone = phone;
    }

    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //*****************************************
    //this is for the checkbox
    boolean selected = false;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected){

            this.selected = selected;

    }


}
