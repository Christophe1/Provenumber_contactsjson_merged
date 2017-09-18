package com.example.chris.tutorialspoint;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
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
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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

    ArrayList <String> allPhonesofContacts;
    ArrayList <String> allNamesofContacts;
    ArrayList<String> MatchingContacts;
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

        listView = (ListView) findViewById(R.id.listviewPhoneContacts);

        // checkBoxforContact.setOnClickListener(this);



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

//**************TESTING***********
        //get the names and numbers from VerifyPhoneNumber and bring them
        //over to this class
        Intent myIntent = getIntent();

        //get the Array list of all contacts' numbers on user's phone, allPhonesofContacts, from PopulistoListView
        allPhonesofContacts = getIntent().getStringArrayListExtra("allPhonesofContacts");
        System.out.println("allphonesofcontacts from NewContact" + allPhonesofContacts);

        //get the Array list of all contacts' names on user's phone, allPhonesofContacts, from PopulistoListView
        allNamesofContacts = getIntent().getStringArrayListExtra("allNamesofContacts");
        System.out.println("allnamesofcontacts from NewContact" + allNamesofContacts);


        //get the JSON Array called the MatchingContacts from PopulistoListView,
        // which in turn gets it from VerifyUserPhoneNumber. This JSON Array is the matching contacts -
        // users of the app and contacts in the user's phone book. We will put them at the top
        //of the listview with a checkbox, distinguished from the other users not yet using the app.
        JsonArrayMatchingContacts = myIntent.getStringExtra("JsonArrayMatchingContacts");
        System.out.println("all matching contacts from NewContact" + JsonArrayMatchingContacts);

        //*******************************************************************
        //for the save button ******************************
        save = (Button) findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {


            public void onClick(View view) {
                System.out.println("you clicked it, save");

                pDialog = new ProgressDialog(NewContact.this);
                // Showing progress dialog for the review being saved
                pDialog.setMessage("Saving...");
                pDialog.show();


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

/*    @Override
    public void onClick(View view) {
    CheckBox t = (CheckBox) view;
        if(t.isChecked())
            Toast.makeText(this, "checked", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "unchecked", Toast.LENGTH_LONG).show();
    }*/


//******for the phone contacts in the listview

    // Load data in background
    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {


//          we want to delete the old selectContacts from the listview when the Activity loads
//          because it may need to be updated and we want the user to see the updated listview,
//          like if the user adds new names and numbers to their phone contacts.
            selectPhoneContacts.clear();

            //Instead of running through the cursor values again, let's just import them from
            //earlier activities

            //Make a string array, phoneNumberofContactStringArray, that will be the same size
            //as the arraylist allPhonesofContacts, which has been imported from PopulistoListView
            phoneNumberofContactStringArray = new String[allPhonesofContacts.size()];

            //Make a string array, phoneNameofContactStringArray, that will be the same size
            //as the arraylist allNamesofContacts, which has been imported from PopulistoListView
            phoneNameofContactStringArray = new String[allNamesofContacts.size()];

            //phoneNumberofContactStringArray will contain all the values in allPhonesofContacts
            phoneNumberofContactStringArray = allPhonesofContacts.toArray(phoneNumberofContactStringArray);

            //phoneNameofContactStringArray will contain all the values in allNamesofContacts
            phoneNameofContactStringArray = allNamesofContacts.toArray(phoneNameofContactStringArray);

            for ( int i = 0; i < phoneNumberofContactStringArray.length; i++) {

                phoneNumberofContact = phoneNumberofContactStringArray[i];
                phoneNameofContact = phoneNameofContactStringArray[i];

                {
                    System.out.println("NewContact: the phone numbers are : " + phoneNumberofContactStringArray[i]);
                    System.out.println("NewContact: the phone names are : " + phoneNameofContactStringArray[i]);
                }

                SelectPhoneContact selectContact = new SelectPhoneContact();

                //make an arraylist so we can get the objects of JsonArrayMatchingContacts
                //which we import from PopulistoListView
                MatchingContacts = new ArrayList<String>();
                try {
                    JSONArray Object = new JSONArray(JsonArrayMatchingContacts);
                    for (int x = 0; x < Object.length(); x++) {
                        final JSONObject obj = Object.getJSONObject(x);
                        MatchingContacts.add(obj.getString("phone_number"));

                    }
                    System.out.println("MatchingContacts :" + MatchingContacts);

                    //pass MatchingContacts values along to the custom adapter,
                    //so we can make checkboxes visible for matching contacts
                    Intent intent = new Intent(NewContact.this, SelectPhoneContactAdapter.class);

                    //pass MatchingContacts ArrayList to SelectPhoneContactAdapter, so we can
                    // display checkboxes in ListView
                    intent.putExtra("MatchingContacts", MatchingContacts);


                } catch(Exception e) {
                    e.printStackTrace();
                }

                //if a phone number is in our array of matching contacts*/
                if (MatchingContacts.contains(phoneNumberofContact))

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
//                    we need to notify the listview that changes may have been made on
//                    the background thread, doInBackground, like adding or deleting contacts,
//                    and these changes need to be reflected visibly in the listview. It works
//                    in conjunction with selectContacts.clear()

            //if a phone number is in our array of matching contacts*/
//            if (MatchingContacts.contains(phoneNumberofContact))
//
//            {
//                checkBoxforContact.setVisibility(View.VISIBLE);
//            }

            adapter.notifyDataSetChanged();




            listView.setAdapter(adapter);

            //*********************
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int i, long id){
//
//                 //   checkBoxforContact = (CheckBox) findViewById(checkBoxContact);
//                  //  System.out.println("The position is :" + listView.getItemAtPosition(i).toString());
//
//
//
//                    //         SelectPhoneContact selectPhoneContact = (SelectPhoneContact) parent.getItemAtPosition(i);
//           //         Toast.makeText(NewContact.this, "clicked on row" + selectPhoneContact.getName(), Toast.LENGTH_SHORT).show();
//
//
//                }
//
//            }
            //  );

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