package com.example.chris.tutorialspoint;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.tutorialspoint.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;

import static android.R.id.list;
import static com.example.tutorialspoint.R.id.cancel;
import static com.example.tutorialspoint.R.id.checkBoxContact;
import static com.example.tutorialspoint.R.id.name;


public class NewContact extends AppCompatActivity implements android.widget.CompoundButton.OnCheckedChangeListener {
    // this is the php file name where to save to.
    // we will post the category, name, phone, address, comment etc into Php and
    // create a new review_id
    private static final String NewContact_URL = "http://www.populisto.com/NewContact.php";

    //in this JSONArray, checkedContacts, we will be storing each checkedContact JSON Object
    //Then we're going to post it to our NewContact.php file
    JSONArray checkedContacts = new JSONArray();

    private ProgressDialog pDialog;

    Button save;

    //thse are the fields in the xml
    private EditText categoryname;
    private EditText namename;
    private EditText phonename;
    private EditText addressname;
    private EditText commentname;

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
    String thestring;
    String phoneNumberofContact;
    String phoneNameofContact;
    CheckBox checkBoxforContact;
    //*******************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);


        //selectPhoneContacts is an empty array list that will hold our SelectPhoneContact info
        selectPhoneContacts = new ArrayList<SelectPhoneContact>();

        System.out.println("NewContact1: selectPhoneContacts " + selectPhoneContacts);


        listView = (ListView) findViewById(R.id.listviewPhoneContacts);

        //*************************

        // pDialog = new ProgressDialog(this);
        // Showing progress dialog as soon as activity starts loading
        // pDialog.setMessage("Loading...");
        // pDialog.show();

        //first of all we want to get the phone number of the current user so we
        //can post it and then get the user_id in php
        //get the phone number, stored in an XML file, when the user first registered the app
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        final String phoneNoofUserCheck = sharedPreferences.getString("phonenumberofuser", "");

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

        //for the checkbox
        checkBoxforContact = (CheckBox) findViewById(R.id.checkBoxContact);

        checkButtonClick();

        //for the save button ******************************
        save = (Button) findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {


            public void onClick(View view) {
                System.out.println("you clicked it, save");

                pDialog = new ProgressDialog(NewContact.this);
                // Showing progress dialog for the review being saved
                pDialog.setMessage("Saving...");
                pDialog.show();

                //see this link : http://bit.ly/2xzgNTw

                try {

                   // StringBuffer responseText = new StringBuffer();
                   // responseText.append("The following were selected...\n");

                    //for every contact in the selectPhoneContacts array list
                    for (int i = 0; i < selectPhoneContacts.size(); i++) {

                        SelectPhoneContact data = selectPhoneContacts.get(i);
                        if (data.isSelected()) {

                            // make each checked contact in selectPhoneContacts into an individual
                            // JSON object called checkedContact
                            JSONObject checkedContact = new JSONObject();
                            // checkedContact will be of the form {"checkedContact":"+353123456"}
                            checkedContact.put("checkedContact", data.getPhone());

                            // Add checkedContact to checkedContacts jsonArray
                            checkedContacts.put(checkedContact);
                            //responseText.append("\n" + data.getPhone());
                        }
                    }

                    Toast.makeText(getApplicationContext(),
                            "checkedContacts JSON Array :" + checkedContacts, Toast.LENGTH_LONG).show();

                    //responseText, Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }





                //When the user clicks save
                //post phoneNoofUserCheck to NewContact.php and from that
                //get the user_id in the user table, then post  category, name, phone etc...
                //to the review table
                StringRequest stringRequest = new StringRequest(Request.Method.POST, NewContact_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //hide the dialogue box when page is saved
                                //  pDialog.dismiss();
                                //response, for testing purposes, is "$last_id"
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
        //scroll is the same speed, be it fast scroll or not
        // listView.setFastScrollEnabled(true);


        //listen for which radio button is clicked
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.SharedWith);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int SelectWho) {
                // SelectWho is the RadioButton selected
                Toast.makeText(NewContact.this, "Select Who", Toast.LENGTH_LONG).show();


            }
        });


    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        int pos = listView.getPositionForView(buttonView);
        if(pos != listView.INVALID_POSITION) {
            SelectPhoneContact data = selectPhoneContacts.get(pos);
            data.setSelected(isChecked);

            Toast.makeText(this, "Clicked on : " + data.getName() + isChecked,
                    Toast.LENGTH_SHORT).show();
        }

    }

    //************************

    private void checkButtonClick() {


        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                try {

                    // StringBuffer responseText = new StringBuffer();
                    // responseText.append("The following were selected...\n");

                    //for every contact in the selectPhoneContacts array list
                    for (int i = 0; i < selectPhoneContacts.size(); i++) {

                        SelectPhoneContact data = selectPhoneContacts.get(i);
                        if (data.isSelected()) {

                            // make each checked contact in selectPhoneContacts into an individual
                            // JSON object called checkedContact
                            JSONObject checkedContact = new JSONObject();
                            // checkedContact will be of the form {"checkedContact":"+353123456"}
                            checkedContact.put("checkedContact", data.getPhone());

                            // Add checkedContact to checkedContacts jsonArray
                            checkedContacts.put(checkedContact);
                            //responseText.append("\n" + data.getPhone());
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(),
                        "checkedContacts JSON Array :" + checkedContacts, Toast.LENGTH_LONG).show();

                //responseText, Toast.LENGTH_LONG).show();

            }

        });
    }

    //**************************


//******for the phone contacts in the listview

    // Load data in background
    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
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

            //  when the activity loads, get the String MatchingContacts in the SharedPreferences file, created in
            // VerifyUserPhoneNumber
            // it will be of the form of a JSONArray, like [{"phone_number":"+35312345"}, {"phone_number": etc...
            // We get this string from our php file, checkcontact.php. Then we want to extract the phone numbers
            //and compare against the contacts on the user's phone.
            SharedPreferences sharedPreferencetheMatchingContacts = getApplication().getSharedPreferences("MyData", Context.MODE_PRIVATE);
            MatchingContactsAsString = sharedPreferencetheMatchingContacts.getString("thematchingcontacts", "");
            System.out.println("NewContact: matchingcontacts :" + MatchingContactsAsString);

            //make an arraylist which will hold the phone_number part of the MatchingContacts string
            MatchingContactsAsArrayList = new ArrayList<String>();
            try {
                JSONArray Object = new JSONArray(MatchingContactsAsString);
                for (int x = 0; x < Object.length(); x++) {
                    final JSONObject obj = Object.getJSONObject(x);
                    MatchingContactsAsArrayList.add(obj.getString("phone_number"));

                }
                System.out.println("NewContact: MatchingContactsAsArrayList :" + MatchingContactsAsArrayList);

                //we want to bring the MatchingContactsAsArrayList array list to our SelectPhoneContactAdapter.
                // It looks like Shared Preferences
                //only works easily with strings so best way to bring the array list in Shared Preferences is with
                //Gson.
                SharedPreferences sharedPreferencesMatchingContactsAsArrayList = PreferenceManager.getDefaultSharedPreferences(getApplication());
                SharedPreferences.Editor editorMatchingContactsAsArrayList = sharedPreferencesMatchingContactsAsArrayList.edit();
                Gson gsonMatchingContactsAsArrayList = new Gson();
                String jsonMatchingContactsAsArrayList = gson.toJson(MatchingContactsAsArrayList);
                editorMatchingContactsAsArrayList.putString("MatchingContactsAsArrayList", jsonMatchingContactsAsArrayList);
                editorMatchingContactsAsArrayList.commit();


            } catch (Exception e) {
                e.printStackTrace();
            }

            //for every value in the allPhonesofContacts array list, call it phoneNumberofContact
            for (int i = 0; i < allPhonesofContacts.size(); i++) {

                phoneNumberofContact = allPhonesofContacts.get(i);
                phoneNameofContact = allNamesofContacts.get(i);

                System.out.println("SelectPhoneContactAdapter: phoneNumberofContact : " + phoneNumberofContact);
                System.out.println("SelectPhoneContactAdapter: phoneNameofContact : " + phoneNameofContact);

                SelectPhoneContact selectContact = new SelectPhoneContact();

                //if a phone number is in our array of matching contacts
                if (MatchingContactsAsArrayList.contains(phoneNumberofContact))

                {
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

            adapter = new SelectPhoneContactAdapter(selectPhoneContacts, NewContact.this);



            listView.setAdapter(adapter);

            //we need to notify the listview that changes may have been made on
            //the background thread, doInBackground, like adding or deleting contacts,
            //and these changes need to be reflected visibly in the listview. It works
            //in conjunction with selectContacts.clear()
            adapter.notifyDataSetChanged();


            //********************

            //this function measures the height of the listview, with all the contacts, and loads it to be that
            //size. We need to do this because there's a problem with a listview in a scrollview.
            justifyListViewHeightBasedOnChildren(listView);

        }
    }


    @Override
    protected void onResume() {

        super.onResume();

        LoadContact loadContact = new LoadContact();
        loadContact.execute();
    }



    //this is the function we call to measure the height of the listview
    //we need this because there are problems with a listview within a scrollview
    public static void justifyListViewHeightBasedOnChildren (ListView listView) {

        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();

        System.out.println("the getcount is " + adapter.getCount());
        System.out.println("the height is " + par.height);
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

}