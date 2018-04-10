package com.example.chris.tutorialspoint;

/**
 * Created by Chris on 06/04/2018.
 */

public class Category {
    String cat_name;
    //  String image;
   // String user_review_ids;
    String user_personal_count;
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

/*    public String getUserReviewIds() {
        //return the value of the JSON key named user_personal_count in php file
       // return user_personal_count;
        return user_review_ids;
    }*/

    public String getUserPersonalCount() {
        //return the value of the JSON key named user_personal_count in php file
        return user_personal_count;
    }

    public String getPrivateCount() {
        //return the value of the JSON key named private_count in php file
        return private_count;
    }

    public String getPublicCount() {
        return public_count;
    }
}
