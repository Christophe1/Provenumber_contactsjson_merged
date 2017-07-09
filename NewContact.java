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
    String[] names;

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

        names = getResources().getStringArray(R.array.name_array);
        listView = (ListView) findViewById(R.id.listviewPhoneContacts);

        //testing purposes, trying to get listview working
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);

        listView.setAdapter(adapter);

        //testing purposes, trying to get listview working
        System.out.println("names are" + Arrays.toString(names));

        //this function measures the height of the listview, with all the contacts, and loads it to be that
        //size. We need to do this because there's a problem with a listview in a scrollview.
        justifyListViewHeightBasedOnChildren(listView);

        //*************************
        // Details below are for the phone contacts listview

        //selectContacts is an empty array list that will hold our SelectContct info
        selectContacts = new ArrayList<SelectContact>();

       // listView = (ListView) findViewById(R.id.listviewPhoneContacts);

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



    }

    //******for the phone contacts in the listview

    // Load data on background
    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected Void doInBackground(Void... voids) {

//            Perhaps running this thread on the UI thread has solved the issue of the app
//            crashing? ListView had not been updating properly, I think.
            runOnUiThread(new Runnable() {
                public void run() {

//          we want to delete the old selectContacts from the listview when the Activity loads
//          because it may need to be updated and we want the user to see the updated listview,
//          like if the user adds new names and numbers to their phone contacts.
                    selectContacts.clear();

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
//                                Projection - the columns you want to query
                                        null,
//                                Selection - with this you are extracting records with assigned (by you) conditions and rules
                                        null,
//                                SelectionArgs - This replaces any question marks (?) in the selection string
//                               if you have something like String[] args = { "first string", "second@string.com" };
                                        null,
//                                display in ascending order
                                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC");

//                get the column number of the Contact_ID column, make it an integer.
//                I think having it stored as a number makes for faster operations later on.
                        int Idx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
//                get the column number of the DISPLAY_NAME column
                        int nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
//                 get the column number of the NUMBER column
                        int phoneNumberIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

//                ****
                        int contactlookupkey = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY);
//                ****
//                cursor.moveToFirst();
//        String contactlookupkey2 = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY));


//                int photoIdIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI);


                        cursor.moveToFirst();

//              We make a new Hashset to hold all our contact_ids, including duplicates, if they come up
                        Set<String> ids = new HashSet<>();
                        do {
                            System.out.println("=====>in while");
//                  get a handle on the contactid, which is a string. Loop through all the contact_ids
                            String contactid = cursor.getString(Idx);
//                  if our Hashset doesn't already contain the contactid string,
//                    then add it to the hashset
                            if (!ids.contains(contactid)) {
                                ids.add(contactid);

                                HashMap<String, String> hashMap = new HashMap<String, String>();
//                        get a handle on the display name, which is a string
                                name = cursor.getString(nameIdx);
//                        get a handle on the phone number, which is a string
                                phoneNumber = cursor.getString(phoneNumberIdx);
//                        String image = cursor.getString(photoIdIdx);

                                lookupkey = cursor.getString(contactlookupkey);

//                    System.out.println("Id--->"+contactid+"Name--->"+name);
                                System.out.println("Id--->" + contactid + " Name--->" + name);
                                System.out.println("Id--->" + contactid + " Number--->" + phoneNumber);
                                System.out.println("Id--->" + contactid + " lookupkey--->" + lookupkey);

                                SelectContact selectContact = new SelectContact();

                                selectContact.setName(name);
                                selectContact.setPhone(phoneNumber);
                                selectContact.setLookup(lookupkey);
//                    selectContact.setCheckedBox(false);
                                selectContacts.add(selectContact);
                            }


                        } while (cursor.moveToNext());


                    } catch (Exception e) {
                        Toast.makeText(NewContact.this, "what the...", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                     //   cursor.close();
                    } finally {

                    }
                }});
            if (cursor != null) {
                cursor.close();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);

//into each inflate_listview, put a name and phone number, which are the details making
//            our SelectContact, above. And SelectContacts is all these inflate_listviews together
//            This is the first property of our SelectContactAdapter, a list
//            The next part, NewContact.this, is our context, which is where we want the list to appear
            adapter = new SelectContactAdapter(selectContacts, NewContact.this);

            adapter.notifyDataSetChanged();


        }
    }

//this is the function we call to measure the height of the listview
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
    }


}