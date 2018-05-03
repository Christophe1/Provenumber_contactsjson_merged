package com.example.chris.tutorialspoint.SharedReviews;

public class SharedReview {
    private String phoneNameonPhone, publicorprivate, category,
            thumbnailUrl, name, phone, address, comment, PhoneNumberofUserFromDB;
    public String reviewid;


    //this needs to be here even though it is empty,
    //otherwise I get an error
    public SharedReview() {
    }

    public SharedReview(String publicorprivate,
                        String category,
                        String phoneNameonPhone,
                        // String thumbnailUrl,
                        String name,
                        String phone,
                        String address,
                        String comment,
                        String PhoneNumberofUserFromDB,
                        String reviewid
                        // double rating,
                        //ArrayList<String> genre
    ) {
        this.publicorprivate = publicorprivate;
        this.phoneNameonPhone = phoneNameonPhone;
        this.category = category;
       // this.thumbnailUrl = thumbnailUrl;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.comment = comment;
        this.PhoneNumberofUserFromDB = PhoneNumberofUserFromDB;
        this.reviewid = reviewid;
    }


    //get 0,1 or 2 value, for Just U, private or public
    public String getPublicorprivate() {
        return publicorprivate;
    }

    public void setPublicorprivate(String publicorprivate) {
        this.publicorprivate = publicorprivate;
    }

    public String getPhoneNumberofUserFromDB() {
        return PhoneNumberofUserFromDB;
    }

    public void setPhoneNumberofUserFromDB(String PhoneNumberofUserFromDB) {
        this.PhoneNumberofUserFromDB = PhoneNumberofUserFromDB;
    }

    public String getPhoneNameonPhone() {
        return phoneNameonPhone;
    }

    public void setphoneNameonPhone(String phoneNameonPhone) {
        this.phoneNameonPhone = phoneNameonPhone;
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
