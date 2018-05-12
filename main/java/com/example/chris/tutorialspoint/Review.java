package com.example.chris.tutorialspoint;

import java.util.ArrayList;

import static java.util.logging.Logger.global;

public class Review {
  private String phone_user_name, category, publicorprivate, name,
      phone, address, comment, phoneNumberofUserFromDB;
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

  public Review(String publicorprivate,
                String phone_user_name,
                String category,
                String name,
                String phone,
                String address,
                String comment,
                String reviewid,
                String phoneNumberofUserFromDB
                //ArrayList<String> genre
  ) {
    this.publicorprivate = publicorprivate;
    this.phone_user_name = phone_user_name;
    this.category = category;
    this.name = name;
    this.phone = phone;
    this.address = address;
    this.comment = comment;
    this.reviewid = reviewid;
    this.phoneNumberofUserFromDB = phoneNumberofUserFromDB;
  }

  //get 0,1 or 2 value, for Just U, private or public
  public String getPublicorprivate() {
    return publicorprivate;
  }

  public void setPublicorprivate(String publicorprivate) {
    this.publicorprivate = publicorprivate;
  }


  public String getPhone_user_name() {
    return phone_user_name;
  }

  public void setPhone_user_name(String phone_user_name) {
    this.phone_user_name = phone_user_name;
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

  public String getPhoneNumberofUserFromDB() {
    return phoneNumberofUserFromDB;
  }

  public void setPhoneNumberofUserFromDB(String phoneNumberofUserFromDB) {
    this.phoneNumberofUserFromDB = phoneNumberofUserFromDB;
  }

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
