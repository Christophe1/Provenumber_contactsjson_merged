package com.example.chris.tutorialspoint;

/**
 * Created by Chris on 30/05/2018.
 */

public class CategoryList {

  private int id;
  private String name;

  public CategoryList(int id, String name){

    this.id = id;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
