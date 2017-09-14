package com.example.chris.tutorialspoint;

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

    Boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected){
        this.selected=selected;
    }

    //******************************************


/*
    Integer value;

    public int getValue(){
        return value;
    }


    String lookup;
    public String getLookup() {
        return lookup;
    }
    public void setLookup(String lookup) {
        this.lookup = lookup;
    }
*/


}
