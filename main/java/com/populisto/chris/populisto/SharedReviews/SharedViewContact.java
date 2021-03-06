package com.populisto.chris.populisto.SharedReviews;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.populisto.chris.populisto.GlobalFunctions;
import com.populisto.chris.populisto.PopulistoContactsAdapter;
import com.populisto.chris.populisto.SelectPhoneContact;
import com.populisto.chris.populisto.R;

import java.util.ArrayList;

import static com.populisto.chris.populisto.R.layout.activity_sharedview_contact;

public class SharedViewContact extends AppCompatActivity {

  //the edit button, if the user wants to edit a review
  //Button edit;
  //Button delete;

  //TextViews..the intents fetched from SharedPopulistoReviewsAdapter will be put in these
  private TextView textphoneNameonPhone;
  private TextView date_created_name;
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
    //which we find in new_contact_toolbar_layout.xml
    View logo = getLayoutInflater().inflate(R.layout.sharedview_toolbar_layout, null);
    toolbar.addView(logo);

    //for the back arrow, tell it to close the activity, when clicked
    ImageView backButton = (ImageView) logo.findViewById(R.id.back_arrow_id);

    //use ontouch listener, so when <- image is DOWN it changes to grey
    //for an instant
    backButton.setOnTouchListener(new View.OnTouchListener() {

      @Override
      public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN: {
            ImageView view = (ImageView) v;

            view.getDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
            view.invalidate();

            //for the back arrow, tell it to close the activity, when clicked
            finish();
            break;
          }
          case MotionEvent.ACTION_UP:
          case MotionEvent.ACTION_CANCEL: {
            ImageView view = (ImageView) v;
            //clear the overlay
            view.getDrawable().clearColorFilter();
            view.invalidate();
            break;
          }
        }
        return true;
      }
    });


    Intent i = this.getIntent();

    //we'll be getting review_id from the cell clicked in the recyclerView,
    //intent review_id is in SharedPopulistoReviewsAdapter,
    review_id = i.getStringExtra("review_id");

    //also get the values of these intents from SharedPopulistoReviewsAdapter
    date_created = i.getStringExtra("date_created");
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
    date_created_name = (TextView) findViewById(R.id.textViewDateCreated);
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
    date_created_name.setText(date_created);
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

      //make the border blue
      GlobalFunctions.sharing_border_colour(this, "#0A7FDA");


    }

    //getItemViewType has been transferred as an intent
    //from SharedPopulistoReviewsAdapter
    //If it is "3" then make phoneNameonPhone in GREEN
    if (getItemViewType == 3) {

      textphoneNameonPhone.setTextColor(Color.parseColor("#2AB40E"));
      textphoneNameonPhone.setText(phoneNameonPhone);

      //make the border green
      GlobalFunctions.sharing_border_colour(this, "#2AB40E");
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