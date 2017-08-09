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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NewContact extends AppCompatActivity {

    //***TESTING
    String name_test;
    //*********

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
    String name;
    String phoneNumber;
    String lookupkey;
    CharSequence nameofcontact;
    Cursor cursor;
    String CountryCode;
    String phoneNumberofContact;
    //to remove duplicates phone numbers
    ArrayList hashMapsArrayList;

    //*******************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        //*************************
        // Details in this comment are for the phone contacts listview

        //selectPhoneContacts is an empty array list that will hold our SelectPhoneContact info
        selectPhoneContacts = new ArrayList<SelectPhoneContact>();

        listView = (ListView) findViewById(R.id.listviewPhoneContacts);

        //*************************

        pDialog = new ProgressDialog(this);
        // Showing progress dialog as soon as activity starts loading
        pDialog.setMessage("Loading...");
        pDialog.show();

        //first of all we want to get the phone number of the current user so we
        //can post it and then get the user_id in php
        //get the phone number, stored in an XML file, when the user first registered the app
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        final String phoneNoofUserCheck = sharedPreferences.getString("phonenumberofuser", "");

        //get the CountryCode, stored in an XML file, when the user first registered the app
        //We need this for putting phone contacts into E164 and comparing against the app db
        SharedPreferences sharedPreferencesCountryCode = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        CountryCode = sharedPreferencesCountryCode.getString("countrycode", "");

        //cast an EditText for each of the field ids in activity_edit_contactact.xml
        //can be edited and changed by the user
        categoryname = (EditText) findViewById(R.id.textViewCategory);
        namename = (EditText) findViewById(R.id.textViewName);
        phonename = (EditText) findViewById(R.id.textViewPhone);
        addressname = (EditText) findViewById(R.id.textViewAddress);
        commentname = (EditText) findViewById(R.id.textViewComment);

//**************TESTING***********
        //get the names and numbers from VerifyPhoneNumber and bring them
        //over to this class
        Intent i = this.getIntent();
        //we need to get review_id to ensure changes made are saved to correct review_id
        name_test = i.getStringExtra("review_id");


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


                    // get a handle on the phone number of contact, which is a string. Loop through all the phone numbers
//                  if our Hashset doesn't already contain the phone number string,
//                    then add it to the hashset
                    if (!ids.contains(phoneNumberofContact)) {
                        ids.add(phoneNumberofContact);

                        //alContacts is a list of all the phone numbers in the user's contacts
                        //alContacts.add(phoneNumberofContact);

//                    System.out.println("Id--->"+contactid+"Name--->"+name);
                        System.out.println(" Name--->" + name);
                        System.out.println(" Phone number of contact--->" + phoneNumberofContact);

                                SelectPhoneContact selectContact = new SelectPhoneContact();

                                selectContact.setName(name);
                                selectContact.setPhone(phoneNumberofContact);
                             //   selectContact.setLookup(lookupkey);
                                selectPhoneContacts.add(selectContact);
                            }

                } while (cursor.moveToNext());


            } catch (Exception e) {
                Toast.makeText(NewContact.this, "what the...", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                //   cursor.close();
            } finally {

            }


    if (cursor != null) {
        cursor.close();

    }
    return null;
}


        @Override
        protected void onPostExecute(Void aVoid) {
//Show the result obtained from doInBackground
            super.onPostExecute(aVoid);


            //into each phone_inflate_listview, put a name and phone number, which are the details making
//            our SelectContact, above. And SelectContacts is all these inflate_listviews together
//            This is the first property of our SelectContactAdapter, a list
//            The next part, NewContact.this, is our context, which is where we want the list to appear
            adapter = new SelectPhoneContactAdapter(selectPhoneContacts, NewContact.this);

//                    we need to notify the listview that changes may have been made on
//                    the background thread, doInBackground, like adding or deleting contacts,
//                    and these changes need to be reflected visibly in the listview. It works
//                    in conjunction with selectContacts.clear()
            adapter.notifyDataSetChanged();

            listView.setAdapter(adapter);

            //this function measures the height of the listview, with all the contacts, and loads it to be that
            //size. We need to do this because there's a problem with a listview in a scrollview.
            justifyListViewHeightBasedOnChildren(listView);



            // Select item on listclick
/*            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

//                    we need to notify the listview that changes may have been made on
//                    the background thread, doInBackground, like adding or deleting contacts,
//                    and these changes need to be reflected visibly in the listview. It works
//                    in conjunction with selectContacts.clear()

//                    adapter.notifyDataSetChanged();


                }



            });*/

            //hide the 'loading' box when the page loads
            pDialog.dismiss();
        }
    }


    @Override
    protected void onResume() {

        super.onResume();

        //    load the contacts again, refresh them, when the user resumes the activity
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) {
            cursor.close();
    }
    }

    public void onStop(){
        super.onDestroy();
        if (cursor != null) {
            cursor.close();
        }

    }


}