package com.example.chris.populisto;

//import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.tutorialspoint.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.example.chris.populisto.PopulistoContactsAdapter.MatchingContactsAsArrayList;
import static com.example.chris.populisto.PopulistoContactsAdapter.checkedContactsAsArrayList;
import static com.example.chris.populisto.PopulistoContactsAdapter.theContactsList;
import static com.example.tutorialspoint.R.id.parent;


public class NewContact extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

  }

  // this is the php file name where to save to.
  // we will post the category, name, phone, address, comment etc into Php and
  // create a new review_id
  private static final String NewContact_URL = "https://www.populisto.com/NewContact.php";

  //matching contacts, those on phone and populisto users
  //public static final ArrayList<String> MatchingContactsAsArrayList  = new ArrayList<String>();

  //make a List containing info about SelectPhoneContact objects
  //public static List<SelectPhoneContact> theContactsList;

  //allPhonesofContacts is a list of all the phone numbers in the user's contacts
  //public static final ArrayList<String> allPhonesofContacts = new ArrayList<String>();

  //in this JSONArray, checkedContacts, we will be storing each checkedContact JSON Object
  //Then we're going to post it to our NewContact.php file
  JSONArray checkedContacts = new JSONArray();

 // private ProgressDialog pDialog;

  Button publicContacts;
  Button phoneContacts;
  Button justMeContacts;
  Button save;
  Button cancel;
  //CheckBox mcheckbox;

  //thse are the fields in the xml
  private EditText categoryname;
  private EditText namename;
  private EditText phonename;
  private EditText addressname;
  private EditText commentname;

  private EditText alertdialog_edittext;

  //if user has no contacts on his phone, like if no
  //permission has been given to getPhoneContacts
  TextView noContactsFound;

  int public_or_private;

  //if it is 0, then show the 'No Contacts Found' textbox
  int noContactFoundCheck;

  // ArrayList called selectPhoneContacts that will contain SelectPhoneContact info
  ArrayList<SelectPhoneContact> selectPhoneContacts;

  String CountryCode;

  String phoneNoofUserCheck;
  public String phoneNumberofContact;
  String phoneNameofContact;

  //For the recycler view, containing the phone contacts
  RecyclerView recyclerView;
  //PopulistoContactsAdapter adapter;

  //for categories autocomplete
  private AutoCompleteTextView AutoCompTextViewcategoryList;

  //for Google Address
  private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
  private AutoCompleteTextView AutoCompTextViewAddress;
  private GoogleApiClient mGoogleApiClient;

  //for autocomplete of categories, the php file will get categories available to user
  //to pick from
  private static final String jsonString = "https://www.populisto.com/CategoryList.php";

  private ArrayList<CategoryList> existingCategoryList;


  private PlaceAutoCompleteAdapter mPlaceAutoCompleteAdapter;

  //sharedprefs for holding all phone numbers of contacts
  //SharedPreferences sharedPreferencesallPhonesofContacts;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_contact);


    noContactsFound = (TextView) findViewById(R.id.noContactsFoundView);

    existingCategoryList = new ArrayList<>();

    //for categories autocomplete...
    AutoCompTextViewcategoryList = (AutoCompleteTextView) findViewById(R.id.textViewCategory);

    //for address autocomplete...
    AutoCompTextViewAddress = (AutoCompleteTextView) findViewById(R.id.textViewAddress);


    PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, NewContact.this, 1);


    // Create a GoogleApiClient instance
    mGoogleApiClient = new GoogleApiClient
        .Builder(this)
        .addApi(Places.GEO_DATA_API)
        .addApi(Places.PLACE_DETECTION_API)
        .enableAutoManage(this, 0, this)
        .build();

    //Google Places
    mPlaceAutoCompleteAdapter = new PlaceAutoCompleteAdapter(this, mGoogleApiClient, LAT_LNG_BOUNDS, null);

    AutoCompTextViewAddress.setAdapter(mPlaceAutoCompleteAdapter);

    //put in a toolbar
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    //remove the app name from the toolbar (don't want it twice)
    getSupportActionBar().setDisplayShowTitleEnabled(false);

    //into the toolbar, inflate the back button and Populisto title,
    //which we find in new_contact_toolbar_layout.xml
    View logo = getLayoutInflater().inflate(R.layout.new_contact_toolbar_layout, null);
    toolbar.addView(logo);

    //for the back arrow, tell it to close the activity, when clicked
    ImageView backButton = (ImageView) logo.findViewById(R.id.back_arrow_id);
    //backButton.setBackgroundColor(Color.rgb(100, 100, 50));

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

    //we are fetching details for the recyclerview - the name, numbers, matching contacts...
    LoadContact loadContact = new LoadContact();
    loadContact.execute();

    //hide keyboard on activity start up
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    //selectPhoneContacts is an empty array list that will hold our SelectPhoneContact info
    selectPhoneContacts = new ArrayList<SelectPhoneContact>();

    System.out.println("NewContact1: selectPhoneContacts " + selectPhoneContacts);

    //first of all we want to get the phone number of the current user so we
    //can post it and then get the user_id in php
    //get the phone number, stored in an XML file, when the user first registered the app
    SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
    phoneNoofUserCheck = sharedPreferences.getString("phonenumberofuser", "");

    System.out.println("NewContact: phone number of user is " + phoneNoofUserCheck);

    //get the CountryCode, stored in an XML file, when the user first registered the app
    //We need this for putting phone contacts into E164 and comparing against the app db
    SharedPreferences sharedPreferencesCountryCode = getSharedPreferences("MyData", Context.MODE_PRIVATE);
    CountryCode = sharedPreferencesCountryCode.getString("countrycode", "");

    //cast an EditText for each of the field ids in activity_new_contactact.xml
    categoryname = (EditText) findViewById(R.id.textViewCategory);
    namename = (EditText) findViewById(R.id.textViewName);
    phonename = (EditText) findViewById(R.id.textViewPhone);
    addressname = (EditText) findViewById(R.id.textViewAddress);
    commentname = (EditText) findViewById(R.id.textViewComment);

    //textlistener for the edittext fields, if it starts with
    //" " then don't let it input in edittext
    categoryname.addTextChangedListener(generalTextWatcher);
    namename.addTextChangedListener(generalTextWatcher);
    phonename.addTextChangedListener(generalTextWatcher);
    addressname.addTextChangedListener(generalTextWatcher);
    commentname.addTextChangedListener(generalTextWatcher);

    //when user touches on "commentname" edittext we want the alertdialog to open
    commentname.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {

        //when user lifts finger....
        if (event.getAction() == MotionEvent.ACTION_UP) {
          AlertDialog.Builder commentname_alertdialog = new AlertDialog.Builder(NewContact.this);

          alertdialog_edittext = new EditText(NewContact.this);

          commentname_alertdialog.setTitle("Ur Comment:");

          commentname_alertdialog.setView(alertdialog_edittext);

          //set the text input type
          alertdialog_edittext.setInputType(InputType.TYPE_CLASS_TEXT);
          //set the max length of characters for the edittext
          alertdialog_edittext.setFilters(new InputFilter[] { new InputFilter.LengthFilter(385)});

          //set to multi-line
          alertdialog_edittext.setSingleLine(false);
          alertdialog_edittext.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);

          alertdialog_edittext.setLines(11);


          //so we can toast a message if text limit is reached
          alertdialog_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

              //toast if text runs on
              if (alertdialog_edittext.length() > 383) {
                Toast.makeText(NewContact.this, "Limit reached", Toast.LENGTH_SHORT).show();
              }
            }
          });

          //set a string to the value of the commentname edittext
          String CommentTextValue = commentname.getText().toString();
          setTextFromComment(CommentTextValue);

          // Set up the buttons
          commentname_alertdialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

              //set AlertDialogTextValue, a value for the text entered in the alert dialog
              String AlertDialogTextValue = alertdialog_edittext.getText().toString();
              if (AlertDialogTextValue.length() > 0) {

                //call setTextFromDialog function and
                //pass the text string entered in the alert dialog
                setTextFromDialog(AlertDialogTextValue);

                dialog.dismiss();

              }
            }
          });
          commentname_alertdialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.cancel();
            }
          });

          AlertDialog dialog = commentname_alertdialog.create();
          dialog.setCanceledOnTouchOutside(false);

          //show keyboard
          dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

          dialog.show();

          return true;

        }
        return false;

       }

      //function to pass text entered in comment to alert_dialog
      private void setTextFromComment(final String textFromComment){
        alertdialog_edittext.setText(textFromComment);
      }

      //function to pass text entered in alert_dialog to commentname edittext
    private void setTextFromDialog(final String textFromDialog){
      commentname.setText(textFromDialog);
    }
    });




    //for the Public, phoneContacts, justMe, save and cancel buttons
    publicContacts = (Button) findViewById(R.id.btnPublic);
    phoneContacts = (Button) findViewById(R.id.btnPhoneContacts);
    justMeContacts = (Button) findViewById(R.id.btnJustMe);
    save = (Button) findViewById(R.id.save);
    cancel = (Button) findViewById(R.id.cancel);

    publicButton();
    phoneContactsButton();
    justMeButton();
    saveContactButton();

    //If permission denied (will only be on Marshmallow +)
    PackageManager manager = getPackageManager();
    int hasPermission = manager.checkPermission("android.permission.READ_CONTACTS", "com.example.chris.populisto");
    if (hasPermission == manager.PERMISSION_DENIED) {

      //there's no READ_CONTACT permissions, so show 'No Contacts textbox'
      noContactFoundCheck = 1;

      //have the just me button selected by default
      //because we don't have access to phone contacts
      justMeContacts.setBackgroundResource(R.drawable.justmecontacts_buttonshapepressed);

      //save it by default to the DB as 0, for just me
      public_or_private = 0;

      //disable the phoneContacts button
      phoneContacts.setEnabled(false);

      //disable the public button
      publicContacts.setEnabled(false);


    } else {

      //there ARE READ_CONTACT permissions, so DON'T show 'No Contacts textbox'
      noContactFoundCheck = 0;

      //have the phoneContacts button selected by default
      //make it blue
      phoneContacts.setBackgroundResource(R.drawable.phonecontacts_buttonshapepressed);

      //save it by default to the DB as 1, for phoneContacts
      public_or_private = 1;

    }

    recyclerView = (RecyclerView) findViewById(R.id.rv);


    cancel.setOnClickListener(new View.OnClickListener() {

      public void onClick(View v) {
        //close the activity
        finish();
      }

    });

  }

  //load this function when the activity is created, put categories
  //in autocomplete
  public void getCategoryList() {

    StringRequest request = new StringRequest(Request.Method.POST, jsonString,
        new Response.Listener<String>() {

          @Override
          public void onResponse(String response) {

           // Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

            try {

              //extract the individual details in jsonString
              existingCategoryList = extractCategoryList(response);

            } catch (Exception e) {
              e.printStackTrace();
            }

            //context, layout, list of users
            CategoryListAdapter userAdapter = new CategoryListAdapter(getApplicationContext(), R.layout.categorylist_dropdown_layout, existingCategoryList);

            AutoCompTextViewcategoryList.setAdapter(userAdapter);
            AutoCompTextViewcategoryList.setThreshold(1);

          }


        }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {

      }
    })

    {
      @Override
      //post info to php
      protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        //phoneNoofUser is the value we get from Android, the user's phonenumber.
        //the key,KEY_PHONENUMBER_USER, is "phonenumberofuser". When we see "phonenumberofuser" in our php,
        //put in phoneNoofUserCheck
        System.out.println("NewContact2: phone number of user is " + phoneNoofUserCheck);
        params.put("phonenumberofuser", phoneNoofUserCheck);
        return params;

      }
    };


    AppController.getInstance().addToRequestQueue(request);

  }

  //for dropdown autocomplete of categories -
  //extract the individual details in jsonString
  private ArrayList<CategoryList> extractCategoryList(String response) {

    //make a list that will hold cat_id and name
    ArrayList<CategoryList> list = new ArrayList<>();

    try {
      //we are getting response from CategoryList.php
      JSONObject rootJO = new JSONObject(response);

      //break down the array in the JSON object
      JSONArray userJA = rootJO.getJSONArray("results");

      for (int i2 = 0; i2 < userJA.length(); i2++) {

        JSONObject jo = userJA.getJSONObject(i2);

        //extract values from each key in the results array
        int id = jo.getInt("cat_id");
        String name = jo.getString("cat_name");

        //make a new categoryList object
        CategoryList categoryList = new CategoryList(id, name);

        //add the object to the list array
        list.add(categoryList);

        Log.e("Parse JSON", "id: " + id + " name: " + name);
      }

      // Toast.makeText(getApplicationContext(), existingCategoryList.toString(), Toast.LENGTH_LONG).show();


    } catch (JSONException e) {
      e.printStackTrace();
    }

    return list;
  }




//******for the phone contacts in the recyclerView

  // Load data in background
  class LoadContact extends AsyncTask<Void, Void, Void> {
    @Override
    protected void onPreExecute() {

      super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {

      // make a copy of MatchingContactsAsArrayList,
      //these will all be checked by default
      checkedContactsAsArrayList = new ArrayList<String>(MatchingContactsAsArrayList);

      //we want to delete the old selectContacts from the listview when the Activity loads
      //because it may need to be updated and we want the user to see the updated listview,
      //like if the user adds new names and numbers to their phone contacts.
      //selectPhoneContacts.clear();

/*      if (PopulistoContactsAdapter.allPhonesofContacts.size() == 0) {

        System.out.println("allPhonesofContacts is null" + PopulistoContactsAdapter.MatchingContactsAsArrayList);
//        Toast.makeText(NewContact.this, "allPhonesofContacts is null", Toast.LENGTH_SHORT).show();

      }*/

      //If permission denied (will only be on Marshmallow +)
      PackageManager manager = getPackageManager();
      int hasPermission = manager.checkPermission("android.permission.READ_CONTACTS", "com.example.chris.populisto");
      if (hasPermission == manager.PERMISSION_DENIED) {

        //and show the "No Contacts found" textbox
        noContactFoundCheck = 1;

        //SelectPhoneContact selectContact = new SelectPhoneContact();
        //selectContact.setType_row("2");
      } else {

        //show matching contacts, other populsito users,
        //checked by default
        noContactFoundCheck = 0;

        //for every value in the allPhonesofContacts array list, call it phoneNumberofContact
        for (int i = 0; i < PopulistoContactsAdapter.allPhonesofContacts.size(); i++) {

          phoneNumberofContact = PopulistoContactsAdapter.allPhonesofContacts.get(i);
          phoneNameofContact = PopulistoContactsAdapter.allNamesofContacts.get(i);

          System.out.println("SelectPhoneContactAdapter: phoneNumberofContact : " + phoneNumberofContact);
          System.out.println("SelectPhoneContactAdapter: phoneNameofContact : " + phoneNameofContact);

          SelectPhoneContact selectContact = new SelectPhoneContact();

          //if a phone number is in our array of matching contacts
          if (MatchingContactsAsArrayList.contains(phoneNumberofContact))

          {   //add the selectContacts to the selectPhoneContacts array
            // insert the contact at the beginning of the listview
            selectPhoneContacts.add(0, selectContact);
            System.out.println("MatchingContactsAsArrayList is : " + MatchingContactsAsArrayList);

            //In SelectContact class, so getItemViewType will know which layout to show
            //:checkbox or Invite Button
            selectContact.setType_row("1");

          } else {
            // insert it at the end (default)
            selectPhoneContacts.add(selectContact);
            selectContact.setType_row("2");

          }

          selectContact.setName(phoneNameofContact);
          selectContact.setPhone(phoneNumberofContact);


        }
      }
      //call the getCategoryList function, it will load all the categories
      //in autocompletetextview available to own-user
      getCategoryList();
      return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
      super.onPostExecute(aVoid);

      PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, NewContact.this, 1);

      recyclerView.setAdapter(adapter);
      recyclerView.setLayoutManager((new LinearLayoutManager(NewContact.this)));

      //this is our progressbar view
      //we find it in nocontactsfound.xml, make it invisible as there is no waiting
      //on info from the server.
      ProgressBar progressbar = findViewById(R.id.progressbar);// change id here
      progressbar.setVisibility(View.GONE);

      //if there's no sharedprefs file containing all phone numbers of contacts
      if (noContactFoundCheck == 1) {
        noContactsFound.setVisibility(View.VISIBLE);
      } else {

        noContactFoundCheck = 0;
        noContactsFound.setVisibility(View.GONE);

        //clear the array list of checked contacts that may be in SharedPrefs
        //checkedContactsAsArrayList.clear();

        //*********set the Matching Contacts to be checked, by default ************
        //loop through the matching contacts
        int count = MatchingContactsAsArrayList.size();
        System.out.println("NewContact: the matching contacts are " + MatchingContactsAsArrayList);

        for (int i = 0; i < count; i++) {

          theContactsList.add(new SelectPhoneContact());
          //if (PopulistoContactsAdapter.theContactsList != null && PopulistoContactsAdapter.theContactsList.size() >= 1)
          // {
          //check all matching contacts, we want it to be 'Phone Contacts' by default
          PopulistoContactsAdapter.theContactsList.get(i).setSelected(true);

          //we need to notify the recyclerview that changes may have been made
          adapter.notifyDataSetChanged();

        }
      }
      //*********************************


      //we need to notify the listview that changes may have been made on
      //the background thread, doInBackground, like adding or deleting contacts,
      //and these changes need to be reflected visibly in the listview. It works
      //in conjunction with selectContacts.clear()
      adapter.notifyDataSetChanged();

    }
  }


  @Override
  protected void onResume() {

    super.onResume();

  }

  //for the Public Contacts button
  private void publicButton() {

    publicContacts.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        // make a copy of MatchingContactsAsArrayList,
        //these will all be checked by default
        //and public_or_private = 2
        checkedContactsAsArrayList = new ArrayList<String>(MatchingContactsAsArrayList);


//              keep the slightly rounded shape, when the button is pressed, and change colour
        publicContacts.setBackgroundResource(R.drawable.publiccontacts_buttonshapepressed);

//               keep the slightly rounded shape of the others, but still grey
        phoneContacts.setBackgroundResource(R.drawable.buttonshape);
        justMeContacts.setBackgroundResource(R.drawable.buttonshape);

        //call the function to change the border colour,
        //if Phone Contacts button is clicked,
        //we want the border to be GREEN
        GlobalFunctions.sharing_border_colour(NewContact.this,"#2AB40E");

        //set sharing to Public.
        // This will be uploaded to server to review table,
        //public_or_private column, if saved in this state
        public_or_private = 2;

        Toast.makeText(NewContact.this, "Everyone can see, ur number is masked to non-contacts", Toast.LENGTH_SHORT).show();

        PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, NewContact.this, 1);

        recyclerView.setAdapter(adapter);

          //loop through the matching contacts
          int count = MatchingContactsAsArrayList.size();

          //i is the number of matching contacts that there are
          for (int i = 0; i < count; i++) {

            //for all contacts, only those that are matching will be checked
            PopulistoContactsAdapter.theContactsList.get(i).setSelected(true);

            //we need to notify the recyclerview that changes may have been made
            adapter.notifyDataSetChanged();
          }
        //}
      }
    });

  }


  //for the phone Contacts button
  private void phoneContactsButton() {

    phoneContacts.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, NewContact.this, 1);

        recyclerView.setAdapter(adapter);

          //keep the slightly rounded shape, when the button is pressed, and change colour
          phoneContacts.setBackgroundResource(R.drawable.phonecontacts_buttonshapepressed);

          //keep the slightly rounded shape of the others, but still grey
          publicContacts.setBackgroundResource(R.drawable.buttonshape);
          justMeContacts.setBackgroundResource(R.drawable.buttonshape);

        //call the function to change the border colour,
        //if Phone Contacts button is clicked,
        //we want the border to be BLUE
        GlobalFunctions.sharing_border_colour(NewContact.this,"#0A7FDA");


          //set sharing to Phone Contacts
          // This will be uploaded to server to review table,
          //public_or_private column, if saved in this state
          public_or_private = 1;

          //loop through the matching contacts
          int count = MatchingContactsAsArrayList.size();

      //  Toast.makeText(NewContact.this, "MatchingContactsAsArrayList size is " + count, Toast.LENGTH_SHORT).show();

        //for each matching contact...
        for (int i = 0; i < count; i++) {

          //add the phone number to the arrayList
          //if it does not exist there already
          if (!checkedContactsAsArrayList.contains(theContactsList.get(i).getPhone())) {
            checkedContactsAsArrayList.add(theContactsList.get(i).getPhone());;
          }

            //check mark all matching contacts, we want it to be 'Phone Contacts'
            PopulistoContactsAdapter.theContactsList.get(i).setSelected(true);

            //we need to notify the recyclerview that changes may have been made
            adapter.notifyDataSetChanged();
          }
       // }
      }
    });

  }


  //for the Just Me button
  private void justMeButton() {

    justMeContacts.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        //keep the slightly rounded shape, when the button is pressed, and change colour
        justMeContacts.setBackgroundResource(R.drawable.justmecontacts_buttonshapepressed);

        //keep the slightly rounded shape of the others, but still grey
        publicContacts.setBackgroundResource(R.drawable.buttonshape);
        phoneContacts.setBackgroundResource(R.drawable.buttonshape);

        //call the function to change the border colour,
        //if Phone Contacts button is clicked,
        //we want the border to be ORANGE
        GlobalFunctions.sharing_border_colour(NewContact.this,"#DA850B");

        //set sharing to Just Me
        // This will be uploaded to server to review table,
        //public_or_private column, if saved in this state
        public_or_private = 0;

        //reset the size of the array to 0
        checkedContactsAsArrayList.clear();

        Toast.makeText(NewContact.this, "Just u can see, edit later", Toast.LENGTH_SHORT).show();

        PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, NewContact.this, 1);

        recyclerView.setAdapter(adapter);
        // recyclerView.setLayoutManager((new LinearLayoutManager(NewContact.this)));

        //we need to notify the recyclerview that changes may have been made
        adapter.notifyDataSetChanged();

        //If permission denied (will only be on Marshmallow +)
        PackageManager manager = getPackageManager();
        int hasPermission = manager.checkPermission("android.permission.READ_CONTACTS", "com.example.chris.populisto");
        if (hasPermission == manager.PERMISSION_DENIED) {

          //show the No Results Textbox
          noContactFoundCheck = 1;

        } else {
          //loop through the matching contacts
          int count = MatchingContactsAsArrayList.size();

          for (int i = 0; i < count; i++) {

            //uncheck all matching contacts, we want it to be 'Just Me'
            PopulistoContactsAdapter.theContactsList.get(i).setSelected(false);

            //we need to notify the recyclerview that changes may have been made
            adapter.notifyDataSetChanged();
          }
        }
      }
    });

  }


  //for the SAVE button
  private void saveContactButton() {

    save.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        String category_name = categoryname.getText().toString();

        if (TextUtils.isEmpty(category_name)) {
          categoryname.setError("Needs to be filled");
          return;
        } else {

          //close the populistolistview class
          //(we'll be opening it again, will close now so it will be refreshed with new contact details)
          PopulistoListView.fa.finish();

          System.out.println("you clicked it, save");

          //If permission denied (will only be on Marshmallow +)
          PackageManager manager = getPackageManager();
          int hasPermission = manager.checkPermission("android.permission.READ_CONTACTS", "com.example.chris.populisto");
          if (hasPermission == manager.PERMISSION_DENIED) {

            try {
              //add only logged-in phone owner's number to the checkedContacts JSON Array
              //new JSON Object called phoneOwner
              JSONObject phoneOwner = new JSONObject();

              //add the phone number
              phoneOwner.put("checkedContact", phoneNoofUserCheck);

              //add user's own number to the Array
              checkedContacts.put(phoneOwner);


            } catch (Exception e) {
              System.out.println("there's a problem here unfortunately");
              e.printStackTrace();
            }
          } else
          //if the logged-in user has enabled READ_CONTACTS Permission
          {

            try {
              //System.out.println("we're in the try part");

              //loop through the matching contacts
              int count = MatchingContactsAsArrayList.size();

              for (int i = 0; i < count; i++) {

                //for  contacts that are checked (they can only be matching contacts cause only they have a checkbox)...
                if (PopulistoContactsAdapter.theContactsList.get(i).getSelected()) {
                  //Toast.makeText(NewContact.this, PopulistoContactsAdapter.theContactsList.get(i).getPhone() + " clicked!", Toast.LENGTH_SHORT).show();

                  // make each checked contact in selectPhoneContacts
                  // into an individual
                  // JSON object called checkedContact
                  JSONObject checkedContact = new JSONObject();

                  //get the phone number of the checked contact
                  // checkedContact will be of the form {"checkedContact":"+353123456"}
                  checkedContact.put("checkedContact", PopulistoContactsAdapter.theContactsList.get(i).getPhone());

                  // Add checkedContact JSON Object to checkedContacts jsonArray
                  //The JSON Array will be of the form
                  // [{"checkedContact":"+3531234567"},{"checkedContact":"+353868132813"}]
                  //we will be posting this JSON Array to Php, further down below
                  checkedContacts.put(checkedContact);
                  System.out.println("NewContact: checkedcontact JSONObject :" + checkedContact);
                }

              }

              //add phone owner's number to the checkedContacts JSON Array
              //new JSON Object called phoneOwner
              JSONObject phoneOwner = new JSONObject();

              //add the phone number
              phoneOwner.put("checkedContact", phoneNoofUserCheck);
              System.out.println("NewContact: phoneOwner: " + phoneOwner);

              //add it to the Array
              checkedContacts.put(phoneOwner);

              System.out.println("New Contact: checkedContacts JSON Array " + checkedContacts);


            } catch (Exception e) {
              System.out.println("there's a problem here unfortunately");
              e.printStackTrace();
            }
          }

          //When the user clicks save
          //post phoneNoofUserCheck to NewContact.php and from that
          //get the user_id in the user table, then post category, name, phone etc...
          //to the review table
          StringRequest stringRequest = new StringRequest(Request.Method.POST, NewContact_URL,
              new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                  //response, this will show the checked numbers being posted
                  // Toast.makeText(NewContact.this, response, Toast.LENGTH_LONG).show();
                }
              },
              new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }

              }) {

            //post these details to the NewContact.php file and do
            //stuff with it
            protected Map<String, String> getParams() {
              Map<String, String> params = new HashMap<String, String>();
              //post the phone number to php to get the user_id in the user table
              params.put("phonenumberofuser", phoneNoofUserCheck);
              //the second value, categoryname.getText().toString() etc...
              // is the value we get from Android.
              //the key is "category", "name" etc.
              // When we see these in our php,  $_POST["category"],
              //put in the value from Android
              params.put("category", categoryname.getText().toString());
              params.put("name", namename.getText().toString());
              params.put("phone", phonename.getText().toString());
              params.put("address", addressname.getText().toString());
              params.put("comment", commentname.getText().toString());
              params.put("public_or_private", String.valueOf(public_or_private));
              System.out.println("public_or_private is " + String.valueOf(public_or_private));

              //this is the JSON Array of checked contacts
              //it will be of the form
              //[{"checkedContact":"+3531234567"},{"checkedContact":"+353868132813"}]
              params.put("checkedContacts", checkedContacts.toString());

              return params;

            }


          };


          // Adding request to request queue
          AppController.getInstance().addToRequestQueue(stringRequest);

          //when saved, go back to the PopulistoListView class and update with
          //the new entry
          Intent j = new Intent(NewContact.this, PopulistoListView.class);
          j.putExtra("phonenumberofuser", phoneNoofUserCheck);

          NewContact.this.startActivity(j);

          finish();

        }
      }
    });


  }

  //this is called from PopulistoContactsAdapter
  //change the colour of the phoneContacts button
  public void changeColourOfPhoneContacts() {

    //checkedContactsAsArrayList = new ArrayList<String>(MatchingContactsAsArrayList);


    //keep the slightly rounded shape, when the button is pressed
    phoneContacts.setBackgroundResource(R.drawable.phonecontacts_buttonshapepressed);

    //keep the slightly rounded shape of the others, but still grey
    publicContacts.setBackgroundResource(R.drawable.buttonshape);
    justMeContacts.setBackgroundResource(R.drawable.buttonshape);

    //call the function to change the border colour,
    //if Phone Contacts button is clicked,
    //we want the border to be BLUE
    GlobalFunctions.sharing_border_colour(NewContact.this,"#0A7FDA");


  }

  //this is called from PopulistoContactsAdapter
  //change the colour of the justMe button
  public void changeColorofJustMe() {

    //keep the slightly rounded shape, when the button is pressed
    justMeContacts.setBackgroundResource(R.drawable.justmecontacts_buttonshapepressed);

    //keep the slightly rounded shape of the others, but still grey
    publicContacts.setBackgroundResource(R.drawable.buttonshape);
    phoneContacts.setBackgroundResource(R.drawable.buttonshape);


  }

  //textlistener for the edittext fields in NewContact
  // and EditContact, if it starts with
  //" " then don't let it input in edittext
  private TextWatcher generalTextWatcher = new TextWatcher() {

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before,
                              int count) {

      String str = charSequence.toString();
      //if it starts with " " then don't recognise it
      if (str.equals(" ")) {
        categoryname.setText("");
        namename.setText("");
        phonename.setText("");
        addressname.setText("");
      }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {


    }

    @Override
    public void afterTextChanged(Editable s) {

    }

  };

}