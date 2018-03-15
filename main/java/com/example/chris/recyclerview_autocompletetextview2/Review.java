package com.example.chris.recyclerview_autocompletetextview2;

/**
 * Created by Chris on 14/03/2018.
 */

public class Review {

    private String category, thumbnailUrl, name, phone, address, comment, cat_id;
    public String reviewid;
    //int reviewid;
    //  private int year;
    //  private double rating;
    //private ArrayList<String> genre;
    //public static String reviewidtoString;
    // public static String reviewid;

    //this needs to be here even though it is empty,
    //otherwise I get an error
    public Review() {
    }

    public Review(String category,
                  // String thumbnailUrl,
                  String name,
                  String phone,
                  String address,
                  String comment,
                  String reviewid,
                  String cat_id
                  // double rating,
                  //ArrayList<String> genre
    ) {
        this.category = category;
        // this.thumbnailUrl = thumbnailUrl;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.comment = comment;
        this.cat_id = cat_id;
        //   this.year = year;
        //   this.rating = rating;
        //  this.genre = genre;
        this.reviewid = reviewid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCat_Id() {
        return cat_id;
    }

/*
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /*public int getYear() {
        return year;
    }*/

/*
    public void setYear(int year) {this.year = year;}
*/

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReviewid() {
        return reviewid;
    }

    public void setReviewid(String reviewid) {
        this.reviewid = reviewid;
        //reviewidtoString = String.valueOf(reviewid);
    }


}
