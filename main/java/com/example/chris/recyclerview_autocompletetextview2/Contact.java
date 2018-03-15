package com.example.chris.recyclerview_autocompletetextview2;

/**
 * Created by Chris on 08/03/2018.
 */

public class Contact {
    String cat_id;
    String cat_name;
  //  String image;
    String phone;
  String type_row;

    public Contact() {
    }

    public String getCat_Id() {
        return cat_id;
    }

    public String getName() {
        return cat_name;
    }

    //which sort of row to show, fetched contacts or just items
    //in the recyclerView
    public String getType_row() {
        return type_row;
    }

    public void setType_row(String type_row) {
        this.type_row = type_row;
    }

   // public String getImage() {
  //      return image;
  //  }

    public String getPhone() {
        return phone;
    }
}