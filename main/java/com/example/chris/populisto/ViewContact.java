package com.example.chris.populisto;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tutorialspoint.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.example.chris.populisto.PopulistoContactsAdapter.allPhonesofContacts;
import static com.example.tutorialspoint.R.id.rv;
import static com.example.tutorialspoint.R.id.sharedWith;
import static com.example.tutorialspoint.R.layout.activity_view_contact;

//DESCRIPTION OF ACTIVITY
//This is one of the logged-in user's own reviews
//He can see EDIT and DELETE buttons and modify accordingly.

public class ViewContact extends AppCompatActivity {
  //
  // this is the php file name where to select from.
  // we will post the review id of the review in ListView (in PopulistoListView.java) into Php and
  // get the matching details - Category, name, phone, address etc...
  private static final String ViewContact_URL = "http://www.populisto.com/ViewContact.php";

  // this is for the Delete button, the php file name where to select from.
  // we will post the review_id and delete associated fields - category, name, phone,
  // address and comment from the review table
  private static final String DeleteContact_URL = "http://www.populisto.com/DeleteContact.php";

  //the edit button, if the user wants to edit a review
  Button edit;
  Button delete;

  //use TextViews instead of EditViews, so they can't be edited unless 'Edit' is selected
  private TextView textphoneNameonPhone;
  private TextView date_created_name;
  private TextView categoryname;
  private TextView namename;
  private TextView phonename;
  private TextView addressname;
  private TextView commentname;
 // 4/7/2018 private TextView publicorprivate;
  private TextView sharedWith;

//4/7/2018
/*  Button publicContacts;
  Button phoneContacts;
  Button justMeContacts;*/

  //5/7/2018
  //for categoryid we only need the value, don't need to cast it to anything
  //String categoryid;

  //this is the review that has been clicked in the recyclerView in PopulistoListView.java
  //We need this for passing to EditContact, so if the changes are saved, we know where to make
  //changes in the DB by following review_id
  String review_id;

  String date_created, category, name, phone, address, comment;

  //5/7/2018
  //String new_category_value;
  //the logged-in user's phone number, which we get in SharedPreferences
  //from VerifyUserPhoneNumber
  //to be compared to String phoneNumberofUserFromDB;
  //String phoneNoofUser;

  //this is the phone number of person who made the clicked review in recyclerView
  // in PopulistoListView.java
  String phoneNumberofUserFromDB;

  private ProgressDialog pDialog;

  //this is for the progress dialog, while logged-in user is
  //waiting for shared contacts of the review from server
  DelayedProgressDialog progressDialog = new DelayedProgressDialog();


  //selectPhoneContacts is an array list that will hold our SelectPhoneContact info
  ArrayList<SelectPhoneContact> selectPhoneContacts;

  //this is phone numbers who the phone owner is sharing the review with
  ArrayList<String> checkedContactsAsArrayList;

  //all phone contact numbers, broken down
  String phoneNumberofContact;
  //all phone contact names, broken down
  String phoneNameofContact;

  //checkedContacts is a String, we get it from "checkedcontacts", on the server
  String checkedContacts;

  //this is for logged-in user's Just U, phone contacts or public
  //amongst other things, we'll be bringing the intent over to EditContact.
  int pub_or_priv;

  //String shared_status;
  //For the recycler view, containing the phone contacts
  RecyclerView recyclerView;

  //Sharedprefs containing all phone contacts of logged-in user
  SharedPreferences sharedPreferencesallPhonesofContacts;

  //if user has no contacts on his phone, like if no
  //permission has been given to getPhoneContacts
  TextView noContactsFound;


  //if it is 0, then show the 'No Contacts Found' textbox
  int noContactFoundCheck;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(activity_view_contact);

    noContactsFound = (TextView) findViewById(R.id.noContactsFoundView);


    //9/8/2018
/*    pDialog = new ProgressDialog(ViewContact.this);
    pDialog.setCancelable(true);
    // Showing progress dialog before making http request
    pDialog.setMessage("Loading...");
    pDialog.show();*/

    //show the "Loading" dialog
    progressDialog.show(getSupportFragmentManager(), "tag");


    //setContentView(R.layout.verify_phone_number);
    //RelativeLayout progressContainer = findViewById(R.id.container);// change id here

    //progressContainer.setVisibility(View.VISIBLE);

    //need to initialize this
    PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, ViewContact.this, 0);

    //selectPhoneContacts is an empty array list that will hold our SelectPhoneContact info
    selectPhoneContacts = new ArrayList<SelectPhoneContact>();

    //System.out.println("ViewContact: selectPhoneContacts " + selectPhoneContacts);

    //rv is for holding the phone contacts, invite button, checkbox etc
    recyclerView = (RecyclerView) findViewById(rv);

    //put in a toolbar
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    //31/7/2018
    //Show the back button (???)
    //ActionBar actionbar = getSupportActionBar();
    //actionbar.setDisplayHomeAsUpEnabled(true);
    //actionbar.setDisplayShowHomeEnabled(true);

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

    //for the delete button
    delete = (Button) findViewById(R.id.delete);

    //call the delete review function
    deleteContactButton();

    //4/7/2018
    //  when the activity loads, check to see if phoneNoofUser is using the App,if the user is
    // already registered, by checking the MyData XML file
    //SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
    //phoneNoofUser = sharedPreferences.getString("phonenumberofuser", "");

    Intent i = getIntent();

    //we'll be getting review_id from the cell clicked in the recyclerView,
    //intent review_id is in UPopulistoListAdapter
    //then posting to ViewContact.php to get associated details
    review_id = i.getStringExtra("review_id");

    //get the values of these intents from UPopulistoListAdapter
    //pub_or_priv is an integer, need the "0"
    pub_or_priv = i.getIntExtra("UPuborPrivVal", 0);
    date_created = i.getStringExtra("date_created");
    category = i.getStringExtra("category");
    name = i.getStringExtra("name");
    phone = i.getStringExtra("phone");
    address = i.getStringExtra("address");
    comment = i.getStringExtra("comment");


    //5/7/2018 category = i.getStringExtra("category2");


    //we'll be getting the phone number of user who made the review
    // clicked in the recyclerView,
    //phoneNumberofUserFromDB is in SharedPopulistoReviewsAdapter and UpulistoListAdapter
    //then posting to ViewContact.php
    phoneNumberofUserFromDB = i.getStringExtra("PhoneNumberofUserFromDB");


    //cast a TextView for each of the field ids in activity_view_contact.xml
    //textphoneNameonPhone if in ViewContact will always be "U"
    textphoneNameonPhone = (TextView) findViewById(R.id.textphoneNameonPhone);
    date_created_name = (TextView) findViewById(R.id.textViewDateCreated);
    categoryname = (TextView) findViewById(R.id.textViewCategory);
    namename = (TextView) findViewById(R.id.textViewName);
    phonename = (TextView) findViewById(R.id.textViewPhone);
    addressname = (TextView) findViewById(R.id.textViewAddress);
    commentname = (TextView) findViewById(R.id.textViewComment);
    //publicorprivate = (TextView) findViewById(R.id.textPublicorPrivate);
    sharedWith = (TextView) findViewById(R.id.textSharedWith);



    //System.out.println("PhoneNumberofUserFromDB:" + phoneNumberofUserFromDB);
    //System.out.println("phoneNoofUser:" + phoneNoofUser);
//5/7/2018
    //if we are coming from EditContact, where no recyclerView cell has been clicked
    //if (review_id == null) {

    //then set review_id to the value we put in shared preferences
    // review_id = PreferenceManager.getDefaultSharedPreferences(this).getString("review_id value", review_id);

    // }

    //System.out.println("ViewContact: review id is " + review_id);

    //5/7/2018
    //coming from PopulistoListView we will always get a value for review_id
    //Let's save the review_id in shared preferences
    //so we can get it easily in EditContact,
    //and load the corresponding values into ViewContact on < press
    //PreferenceManager.getDefaultSharedPreferences(this).edit().putString("review_id value", review_id).apply();


    //4/7/2018
    //for the Public, phoneContacts, justMe, save and cancel buttons
    //publicContacts = (Button) findViewById(R.id.btnPublic);
    //phoneContacts = (Button) findViewById(R.id.btnPhoneContacts);
    //justMeContacts = (Button) findViewById(R.id.btnJustMe);



    //set text in textboxes to the values in cell of recyclerView, intents passed from UPopulistoListAdapter
    date_created_name.setText(date_created);
    categoryname.setText(category);
    namename.setText(name);
    phonename.setText(phone);
    addressname.setText(address);
    commentname.setText(comment);

    Toast.makeText(ViewContact.this, "coming from UPopulisto: " + i.getStringExtra("review_id"), Toast.LENGTH_LONG).show();

    //if we are coming from UPopulistoListAdapter then
    //we will have a value for review_id at this stage
    //so review_id is not null, make the volley call

    //(Otherwise, we are coming from EditContact, review_id
    //IS NULL at this stage. So don't make the Volley call,
    //we will be passing checkContacts as an intent, from EditContact to ViewContact)
    if (i.getStringExtra("review_id")!=null) {

      Toast.makeText(ViewContact.this, categoryname.getText().toString(), Toast.LENGTH_LONG).show();

      //post the review_id that has been clicked in the recyclerView of previous activity and send it to
      //viewContact.php. from that we will get the checked contact details,
      //who the review is shared with
      StringRequest stringRequest = new StringRequest(Request.Method.POST, ViewContact_URL,
          new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

              //dismiss the dialog when we get the response
              progressDialog.cancel();

              //toast the response of ViewContact.php, which has been converted to a
              //JSON object by the Php file with JSON encode
              Toast.makeText(ViewContact.this, "OnResponse is : " + response, Toast.LENGTH_LONG).show();
              // System.out.println("ViewContact: And the response is " + response);


              try {

                // Parsing json object response which we receive from PHP
                // make a JSONObject called responseObject, break it down into
                //respective parts
                JSONObject responseObject = new JSONObject(response);

                //5/7/2018
                // String category = responseObject.getString("category");
                //String category_id = responseObject.getString("category_id");
                // String name = responseObject.getString("name");
                // String phone = responseObject.getString("phone");
                // String address = responseObject.getString("address");
                // String comment = responseObject.getString("comment");
                //String public_or_private = responseObject.getString("publicorprivate");

                //get the phone numbers with whom the review is shared
                checkedContacts = responseObject.getString("checkedcontacts");

                //load the asyncTask straight after we have got the response and
                // the checked Arraylist has been created
                //so the PopulistoContactsAdapter will show recyclerView with contacts, checked etc...
                ViewContact.LoadContact loadContact = new ViewContact.LoadContact();
                loadContact.execute();

                //5/7/2018
                //assign a textview to each of the fields in the review
                //categoryname.setText(category);
                //namename.setText(name);
                //phonename.setText(phone);
                //addressname.setText(address);
                //commentname.setText(comment);

                //convert public_or_private to an integer
                //pub_or_priv = Integer.parseInt(public_or_private);


                // System.out.println("ViewContact: public or private value :" + pub_or_priv);

                //05/7/2018
                //for categoryid we only need the value, don't need to cast it to anything
                //we'll be sending this to EditContact with an intent
                //categoryid = category_id;

                // System.out.println("here are the checkedcontacts" + checkedContacts);
                //  Toast.makeText(ViewContact.this, "here are the checkedcontacts" + checkedContacts, Toast.LENGTH_SHORT).show();


                //05/07/2018
//*              if (phoneNoofUser.equals(phoneNumberofUserFromDB)) {
//05/07/2018
                // Toast.makeText(ViewContact.this, "Yes they match!", Toast.LENGTH_SHORT).show();
/*                edit.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
              }*/


                //System.out.println("ViewContact2: checkedContactsAsArrayList is " + checkedContactsAsArrayList);


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
              Toast.makeText(ViewContact.this, error.toString(), Toast.LENGTH_LONG).show();

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

    //we are coming from EditContact, review_id at this stage IS NULL...
    //...so we won't be making the Volley call for checkedContacts
    else
    {
      Intent intent = getIntent();

      //update the class with these values from EditView

      review_id = intent.getStringExtra("reviewfromedit");

      String date_created_value = intent.getStringExtra("datecreatedfromedit");
      String category_value = intent.getStringExtra("categoryfromedit");
      String name_value = intent.getStringExtra("namefromedit");
      String phone_value = intent.getStringExtra("phonefromedit");
      String address_value = intent.getStringExtra("addressfromedit");
      String comment_value = intent.getStringExtra("commentfromedit");

      //we get this intent from EditContact
      //(we passed it originally from ViewContact) because otherwise it is null, as not
      //coming from UPopulistoListAdapter
      checkedContacts = intent.getStringExtra("checkedContactsfromedit");

      pub_or_priv = intent.getIntExtra("publicorprivatefromedit", 0);

      date_created_name.setText(date_created_value);
      categoryname.setText(category_value);
      namename.setText(name_value);
      phonename.setText(phone_value);
      addressname.setText(address_value);
      commentname.setText(comment_value);

      ViewContact.LoadContact loadContact = new ViewContact.LoadContact();
      loadContact.execute();

      //dismiss the dialog when we come back to ViewContact
      progressDialog.cancel();

    }


    //shared_status will be "Just U", "Phone Contacts" or "Public"
    String shared_status = "";

    //pub_or_priv is an intent passed from UPopulistoListAdapter
    if (pub_or_priv == 0) {
      //change colour depending on value
      sharedWith.setTextColor(Color.parseColor("#DA850B"));
      shared_status = "Just U";
      //for "U"
      textphoneNameonPhone.setTextColor(Color.parseColor("#DA850B"));
      textphoneNameonPhone.setText("U");
    }

    if (pub_or_priv == 1) {
      sharedWith.setTextColor(Color.parseColor("#0A7FDA"));
      shared_status = "Phone Contacts";
      textphoneNameonPhone.setTextColor(Color.parseColor("#0A7FDA"));
      textphoneNameonPhone.setText("U");
    }

    if (pub_or_priv == 2) {
      sharedWith.setTextColor(Color.parseColor("#2AB40E"));
      shared_status = "Public";
      textphoneNameonPhone.setTextColor(Color.parseColor("#2AB40E"));
      textphoneNameonPhone.setText("U");
    }

    //put pub_or_priv in the textbox called publicorprivate
    sharedWith.setText("Shared with: " + shared_status);


    //*****************************************
    //for the edit button
    edit = (Button) findViewById(R.id.edit);


    edit.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        System.out.println("you clicked it, edit");
        //open the Edit Activity, pass over the review_id so we can get that reviews
        //associated fields
        Intent i = new Intent(ViewContact.this, EditContact.class);
        //pass review_id to EditContact, so we can get the review we are
        //editing and save it
        i.putExtra("review_id", review_id);
        //"category" is the key
        // which we will be looking for from EditContact.class, categoryname.getText() is the
        // content to pass from ViewContact.class etc....
        i.putExtra("date_created", date_created_name.getText());
        i.putExtra("category", categoryname.getText());
        //i.putExtra("category_id", categoryid);
        i.putExtra("name", namename.getText());
        i.putExtra("phone", phonename.getText());
        i.putExtra("address", addressname.getText());
        i.putExtra("comment", commentname.getText());
        i.putExtra("checkedContacts", checkedContacts);

        //bring the pub_or_private value to EditContact.class, for the right button to be
        // appropriately coloured
        i.putExtra("publicorprivate", pub_or_priv);

        startActivity(i);

        //close this activity, ViewContact
        finish();

      }
    });


    //This is for when we are coming back to ViewContact class,
    //after user has finished editing in EditContact
    //get the "category" key,make it into a string called
    //new_category_value, and then set it into the categoryname
    //text box.
    //update the class with all these new values from EditView
    //Intent intent = getIntent();

    //if <- button is pressed in EditContact
    //get the intent values and fill in text values in ViewContact
    //length of categoryname will be 0 as no intent has been passed
    //from UPopulistoListAdapter, because we are coming from EditContact,
    //so the textbox length is 0
    //if (categoryname.getText().toString().trim().length() == 0) {
/*
    {
      Intent intent = getIntent();

      //update the class with these values from EditView
      String category_value = intent.getStringExtra("categoryfromedit");
      String name_value = intent.getStringExtra("namefromedit");
      String phone_value = intent.getStringExtra("phonefromedit");
      String address_value = intent.getStringExtra("addressfromedit");
      String comment_value = intent.getStringExtra("commentfromedit");

      pub_or_priv = i.getIntExtra("UPuborPrivVal", 0);

      categoryname.setText(category_value);
      namename.setText(name_value);
      phonename.setText(phone_value);
      addressname.setText(address_value);
      commentname.setText(comment_value);

    }*/


      // fetchCheckedContacts();
   // }
  }

  //this is the function for Volley, trying to change from AsycnTask to Volley
  //for getting the checked contacts on phone
  //private void fetchCheckedContacts() {


  //}

  //code for the '<', back button. Go back to PopulistoListView, as defined
  //in Manifest, PARENT_ACTIVITY
/*  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }*/


  private void deleteContactButton() {

    delete.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {

        //add a dialogue box
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage("Are you sure you want to delete this?").setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener).show();

      }

    });


  }

  //Are you sure you want to delete? dialogue
  DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
      switch (which) {
        case DialogInterface.BUTTON_POSITIVE:
          //Yes button clicked

          //close the populistolistview class
          //(we'll be opening it again, will close now so it will be refreshed)
          PopulistoListView.fa.finish();

          Toast.makeText(ViewContact.this, "delete stuff", Toast.LENGTH_SHORT).show();

          pDialog = new ProgressDialog(ViewContact.this);
          // Showing progress dialog for the review being deleted
          pDialog.setMessage("Deleting...");
          pDialog.show();

          //post the review_id in the current activity to DeleteContact.php and
          //delete the review
          StringRequest stringRequest = new StringRequest(Request.Method.POST, DeleteContact_URL,
              new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                  //hide the dialogue saying 'Deleting...' when page is deleted
                  pDialog.dismiss();
                  //the response in deleteContact.php is "deleted successfully"
                  Toast.makeText(ViewContact.this, response, Toast.LENGTH_LONG).show();
                }
              },
              new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                  Toast.makeText(ViewContact.this, "problem here", Toast.LENGTH_LONG).show();

                }

              }) {

            @Override
            protected Map<String, String> getParams() {
              Map<String, String> params = new HashMap<String, String>();
              //we are posting review_id into our DeleteContact.php file,
              //the second value, review_id,
              // is the value we get from Android.
              // When we see this in our php,  $_POST["review_id"],
              //put in the value from Android
              params.put("review_id", review_id);
              return params;
            }


          };


          AppController.getInstance().addToRequestQueue(stringRequest);

          //when deleted, back to the PopulistoListView class and update

          Intent j = new Intent(ViewContact.this, PopulistoListView.class);

          startActivity(j);

//                finish();


          break;

        case DialogInterface.BUTTON_NEGATIVE:
          //No button clicked, just close the dialogue
          break;
      }
    }
  };


  //******for the phone contacts in the recyclerView

  // Load data in background
  class LoadContact extends AsyncTask<Void, Void, Void> {
    @Override
    protected void onPreExecute() {
      super.onPreExecute();


    }

    @Override
    protected Void doInBackground(Void... voids) {

      //convert the checkedContacts string which we get from server to an arraylist
      //then we will search through the arraylist and check the associated
      //check boxes
      //First, take out the double quotes in the string, \ is to escape the "
      String replace = checkedContacts.replace("\"", "");
      //take out the starting [
      String replace1 = replace.replace("[", "");
      //and then the ending ]
      String replace2 = replace1.replace("]", "");
      System.out.println("here is replace2 " + replace2);

      //convert the checkedContacts string to an arraylist
      checkedContactsAsArrayList = new ArrayList<String>(Arrays.asList(replace2.split(",")));
      System.out.println("ViewContact1: checkedContactsAsArrayList is " + checkedContactsAsArrayList);

     // Toast.makeText(ViewContact.this, "checked contacts is: " + checkedContactsAsArrayList, Toast.LENGTH_SHORT).show();

      //we want to bring the checkedContactsAsArrayList array list to our PopulistoContactAdapter so
      //we can show contacts with ticked checkboxes in recyclerView
      // It looks like Shared Preferences
      //only works easily with strings so best way to bring the array list in Shared Preferences is with
      //Gson.
      //Here, we PUT the arraylist into the sharedPreferences
      SharedPreferences sharedPreferencescheckedContactsAsArrayList = PreferenceManager.getDefaultSharedPreferences(getApplication());
      SharedPreferences.Editor editorcheckedContactsAsArrayList = sharedPreferencescheckedContactsAsArrayList.edit();
      Gson gsoncheckedContactsAsArrayList = new Gson();
      String jsoncheckedContactsAsArrayList = gsoncheckedContactsAsArrayList.toJson(checkedContactsAsArrayList);
      editorcheckedContactsAsArrayList.putString("checkedContactsAsArrayList", jsoncheckedContactsAsArrayList);
      editorcheckedContactsAsArrayList.commit();

      System.out.println("ViewContact: checkedContacts are: " + checkedContacts.toString());


      //System.out.println("ViewContact: jsoncheckedContactsAsArrayList is " + jsoncheckedContactsAsArrayList);


      //we want to delete the old selectContacts from the listview when the Activity loads
      //because it may need to be updated and we want the user to see the updated listview,
      //like if the user adds new names and numbers to their phone contacts.
      selectPhoneContacts.clear();

      //If permission denied (will only be on Marshmallow +)
      PackageManager manager = getPackageManager();
      int hasPermission = manager.checkPermission("android.permission.READ_CONTACTS", "com.example.chris.populisto");
      if (hasPermission == manager.PERMISSION_DENIED) {

        //there's no READ_CONTACT permissions, so show 'No Contacts textbox'
        noContactFoundCheck = 1;

      } else {

        //there ARE READ_CONTACT permissions, so DON'T show 'No Contacts textbox'
        noContactFoundCheck = 0;

        //for every value in the allPhonesofContacts array list, call it phoneNumberofContact
        for (int i = 0; i < PopulistoContactsAdapter.allPhonesofContacts.size(); i++) {

          phoneNumberofContact = PopulistoContactsAdapter.allPhonesofContacts.get(i);
          phoneNameofContact = PopulistoContactsAdapter.allNamesofContacts.get(i);

          System.out.println("ViewContact: it is in shared prefs" + allPhonesofContacts);
          System.out.println("ViewContact: phoneNumberofContact : " + phoneNumberofContact);
          System.out.println("ViewContact: phoneNameofContact : " + phoneNameofContact);

          SelectPhoneContact selectContact = new SelectPhoneContact();

          //if a phone number is in our array of matching contacts
          if (PopulistoContactsAdapter.MatchingContactsAsArrayList.contains(phoneNumberofContact))

          {   //add the selectContacts to the selectPhoneContacts array
            // insert the contact at the beginning of the listview
            selectPhoneContacts.add(0, selectContact);

            //In SelectPhoneContact class, so getItemViewType will know which layout to show
            //:checkbox or Invite Button
            selectContact.setType_row("1");

          } else {
            // insert it at the end (default)
            selectPhoneContacts.add(selectContact);
            selectContact.setType_row("2");

          }


          selectContact.setName(phoneNameofContact);
          //    selectContact.setPhone(phoneNumberofContact);
          selectContact.setPhone(phoneNumberofContact);
          //selectContact.setSelected(is);

        }
      }

      return null;


    }


    @Override
    protected void onPostExecute(Void aVoid) {
      super.onPostExecute(aVoid);
     // hidePDialog();

      // System.out.println("postexecute: checkedContactsAsArrayList is " + checkedContactsAsArrayList);

      PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, ViewContact.this, 0);

      recyclerView.setAdapter(adapter);

      recyclerView.setLayoutManager((new LinearLayoutManager(ViewContact.this)));

      //if READ_CONTACTS Permission is disabled
      if (noContactFoundCheck == 1) {
        noContactsFound.setVisibility(View.VISIBLE);
      } else {

        noContactFoundCheck = 0;
        noContactsFound.setVisibility(View.GONE);

        //we need to notify the listview that changes may have been made on
        //the background thread, doInBackground, like adding or deleting contacts,
        //and these changes need to be reflected visibly in the listview. It works
        //in conjunction with selectContacts.clear()
        adapter.notifyDataSetChanged();

      }
    }
  }


/*  @Override
  protected void onResume() {

    super.onResume();*/

    // getPrefs();

    //    ViewContact.LoadContact loadContact = new ViewContact.LoadContact();


    //    loadContact.execute();
//        adapter.notifyDataSetChanged();
    //Toast.makeText(ViewContact.this, "resuming!", Toast.LENGTH_SHORT).show();


  //}


  public void hidePDialog() {
    if (pDialog != null) {
      pDialog.dismiss();
      pDialog = null;
    }
  }


  protected void onDestroy() {

    super.onDestroy();
    //make sure that when the activity dies the load dialogue dies
    //with it, otherwise we get a memory leak error and app can crash
    hidePDialog();

  }

}