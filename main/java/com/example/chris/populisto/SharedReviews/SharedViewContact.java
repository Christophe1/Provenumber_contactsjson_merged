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
  //
  // this is the php file name where to select from.
  // we will post the review id of the review in ListView (in PopulistoListView.java) into Php and
  // get the matching details - Category, name, phone, address etc...
  private static final String ViewContact_URL = "http://www.populisto.com/ViewContact.php";

  // this is for the Delete button, the php file name where to select from.
  // we will post the review_id and delete associated fields - category, name, phone,
  // address and comment from the review table
  //private static final String DeleteContact_URL = "http://www.populisto.com/DeleteContact.php";

  //the edit button, if the user wants to edit a review
  //Button edit;
  //Button delete;

  //use TextViews instead of EditViews, so they can't be edited unless 'Edit' is selected
  private TextView textphoneNameonPhone;
  private TextView categoryname;
  private TextView namename;
  private TextView phonename;
  private TextView addressname;
  private TextView commentname;
  private TextView publicorprivate;
  private TextView publicorprivate2;

  // Button publicContacts;
  // Button phoneContacts;
  // Button justMeContacts;

  //for categoryid we only need the value, don't need to cast it to anything
  String categoryid;

  //this is the review that has been clicked in the recyclerView in PopulistoListView.java
  String review_id;

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
  String phoneNoofUser;

  //this is the phone number of person who made the clicked review in recyclerView
  // in PopulistoListView.java
  String phoneNumberofUserFromDB;

  private ProgressDialog pDialog;

  //selectPhoneContacts is an array list that will hold our SelectPhoneContact info
  ArrayList<SelectPhoneContact> selectPhoneContacts;

  //this is phone numbers who the phone owner is sharing the review with
  //ArrayList<String> checkedContactsAsArrayList;

  //all phone contact numbers, broken down
  //String phoneNumberofContact;


  //checkedContacts is a String, we get it from "checkedcontacts", on the server
  String checkedContacts;

  //this is for public or private reviews
  //amongst other things, we'll be bringing the intent over to EditContact.
  int pub_or_priv;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(activity_sharedview_contact);

    pDialog = new ProgressDialog(SharedViewContact.this);
    pDialog.setCancelable(false);
    // Showing progress dialog before making http request
    pDialog.setMessage("Loading...");
    pDialog.show();

    //need to initialize this
    PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, SharedViewContact.this, 0);

    //selectPhoneContacts is an empty array list that will hold our SelectPhoneContact info
    selectPhoneContacts = new ArrayList<SelectPhoneContact>();

    //rv is for holding the phone contacts, invite button, checkbox etc
    recyclerView = (RecyclerView) findViewById(rv);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    //31/7/2018
    //Show the back button (???)
    //ActionBar actionbar = getSupportActionBar();
    //actionbar.setDisplayHomeAsUpEnabled(true);
    //actionbar.setDisplayShowHomeEnabled(true);

    //31/7/2018
    //show the App title
    //actionbar.setTitle("Populisto");

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

    //31/7/2018
    //into the toolbar, inflate the back button and Populisto title,
    //which we find in toolbar_custom_view_layout.xml
    //View logo = getLayoutInflater().inflate(R.layout.toolbar_custom_view_layout, null);
    //toolbar.addView(logo);

    //31/7/2018
    //  when the activity loads, check to see if phoneNoofUser is using the App,if the user is
    // already registered, by checking the MyData XML file
    //SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
    //phoneNoofUser = sharedPreferences.getString("phonenumberofuser", "");

    Intent i = this.getIntent();

    //we'll be getting review_id from the cell clicked in the recyclerView,
    //intent review_id is in SharedPopulistoReviewsAdapter,
    //then posting to ViewContact.php to get associated details
    review_id = i.getStringExtra("review_id");

    phoneNameonPhone = i.getStringExtra("PhoneNameonPhone");


    getItemViewType = i.getIntExtra("getItemViewType", 0);

    //we'll be getting the phone number of user who made the review
    // clicked in the recyclerView,
    //phoneNumberofUserFromDB is in SharedPopulistoReviewsAdapter and UopulistoListAdapter
    //then posting to ViewContact.php
    //so we can decide what parts of review to show the logged-in user
    phoneNumberofUserFromDB = i.getStringExtra("PhoneNumberofUserFromDB");
    System.out.println("PhoneNumberofUserFromDB:" + phoneNumberofUserFromDB);
    System.out.println("phoneNoofUser:" + phoneNoofUser);

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
    publicorprivate = (TextView) findViewById(R.id.textPublicorPrivate);
    publicorprivate2 = (TextView) findViewById(R.id.textPublicorPrivate2);

    //for the Public, phoneContacts, justMe, save and cancel buttons
    // publicContacts = (Button) findViewById(R.id.btnPublic);
    // phoneContacts = (Button) findViewById(R.id.btnPhoneContacts);
    // justMeContacts = (Button) findViewById(R.id.btnJustMe);


    //post the review_id that has been clicked in the ListView and send it to
    // viewContact.php and from that get other review details, like name, address etc..
    StringRequest stringRequest = new StringRequest(Request.Method.POST, ViewContact_URL,
        new Response.Listener<String>() {

          @Override
          public void onResponse(String response) {

            //toast the response of ViewContact.php, which has been converted to a
            //JSON object by the Php file with JSON encode
            // Toast.makeText(ViewContact.this, "OnResponse is" + response, Toast.LENGTH_LONG).show();
            // System.out.println("ViewContact: And the response is " + response);


            try {

              // Parsing json object response which we receive from PHP
              // make a JSONObject called responseObject, break it down into
              //respective parts
              JSONObject responseObject = new JSONObject(response);
              String category = responseObject.getString("category");
              String category_id = responseObject.getString("category_id");
              String name = responseObject.getString("name");
              String phone = responseObject.getString("phone");
              String address = responseObject.getString("address");
              String comment = responseObject.getString("comment");
              String public_or_private = responseObject.getString("publicorprivate");
              checkedContacts = responseObject.getString("checkedcontacts");

              //load the asyncTask straight after we have got the response and
              // the checked Arraylist has been created
              //so the custom adapter will pick up the changes
/*
              SharedViewContact.LoadContact loadContact = new SharedViewContact.LoadContact();
              loadContact.execute();
*/


              //assign a textview to each of the fields in the review
              categoryname.setText(category);
              namename.setText(name);
              phonename.setText(phone);
              addressname.setText(address);
              commentname.setText(comment);

              //convert public_or_private to an integer
              pub_or_priv = Integer.parseInt(public_or_private);

              //shared_status will be Public, Phone Contacts or Just Me
              String shared_status = "";

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

              //for categoryid we only need the value, don't need to cast it to anything
              //we'll be sending this to EditContact with an intent
              categoryid = category_id;

              hidePDialog();
              //System.out.println("heree it is" + jsonResponse);
              //Toast.makeText(ContactView.this, jsonResponse, Toast.LENGTH_LONG).show();

            } catch (JSONException e) {
              e.printStackTrace();
              Toast.makeText(getApplicationContext(),
                  "Error: " + e.getMessage(),
                  Toast.LENGTH_LONG).show();
            }


          }


        },
        new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {
            Toast.makeText(SharedViewContact.this, "oh balls", Toast.LENGTH_LONG).show();

          }

        })


    {
      @Override
      protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        //we are posting review_id into our ViewContact.php file, which
        //we get when a row is clicked in populistolistview
        //to get matching details
        params.put("review_id", review_id);
        return params;

      }

    };
    //this is to hopefully end the VolleyTimeOut error message
    stringRequest.setRetryPolicy(new RetryPolicy() {
      @Override
      public int getCurrentTimeout() {
        return 50000;
      }

      @Override
      public int getCurrentRetryCount() {
        return 50000;
      }

      @Override
      public void retry(VolleyError error) throws VolleyError {

      }
    });

    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(stringRequest);

  }

  @Override
  protected void onResume() {

    super.onResume();

    // getPrefs();

    //    ViewContact.LoadContact loadContact = new ViewContact.LoadContact();


    //    loadContact.execute();
//        adapter.notifyDataSetChanged();
    //Toast.makeText(ViewContact.this, "resuming!", Toast.LENGTH_SHORT).show();


  }


  public void hidePDialog() {
    if (pDialog != null) {
      pDialog.dismiss();
      pDialog = null;
    }
  }


  //for the backbutton, clear the checkbox state
  //@Override
  public void onBackPressed() {
    // your code.
    //Integer i = null;
    SharedPreferences preferences = getSharedPreferences("sharedPrefsFile", 0);
    preferences.edit().clear().commit();
    finish();
  }


  protected void onDestroy() {

    super.onDestroy();
    //make sure that when the activity dies the load dialogue dies
    //with it, otherwise we get a memory leak error and app can crash
    hidePDialog();

  }


}