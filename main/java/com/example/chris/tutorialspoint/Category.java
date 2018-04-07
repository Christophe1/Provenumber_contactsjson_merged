package com.example.chris.tutorialspoint;

/**
 * Created by Chris on 06/04/2018.
 */

public class Category {
    String cat_name;
    //  String image;
    String private_count;
    String public_count;

    public Category() {
    }

    public String getName() {
        //return the value of the JSON key named cat_name in php file
        return cat_name;
    }

    // public String getImage() {
    //      return image;
    //  }

    public String getPrivateCount() {
        //return the value of the JSON key named private_count in php file
        return private_count;
    }

    public String getPublicCount() {
        return public_count;
    }
}
