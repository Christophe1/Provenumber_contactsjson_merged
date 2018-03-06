package com.example.chris.tutorialspoint;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.tutorialspoint.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.support.v7.appcompat.R.styleable.MenuItem;


public class NewContact extends AppCompatActivity {
    // this is the php file name where to save to.
    // we will post the category, name, phone, address, comment etc into Php and
    // create a new review_id
    private static final String NewContact_URL = "http://www.populisto.com/NewContact.php";

    //in this JSONArray, checkedContacts, we will be storing each checkedContact JSON Object
    //Then we're going to post it to our NewContact.php file
    JSONArray checkedContacts = new JSONArray();

    private ProgressDialog pDialog;

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


    int public_or_private;

    // ArrayList called selectPhoneContacts that will contain SelectPhoneContact info
    ArrayList<SelectPhoneContact> selectPhoneContacts;

    String CountryCode;

    String phoneNoofUserCheck;
    public String phoneNumberofContact;
    String phoneNameofContact;

    //For the recycler view, containing the phone contacts
    RecyclerView recyclerView;
    PopulistoContactsAdapter adapter;

    String[] fruits = {"Apple", "Apron", "Banana","Butthead","Bath", "Cherry", "Date", "Grape", "Kiwi", "Mango", "Pear"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        //Creating the instance of ArrayAdapter containing list of fruit names
        ArrayAdapter<String> auto_adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, fruits);

        //Getting the instance of AutoCompleteTextView
        AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.textViewCategory);

        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(auto_adapter);//setting the adapter data into the AutoCompleteTextView

        PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, NewContact.this,1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Show the back button (???)
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        //actionbar.setDisplayShowHomeEnabled(true);

        //show the App title
        actionbar.setTitle("Populisto");


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

        //have the phoneContacts button selected by default
        //make it blue
        phoneContacts.setBackgroundResource(R.drawable.phonecontacts_buttonshapepressed);

        recyclerView = (RecyclerView) findViewById(R.id.rv);

        //save it by default to the DB as 1, for phoneContacts
        public_or_private = 1;

    }

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

//******for the phone contacts in the recyclerView

    // Load data in background
    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            //for every value in the allPhonesofContacts array list, call it phoneNumberofContact
            for (int i = 0; i < PopulistoContactsAdapter.allPhonesofContacts.size(); i++) {

                phoneNumberofContact = PopulistoContactsAdapter.allPhonesofContacts.get(i);
                phoneNameofContact = PopulistoContactsAdapter.allNamesofContacts.get(i);

                System.out.println("SelectPhoneContactAdapter: phoneNumberofContact : " + phoneNumberofContact);
                System.out.println("SelectPhoneContactAdapter: phoneNameofContact : " + phoneNameofContact);

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
                selectContact.setPhone(phoneNumberofContact);

            }

            return null;

        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, NewContact.this, 1);

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager((new LinearLayoutManager(NewContact.this)));


            //*********set the Matching Contacts to checked, by default ************
            //loop through the matching contacts
            int count = PopulistoContactsAdapter.MatchingContactsAsArrayList.size();

            for (int i = 0; i < count; i++) {

                //check all matching contacts, we want it to be 'Phone Contacts' by default
                PopulistoContactsAdapter.theContactsList.get(i).setSelected(true);

                //we need to notify the recyclerview that changes may have been made
                adapter.notifyDataSetChanged();
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

//              keep the slightly rounded shape, when the button is pressed, and change colour
                publicContacts.setBackgroundResource(R.drawable.publiccontacts_buttonshapepressed);

//               keep the slightly rounded shape of the others, but still grey
                phoneContacts.setBackgroundResource(R.drawable.buttonshape);
                justMeContacts.setBackgroundResource(R.drawable.buttonshape);

                //set sharing to Public.
                // This will be uploaded to server to review table,
                //public_or_private column, if saved in this state
                public_or_private = 2;

                PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, NewContact.this,1);

                recyclerView.setAdapter(adapter);
                // recyclerView.setLayoutManager((new LinearLayoutManager(NewContact.this)));

                //loop through the matching contacts
                int count = PopulistoContactsAdapter.MatchingContactsAsArrayList.size();

                //i is the number of matching contacts that there are
                for (int i = 0; i < count; i++) {

                    //for all contacts, only those that are matching will be checked
                    PopulistoContactsAdapter.theContactsList.get(i).setSelected(true);

                    //we need to notify the recyclerview that changes may have been made
                    adapter.notifyDataSetChanged();
                }
            }

        });

    }


    //for the phone Contacts button
    private void phoneContactsButton() {

        phoneContacts.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //keep the slightly rounded shape, when the button is pressed, and change colour
                phoneContacts.setBackgroundResource(R.drawable.phonecontacts_buttonshapepressed);


//               keep the slightly rounded shape of the others, but still grey
                publicContacts.setBackgroundResource(R.drawable.buttonshape);
                justMeContacts.setBackgroundResource(R.drawable.buttonshape);

                //set sharing to Phone Contacts
                // This will be uploaded to server to review table,
                //public_or_private column, if saved in this state
                public_or_private = 1;

                PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, NewContact.this,1);

                recyclerView.setAdapter(adapter);
                // recyclerView.setLayoutManager((new LinearLayoutManager(NewContact.this)));

                //loop through the matching contacts
                int count = PopulistoContactsAdapter.MatchingContactsAsArrayList.size();

                for (int i = 0; i < count; i++) {

                    //check all matching contacts, we want it to be 'Phone Contacts'
                    PopulistoContactsAdapter.theContactsList.get(i).setSelected(true);

                    //we need to notify the recyclerview that changes may have been made
                    adapter.notifyDataSetChanged();
                }
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

//               keep the slightly rounded shape of the others, but still grey
                publicContacts.setBackgroundResource(R.drawable.buttonshape);
                phoneContacts.setBackgroundResource(R.drawable.buttonshape);

                //set sharing to Just Me
                // This will be uploaded to server to review table,
                //public_or_private column, if saved in this state
                public_or_private = 0;

                PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, NewContact.this,1);

                recyclerView.setAdapter(adapter);
                // recyclerView.setLayoutManager((new LinearLayoutManager(NewContact.this)));

                //loop through the matching contacts
                int count = PopulistoContactsAdapter.MatchingContactsAsArrayList.size();

                for (int i = 0; i < count; i++) {

                    //uncheck all matching contacts, we want it to be 'Just Me'
                    PopulistoContactsAdapter.theContactsList.get(i).setSelected(false);

                    //we need to notify the recyclerview that changes may have been made
                    adapter.notifyDataSetChanged();
                }
            }

        });

    }


    //for the SAVE button
    private void saveContactButton() {

        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //close the populistolistview class
                //(we'll be opening it again, will close now so it will be refreshed)
                PopulistoListView.fa.finish();

                System.out.println("you clicked it, save");

                try {
                    System.out.println("we're in the try part");

                        //loop through the matching contacts
                        int count = PopulistoContactsAdapter.MatchingContactsAsArrayList.size();

                        for (int i = 0; i < count; i++) {

                            //for  contacts that are checked (they can only be matching contacts)...
                            if (PopulistoContactsAdapter.theContactsList.get(i).getSelected()) {
                                //Toast.makeText(NewContact.this, PopulistoContactsAdapter.theContactsList.get(i).getPhone() + " clicked!", Toast.LENGTH_SHORT).show();

                                // make each checked contact in selectPhoneContacts
                                // into an individual
                                // JSON object called checkedContact
                                JSONObject checkedContact = new JSONObject();

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

                        System.out.println("checkedContacts JSON Array " + checkedContacts);


                } catch (Exception e) {
                    System.out.println("there's a problem here unfortunately");
                    e.printStackTrace();
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
                                Toast.makeText(NewContact.this, response, Toast.LENGTH_LONG).show();
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

        });


    }

    //this is called from PopulistoContactsAdapter
    //change the colour of the phoneContacts button
    public void changeColourOfPhoneContacts() {
        //keep the slightly rounded shape, when the button is pressed
        phoneContacts.setBackgroundResource(R.drawable.phonecontacts_buttonshapepressed);

        //keep the slightly rounded shape of the others, but still grey
        publicContacts.setBackgroundResource(R.drawable.buttonshape);
        justMeContacts.setBackgroundResource(R.drawable.buttonshape);

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


}