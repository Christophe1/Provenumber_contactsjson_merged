package com.example.chris.populisto;

public class Category {
    String cat_name;
    String [] user_review_ids;
    String [] private_review_ids;
    String [] public_review_ids;
    String user_personal_count;
    String private_count;
    String public_count;

    public Category() {
    }

    public String getName() {
        //return the value of the JSON key named cat_name in php file
        return cat_name;
    }

/*    public void setName(String cat_name) {
        this.cat_name = cat_name;
    }*/

    public String[] getUserReviewIds() {
        //return the value of the JSON key named user_review_ids in php file
        return user_review_ids;
    }

    public String[] getPrivateReviewIds() {
        //return the value of the JSON key named private_review_ids in php file
        // return user_personal_count;
        return private_review_ids;
    }

    public String[] getPublicReviewIds() {
        //return the value of the JSON key named private_review_ids in php file
        // return user_personal_count;
        return public_review_ids;
    }

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
