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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.tutorialspoint.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NewContact extends AppCompatActivity {

    // this is the php file name where to save to.
    // we will post the category, name, phone, address, comment etc into Php and
    // create a new review_id
    private static final String NewContact_URL = "http://www.populisto.com/NewContact.php";

    private ProgressDialog pDialog;

    //testing purposes, trying to get listview working
   // String[] names;

    //this is the review of the current activity,
   // String review_id;

    Button save;

    private EditText categoryname;
    private EditText namename;
    private EditText phonename;
    private EditText addressname;
    private EditText commentname;

    //*******************

   // Details below are for the phone contacts listview

    // ArrayList called selectContacts that will contain SelectContact info
    ArrayList<SelectContact> selectContacts;
    ListView listView;
    SelectContactAdapter adapter;
    String name;
    String phoneNumber;
    String lookupkey;
    //CharSequence nameofcontact;
    Cursor cursor;

    //*******************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        //*************************
        // Details below are for the phone contacts listview

        //selectContacts is an empty array list that will hold our SelectContact info
        selectContacts = new ArrayList<SelectContact>();

        listView = (ListView) findViewById(R.id.listviewPhoneContacts);

        //*************************


        //first of all we want to get the phone number of the current user so we
        //can post it and then get the user_id in php
        //get the phone number, stored in an XML file, when the user first registered the app
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        final String phoneNoofUserCheck = sharedPreferences.getString("phonenumberofuser", "");

        //cast an EditText for each of the field ids in activity_edit_contactact.xml
        //can be edited and changed by the user
        categoryname = (EditText) findViewById(R.id.textViewCategory);
        namename = (EditText) findViewById(R.id.textViewName);
        phonename = (EditText) findViewById(R.id.textViewPhone);
        addressname = (EditText) findViewById(R.id.textViewAddress);
        commentname = (EditText) findViewById(R.id.textViewComment);


        //for the save button ******************************
        save = (Button) findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                System.out.println("you clicked it, save");

                pDialog = new ProgressDialog(NewContact.this);
                // Showing progress dialog for the review being saved
                pDialog.setMessage("Saving...");
                pDialog.show();

                //post phoneNoofUserCheck to NewContact.php and from that
                //get the user_id in the user table, then post  category, name, phone etc...
                //to the review table
                StringRequest stringRequest = new StringRequest(Request.Method.POST, NewContact_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //hide the dialogue box when page is saved
                                pDialog.dismiss();
                                //response, for testing purposes, is "$last_id"
                                Toast.makeText(NewContact.this, response, Toast.LENGTH_LONG).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }

                        }) {

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
                Intent j = new Intent(NewContact.this,PopulistoListView.class);
                j.putExtra("phonenumberofuser",phoneNoofUserCheck);
               // j.putExtra("category", categoryname.getText().toString());
               // j.putExtra("name", namename.getText().toString());
               // j.putExtra("phone", phonename.getText().toString());
              //  j.putExtra("address", addressname.getText().toString());
               // j.putExtra("comment", commentname.getText().toString());

               // Toast.makeText(NewContact.this, categoryname.getText().toString(), Toast.LENGTH_LONG).show();
                //startActivity(j);
                NewContact.this.startActivity(j);

                finish();

            }



        });

    }









}