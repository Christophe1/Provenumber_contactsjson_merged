package com.example.chris.populisto;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chris.populisto.SharedReviews.SharedReview;
import com.example.tutorialspoint.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.example.chris.populisto.PopulistoContactsAdapter.checkedContactsAsArrayList;
import static com.example.tutorialspoint.R.layout.verify_user_phone_number;


import static com.example.tutorialspoint.R.layout.activity_view_contact;

public class VerifyUserPhoneNumber extends AppCompatActivity {

  // this is the php file name where to insert into the database, the user's phone number
  private static final String REGISTER_URL = "http://www.populisto.com/insert.php";

  // this is the php file name where to check if hashcode is in the DB
  private static final String HASHCHECK_URL = "http://www.populisto.com/HashCompare.php";

  // this is the php file we are contacting with Volley to see what contacts are using the App
  private static final String CHECKPHONENUMBER_URL = "http://www.populisto.com/checkcontact.php";

  //allPhonesofContacts is a list of all the phone numbers in the user's contacts
  public static final ArrayList<String> allPhonesofContacts = new ArrayList<String>();

  //allNamesofContacts is a list of all the names in the user's contacts
  public static final ArrayList<String> allNamesofContacts = new ArrayList<String>();

  // we will be making all phone contacts as a JsonArray
  JSONArray jsonArrayAllPhonesandNamesofContacts = new JSONArray();

  //matching contacts, those on phone and populisto users
  public static final ArrayList<String> MatchingContactsAsArrayList = new ArrayList<String>();
  //ArrayList<String> MatchingContactsAsArrayList;

  // Request code for READ_CONTACTS. It can be any number > 0.
  //We need this for version greater than Android 6, READ_CONTACTS in
  //Manifest alone is not enough
  private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
  //Likewise...
  private static final int PERMISSION_SEND_SMS = 1;

  Cursor cursor;
  String name;
  String phoneNumberofContact;
//    String lookupkey;

  //*************************************************************************************

  //related to SMS verification
  Button btnSendSMS;
  EditText txtphoneNoofUser;

  //phoneNoofUser is the number (including int code) of the logged-in user that will be stored in
  //in the db and in shared preferences. It is posted when the number is verified.
  //Of the format +353872934480
  String phoneNoofUser;

  //phoneNoofUserInternationalFormat is for formatting purposes only,
  //e.g: +353 87 293 44 80
  String phoneNoofUserInternationalFormat;

  //the generated hash value
  String hashedPassWord;

  //time and date the user registered at
  String time_stamp;

  //the hash value stored in XML
  String hashedPassinXML;

  //the response from HashCompare.php, will be "True" or "False"
  String hashPassTrueorFalse;

  TextView txtSelectCountry;

  TextView txtCountryCode;
  String CountryCode;
  String CountryName;
  String phoneNoofUserbeforeE164;

  EditText txtVerificationCode;

  //This is the verification id that will be sent to the user
  private String mVerificationId;

  //firebase auth object
  private FirebaseAuth mAuth;

  Button buttonSignIn;

  //this is for the progress dialog, while logged-in user is
  //waiting for verification code
  //DelayedProgressDialog progressDialog = new DelayedProgressDialog();


  public static Activity activity = null;

  //24/8/2018
  // ArrayList called sharedReviews that will contain sharedReviews info
  //we use this to pass jsonArrayofPhonesandNamesofContacts to sharedReviews,
  //so we can put the phone name beside the review
  //ArrayList<SharedReview> sharedReviews;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


    //so we can close the activity, if the user has no internet access and they
    //choose "Try again later"
    activity = this;

    //execute the AsyncTask, do stuff in the background
    VerifyUserPhoneNumber.StartUpInfo startUpInfo = new VerifyUserPhoneNumber.StartUpInfo();
    startUpInfo.execute();

  }


  //AsyncTask
  private class StartUpInfo extends AsyncTask<Void, Void, Void> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      setContentView(verify_user_phone_number);

    }

    @Override
    protected Void doInBackground(Void... params) {

      // when the activity loads, check to see if phoneNoofUser is using the App,if the user is
      // already registered.
      // We do this by comparing hash and phone number in MyData XML file against our DB.
      //If they match, user is legit user.
      SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
      hashedPassinXML = sharedPreferences.getString("hashedpassword", "");

      SharedPreferences sharedPreferences2 = getSharedPreferences("MyData", Context.MODE_PRIVATE);
      phoneNoofUser = sharedPreferences2.getString("phonenumberofuser", "");

      System.out.println("hashpassinXML is:" + hashedPassinXML);
      System.out.println("phoneNoofUser is:" + phoneNoofUser);

      //  when the activity loads, check to see if CountryCode is in there,if the user is
      // already registered, by checking the MyData XML file
      // We need this for putting phone contacts into E164
      SharedPreferences sharedPreferencesCountryCode = getSharedPreferences("MyData", Context.MODE_PRIVATE);
      CountryCode = sharedPreferencesCountryCode.getString("countrycode", "");

//            Log.v("index value", phoneNoofUser);
//            Log.v("index value", CountryCode);

      //clear these arraylists when the app starts
      //because I was getting repeats of names and phone numbers
      allPhonesofContacts.clear();
      allNamesofContacts.clear();

      //if checked values for a review still exist in sharedprefs,
      //delete them
      if (checkedContactsAsArrayList != null) {
        //reset the size of the array to 0
        checkedContactsAsArrayList.clear();
      }

      //We need to post hashedpass XML and logged-in phone number
      // to the server and see if the row exists in our DB
      //if it does, then get corresponding user_id and do stuff
      //HASHCHECK_URL is HashCompare.php
      StringRequest stringRequest = new StringRequest(Request.Method.POST, HASHCHECK_URL,
          new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

              //hide the pDialog 'loading' box when the page loads
              //hidePDialog();

              System.out.println("third, hashpassinXML is:" + hashedPassinXML);

              //make hashPassTrueorFalse = the response string, "True" or "False"
              hashPassTrueorFalse = response.toString();
              Toast.makeText(VerifyUserPhoneNumber.this, hashPassTrueorFalse, Toast.LENGTH_LONG).show();
              System.out.println("hashPassTrueorFalse is:" + hashPassTrueorFalse);
              //If the hash on the user's phone does not equal the hash in the DB..
              //and the phone number does not match...

              //If false...
              if (hashPassTrueorFalse.equals("False")) {

                //then show the registration page
                sendSMSandRegisterUser();

              } else {
                // if it is registered, if "True", then...
                //start the next activity, PopulistoListView
                Intent myIntent = new Intent(VerifyUserPhoneNumber.this, PopulistoListView.class);

                //Toast.makeText(getApplicationContext(), "You've allowed Contacts Access", Toast.LENGTH_LONG).show();

                //we need phoneNoofUser so we can get user_id and corresponding
                //reviews in the next activity
                myIntent.putExtra("keyName", phoneNoofUser);
                VerifyUserPhoneNumber.this.startActivity(myIntent);

                //If READ_CONTACTS has been authorized
                PackageManager manager = getPackageManager();
                int hasPermission = manager.checkPermission("android.permission.READ_CONTACTS", "com.example.chris.populisto");
                if (hasPermission == manager.PERMISSION_GRANTED) {
                  //you have permission
                  //Toast.makeText(getApplicationContext(), "Read contact granted!", Toast.LENGTH_LONG).show();
                  //get all the contacts on the user's phone
                  getPhoneContacts();

                }
                //If READ_CONTACTS Permission has been denied
                else {
                  Toast.makeText(getApplicationContext(), "Read contacts not granted yet", Toast.LENGTH_LONG).show();

                }
              }

            }
          },
          new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

              //dismiss the dialog before showing the error dialog
              //progressDialog.cancel();

              //If there is an error (such as contacting server for example) then
              //show a message like:
              //Sorry, can't contact server right now. Is internet access enabled?, try again, Cancel
              GlobalFunctions.troubleContactingServerDialog(VerifyUserPhoneNumber.this);

            }
          }) {
        @Override
        protected Map<String, String> getParams() {
          Map<String, String> params = new HashMap<String, String>();
          //the second value, hashedPassinXML
          //is the value we get from Android.
          //the key is "hashpass",
          // When we see this in our php,  $_POST["hashpass"],
          //put in the value from Android, hashedPassinXML
          params.put("hashpass", hashedPassinXML);
          params.put("phonenumberofuser", phoneNoofUser);
          System.out.println("second, hashpassinXML is:" + hashedPassinXML);
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


      RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
      requestQueue.add(stringRequest);

      return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
      super.onPostExecute(aVoid);


    }
  }


  protected void sendSMSandRegisterUser() {
    //we are loading the xml for first registering so we need to run a UI Thread. This is necessary
    //when showing xml from the doInBackground
    runOnUiThread(new Runnable() {
      @Override
      public void run() {

        //if the user has not yet registered then start the verify number xml

        setContentView(R.layout.verify_phone_number);

        //initialize objects
        mAuth = FirebaseAuth.getInstance();

        btnSendSMS = (Button) findViewById(R.id.btnSendSMS);

        txtphoneNoofUser = (EditText) findViewById(R.id.txtphoneNoofUser);

        txtSelectCountry = (TextView) findViewById(R.id.txtSelectCountry);

        txtCountryCode = (TextView) findViewById(R.id.txtCountryCode);

        txtVerificationCode = (EditText) findViewById(R.id.txtVerificationCode);

        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);

        //dismiss the dialog when sendSMSandRegisterUser() has been called
        //progressDialog.cancel();

        //check if Marshmallow or newer
        //if so, we need to manually check for permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

          //check if permissions denied
          if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {

            //if denied, show the standard Android dialog, 'Allow access to Contacts?'
            ActivityCompat.requestPermissions(VerifyUserPhoneNumber.activity,
                new String[]{Manifest.permission.READ_CONTACTS},
                PERMISSIONS_REQUEST_READ_CONTACTS);

          }

          //check if permissions granted
          if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            if (hashPassTrueorFalse.equals("True")) {
              //if so, get phone contacts
              getPhoneContacts();
            }
          }

        }
        //when 'Select Country' Text is clicked
        //load the activity CountryCodes showing the list of all countries
        //and retain details from VerifyUserPhoneNumber
        txtSelectCountry.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

            SelectCountryorCodeClicked();

          }
        });

        //when 'Select Code' Text is clicked
        //load the activity CountryCodes showing the list of all countries
        //and retain details from VerifyUserPhoneNumber
        txtCountryCode.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

            SelectCountryorCodeClicked();

          }
        });

        //coming back from CountryCodes.java to this activity,
        //put in the Country code selected
        //by the user in CountryCodes.java
        Intent myIntent = VerifyUserPhoneNumber.this.getIntent();
        CountryCode = myIntent.getStringExtra("CountryCode");
        txtCountryCode.setText(CountryCode);

        //put in the country name selected
        //by the user in CountryCodes.java
        CountryName = myIntent.getStringExtra("CountryName");
        txtSelectCountry.setText(CountryName);

        //put in the phone number entered in VerifyUserPhoneNumber
        //before having gone on to CountryCodes.java
        phoneNoofUserbeforeE164 = myIntent.getStringExtra("PhoneNumber");
        txtphoneNoofUser.setText(phoneNoofUserbeforeE164);


        //When the Send button is clicked, send the message
        //to phone number the user has entered
        btnSendSMS.setOnClickListener(new View.OnClickListener() {
          public void onClick(View view) {

            //If permission denied (will only be on Marshmallow +)
            PackageManager manager = getPackageManager();
            int hasPermission = manager.checkPermission("android.permission.READ_CONTACTS", "com.example.chris.populisto");
            if (hasPermission == manager.PERMISSION_DENIED) {
              //you don't have permission
              Toast.makeText(getApplicationContext(), "No. Read contacts not granted", Toast.LENGTH_LONG).show();

              new AlertDialog.Builder(VerifyUserPhoneNumber.this).
                  setCancelable(false).
                  // setTitle("You need to enable Read Contacts").
                      setMessage("To get full use of Populisto, allow Populisto access to your contacts. Tap Settings > Permisions and turn Contacts on.").
                  //setIcon(R.drawable.ninja).
                      setPositiveButton("SETTINGS",
                      new DialogInterface.OnClickListener() {
                        @TargetApi(11)
                        public void onClick(DialogInterface dialog, int id) {
                          //showToast("Thank you! You're awesome too!");
                          dialog.cancel();

                          Intent intent = new Intent();
                          intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                          Uri uri = Uri.fromParts("package", getPackageName(), null);
                          intent.setData(uri);
                          startActivity(intent);
                        }
                      })
                  .setNegativeButton("NOT NOW", new DialogInterface.OnClickListener() {
                    @TargetApi(11)
                    public void onClick(DialogInterface dialog, int id) {
                      //showToast("Mike is not awesome for you. :(");
                      dialog.cancel();
                    }
                  }).show();

            } else {
              //for phones older then Marshmallow, go straight to here
              Toast.makeText(getApplicationContext(), "Yes!Read contacts granted", Toast.LENGTH_LONG).show();

            }

            //if the box where user enters phone number is empty
            String mobile = txtphoneNoofUser.getText().toString().trim();

            if (mobile.isEmpty()) {
              txtphoneNoofUser.setError("Please enter your number");
              txtphoneNoofUser.requestFocus();
              return;
            }

            //if the country code is empty
            if (txtCountryCode.getText().toString().equals("")) {

              txtCountryCode.setError("Please select country");
              txtCountryCode.requestFocus();
              return;
            }

            //add the country code onto the phone number, before we parse it
            phoneNoofUserInternationalFormat = String.valueOf(CountryCode) + String.valueOf(txtphoneNoofUser.getText().toString());

            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            try {
              //For the second parameter, CountryCode, put whatever country code the user picks
              //we pass it through phoneUtil to get rid of first 0 like in +353087 etc
              Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNoofUserInternationalFormat, CountryCode);

              //phoneNoofUser in the correct format
              phoneNoofUserInternationalFormat = phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);

            } catch (NumberParseException e) {
              System.err.println("NumberParseException was thrown: " + e.toString());
            }

            Toast.makeText(getApplicationContext(), "user's number is:" + phoneNoofUserInternationalFormat, Toast.LENGTH_LONG).show();

            //Before sending, show an alert dialogue
            AlertDialog.Builder builder = new AlertDialog.Builder(btnSendSMS.getContext());
            String alert1 = "We will be verifying the phone number:";
            String alert2 = "Is this OK, or would you like to edit the number?";
            builder.setMessage(alert1 + "\n" + "\n" + phoneNoofUserInternationalFormat + "\n"  + "\n" + alert2).setPositiveButton("OK", dialogClickListener)
                .setNegativeButton("EDIT", dialogClickListener).show();

          }
        });

        //if the automatic sms detection did not work, user can also enter the code manually
        //so adding a click listener to the button
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            String code = txtVerificationCode.getText().toString().trim();
            if (code.isEmpty() || mVerificationId == null) {
              txtVerificationCode.setError("Code is not correct");
              txtVerificationCode.requestFocus();
              return;
            }

            //verifying the code entered manually
            verifyVerificationCode(code);
          }
        });

      }

    });


  }

  //this is done just once, on registeration.
  // register the user's phone number, timestamp and the corresponding hash in the user table, this is called
  //when the phone number is verified, when the originating number = sent to number
  private void registerUser() {
    Toast.makeText(VerifyUserPhoneNumber.this, "the numbers match dude" + phoneNoofUser, Toast.LENGTH_LONG).show();


    //REGISTER_URL is insert.php
    StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
        new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {
            // Toast.makeText(VerifyUserPhoneNumber.this, response, Toast.LENGTH_LONG).show();
          }
        },
        new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {
            Toast.makeText(VerifyUserPhoneNumber.this, error.toString(), Toast.LENGTH_LONG).show();

          }

        }) {
      @Override
      protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        //the second value, phoneNoofUser
        // is the value we get from Android.
        //the key is "phonenumberofuser",
        // When we see these in our php,  $_POST["phonenumberofuser"],
        //put in the value from Android
        //Likewise, hashpass is the key in PHP etc..
        params.put("phonenumberofuser", phoneNoofUser);
        params.put("hashpass", hashedPassWord);
        params.put("timestamp", time_stamp);
        return params;

      }

    };
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(stringRequest);
  }


  @Override
  protected void onDestroy() {
 /*   if (receiver != null) {
      unregisterReceiver(receiver);
      receiver = null;
    }*/
    super.onDestroy();
    // hidePDialog();
    //dismiss the dialog when we get the response
    //progressDialog.cancel();
  }


  //get the names and phone numbers of all phone contacts in phone book, take out duplicates
  //and put the phone numbers in E164 format
  private void getPhoneContacts() {

  //  Toast.makeText(this, "getPhoneContacts has been called but nothing done apart from this toast.", Toast.LENGTH_SHORT).show();

    // Check the SDK version and whether the read contacts permission is already granted or not.
    // If the phone is Android 6/ SDK 23+ (??) then the phone user has to authorize some "dangerous" commands
    //at run-time.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
      //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
      Toast.makeText(this, "Permission sought", Toast.LENGTH_SHORT).show();


    }

//    Toast.makeText(this, "getContacts function called", Toast.LENGTH_SHORT).show();
//          we have this here to avoid cursor errors
    if (cursor != null) {
      cursor.moveToFirst();

    }


    try {

//                get a handle on the Content Resolver, so we can query the provider,
      cursor = getApplicationContext().getContentResolver()
//                the table to query
          .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//               Null. This means that we are not making any conditional query into the contacts table.
//               Hence, all data is returned into the cursor.
//               Projection - the columns you want to query
              null,
//               Selection - with this you are extracting records with assigned (by you) conditions and rules
              null,
//               SelectionArgs - This replaces any question marks (?) in the selection string
//               if you have something like String[] args = { "first string", "second@string.com" };
              null,
//               display in ascending order
              ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC");

//                get the column number of the Contact_ID column, make it an integer.
//                I think having it stored as a number makes for faster operations later on.
//            int Idx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
//                get the column number of the DISPLAY_NAME column
      int nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
//                 get the column number of the NUMBER column
      int phoneNumberofContactIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

      cursor.moveToFirst();

//              We make a new Hashset to hold all our contact_ids, including duplicates, if they come up
      Set<String> ids = new HashSet<>();
//              We make a new Hashset to hold all our lookup keys, including duplicates, if they come up
//            Set<String> ids2 = new HashSet<>();
      do {
        System.out.println("=====>in while");

//                        get a handle on the display name, which is a string
        name = cursor.getString(nameIdx);

//                        get a handle on the phone number, which is a string
        phoneNumberofContact = cursor.getString(phoneNumberofContactIdx);

        //----------PUT INTO E164 FORMAT--------------------------------------
        //need to strip all characters except numbers and + (+ for the first character)
        phoneNumberofContact = phoneNumberofContact.replaceAll("[^+0-9]", "");
        //replace numbers starting with 00 with +
        if (phoneNumberofContact.startsWith("00")) {
          phoneNumberofContact = phoneNumberofContact.replaceFirst("00", "+");
        }

        //all phone numbers not starting with +, make them E.164 format,
        //for the country code the user has chosen.
        if (!phoneNumberofContact.startsWith("+")) {
          //CountryCode is the country code chosen by the user originally
          phoneNumberofContact = String.valueOf(CountryCode) + String.valueOf(phoneNumberofContact);

          PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
          try {
            //if phone number on user's phone is not in E.164 format,
            //precede the number with user's country code.
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNumberofContact, "");
            phoneNumberofContact = phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
            //If an error happens :
          } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
            // System.out.println(phoneNumberofContact);
          }
        }

        //----------------------------------------------------------

//                  if our Hashset doesn't already contain the phone number string,
//                    then add it to the hashset
        if (!ids.contains(phoneNumberofContact)) {
          ids.add(phoneNumberofContact);

          //allPhonesofContacts is a list of all the phone numbers in the user's contacts
          allPhonesofContacts.add(phoneNumberofContact);

          //allNamesofContacts is a list of all the names in the user's contacts
          allNamesofContacts.add(name);

          System.out.println(" Name--->" + name);
          System.out.println(" Phone number of contact--->" + phoneNumberofContact);


          // then start the next activity, PopulistoListView
          Intent myIntent1 = new Intent(VerifyUserPhoneNumber.this, PopulistoListView.class);

          //it looks like the putExtra info here is not needed,
          //there's no phoneNumberofContact or phoneNameofContact
          //in populistolistview

          // myIntent1.putExtra("phoneNumberofContact", phoneNumberofContact);
          //myIntent1.putExtra("phoneNameofContact", name);
          VerifyUserPhoneNumber.this.startActivity(myIntent1);


        }

      }

      while (cursor.moveToNext());
      System.out.println("size of allPhonesofContacts :" + allPhonesofContacts.size());
      System.out.println("here is the list of allPhonesofContacts :" + allPhonesofContacts);
      System.out.println("size of all names :" + allNamesofContacts.size());
      System.out.println("here is the list of names in contacts :" + allNamesofContacts);


      //we will save the array list allPhonesofContacts,
      //with this we will put all phone numbers of contacts on user's phone into our RecyclerView, in other activities
      SharedPreferences sharedPreferencesallPhonesofContacts = PreferenceManager.getDefaultSharedPreferences(getApplication());
      SharedPreferences.Editor prefsEditor = sharedPreferencesallPhonesofContacts.edit();

      Gson gson = new Gson();
      String json = gson.toJson(allPhonesofContacts);
      prefsEditor.putString("allPhonesofContacts", json);
      prefsEditor.commit();
      System.out.println("allPhonesofContacts json is:" + json);

      //now, let's put in the string of names
      //save the array list allNamesofContacts,
      //with this we will put all phone names of contacts on user's phone into our ListView, in other activities
      SharedPreferences sharedPreferencesallNamesofContacts = PreferenceManager.getDefaultSharedPreferences(getApplication());
      SharedPreferences.Editor prefsEditor2 = sharedPreferencesallNamesofContacts.edit();

      //now, let's put in the string of names
      Gson gsonNames = new Gson();
      String jsonNames = gsonNames.toJson(allNamesofContacts);
      prefsEditor2.putString("allNamesofContacts", jsonNames);
      prefsEditor2.commit();
      System.out.println("allNamesofContacts json is:" + jsonNames);


    } catch (Exception e) {
      e.printStackTrace();
      if (cursor != null) {
        cursor.close();
      }
/*      finally {

                if (cursor != null) {
        cursor.close();
                }
      }*/
    }
    //convert all contacts on the user's phone to JSON
    convertNumberstoJSON();

    // }

  }

/*  @Override
  //we need this for newer phones, so permissions can
  //be given by the user at run-time
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                         int[] grantResults) {
    if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        // Permission is granted
        getPhoneContacts();
      }
      if (requestCode == PERMISSION_SEND_SMS) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          // Permission is granted
          sendSMSMessage();
        }

*//*      else {
        Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
      }*//*
      }
    }
  }*/

  //CONVERT all phone contacts on the user's phone  - the allPhonesofContacts array, into JSON
  //we will be using this array to see which numbers are already users of our app
  protected void convertNumberstoJSON() {

    try {

      //allPhonesofContacts is our arraylist with all the phone numbers
      for (int i = 0; i < allPhonesofContacts.size(); i++) {
        // make each contact in allPhonesofContacts into an individual JSON object called jsonObjectContact
        JSONObject jsonObjectContact = new JSONObject();
        // jsonObjectContact will be of the form {"name":"Bob", "phone_number":"123456789"}
        jsonObjectContact.put("phone_number", allPhonesofContacts.get(i));
        jsonObjectContact.put("name", allNamesofContacts.get(i));

        //make all the Json Objects into a JsonArray
        jsonArrayAllPhonesandNamesofContacts.put(jsonObjectContact);

      }


      System.out.println("all of the json array phones and names are :" + jsonArrayAllPhonesandNamesofContacts);
      System.out.println("the amount in allPhonesofContacts :" + allPhonesofContacts.size());
      // System.out.println("here is the list of allPhonesofContacts :" + allPhonesofContacts);
      //System.out.println("JSON object datatoSend: " + dataToSend.toString());


      //put jsonArrayAllPhonesandNamesofContacts into shared preferences file as a String
      //We will convert back to Json later, in PopulistoListView,for linking phone no with contact
      SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
      //we want to edit SharedPreferences
      SharedPreferences.Editor editor = sharedPrefs.edit();
      //put the string value into SharedPreferences, with the key "AllPhonesandNamesofContacts"
      editor.putString("AllPhonesandNamesofContacts", jsonArrayAllPhonesandNamesofContacts.toString());
      //commit the string
      editor.commit();
      System.out.println("jsonArrayAllPhonesandNamesofContacts: " + jsonArrayAllPhonesandNamesofContacts.toString());


    } catch (final JSONException e) {
      Log.e("FAILED", "Json parsing error: " + e.getMessage());
    }

    CheckifUserisContact();
  }

  //CHECK IF USER IS A CONTACT
//this will have to be verified every so often as user may add or delete contacts
// it's not a static once off thing.
  private void CheckifUserisContact() {
//CHECKPHONENUMBER_URL is checkcontact.php
    StringRequest stringRequest = new StringRequest(Request.Method.POST, CHECKPHONENUMBER_URL,
        new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {
            //AppController.getInstance().getRequestQueue().getCache().remove(CHECKPHONENUMBER_URL);

            //toast the phone contacts who are also app contacts, this is done on the PHP side
            // it will be a JSONArray of the form [{"phone_number":"+35312345"}, {"phone_number": etc...
            // We get this from our php file, checkcontact.php. Then we will convert to a string
            // and extract the phone numbers and compare against the contacts on the user's phone.
            Toast.makeText(VerifyUserPhoneNumber.this, "thek Populisto contacts of this user are :" + response, Toast.LENGTH_LONG).show();
            System.out.println("the Populisto contacts of this user are :" + response);

            //convert the JSONArray, the response, to a string
            //It will be like: [{"phone_number":"+353864677745"}, etc...]
            String MatchingContactsAsString = response.toString();
            System.out.println("VerifyUserPhoneNumber1: matching contacts of this user are :" + MatchingContactsAsString);

            //make an arraylist which will hold the phone_number part of the MatchingContacts string
            //MatchingContactsAsArrayList = new ArrayList<String>();

            try {
              JSONArray Object = new JSONArray(MatchingContactsAsString);
              //for every object in the Array
              for (int x = 0; x < Object.length(); x++) {
                final JSONObject obj = Object.getJSONObject(x);
                //strip out the phone number
                //so it will be like: [+353864677745, +35387123456, etc...]
                MatchingContactsAsArrayList.add(obj.getString("phone_number"));

              }
              System.out.println("VerifyUserPhoneNumber: MatchingContactsAsArrayList :" + MatchingContactsAsArrayList);

              //save MatchingContactsAsArrayList into sharedpreferences so we can use it elsewhere
              //in our project. It looks like Shared Preferences
              //only works easily with strings so best way to bring the array list in Shared Preferences is with
              //Gson. Here, we PUT the arraylist into the sharedPreferences
              SharedPreferences sharedPreferencesMatchingContactsAsArrayList = PreferenceManager.getDefaultSharedPreferences(getApplication());
              SharedPreferences.Editor editorMatchingContactsAsArrayList = sharedPreferencesMatchingContactsAsArrayList.edit();
              Gson gsonMatchingContactsAsArrayList = new Gson();
              String jsonMatchingContactsAsArrayList = gsonMatchingContactsAsArrayList.toJson(MatchingContactsAsArrayList);
              editorMatchingContactsAsArrayList.putString("MatchingContactsAsArrayList", jsonMatchingContactsAsArrayList);
              editorMatchingContactsAsArrayList.commit();
              System.out.println("VerifyUserPhoneNumber2: jsonMatchingContactsAsArrayList :" + jsonMatchingContactsAsArrayList);
              System.out.println("VerifyUserPhoneNumber2: MatchingContactsAsArrayList :" + MatchingContactsAsArrayList);

              System.out.println("phonenoofuser" + phoneNoofUser);
              System.out.println("VerifyUserPhoneNumber: all contacts on phone are " + jsonArrayAllPhonesandNamesofContacts);
              System.out.println("the matching contacts are " + MatchingContactsAsString);

            } catch (Exception e) {
              e.printStackTrace();
            }


          }
        },
        new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {
            Toast.makeText(VerifyUserPhoneNumber.this, error.toString(), Toast.LENGTH_LONG).show();

          }

        }) {


      @Override
      protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> params = new HashMap<String, String>();
        //We are  posting the user's phone number in this request, so we can get
        //matching user_id for checking contacts
        //the second value, phoneNoofUser
        // is the value we get from Android.
        //the key is "phonenumberofuser",
        // When we see these in our php,  $_POST["phonenumberofuser"],
        //put in the value from Android.
        params.put("phonenumberofuser", phoneNoofUser);
        //In PHP we will have $_POST["phonenumberofcontact"]
        //The VALUE, jsonArrayAllPhonesandNamesofContacts.toString, is Android side, it will be a sequence of phone numbers
        // of the form "+12345678"
        params.put("phonenumberofcontact", jsonArrayAllPhonesandNamesofContacts.toString());
        System.out.println("in post: all of the json array phones and names are :" + jsonArrayAllPhonesandNamesofContacts);
        System.out.println(Collections.singletonList(params));
        //System.out.println("contact is : " + jsonArrayAllPhonesandNamesofContacts);
        return params;


      }
    };


    // Adding request to request queue
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(stringRequest);

  }

  //You sure you want to send a text to this number? Edit it?
  DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
      switch (which) {
        case DialogInterface.BUTTON_POSITIVE:
          //OK button clicked
          //send the text
          //sendSMSMessage();
          sendVerificationCode();

        case DialogInterface.BUTTON_NEGATIVE:

          //close the dialog
          dialog.dismiss();
      }
    }
  };


  //function to convert string to md5 hash
  //we will be putting this into the DB
  public static String MD5(String input) {

    try {

      MessageDigest md = MessageDigest.getInstance("MD5");
      //convert to bytes
      byte[] messageDigest = md.digest(input.getBytes());
      BigInteger number = new BigInteger(1, messageDigest);
      String hashtext = number.toString(16);
      while (hashtext.length() < 32) {
        hashtext = "0" + hashtext;
      }
      return hashtext;
    } catch (Exception e) {

      throw new RuntimeException(e);
    }
  }

  private void SelectCountryorCodeClicked() {

    //this is the number the user enters in the Phone Number textbox
    //We need to parse this when sent, to make it into E.164 format
    phoneNoofUserbeforeE164 = txtphoneNoofUser.getText().toString();

    // txtphoneNoofUser.setText(phoneNoofUserbeforeE164);
    //System.out.println("the number is " + phoneNoofUserbeforeE164);

    Intent myIntent = new Intent(VerifyUserPhoneNumber.this, CountryCodes.class);
    //bring over the string value CountryCode if it exists, from VerifyPhoneNumber to CountryCodes.java
    //we do this so, if a Country Code has been chosen for the Country Code field in VerifyPhoneNumber,
    // and the "<" button is pressed in CountryCodes.java,
    //it will revert to the already chosen Country Code.
    myIntent.putExtra("CountryCode", CountryCode);

    //same for Country Name
    myIntent.putExtra("CountryName", CountryName);

    //also, we want to keep track of the phone number, if it has been entered in VerifyPhoneNumber
    myIntent.putExtra("PhoneNumber", phoneNoofUserbeforeE164);
    Toast.makeText(getApplicationContext(), "vvv" + phoneNoofUserbeforeE164, Toast.LENGTH_LONG).show();

    VerifyUserPhoneNumber.this.startActivity(myIntent);

  }

  //the method is sending verification code to phoneNoofUserInternationalFormat,
  //the number entered by the user
  private void sendVerificationCode() {

    //setContentView(R.layout.verify_phone_number);
    RelativeLayout progressContainer = findViewById(R.id.container);// change id here

    progressContainer.setVisibility(View.VISIBLE);

    //this is the number the user enters in the Phone Number textbox
    //We need to parse this, to make it into E.164 format (like +353 etc...)
    phoneNoofUserbeforeE164 = txtphoneNoofUser.getText().toString();

    //add the country code onto the phone number, before we parse it
    phoneNoofUser = String.valueOf(CountryCode) + String.valueOf(phoneNoofUserbeforeE164);

    //Now make it into E.164...
    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    try {
      //For the second parameter, CountryCode, put whatever country code the user picks
      //we pass it through phoneUtil to get rid of first 0 like in +353087 etc
      Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNoofUser, CountryCode);

      //phoneNoofUser in the format of E164, like +353872934480
      phoneNoofUser = phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164);

    } catch (NumberParseException e) {
      System.err.println("NumberParseException was thrown: " + e.toString());
    }

    PhoneAuthProvider.getInstance().verifyPhoneNumber(
        //send a text to the phone number entered by the user
        phoneNoofUserInternationalFormat,
        60,
        TimeUnit.SECONDS,
        TaskExecutors.MAIN_THREAD,
        mCallbacks);
  }

  //the callback to detect the verification status
  private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
    @Override
    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

      //Getting the code sent by SMS
      String code = phoneAuthCredential.getSmsCode();

      //sometimes the code is not detected automatically
      //in this case the code will be null
      //so user has to manually enter the code
      if (code != null) {
        txtVerificationCode.setText(code);
        //verifying the code
        verifyVerificationCode(code);
      }
    }

    @Override
    public void onVerificationFailed(FirebaseException e) {
      Toast.makeText(VerifyUserPhoneNumber.this, e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
      super.onCodeSent(s, forceResendingToken);

      //storing the verification id that is sent to the user
      mVerificationId = s;
    }
  };

  private void verifyVerificationCode(String code) {

    //creating the credential, with the code texted to the user and the code
    // entered by the user
    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

    //signing in the user with above details
    signInWithPhoneAuthCredential(credential);
  }

  private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
    mAuth.signInWithCredential(credential)
        .addOnCompleteListener(VerifyUserPhoneNumber.this, new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {

              Toast.makeText(getApplicationContext(), "text received!", Toast.LENGTH_LONG).show();

              //save the phone number so this process is skipped in future
              SharedPreferences sharedPreferencesphoneNoofUser = getSharedPreferences("MyData", Context.MODE_PRIVATE);
              SharedPreferences.Editor editor = sharedPreferencesphoneNoofUser.edit();

              //phoneNoofUser String is unique, the username of this particular user
              //it will be of the form +353872934480
              editor.putString("phonenumberofuser", phoneNoofUser);
              editor.commit();

              //save the country code so this process is skipped in future
              SharedPreferences sharedPreferencesCountryCode = getSharedPreferences("MyData", Context.MODE_PRIVATE);
              SharedPreferences.Editor editor2 = sharedPreferencesCountryCode.edit();
              //we need the Country code as it is needed for determining phone contacts in E164 format
              editor2.putString("countrycode", CountryCode);
              editor2.commit();

              //let's get the current date and time, for time_stamp
              SimpleDateFormat s = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
              time_stamp = s.format(new Date());

              //make a password combining time_stamp and phone number
              String password = time_stamp + phoneNoofUser;
              hashedPassWord = MD5(password);

              //When the user registers, when the verification code has been approved,
              //save the hashedPassWord into xml shared prefs
              //we will also be posting hashedPassWord into the DB, further below
              SharedPreferences sharedPreferenceshashedpassword = getSharedPreferences("MyData", Context.MODE_PRIVATE);
              SharedPreferences.Editor editor3 = sharedPreferenceshashedpassword.edit();

              //put hashedPassWord into XML
              editor3.putString("hashedpassword", hashedPassWord);
              editor3.commit();

              System.out.println("MD5 " + hashedPassWord);

              System.out.println("date format : " + time_stamp);

              //Here we want to add the user's phone number, timestamp and the hash
              //(timestamp + phone) to the user table
              //using Volley. this is a once-off
              registerUser();

              //If READ_CONTACTS has been enabled...
              PackageManager manager = getPackageManager();
              int hasPermission = manager.checkPermission("android.permission.READ_CONTACTS", "com.example.chris.populisto");
              if (hasPermission == manager.PERMISSION_GRANTED) {
                //you don't have permission
                Toast.makeText(getApplicationContext(), "Yes!Read contacts granted", Toast.LENGTH_LONG).show();

                //get all the contacts on the user's phone
                getPhoneContacts();

                //convert all contacts on the user's phone to JSON
                convertNumberstoJSON();


              } else {
                Toast.makeText(getApplicationContext(), "No. Read contacts not granted", Toast.LENGTH_LONG).show();

              }


              //verification successful, we will start PopulistoListView activity
              Intent intent = new Intent(VerifyUserPhoneNumber.this, PopulistoListView.class);
              intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
              startActivity(intent);

             // Toast.makeText(getApplicationContext(), "You've allowed Contacts Access", Toast.LENGTH_LONG).show();


            } else {

              //verification unsuccessful.. display an error message

              String message = "Something is wrong, we will fix it soon...";

              if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                message = "Invalid code entered...";
              }
            }
          }
        });
  }


  //if the user chooses to refresh the Activity, when "Try Again" button is clicked...
  public void refresh() {
    Intent intent = getIntent();
    overridePendingTransition(0, 0);
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    finish();
    overridePendingTransition(0, 0);
    startActivity(intent);
  }

  @Override
  //check Permissions status, this is called when the user clicks ALLOW on the dialog
  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[], int[] grantResults) {
    switch (requestCode) {

      //if Permission is granted, then continue as normal
      case PERMISSIONS_REQUEST_READ_CONTACTS: {

        if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          //getPhoneContacts();
          Toast.makeText(getApplicationContext(), "Yay! Granted now", Toast.LENGTH_LONG).show();

          //if Permission is denied, then show our custom made dialog
          //This is important for if user chooses, 'Don't Show Again',
          //It will open up SETTINGS and user can change it
        } else {
          new AlertDialog.Builder(this).
              setCancelable(false).
              // setTitle("You need to enable Read Contacts").
                  setMessage("To get full use of Populisto, allow Populisto access to your contacts. Click SETTINGS, tap Permissions and turn Contacts on.").
              //setIcon(R.drawable.ninja).
                  setPositiveButton("SETTINGS",
                  new DialogInterface.OnClickListener() {
                    @TargetApi(11)
                    public void onClick(DialogInterface dialog, int id) {
                      dialog.cancel();

                      //Open up the SETTINGS for the App, user can enable contacts
                      Intent intent = new Intent();
                      intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                      Uri uri = Uri.fromParts("package", getPackageName(), null);
                      intent.setData(uri);
                      startActivity(intent);
                    }
                  })
              .setNegativeButton("NOT NOW", new DialogInterface.OnClickListener() {
                @TargetApi(11)
                public void onClick(DialogInterface dialog, int id) {
                  dialog.cancel();
                }
              }).show();

        }
        return;
      }

    }
  }


}