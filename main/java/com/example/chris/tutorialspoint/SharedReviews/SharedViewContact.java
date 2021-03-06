package com.example.chris.tutorialspoint.SharedReviews;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chris.tutorialspoint.AppController;
import com.example.chris.tutorialspoint.EditContact;
import com.example.chris.tutorialspoint.PopulistoContactsAdapter;
import com.example.chris.tutorialspoint.PopulistoListView;
import com.example.chris.tutorialspoint.SelectPhoneContact;
import com.example.tutorialspoint.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.example.tutorialspoint.R.id.rv;
import static com.example.tutorialspoint.R.layout.activity_sharedview_contact;
import static com.example.tutorialspoint.R.layout.activity_view_contact;

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
  ArrayList<String> checkedContactsAsArrayList;

  //all phone contact numbers, broken down
  String phoneNumberofContact;
  //all phone contact names, broken down
  String phoneNameofContact;

  //checkedContacts is a String, we get it from "checkedcontacts", on the server
  String checkedContacts;

  //this is for public or private groups
  //amongst other things, we'll be bringing the intent over to EditContact.
  int pub_or_priv;

  //For the recycler view, containing the phone contacts
 // RecyclerView recyclerView;

  //ViewContact mn;
  // Context mContext;
  // private Thread mThread;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(activity_sharedview_contact);

    pDialog = new ProgressDialog(SharedViewContact.this);
    pDialog.setCancelable(false);
    // Showing progress dialog before making http request
    pDialog.setMessage("Loading bud...");
    pDialog.show();

    //need to initialize this
    PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, SharedViewContact.this, 0);

    //selectPhoneContacts is an empty array list that will hold our SelectPhoneContact info
    selectPhoneContacts = new ArrayList<SelectPhoneContact>();

    //System.out.println("ViewContact: selectPhoneContacts " + selectPhoneContacts);

    //rv is for holding the phone contacts, invite button, checkbox etc
    //recyclerView = (RecyclerView) findViewById(rv);


    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    //Show the back button (???)
    ActionBar actionbar = getSupportActionBar();
    actionbar.setDisplayHomeAsUpEnabled(true);
    //actionbar.setDisplayShowHomeEnabled(true);

    //show the App title
    actionbar.setTitle("Pop");

    //  when the activity loads, check to see if phoneNoofUser is using the App,if the user is
    // already registered, by checking the MyData XML file
    SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
    phoneNoofUser = sharedPreferences.getString("phonenumberofuser", "");

    Intent i = this.getIntent();

    //we'll be getting review_id from the cell clicked in the recyclerView,
    //intent review_id is in SharedPopulistoReviewsAdapter,
    //then posting to ViewContact.php to get associated details
    review_id = i.getStringExtra("review_id");

    //we'll be getting the phone number of user who made the review
    // clicked in the recyclerView,
    //phoneNumberofUserFromDB is in SharedPopulistoReviewsAdapter and UopulistoListAdapter
    //then posting to ViewContact.php
    //so we can decide what parts of review to show the logged-in user
    phoneNumberofUserFromDB = i.getStringExtra("PhoneNumberofUserFromDB");
    System.out.println("PhoneNumberofUserFromDB:" + phoneNumberofUserFromDB);
    System.out.println("phoneNoofUser:" + phoneNoofUser);

    //if we are coming from EditContact, where no recyclerView cell has been clicked
   // if (review_id == null) {

      //then set review_id to the value we put in shared preferences
     // review_id = PreferenceManager.getDefaultSharedPreferences(this).getString("review_id value", review_id);

    //}

    //System.out.println("ViewContact: review id is " + review_id);

    //coming from PopulistoListView we will always get a value for review_id
    //Let's save the review_id in shared preferences
    //so we can get it easily in EditContact,
    //and load the corresponding values into ViewContact on < press
    PreferenceManager.getDefaultSharedPreferences(this).edit().putString("review_id value", review_id).apply();


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

              if (pub_or_priv == 0) {
                //change colour depending on value
                publicorprivate2.setTextColor(Color.parseColor("#DA850B"));
                shared_status = "Just U";
                //for "U"
                textphoneNameonPhone.setTextColor(Color.parseColor("#DA850B"));
                textphoneNameonPhone.setText("U");
              }

              if (pub_or_priv == 1) {
                publicorprivate2.setTextColor(Color.parseColor("#0A7FDA"));
                shared_status = "Phone Contacts";
                textphoneNameonPhone.setTextColor(Color.parseColor("#0A7FDA"));
                textphoneNameonPhone.setText("U");
              }

              if (pub_or_priv == 2) {
                publicorprivate2.setTextColor(Color.parseColor("#2AB40E"));
                shared_status = "Public";
                textphoneNameonPhone.setTextColor(Color.parseColor("#2AB40E"));
                textphoneNameonPhone.setText("U");
              }

              //put pub_or_priv in the textbox called publicorprivate
              publicorprivate.setText("Shared with: ");

              publicorprivate2.setText(shared_status);

             // System.out.println("ViewContact: public or private value :" + pub_or_priv);


              //for categoryid we only need the value, don't need to cast it to anything
              //we'll be sending this to EditContact with an intent
              categoryid = category_id;

             // System.out.println("here are the checkedcontacts" + checkedContacts);
              //  Toast.makeText(ViewContact.this, "here are the checkedcontacts" + checkedContacts, Toast.LENGTH_SHORT).show();


/*              if (phoneNoofUser.equals(phoneNumberofUserFromDB)) {

               // Toast.makeText(ViewContact.this, "Yes they match!", Toast.LENGTH_SHORT).show();
                edit.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
              }*/


              //System.out.println("ViewContact2: checkedContactsAsArrayList is " + checkedContactsAsArrayList);

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
            Toast.makeText(SharedViewContact.this, error.toString(), Toast.LENGTH_LONG).show();

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


    //*****************************************
    //for the edit button
    //edit = (Button) findViewById(R.id.edit);


/*    edit.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        System.out.println("you clicked it, edit");
        //open the Edit Activity, pass over the review_id so we can get that reviews
        //associated fields
        Intent i = new Intent(SharedViewContact.this, EditContact.class);
        i.putExtra("review_id", review_id);
        //"category" is the key
        // which we will be looking for from EditContact.class, categoryname.getText() is the
        // content to pass from ViewContact.class etc....
        i.putExtra("category", categoryname.getText());
        i.putExtra("category_id", categoryid);
        i.putExtra("name", namename.getText());
        i.putExtra("phone", phonename.getText());
        i.putExtra("address", addressname.getText());
        i.putExtra("comment", commentname.getText());

        //bring the pub_or_private value to EditContact.class, for the radio button to be
        // appropriately checked
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
    Intent j = getIntent();
    String new_category_value = j.getStringExtra("category");
    String new_name_value = j.getStringExtra("name");
    String new_phone_value = j.getStringExtra("phone");
    String new_address_value = j.getStringExtra("address");
    String new_comment_value = j.getStringExtra("comment");
    categoryname.setText(new_category_value);
    namename.setText(new_name_value);
    phonename.setText(new_phone_value);
    addressname.setText(new_address_value);
    commentname.setText(new_comment_value);


    //*****************************************
    //for the delete button
   // delete = (Button) findViewById(R.id.delete);

    //call the delete review function
//    deleteContactButton();

   // fetchCheckedContacts();
  }

  //this is the function for Volley, trying to change from AsycnTask to Volley
  //for getting the checked contacts on phone
  //private void fetchCheckedContacts() {


  //}

  //code for the '<', back button. Go back to PopulistoListView, as defined
  //in Manifest, PARENT_ACTIVITY
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }


/*  private void deleteContactButton() {

    delete.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {

        //add a dialogue box
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage("Are you sure you want to delete this?").setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener).show();

      }

    });


  }*/

  //Are you sure you want to delete? dialogue
  /*DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
      switch (which) {
        case DialogInterface.BUTTON_POSITIVE:
          //Yes button clicked

          //close the populistolistview class
          //(we'll be opening it again, will close now so it will be refreshed)
          PopulistoListView.fa.finish();

          Toast.makeText(SharedViewContact.this, "delete stuff", Toast.LENGTH_SHORT).show();

          pDialog = new ProgressDialog(SharedViewContact.this);
          // Showing progress dialog for the review being saved
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
                  Toast.makeText(SharedViewContact.this, response, Toast.LENGTH_LONG).show();
                }
              },
              new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                  Toast.makeText(SharedViewContact.this, "problem here", Toast.LENGTH_LONG).show();

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

          Intent j = new Intent(SharedViewContact.this, PopulistoListView.class);

          startActivity(j);

//                finish();


          break;

        case DialogInterface.BUTTON_NEGATIVE:
          //No button clicked, just close the dialogue
          break;
      }
  */
  };


  //******for the phone contacts in the recyclerView

  // Load data in background
 /* class LoadContact extends AsyncTask<Void, Void, Void> {
    @Override
    protected void onPreExecute() {
      super.onPreExecute();


    }

    @Override
    protected Void doInBackground(Void... voids) {

      //convert the checkedContacts string to an arraylist
      //then we will search through the arraylist and check the associated
      //check boxes
      //First, take out the double quotes in the string, \ is to escape the "
     *//* String replace = checkedContacts.replace("\"", "");
      //take out the starting [
      String replace1 = replace.replace("[", "");
      //and then the ending ]
      String replace2 = replace1.replace("]", "");
      System.out.println("here is replace2 " + replace2);
      //convert the checkedContacts string to an arraylist
      checkedContactsAsArrayList = new ArrayList<String>(Arrays.asList(replace2.split(",")));
      //System.out.println("ViewContact1: checkedContactsAsArrayList is " + checkedContactsAsArrayList);


      //we want to bring the checkedContactsAsArrayList array list to our PopulistoContactAdapter.
      // It looks like Shared Preferences
      //only works easily with strings so best way to bring the array list in Shared Preferences is with
      //Gson.
      //Here, we PUT the arraylist into the sharedPreferences*//**//*
      SharedPreferences sharedPreferencescheckedContactsAsArrayList = PreferenceManager.getDefaultSharedPreferences(getApplication());
      SharedPreferences.Editor editorcheckedContactsAsArrayList = sharedPreferencescheckedContactsAsArrayList.edit();
      Gson gsoncheckedContactsAsArrayList = new Gson();
      String jsoncheckedContactsAsArrayList = gsoncheckedContactsAsArrayList.toJson(checkedContactsAsArrayList);
      editorcheckedContactsAsArrayList.putString("checkedContactsAsArrayList", jsoncheckedContactsAsArrayList);
      editorcheckedContactsAsArrayList.commit();


      System.out.println("ViewContact: jsoncheckedContactsAsArrayList is " + jsoncheckedContactsAsArrayList);


      //we want to delete the old selectContacts from the listview when the Activity loads
      //because it may need to be updated and we want the user to see the updated listview,
      //like if the user adds new names and numbers to their phone contacts.
      //selectPhoneContacts.clear();

      //for every value in the allPhonesofContacts array list, call it phoneNumberofContact
      for (int i = 0; i < PopulistoContactsAdapter.allPhonesofContacts.size(); i++) {

        phoneNumberofContact = PopulistoContactsAdapter.allPhonesofContacts.get(i);
        phoneNameofContact = PopulistoContactsAdapter.allNamesofContacts.get(i);

        System.out.println("ViewContact: phoneNumberofContact : " + phoneNumberofContact);
        System.out.println("ViewContact: phoneNameofContact : " + phoneNameofContact);

        SelectPhoneContact selectContact = new SelectPhoneContact();

        //if a phone number is in our array of matching contacts
        if (PopulistoContactsAdapter.MatchingContactsAsArrayList.contains(phoneNumberofContact))

        {   //add the selectContacts to the selectPhoneContacts array
          // insert the contact at the beginning of the listview
          selectPhoneContacts.add(0, selectContact);

          //In SelectContact class, so getItemViewType will know which layout to show
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

      }*//*


      return null;


    }


    @Override
    protected void onPostExecute(Void aVoid) {
      super.onPostExecute(aVoid);
      hidePDialog();

     // System.out.println("postexecute: checkedContactsAsArrayList is " + checkedContactsAsArrayList);

      PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, SharedViewContact.this, 0);

      recyclerView.setAdapter(adapter);


      recyclerView.setLayoutManager((new LinearLayoutManager(SharedViewContact.this)));


      //we need to notify the listview that changes may have been made on
      //the background thread, doInBackground, like adding or deleting contacts,
      //and these changes need to be reflected visibly in the listview. It works
      //in conjunction with selectContacts.clear()
      adapter.notifyDataSetChanged();


    }
  }*/


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