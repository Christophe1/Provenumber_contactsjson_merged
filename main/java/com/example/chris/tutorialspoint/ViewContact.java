package com.example.chris.tutorialspoint;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tutorialspoint.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewContact extends AppCompatActivity implements android.widget.CompoundButton.OnCheckedChangeListener  {
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
    private TextView categoryname;
    private TextView namename;
    private TextView phonename;
    private TextView addressname;
    private TextView commentname;

    //for categoryid we only need the value, don't need to cast it to anything
    String categoryid;

    //this is the review that has been clicked in the ListView in PopulistoListView.java
    String review_id;
    private ProgressDialog pDialog;

    //selectPhoneContacts is an empty array list that will hold our SelectPhoneContact info
    ArrayList<SelectPhoneContact> selectPhoneContacts;
    //an arraylist of all contacts phone numbers, which we will get from VerifyUserPhoneNumber
    ArrayList <String> allPhonesofContacts;
    //an arraylist of all contacts names, which we will get from VerifyUserPhoneNumber
    ArrayList <String> allNamesofContacts;
    String MatchingContactsAsString;
    ArrayList<String> MatchingContactsAsArrayList;
    ArrayList<String> checkedContactsAsArrayList;
    String phoneNumberofContact;
    String phoneNameofContact;
    ListView listView;
    SelectPhoneContactAdapter adapter;
    CheckBox checkBoxforContact;
    String checkedContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact);

        //********************
        //selectPhoneContacts is an empty array list that will hold our SelectPhoneContact info
        selectPhoneContacts = new ArrayList<SelectPhoneContact>();

        System.out.println("ViewContact: selectPhoneContacts " + selectPhoneContacts);

        listView = (ListView) findViewById(R.id.listviewPhoneContacts);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        //********************

        Intent i = this.getIntent();
        //we want review_id of the review clicked in the ListView,
        // get it from PopulistoListView activity
        //this is the review_id we will be posting to php with Volley, below
        review_id = i.getStringExtra("review_id");
        System.out.println("ViewContact: review id is " + review_id);

        //Toast.makeText(ViewContact.this, review_id, Toast.LENGTH_SHORT).show();






        //cast a TextView for each of the field ids in activity_view_contact.xml
         categoryname = (TextView) findViewById(R.id.textViewCategory);
         namename = (TextView) findViewById(R.id.textViewName);
         phonename = (TextView) findViewById(R.id.textViewPhone);
         addressname = (TextView) findViewById(R.id.textViewAddress);
         commentname = (TextView) findViewById(R.id.textViewComment);

        // textphonenumber.setText(phoneNoofUser);

        //for the checkbox
        checkBoxforContact = (CheckBox) findViewById(R.id.checkBoxContact);



            //post the review_id that has been clicked in the ListView and send it to
        // viewContact.php and from that get other review details, like name, address etc..
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ViewContact_URL,
                new Response.Listener<String>() {

            @Override
                    public void onResponse(String response) {

                        //toast the response of ViewContact.php, which has been converted to a
                        //JSON object by the Php file with JSON encode
                        Toast.makeText(ViewContact.this, "OnResponse is" + response, Toast.LENGTH_LONG).show();
                        System.out.println("ViewContact: And the response is " + response);


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

                            //checkedContacts is a String
                            checkedContacts = responseObject.getString("checkedcontacts");

                            //assign a textview to each of the fields in the review
                            categoryname.setText(category);
                            namename.setText(name);
                            phonename.setText(phone);
                            addressname.setText(address);
                            commentname.setText(comment);

                            //we don't need to assign category id text to a textbox
                            categoryid = category_id;

                    System.out.println("here are the checkedcontacts" + checkedContacts);
                  //  Toast.makeText(ViewContact.this, "here are the checkedcontacts" + checkedContacts, Toast.LENGTH_SHORT).show();


                    //convert the checkedContacts string to an arraylist
                    //then we will pass this on to the SelectPhoneContactAdapter and put a tick
                    //in the checkbox of the checkedContacts
                    //First, take out the double quotes,
                    String replace = checkedContacts.replace("\"","");
                    //take out the starting [
                    String replace1 = replace.replace("[","");
                    //and then the ending ]
                    String replace2 = replace1.replace("]","");
                    System.out.println("here is replace2 "+ replace2);
                    //convert the checkedContacts string to an arraylist
                    checkedContactsAsArrayList = new ArrayList<String>(Arrays.asList(replace2.split(",")));
                    System.out.println("ViewContact1: checkedContactsAsArrayList is " + checkedContactsAsArrayList);


                  //  getPrefs();

                    //we want to bring the checkedContactsAsArrayList array list to our SelectPhoneContactAdapter.
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

                    System.out.println("ViewContact2: checkedContactsAsArrayList is " + checkedContactsAsArrayList);

                            //System.out.println("heree it is" + jsonResponse);
                            //Toast.makeText(ContactView.this, jsonResponse, Toast.LENGTH_LONG).show();
                            hidePDialog();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }



                    }

                        // System.out.println("size of reviewlist " + reviewList.size());
                        //System.out.println("heree it is" + reviewList.toString());


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ViewContact.this, error.toString(), Toast.LENGTH_LONG).show();

                    }

                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //we are posting review_id into our ViewContact.php file,
                //to get matching details
                params.put("review_id", review_id);
                return params;

            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


        //*****************************************
        //for the edit button
        edit = (Button) findViewById(R.id.edit);


        edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                System.out.println("you clicked it, edit");
            //open the Edit Activity, pass over the review_id so we can get that reviews
                //associated fields
                Intent i = new Intent(ViewContact.this, EditContact.class);
                i.putExtra("review_id",  review_id);
                //"category" is the key, categoryname.getText() is the
                // content to pass. etc....
                i.putExtra("category",  categoryname.getText());
                i.putExtra("category_id",  categoryid);
                i.putExtra("name", namename.getText());
                i.putExtra("phone",  phonename.getText());
                i.putExtra("address",  addressname.getText());
                i.putExtra("comment",  commentname.getText());
                startActivity(i);

            }
        });


        //update the class with new values from EditView
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
        delete = (Button) findViewById(R.id.delete);

        delete.setOnClickListener(new View.OnClickListener() {
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
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked

                    Toast.makeText(ViewContact.this, "delete stuff", Toast.LENGTH_SHORT).show();

                    pDialog = new ProgressDialog(ViewContact.this);
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

                    //when deleted, go back to the PopulistoListView class and update

                    Intent j = new Intent(ViewContact.this,PopulistoListView.class);

                    startActivity(j);

//                finish();


                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked, just close the dialogue
                    break;
            }
        }
    };


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        //checkBoxforContact.setChecked(true);


    }




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
            //with this we will put all phone numbers of contacts on user's phone into our ListView in ViewContact activity
            SharedPreferences sharedPreferencesallPhonesofContacts = PreferenceManager.getDefaultSharedPreferences(getApplication());
            Gson gson = new Gson();
            String json = sharedPreferencesallPhonesofContacts.getString("allPhonesofContacts", "");
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            allPhonesofContacts = gson.fromJson(json, type);
            System.out.println("ViewContact: allPhonesofContacts :" + allPhonesofContacts);

            //we are fetching the array list allNamesofContacts, created in VerifyUserPhoneNumber.
            //with this we will put all phone names of contacts on user's phone into our ListView in ViewContact activity
            SharedPreferences sharedPreferencesallNamesofContacts = PreferenceManager.getDefaultSharedPreferences(getApplication());
            Gson gsonNames = new Gson();
            String jsonNames = sharedPreferencesallNamesofContacts.getString("allNamesofContacts", "");
            Type typeNames = new TypeToken<ArrayList<String>>() {
            }.getType();
            allNamesofContacts = gsonNames.fromJson(jsonNames, typeNames);
            System.out.println("ViewContact: allNamesofContacts :" + allNamesofContacts);

            System.out.println("ViewContact:the amount in allPhonesofContacts :" + allPhonesofContacts.size());
            System.out.println("ViewContact:the amount in allNamesofContacts :" + allNamesofContacts.size());


            //we are fetching the array list MatchingContactsAsArrayList, created in VerifyUserPhoneNumber.
            //With that we'll put our
            //matching contacts at the top of the listview, display check boxes beside them etc...
            SharedPreferences sharedPreferencesMatchingContactsAsArrayList = PreferenceManager.getDefaultSharedPreferences(getApplication());
            Gson gsonMatchingContactsAsArrayList = new Gson();
            String jsonMatchingContactsAsArrayList = sharedPreferencesMatchingContactsAsArrayList.getString("MatchingContactsAsArrayList", "");
            Type type1 = new TypeToken<ArrayList<String>>() {
            }.getType();
            MatchingContactsAsArrayList = gsonMatchingContactsAsArrayList.fromJson(jsonMatchingContactsAsArrayList, type1);
            System.out.println("ViewContact MatchingContactsAsArrayList :" + MatchingContactsAsArrayList);



            //for every value in the allPhonesofContacts array list, call it phoneNumberofContact
            for (int i = 0; i < allPhonesofContacts.size(); i++) {

                phoneNumberofContact = allPhonesofContacts.get(i);
                phoneNameofContact = allNamesofContacts.get(i);

                System.out.println("ViewContact: phoneNumberofContact : " + phoneNumberofContact);
                System.out.println("ViewContact: phoneNameofContact : " + phoneNameofContact);

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

            adapter = new SelectPhoneContactAdapter(selectPhoneContacts, ViewContact.this,0);



            listView.setAdapter(adapter);

            //we need to notify the listview that changes may have been made on
            //the background thread, doInBackground, like adding or deleting contacts,
            //and these changes need to be reflected visibly in the listview. It works
            //in conjunction with selectContacts.clear()
            adapter.notifyDataSetChanged();



            //********************

            //this function measures the height of the listview, with all the contacts, and loads it to be that
            //size. We need to do this because there's a problem with a listview in a scrollview.
            //The function is in GlobalFunctions
            GlobalFunctions.justifyListViewHeightBasedOnChildren(ViewContact.this,listView);

        }
    }


    @Override
    protected void onResume() {

        super.onResume();

       // getPrefs();

        ViewContact.LoadContact loadContact = new ViewContact.LoadContact();


        loadContact.execute();

         Toast.makeText(ViewContact.this, "resuming!", Toast.LENGTH_SHORT).show();


    }




    public void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    private void getPrefs() {





    }



}
