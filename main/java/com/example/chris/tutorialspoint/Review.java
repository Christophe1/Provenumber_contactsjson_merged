package com.example.chris.tutorialspoint;

import java.util.ArrayList;

import static java.util.logging.Logger.global;

public class Review {
    private String category, thumbnailUrl, name, phone, address, comment;
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
                  String reviewid
                  // double rating,
                  //ArrayList<String> genre
    ) {
        this.category = category;
       // this.thumbnailUrl = thumbnailUrl;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.comment = comment;
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

/*
    public ArrayList<String> getGenre() {
        return genre;
    }

    public void setGenre(ArrayList<String> genre) {
        this.genre = genre;
    }
*/

}
