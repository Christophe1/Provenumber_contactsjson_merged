package com.example.chris.tutorialspoint;

import java.util.ArrayList;

public class Review {
    private String category, thumbnailUrl, name, phone, comment;
    private int year;
    private double rating;
    private ArrayList<String> genre;

    public Review() {
    }

    public Review(String category, String thumbnailUrl, String name, String phone,
                  String comment, int year, double rating,
                  ArrayList<String> genre) {
        this.category = category;
        this.thumbnailUrl = thumbnailUrl;
        this.name = name;
        this.phone = phone;
        this.comment = comment;
        this.year = year;
        this.rating = rating;
        this.genre = genre;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {this.year = year;}

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

    public ArrayList<String> getGenre() {
        return genre;
    }

    public void setGenre(ArrayList<String> genre) {
        this.genre = genre;
    }

}
