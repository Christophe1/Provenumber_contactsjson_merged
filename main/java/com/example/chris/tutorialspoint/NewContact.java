package com.example.chris.tutorialspoint;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.tutorialspoint.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class NewContact extends AppCompatActivity  {
    // this is the php file name where to save to.
    // we will post the category, name, phone, address, comment etc into Php and
    // create a new review_id
    private static final String NewContact_URL = "http://www.populisto.com/NewContact.php";

    //in this JSONArray, checkedContacts, we will be storing each checkedContact JSON Object
    //Then we're going to post it to our NewContact.php file
    JSONArray checkedContacts = new JSONArray();

    private ProgressDialog pDialog;

    Button save;
    Button cancel;

    //rbu1 is the radio button for Phone Contacts
    RadioButton rbu1;


    //thse are the fields in the xml
    private EditText categoryname;
    private EditText namename;
    private EditText phonename;
    private EditText addressname;
    private EditText commentname;

    //depends on radio button selected
    int public_or_private;

    //these are strings for cat name, name etc..
    //we will be putting these values into our sqlLite db
    String cat_name, name;
    //*******************

    // Details in this comment are for the PHONE CONTACTS LISTVIEW, so
    //user can add,edit, delete contacts who can see a review

    // ArrayList called selectPhoneContacts that will contain SelectPhoneContact info
    ArrayList<SelectPhoneContact> selectPhoneContacts;
    ListView listView;
    SelectPhoneContactAdapter adapter;
    //  Cursor cursor;
    String CountryCode;
    String [] phoneNumberofContactStringArray;
    String [] phoneNameofContactStringArray;
    String JsonArrayMatchingContacts;
    String MatchingContactsAsString;
    ArrayList <String> allPhonesofContacts;
    ArrayList <String> allNamesofContacts;
    ArrayList<String> MatchingContactsAsArrayList;
    String phoneNoofUserCheck;
    String phoneNumberofContact;
    String phoneNameofContact;
   // CheckBox checkBox2;

    //for SQLiteDatabaseOperations
    Context ctx = this;
    //*******************************
   // ArrayList<SelectPhoneContact> possiblecheckedContacts = selectPhoneContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        //hide keyboard on activity start up
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //selectPhoneContacts is an empty array list that will hold our SelectPhoneContact info
        selectPhoneContacts = new ArrayList<SelectPhoneContact>();

        System.out.println("NewContact1: selectPhoneContacts " + selectPhoneContacts);


        listView = (ListView) findViewById(R.id.listviewPhoneContacts);

        //  showPDialog();


        //*************************


        //first of all we want to get the phone number of the current user so we
        //can post it and then get the user_id in php
        //get the phone number, stored in an XML file, when the user first registered the app
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        phoneNoofUserCheck = sharedPreferences.getString("phonenumberofuser", "");
        System.out.println("phone number of user is " + phoneNoofUserCheck);

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


        rbu1 = (RadioButton) findViewById(R.id.PhoneContacts);

        //cancel button, testing save
        cancel = (Button) findViewById(R.id.cancel);


        //for the save button ******************************
        save = (Button) findViewById(R.id.save);

        final Button btnCheckAll = (Button) findViewById(R.id.btnCheckAll);

        final Button btnClearAll = (Button) findViewById(R.id.btnClearAll);


        saveContactButton();
       // addListenerToCheckBox();
        //scroll is the same speed, be it fast scroll or not
        // listView.setFastScrollEnabled(true);

        //If Public radio button is selected then check all the boxes
        //and change the button text to 'Clear All'
        //listen for which radio button is clicked
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.SharedWith);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int SelectWho) {
                // find which radio button is selected
                if (SelectWho == R.id.Public) {
                    Toast.makeText(NewContact.this, "Public", Toast.LENGTH_LONG).show();
                    //call the function to check all checkboxes in NewContact
                    //loop through the Matching Contacts
                    int count = MatchingContactsAsArrayList.size();
                    for (int i = 0; i < count; i++) {
                        LinearLayout itemLayout = (LinearLayout) listView.getChildAt(i); // Find by under LinearLayout
                        CheckBox checkbox = (CheckBox) itemLayout.findViewById(R.id.checkBoxContact);
                        checkbox.setChecked(true);
                        btnCheckAll.setText("Clear All");
                    }

                }
            }
        });

        //Select All / Clear All Button
        //Check all or clear all checkboxes
        btnCheckAll.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                boolean toCheck = true;

                //THE CONDITION SHOULD BE OUTSIDE THE LOOP!

                if (btnCheckAll.getText().toString().equalsIgnoreCase("Select All")) {
                    toCheck = true;
                    btnCheckAll.setText("Clear All");
                } else if (btnCheckAll.getText().toString().equalsIgnoreCase("Clear All")) {
                    toCheck = false;
                    rbu1.setChecked(true);
                    btnCheckAll.setText("Select All");
                }

                int count = MatchingContactsAsArrayList.size();
                for (int i = 0; i < count; i++) {
                    LinearLayout itemLayout = (LinearLayout) listView.getChildAt(i); // Find by under LinearLayout
                    CheckBox checkbox = (CheckBox) itemLayout.findViewById(R.id.checkBoxContact);
                    checkbox.setChecked(toCheck);
                }
            }
        });


        //Clear All
        //Button btnClearAll = (Button) findViewById(R.id.btnClearAll);
        // btnClearAll.setOnClickListener(new View.OnClickListener() {
        //     public void onClick(View v) {
        //loop through the Matching Contacts
     /*           int count = MatchingContactsAsArrayList.size();
                for (int i = 0; i < count; i++) {
                    LinearLayout itemLayout = (LinearLayout) listView.getChildAt(i); // Find by under LinearLayout
                    CheckBox checkbox = (CheckBox) itemLayout.findViewById(R.id.checkBoxContact);
                    checkbox.setChecked(false);

                }*/

/*
                RadioGroup rg1 =(RadioGroup)findViewById(R.id.SharedWith);
                if (rg1.getCheckedRadioButtonId()==R.id.Public){
                    System.out.println("It is public");
                    public_or_private =1;
                }
                if (rg1.getCheckedRadioButtonId()==R.id.PhoneContacts){
                    System.out.println("It is private");
                    public_or_private =0;
                }

            }});*/
        //for the checkbox
  /*      checkBoxforContact = (CheckBox) findViewById(R.id.checkBoxContact);


       // Button btnGetItem = (Button) findViewById(R.id.btnGetItem);
        checkBoxforContact.setOnItemClickListener(
                new View.OnClickListener() {
            public void onClick(View v) {

                int count = MatchingContactsAsArrayList.size();
                for (int i = 0; i < count; i++) {
                    LinearLayout itemLayout = (LinearLayout) listView.getChildAt(i); // Find by under LinearLayout
                    CheckBox checkbox = (CheckBox) itemLayout.findViewById(R.id.checkBoxContact);*/


                    // public void addListenerToCheckBox() {


                    //checkBoxforContact.setOnClickListener(
                    //   new View.OnClickListener() {

                    //@Override
                    // public void onClick(View v) {
                    //is chkIos checked?
       /*             if (((checkbox) v).isChecked()) {
                        //Case 1
                        Toast.makeText(NewContact.this, "checked", Toast.LENGTH_LONG).show();
                    } else
                        //case 2
                        Toast.makeText(NewContact.this, "not checked", Toast.LENGTH_LONG).show();


                        }}
                    });*/







        //Get item
        Button btnGetItem = (Button) findViewById(R.id.btnGetItem);
        btnGetItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //loop through the Matching Contacts
                int count = MatchingContactsAsArrayList.size();

                for (int i = 0; i < count; i++) {
                    //for each Matching Contacts row in the listview
                    LinearLayout itemLayout = (LinearLayout) listView.getChildAt(i); // Find by under LinearLayout
                    //for each Matching Contacts checkbox in the listview
                    CheckBox checkbox = (CheckBox) itemLayout.findViewById(R.id.checkBoxContact);
                    //get the other data related to the selected contact - name and number
                    SelectPhoneContact data = (SelectPhoneContact) checkbox.getTag();

                    //checkbox.setChecked(true);
                    //if that checkbox is checked, then get the phone number
                    if (checkbox.isChecked()) {
                        Log.d("Item " + String.valueOf(i), checkbox.getTag().toString());
                        Toast.makeText(NewContact.this, data.getPhone(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


/*        CheckBox checktheBox = (CheckBox) findViewById(R.id.checkBoxContact);

        checktheBox.setOnClickListener(new View.OnClickListener()  {

            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((checktheBox) v).isChecked()) {
                    //Case 1
                    Toast.makeText(NewContact.this, "checked", Toast.LENGTH_LONG).show();
                }
                else
                //case 2
                    Toast.makeText(NewContact.this, "not checked", Toast.LENGTH_LONG).show();


            }
        });*/


/*        checkBoxforContact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Toast.makeText(getApplicationContext(), isChecked ? "ON" : "OFF",
                        Toast.LENGTH_SHORT).show();
            }


        });*/



    }

/*    public void addListenerToCheckBox() {
        //for the checkbox

            LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.phone_inflate_listview, null);
            final CheckBox checkBox2 = (CheckBox)view.findViewById(R.id.checkBoxContact);

        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {  @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    // perform logic
                    Toast.makeText(NewContact.this, "isChecked - " + checkBox2.isChecked(), Toast.LENGTH_SHORT).show();
                }

            }
        });}*/


  /*          checkBox2.setOnClickListener(


                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Toast.makeText(NewContact.this, "checked", Toast.LENGTH_LONG).show();

                    }});}*/

                        /*
                        CheckBox cb = (CheckBox) v ;
                        SelectPhoneContact data = (SelectPhoneContact) cb.getTag();

                        //loop through the Matching Contacts
                          int count = MatchingContactsAsArrayList.size();

                        for (int i = 0; i < count; i++) {
                        //for each Matching Contacts row in the listview
                            LinearLayout itemLayout = (LinearLayout) listView.getChildAt(i); // Find by under LinearLayout
                        //for each Matching Contacts checkbox in the listview
                            CheckBox checkbox = (CheckBox) itemLayout.findViewById(R.id.checkBoxContact);
                        //get the other data related to the selected contact - name and number
                          // SelectPhoneContact data = (SelectPhoneContact) checkbox.getTag();

                        Toast.makeText(NewContact.this,
                                "Clicked on Checkbox: " + data.getPhone() +
                                        " is " + cb.isChecked(),
                                Toast.LENGTH_LONG).show();
                        data.setSelected(cb.isChecked());

                        //is chkIos checked?
                        //  if (((data) v).isChecked()) {
                        //Case 1
                        //     Toast.makeText(NewContact.this, "checked", Toast.LENGTH_LONG).show();
                        //  } else
                        //case 2
                        //      Toast.makeText(NewContact.this, "not checked", Toast.LENGTH_LONG).show();

                    }
                    // }
                }

    });}*/

//******for the phone contacts in the listview

    // Load data in background
    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {

            pDialog = new ProgressDialog(NewContact.this);
            // Showing progress dialog before making http request
            pDialog.setMessage("Loading...");
            pDialog.show();

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {



            //we want to delete the old selectContacts from the listview when the Activity loads
            //because it may need to be updated and we want the user to see the updated listview,
            //like if the user adds new names and numbers to their phone contacts.
            selectPhoneContacts.clear();

            //we are fetching the array list allPhonesofContacts, created in VerifyUserPhoneNumber.
            //with this we will put all phone numbers of contacts on user's phone into our ListView in NewContact activity
            SharedPreferences sharedPreferencesallPhonesofContacts = PreferenceManager.getDefaultSharedPreferences(getApplication());
            Gson gson = new Gson();
            String json = sharedPreferencesallPhonesofContacts.getString("allPhonesofContacts", "");
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            allPhonesofContacts = gson.fromJson(json, type);
            System.out.println("NewContact: allPhonesofContacts :" + allPhonesofContacts);

            //we are fetching the array list allNamesofContacts, created in VerifyUserPhoneNumber.
            //with this we will put all phone names of contacts on user's phone into our ListView in NewContact activity
            SharedPreferences sharedPreferencesallNamesofContacts = PreferenceManager.getDefaultSharedPreferences(getApplication());
            Gson gsonNames = new Gson();
            String jsonNames = sharedPreferencesallNamesofContacts.getString("allNamesofContacts", "");
            Type typeNames = new TypeToken<ArrayList<String>>() {
            }.getType();
            allNamesofContacts = gson.fromJson(jsonNames, type);
            System.out.println("NewContact: allNamesofContacts :" + allNamesofContacts);

            System.out.println("NewContact:the amount in allPhonesofContacts :" + allPhonesofContacts.size());
            System.out.println("NewContact:the amount in allNamesofContacts :" + allNamesofContacts.size());

            //we are fetching the array list MatchingContactsAsArrayList, created in VerifyUserPhoneNumber.
            //With that we'll put our
            //matching contacts at the top of the listview, display check boxes beside them etc...
            SharedPreferences sharedPreferencesMatchingContactsAsArrayList = PreferenceManager.getDefaultSharedPreferences(getApplication());
            Gson gsonMatchingContactsAsArrayList = new Gson();
            String jsonMatchingContactsAsArrayList = sharedPreferencesMatchingContactsAsArrayList.getString("MatchingContactsAsArrayList", "");
            Type type1 = new TypeToken<ArrayList<String>>() {
            }.getType();
            MatchingContactsAsArrayList = gsonMatchingContactsAsArrayList.fromJson(jsonMatchingContactsAsArrayList, type1);
            System.out.println("SelectPhoneContactAdapter MatchingContactsAsArrayList :" + MatchingContactsAsArrayList);


            //for every value in the allPhonesofContacts array list, call it phoneNumberofContact
            for (int i = 0; i < allPhonesofContacts.size(); i++) {

                phoneNumberofContact = allPhonesofContacts.get(i);
                phoneNameofContact = allNamesofContacts.get(i);

                System.out.println("SelectPhoneContactAdapter: phoneNumberofContact : " + phoneNumberofContact);
                System.out.println("SelectPhoneContactAdapter: phoneNameofContact : " + phoneNameofContact);

                SelectPhoneContact selectContact = new SelectPhoneContact();

                //if a phone number is in our array of matching contacts
                if (MatchingContactsAsArrayList.contains(phoneNumberofContact))

                {   //add the selectContacts to the selectPhoneContacts array
                    // insert the contact at the beginning of the listview
                    selectPhoneContacts.add(0, selectContact);
                    // checkBoxforContact.setVisibility(View.VISIBLE);

                }

                else {
                    // insert it at the end (default)
                    selectPhoneContacts.add(selectContact);
                    //makeinvisible();
                }


                selectContact.setName(phoneNameofContact);
                //    selectContact.setPhone(phoneNumberofContact);
                selectContact.setPhone(phoneNumberofContact);
                //selectContact.setSelected(is);


            }




            return null;


        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);






            adapter = new SelectPhoneContactAdapter(selectPhoneContacts, NewContact.this,1);

            listView.setAdapter(adapter);

            adapter.radioButtontoPhoneContacts = new changeRadioButtontoPhoneContacts() {
                //@Override
                public void update() {
                    NewContact.this.rbu1.setChecked(true);
                }
            };

            //we need to notify the listview that changes may have been made on
            //the background thread, doInBackground, like adding or deleting contacts,
            //and these changes need to be reflected visibly in the listview. It works
            //in conjunction with selectContacts.clear()
            adapter.notifyDataSetChanged();


            //********************

            //this function measures the height of the listview, with all the contacts, and loads it to be that
            //size. We need to do this because there's a problem with a listview in a scrollview.
            //The function is in GlobalFunctions
            GlobalFunctions.justifyListViewHeightBasedOnChildren(NewContact.this,listView);
            //call the hidePDialog function, hide the loading dialog
            hidePDialog();

        }
    }


    @Override
    protected void onResume() {

        super.onResume();

        LoadContact loadContact = new LoadContact();
        loadContact.execute();
    }



//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (cursor != null) {
//            cursor.close();
//        }
//
//    }

/*    public void makeinvisible(){
        checkBoxforContact.setVisibility(View.INVISIBLE);
    }*/

//    public void showPDialog() {
//        Thread mThread;
//
//        mThread = new Thread() {
//            @Override
//            public void run() {
//              //  try {
//               //     synchronized (this) {
//                        pDialog = new ProgressDialog(NewContact.this);
//                        // Showing progress dialog before making http request
//                        pDialog.setMessage("Loading...");
//                        pDialog.show();
//                    }
//            //    }
//
//            //    catch (Exception ex) {
//           //     }
//
//            };}


    public void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    private void saveContactButton() {

        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //we want to save into a SQLlite db category
                //name
                //phone
                //address
                //comment
                //and contacts with whom review is shared with
                cat_name = categoryname.getText().toString();
                name = namename.getText().toString();



                try {
                    System.out.println("we're in the try part");

                    //if the public radio button is selected
                    //then in the public_or_private column of the review
                    //table put 1. The review is for public view.
                    //otherwise, if PhoneContacts is selected,
                    //put 0. The review is for private view.
                    RadioGroup rg1 =(RadioGroup)findViewById(R.id.SharedWith);
                    if (rg1.getCheckedRadioButtonId()==R.id.Public){
                        System.out.println("It is public");
                        public_or_private =1;
                    }
                    if (rg1.getCheckedRadioButtonId()==R.id.PhoneContacts){
                        System.out.println("It is private");
                        public_or_private =0;
                    }

                    //select from matching contacts
                    //the user will be able to check contacts who to share the review
                    // with, from their matching contacts
                    int count = MatchingContactsAsArrayList.size();

                    for (int i = 0; i < count; i++) {
                        //for each Matching Contacts row in the listview
                        LinearLayout itemLayout = (LinearLayout)listView.getChildAt(i); // Find by under LinearLayout
                        //for each Matching Contacts checkbox in the listview
                        CheckBox checkbox = (CheckBox)itemLayout.findViewById(R.id.checkBoxContact);
                        //get the other data related to the selected contact - name and number
                        SelectPhoneContact contact = (SelectPhoneContact) checkbox.getTag();

                        // make each checked contact in selectPhoneContacts
                        // into an individual
                        // JSON object called checkedContact
                        JSONObject checkedContact = new JSONObject();

                        //if that checkbox is checked, then get the phone number
                        if(checkbox.isChecked()) {
                            Log.d("Item " + String.valueOf(i), checkbox.getTag().toString());
                            Toast.makeText(NewContact.this, contact.getPhone(), Toast.LENGTH_LONG).show();

                            // checkedContact will be of the form {"checkedContact":"+353123456"}
                            checkedContact.put("checkedContact", contact.getPhone());

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

                    //responseText, Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    System.out.println("there's a problem here unfortunately");
                    e.printStackTrace();
                }






                //execute the AsyncTask, do stuff in the background
                NewContact.SaveNewContact saveNewContact = new NewContact.SaveNewContact();
                saveNewContact.execute();




        }
    });

}




    private class SaveNewContact extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }



        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate();


            //when I put this in this thread I don't et any memory leak errors
            pDialog = new ProgressDialog(NewContact.this);
            // Showing progress dialog for the review being saved
            pDialog.setMessage("Saving...");
            pDialog.show();


        }



        protected Void doInBackground(Void... params)
        {

            //close the populistolistview class
            //(we'll be opening it again, will close now so it will be refreshed)
            PopulistoListView.fa.finish();

            System.out.println("you clicked it, save");

            //we want to save into a SQLlite db category
            //name
            //phone
            //address
            //comment
            //and contacts with whom review is shared with
            //cat_name = categoryname.getText().toString();
            //name = namename.getText().toString();

            //put cat and name into the table
            SQLiteDatabaseOperations sQLiteDatabaseOperations = new SQLiteDatabaseOperations(ctx);
            SQLiteDatabase db = sQLiteDatabaseOperations.getWritableDatabase();
            sQLiteDatabaseOperations.putInformation(db, cat_name, name);



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

            //finish this activity
            finish();

            return null;

        }




        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);

            //toast that the latest review details have been put into sqLite db
            Toast.makeText(NewContact.this, cat_name + " and " + name + " put into SQLLite", Toast.LENGTH_LONG).show();

        }
    }


    //need this to change radio button to Phone Contacts,
    //if a checkbox is changed to false
    public abstract class changeRadioButtontoPhoneContacts
    {
        public void update() {}
    }

    //for the backbutton, remove the saved checkbox state
    //@Override
    public void onBackPressed() {
        // your code.
        Integer i = null;
        SharedPreferences preferences = getSharedPreferences("sharedPrefsFile", 0);
        preferences.edit().clear().commit();
        finish();
    }


}