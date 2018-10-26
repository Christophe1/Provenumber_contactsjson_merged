package com.example.chris.populisto;

public class Review {
  private String phone_user_name, date_created, category, type_row,publicorprivate, name,
      phone, address, comment, phoneNumberofUserFromDB, phoneNameonPhone;
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
                String date_created,
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
    this.date_created = date_created;
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

  public String getDate_created() {
    return date_created;
  }

  public void setDate_created(String date_created) {
    this.date_created = date_created;
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

  //is it a U review, or somebody else's review?
  //we set it to either 1 or 2
  public String getType_row() {
    return type_row;
  }

  //is it a U review, or somebody else's review?
  public void setType_row(String type_row) {
    this.type_row = type_row;
  }

  //it will be "U" or the name of person on logged-in user's phone
  //or a masked phone number
  public String getPhoneNameonPhone() {
    return phoneNameonPhone;
  }

  public void setphoneNameonPhone(String phoneNameonPhone) {
    this.phoneNameonPhone = phoneNameonPhone;
  }

}
