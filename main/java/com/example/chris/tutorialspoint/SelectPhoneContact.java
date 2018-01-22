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

    public String getPhone() {
        return phone;
    }

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

    boolean isMatching;

    public boolean isMatching(){return isMatching;}

    public void setIsMatchingContact(boolean isMatching){

        this.isMatching = isMatching;

    }



    //*****************************************
    //this is for the checkbox
    //by default, make it unchecked
    boolean isSelected = false;

    public boolean getSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected){

            isSelected = selected;

    }


    String type_row;

    public String getType_row() {
        return type_row;
    }

    public void setType_row(String type_row) {
        this.type_row = type_row;
    }


}
