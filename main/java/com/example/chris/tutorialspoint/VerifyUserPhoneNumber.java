package com.example.chris.tutorialspoint;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tutorialspoint.R;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VerifyUserPhoneNumber extends AppCompatActivity  {

    // this is the php file name where to insert into the database, the user's phone number
    private static final String REGISTER_URL = "http://www.populisto.com/insert.php";

    //into insert.php we are posting phoneNoofUser, which in PHP is phonenumberofuser
    public static final String KEY_PHONENUMBER_USER = "phonenumberofuser";

    //*************TO DO WITH COMPARING APP CONTACTS AND PHONE CONTACTS*******************

    // this is the php file we are contacting with Volley to see what contacts are using the App
    private static final String CHECKPHONENUMBER_URL = "http://www.populisto.com/checkcontact.php";

    //into checkcontact.php we are posting phoneNoofContact, which in PHP is phonenumberofcontact
    public static final String KEY_PHONENUMBER_CONTACT = "phonenumberofcontact";

    //allPhonesofContacts is a list of all the phone numbers in the user's contacts
    public static final ArrayList<String> allPhonesofContacts = new ArrayList<String>();

    //allNamesofContacts is a list of all the names in the user's contacts
    public static final ArrayList<String> allNamesofContacts = new ArrayList<String>();

    // we will be making all phone contacts as a JsonArray
    JSONArray jsonArrayAllPhonesandNamesofContacts = new JSONArray();

    Cursor cursor;
    String name;
    String phoneNumberofContact;
//    String lookupkey;

    //*************************************************************************************

    //related to SMS verification
    Button btnSendSMS;
    EditText txtphoneNoofUser;

    String phoneNoofUser;
    String origNumber;
    TextView txtSelectCountry;

    private BroadcastReceiver receiver;

    TextView txtCountryCode;
    String CountryCode;
    String phoneNoofUserbeforeE164;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_phone_number);

        btnSendSMS = (Button) findViewById(R.id.btnSendSMS);


        txtphoneNoofUser = (EditText) findViewById(R.id.txtphoneNoofUser);

        txtSelectCountry = (TextView) findViewById(R.id.txtSelectCountry);

        //  when the activity loads, check to see if phoneNoofUser is using the App,if the user is
        // already registered, by checking the MyData XML file
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        phoneNoofUser = sharedPreferences.getString("phonenumberofuser", "");

        //  when the activity loads, check to see if CountryCode is in there,if the user is
        // already registered, by checking the MyData XML file
        // We need this for putting phone contacts into E164
        SharedPreferences sharedPreferencesCountryCode = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        CountryCode = sharedPreferencesCountryCode.getString("countrycode", "");

        Log.v("index value", phoneNoofUser);
        Log.v("index value", CountryCode);

        //  if the user has not already registered, if there is nothing in the SharedPreferences file,
        // then when they click the Send Message button
        //call sendSMSMessage()
        if ( phoneNoofUser == null || phoneNoofUser.equals("") ) {

            btnSendSMS.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    System.out.println("you clicked it, send message");
                    sendSMSMessage();

                }
            });


        }
        else {
            // if it is registered then
            //get all the contacts on the user's phone
            getPhoneContacts();

            //convert all contacts on the user's phone to JSON
            convertNumberstoJSON();

            // then start the next activity, PopulistoListView
            Intent myIntent = new Intent(VerifyUserPhoneNumber.this, PopulistoListView.class);
            //we need phoneNoofUser so we can get user_id and corresponding
            //reviews in the next activity
            myIntent.putExtra("keyName", phoneNoofUser);
            VerifyUserPhoneNumber.this.startActivity(myIntent);


        }

        //...back to if the user is registering, has not yet used the App
        //when 'Select Country' Text is clicked
        //load the new activity CountryCodes showing the list of all countries
        txtSelectCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(VerifyUserPhoneNumber.this, CountryCodes.class);
                VerifyUserPhoneNumber.this.startActivity(myIntent);
            }
        });

        txtCountryCode =(TextView) findViewById(R.id.txtCountryCode);
        Intent myIntent =this.getIntent();
        //coming back to this activity, put in the Country code selected by the user in CountryCodes.java
        CountryCode = myIntent.getStringExtra("CountryCode");
        txtCountryCode.setText(CountryCode);
    }




    protected void sendSMSMessage() {

        IntentFilter filter = new IntentFilter();
//        the thing we're looking out for is received SMSs
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");

        //this is to check the incoming text message
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent)

            {
                Bundle extras = intent.getExtras();

                if (extras == null)
                    return;

                SmsMessage smsMessage;

                //apparently this code deals with the deprecated createFromPdu
                //issue, for more modern phones
                if (Build.VERSION.SDK_INT >= 19) { //KITKAT
                    SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                    smsMessage = msgs[0];
                    origNumber = smsMessage.getOriginatingAddress();
                } else {
                    Object pdus[] = (Object[]) extras.get("pdus");
                    smsMessage = SmsMessage.createFromPdu((byte[]) pdus[0]);
                    origNumber = smsMessage.getOriginatingAddress();

                }

//                Object[] pdus = (Object[]) extras.get("pdus");
//                SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdus[0]);
//                origNumber = msg.getOriginatingAddress();

                Toast.makeText(getApplicationContext(), "Originating number" + origNumber, Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Sent to number" + phoneNoofUser, Toast.LENGTH_LONG).show();

                //when the text message is received, see if originating number matches the
                //sent to number
                if (origNumber.equals(phoneNoofUser)) {
                    //save the phone number so this process is skipped in future
                    SharedPreferences sharedPreferencesphoneNoofUser = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                    //save the country code so this process is skipped in future
                    SharedPreferences sharedPreferencesCountryCode = getSharedPreferences("MyData", Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedPreferencesphoneNoofUser.edit();
                    SharedPreferences.Editor editor2 = sharedPreferencesCountryCode.edit();

                    //phoneNoofUser String is unique, the username of this particular user
                    editor.putString("phonenumberofuser", phoneNoofUser);
                    //we need the Country code as it is needed for determining phone contacts in E164 format
                    editor2.putString("countrycode", CountryCode);

                    editor.commit();
                    editor2.commit();

                    //Here we want to add the user's phone number to the user table
                    //using Volley. this is a once-off
                    registerUser();
                    //get all the contacts on the user's phone
                    getPhoneContacts();
                    //convert all contacts on the user's phone to JSON
                    convertNumberstoJSON();

                    //start next activity, taking the phone number
                    Intent myIntent = new Intent(VerifyUserPhoneNumber.this, PopulistoListView.class);
                    myIntent.putExtra("keyName", phoneNoofUser);
                    VerifyUserPhoneNumber.this.startActivity(myIntent);


                }

                else {
                    Toast.makeText(getApplicationContext(), "Number not correct.", Toast.LENGTH_LONG).show();

                }
            }

        };
        registerReceiver(receiver, filter);




        //this is the number the user enters in the Phone Number textbox
        //We need to parse this, to make it into E.164 format
        phoneNoofUserbeforeE164 = txtphoneNoofUser.getText().toString();
        //add the country code onto the phone number, before we parse it
        phoneNoofUser = String.valueOf(CountryCode) +  String.valueOf(phoneNoofUserbeforeE164);

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            //For the second parameter, CountryCode, put whatever country code the user picks
            //we pass it through phoneUtil to get rid of first 0 like in +353087 etc
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNoofUser, CountryCode);

            //phoneNoofUser in the format of E164
            phoneNoofUser = phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
            //Since you know the country you can format it as follows:
            //System.out.println(phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164));
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }


        //this is the text of the SMS received
        String message = "Verification test code. Please ignore this message. Thank you.";

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNoofUser, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();

        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    // register the user's phone number in the user table, this is called
    //when the phone number is verified, when the originating number = sent to number
    private void registerUser() {
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
                params.put(KEY_PHONENUMBER_USER, phoneNoofUser);
                return params;

            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this) ;
        requestQueue.add(stringRequest);
    }





    @Override
    protected void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onDestroy();
    }


    //get the names and phone numbers of all phone contacts in phone book, take out duplicates
    //and put the phone numbers in E164 format
    private void getPhoneContacts() {

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
                    myIntent1.putExtra("phoneNumberofContact", phoneNumberofContact);
                    myIntent1.putExtra("phoneNameofContact", name);
                    //VerifyUserPhoneNumber.this.startActivity(myIntent1);

                }

            }


            while (cursor.moveToNext());
            System.out.println("size of allPhonesofContacts :" + allPhonesofContacts.size());
            System.out.println("here is the list of allPhonesofContacts :" + allPhonesofContacts);
            System.out.println("size of all names :" + allNamesofContacts.size());
            System.out.println("here is the list of names in contacts :" + allNamesofContacts);



        } catch (Exception e) {
            e.printStackTrace();
            cursor.close();
        } finally {
//                if (cursor != null) {
            cursor.close();
//                }
        }

    }


    //CONVERT all phone contacts on the user's phone  - the allPhonesofContacts array, into JSON
    //we will be using this array to see which numbers are already users of our app
    protected void convertNumberstoJSON() {

        try {

            //allPhonesofContacts is our arraylist with all the phone numbers
            for (int i = 0; i < allPhonesofContacts.size(); i++)
            {
                // make each contact in allPhonesofContacts into an individual JSON object called jsonObjectContact
                JSONObject jsonObjectContact = new JSONObject();
                // jsonObjectContact will be of the form {"name":"Bob", "phone_number":"123456789"}
                jsonObjectContact.put("phone_number", allPhonesofContacts.get(i));
                jsonObjectContact.put("name", allNamesofContacts.get(i));

                //make all the Json Objects into a JsonArray
                jsonArrayAllPhonesandNamesofContacts.put(jsonObjectContact);

            }
            System.out.println("the amount in allPhonesofContacts :" + allPhonesofContacts.size());
            // System.out.println("here is the list of allPhonesofContacts :" + allPhonesofContacts);
            System.out.println("JSONarrayAllPhonecontacts: " + jsonArrayAllPhonesandNamesofContacts.toString());
            //System.out.println("JSON object datatoSend: " + dataToSend.toString());


        } catch (final JSONException e) {
            Log.e("FAILED", "Json parsing error: " + e.getMessage());
        }

        CheckifUserisContact();
    }

    //CHECK IF USER IS A CONTACT
//this will have to be checked every so often as user's may add or delete contacts
// it's not a static once off thing.
    private void CheckifUserisContact() {
//CHECKPHONENUMBER_URL is checkcontact.php
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CHECKPHONENUMBER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //echo the phone contacts who are also app contacts, this is done on the PHP side
                        Toast.makeText(VerifyUserPhoneNumber.this, "the Populisto contacts of this user are :" + response, Toast.LENGTH_LONG).show();
                        System.out.println("the Populisto contacts of this user are :" + response);

                        String MatchingContactsAsString = response.toString();
                        System.out.println("matching contacts of this user are :" + MatchingContactsAsString);

                        //save the matchingcontacts string into shared preferences file. Intents don't work
                        //in CustomAdapters. So we'll get the values of matchingContacts into
                        //the CustomAdapter by calling it from the SelectPhoneContactAdapter. With that we'll put our
                        //matching contacts at the top of the listview, display check boxes beside them etc...
                        SharedPreferences sharedPreferencetheMatchingContacts = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editortheMatchingContacts = sharedPreferencetheMatchingContacts.edit();

                        editortheMatchingContacts.putString("thematchingcontacts", MatchingContactsAsString);
                        editortheMatchingContacts.commit();
                        System.out.println("VerifyUserPhoneNumber: matching contacts of this user are :" + MatchingContactsAsString);


                        // then start the next activity, PopulistoListView
                        //Intent myIntent = new Intent(VerifyUserPhoneNumber.this, PopulistoListView.class);
                        //we want to send allPhonesofContacts, all contacts in the user's phone book,
                        //to the next activity. "allPhonesofContacts" is the key to look out for, in the next activity
                        //myIntent.putExtra("allPhonesofContacts", allPhonesofContacts);
                        //System.out.println("VerifyUserPhoneNumber: allPhonesofContacts are :" + allPhonesofContacts);

                        //we want to bring the allPhonesofContacts array list to our adapter.It looks like Shared Preferences
                        //only works easily with strings so best way to bring the array list in Shared Preferences is with
                        //Gson.
//                        SharedPreferences sharedPreferencesallPhonesofContacts = PreferenceManager.getDefaultSharedPreferences(getApplication());
//                        SharedPreferences.Editor editorallPhonesofContacts = sharedPreferencesallPhonesofContacts.edit();
//                        Gson gson = new Gson();
//
//                        String json = gson.toJson(allPhonesofContacts);
//
//                        editorallPhonesofContacts.putString("allPhonesofContacts", json);
//                        editorallPhonesofContacts.commit();


                        //we want to bring the allNamesofContacts array list to our adapter.It looks like Shared Preferences
                        //only works easily with strings so best way to bring the array list in Shared Preferences is with
                        //Gson.
                      //  SharedPreferences sharedPreferencesallNamesofContacts = PreferenceManager.getDefaultSharedPreferences(getApplication());
//                        SharedPreferences.Editor editorallNamesofContacts = sharedPreferencesallNamesofContacts.edit();
//                        Gson gsonNames = new Gson();
//
//                        String jsonNames = gson.toJson(allNamesofContacts);
//
//                        editorallNamesofContacts.putString("allNamesofContacts", jsonNames);
//                        editorallNamesofContacts.commit();
//
//
//
//                        //myIntent.putExtra("allNamesofContacts", allNamesofContacts);
//                        System.out.println("VerifyUserPhoneNumber: allNamesofContacts are :" + allNamesofContacts);

                        //we want to send the JSON Array jsonArrayAllPhonesandNamesofContacts, all names and phone numbers
                        // of contacts in the user's phone book, to the next activity, PopulistoListView
                        //myIntent.putExtra("jsonArrayAllPhonesandNamesofContacts", jsonArrayAllPhonesandNamesofContacts.toString());

                        //we want to send the JSON Array theMatchingContacts, all contacts in the user's phone book who
                        // also use the app, to the next activity, PopulistoListView
                       // myIntent.putExtra("JsonArrayMatchingContacts", MatchingContactsAsString);

                        System.out.println("phonenoofuser" + phoneNoofUser);
                        System.out.println("all contacts on phone are " + jsonArrayAllPhonesandNamesofContacts);
                        System.out.println("the matching contacts are " + MatchingContactsAsString);

                        //VerifyUserPhoneNumber.this.startActivity(myIntent);



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
                //The KEY is php, KEY_PHONENUMBER_USER = "phonenumberofuser" .
                // In PHP we will have $_POST["phonenumberofuser"]
                params.put(KEY_PHONENUMBER_USER, phoneNoofUser);
                //The KEY is php, KEY_PHONENUMBER_CONTACT = "phonenumberofcontact" . In PHP we will have $_POST["phonenumberofcontact"]
                //The VALUE, jsonArrayAllPhonesandNamesofContacts.toString, is Android side, it will be a sequence of phone numbers
                // of the form "+12345678"
                params.put(KEY_PHONENUMBER_CONTACT, jsonArrayAllPhonesandNamesofContacts.toString());

                System.out.println(Collections.singletonList(params));
                //System.out.println("contact is : " + jsonArrayAllPhonesandNamesofContacts);
                return params;



            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }


}
