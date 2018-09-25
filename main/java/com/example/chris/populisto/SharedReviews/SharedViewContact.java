package com.example.chris.populisto.SharedReviews;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chris.populisto.PopulistoContactsAdapter;
import com.example.chris.populisto.SelectPhoneContact;
import com.example.tutorialspoint.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.chris.populisto.PopulistoListView.recyclerView;
import static com.example.tutorialspoint.R.id.rv;
import static com.example.tutorialspoint.R.layout.activity_sharedview_contact;

public class SharedViewContact extends AppCompatActivity {

  //the edit button, if the user wants to edit a review
  //Button edit;
  //Button delete;

  //TextViews..the intents fetched from SharedPopulistoReviewsAdapter will be put in these
  private TextView textphoneNameonPhone;
  private TextView categoryname;
  private TextView namename;
  private TextView phonename;
  private TextView addressname;
  private TextView commentname;

  //this is the review that has been clicked in the recyclerView in PopulistoListView.java
  String review_id;

  String date_created, category, name, phone, address, comment;

  //the person who made the review,
  //can be "U", or the name of the person who made the review (if a contact)
  //or a masked phonenumber, if Public, to conceal identity
  String phoneNameonPhone;

  //an intent passed from SharedPopulistoListView so
  //we will know whether to make phoneNameonPhone BLUE or GREEN
  Integer getItemViewType;

  //the logged-in user's phone number, which we get in SharedPreferences
  //from VerifyUserPhoneNumber
  //to be compared to String phoneNumberofUserFromDB;
  //String phoneNoofUser;

  //this is the phone number of person who made the clicked review in recyclerView
  // in PopulistoListView.java
  String phoneNumberofUserFromDB;

  //private ProgressDialog pDialog;

  //selectPhoneContacts is an array list that will hold our SelectPhoneContact info
  ArrayList<SelectPhoneContact> selectPhoneContacts;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(activity_sharedview_contact);

    //need to initialize this
    PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, SharedViewContact.this, 0);

    //selectPhoneContacts is an empty array list that will hold our SelectPhoneContact info
    selectPhoneContacts = new ArrayList<SelectPhoneContact>();

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    //remove the app name from the toolbar (don't want it twice)
    getSupportActionBar().setDisplayShowTitleEnabled(false);

    //into the toolbar, inflate the back button and Populisto title,
    //which we find in toolbar_custom_view_layout.xml
    View logo = getLayoutInflater().inflate(R.layout.toolbar_custom_view_layout, null);
    toolbar.addView(logo);

    //for the back arrow, tell it to close the activity, when clicked
    ImageView backButton = (ImageView) logo.findViewById(R.id.back_arrow_id);
    backButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });

    Intent i = this.getIntent();

    //we'll be getting review_id from the cell clicked in the recyclerView,
    //intent review_id is in SharedPopulistoReviewsAdapter,
    review_id = i.getStringExtra("review_id");

    //also get the values of these intents from SharedPopulistoReviewsAdapter
    category = i.getStringExtra("category");
    name = i.getStringExtra("name");
    phone = i.getStringExtra("phone");
    address = i.getStringExtra("address");
    comment = i.getStringExtra("comment");

    phoneNameonPhone = i.getStringExtra("PhoneNameonPhone");

    getItemViewType = i.getIntExtra("getItemViewType", 0);

    //we'll be getting the phone number of user who made the review
    // clicked in the recyclerView,
    //phoneNumberofUserFromDB is in SharedPopulistoReviewsAdapter and UopulistoListAdapter
    //then posting to ViewContact.php
    //so we can decide what parts of review to show the logged-in user
    //phoneNumberofUserFromDB = i.getStringExtra("PhoneNumberofUserFromDB");
    //System.out.println("PhoneNumberofUserFromDB:" + phoneNumberofUserFromDB);
    //System.out.println("phoneNoofUser:" + phoneNoofUser);

    //coming from PopulistoListView we will always get a value for review_id
    //Let's save the review_id in shared preferences
    //so we can get it easily in EditContact,
    //and load the corresponding values into ViewContact on < press
    //PreferenceManager.getDefaultSharedPreferences(this).edit().putString("review_id value", review_id).apply();


    //cast a TextView for each of the field ids in activity_view_contact.xml
    textphoneNameonPhone = (TextView) findViewById(R.id.textphoneNameonPhone);
    categoryname = (TextView) findViewById(R.id.textViewCategory);
    namename = (TextView) findViewById(R.id.textViewName);
    phonename = (TextView) findViewById(R.id.textViewPhone);
    addressname = (TextView) findViewById(R.id.textViewAddress);
    commentname = (TextView) findViewById(R.id.textViewComment);

/*    24/9/2018
    publicorprivate = (TextView) findViewById(R.id.textPublicorPrivate);
    publicorprivate2 = (TextView) findViewById(R.id.textPublicorPrivate2);*/

    //for the Public, phoneContacts, justMe, save and cancel buttons
    // publicContacts = (Button) findViewById(R.id.btnPublic);
    // phoneContacts = (Button) findViewById(R.id.btnPhoneContacts);
    // justMeContacts = (Button) findViewById(R.id.btnJustMe);

    //assign a textview to each of the fields in the review
    categoryname.setText(category);
    namename.setText(name);
    phonename.setText(phone);
    addressname.setText(address);
    commentname.setText(comment);

    //getItemViewType has been passed with an intent
    //from SharedPopulistoReviewsAdapter
    //If it is "2" then make phoneNameonPhone in BLUE
    if (getItemViewType == 2) {

      textphoneNameonPhone.setTextColor(Color.parseColor("#0A7FDA"));
      textphoneNameonPhone.setText(phoneNameonPhone);

    }

    //getItemViewType has been transferred as an intent
    //from SharedPopulistoReviewsAdapter
    //If it is "3" then make phoneNameonPhone in GREEN
    if (getItemViewType == 3) {

      textphoneNameonPhone.setTextColor(Color.parseColor("#2AB40E"));
      textphoneNameonPhone.setText(phoneNameonPhone);

    }

  }

  @Override
  protected void onResume() {

    super.onResume();

  }

  protected void onDestroy() {

    super.onDestroy();
    //make sure that when the activity dies the load dialogue dies
    //with it, otherwise we get a memory leak error and app can crash
    //hidePDialog();

  }


}