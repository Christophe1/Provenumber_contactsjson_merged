package com.example.chris.tutorialspoint;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;
import java.util.Map;

public class EditContact extends AppCompatActivity {

    // this is the php file name where to select from.
    // we will post the category, name, phone, address and comment into Php and
    // save with matching review_id
    private static final String EditContact_URL = "http://www.populisto.com/EditContact.php";

    private ProgressDialog pDialog;

    //in this JSONArray, checkedContacts, we will be storing each checkedContact JSON Object
    //Then we're going to post it to our EditContact.php file
    JSONArray checkedContacts = new JSONArray();

    //selectPhoneContacts is an empty array list that will hold our SelectPhoneContact info
    ArrayList<SelectPhoneContact> selectPhoneContacts;

    String phoneNumberofContact;
    String phoneNameofContact;

    PopulistoContactsAdapter adapter;
    public static String phoneNoofUserCheck;

    //For the recycler view, containing the phone contacts
    RecyclerView recyclerView;

    //this is the review of the current activity
    String review_id;

    Button publicContacts;
    Button phoneContacts;
    Button justMeContacts;

    Button save;
    Button cancel;


    private EditText categoryname;
    private EditText namename;
    private EditText phonename;
    private EditText addressname;
    private EditText commentname;

    //this is an integer, different to the string public_or_private in ViewContact. Not related
    int public_or_private;

    //string for getting intent info from ContactView class
    String categoryid, category, name, phone, address, comment;

    //int for getting intent info for the sharing buttons in ViewContact
    int pub_or_priv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, EditContact.this,2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Show the back button (???)
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        //actionbar.setDisplayShowHomeEnabled(true);

        //show the App title
        actionbar.setTitle("Populisto");

        //get the phone number, stored in an XML file, when the user first registered the app
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        phoneNoofUserCheck = sharedPreferences.getString("phonenumberofuser", "");


        //cast an EditText for each of the field ids in activity_edit_contact.xmlxml
        //can be edited and changed by the user
        categoryname = (EditText) findViewById(R.id.textViewCategory);
        namename = (EditText) findViewById(R.id.textViewName);
        phonename = (EditText) findViewById(R.id.textViewPhone);
        addressname = (EditText) findViewById(R.id.textViewAddress);
        commentname = (EditText) findViewById(R.id.textViewComment);

        //for the save button ******************************
        save = (Button) findViewById(R.id.save);

        saveContactButton();

        //get the intent we created in ViewContact class, to bring the details over
        //to this class
        Intent i = this.getIntent();
        //we need to get review_id to ensure changes made are saved to correct review_id
        review_id = i.getStringExtra("review_id");
        //get the key, "category", in ContactView activity
        categoryid = i.getStringExtra("category");
        //etc..
        category = i.getStringExtra("category");
        name = i.getStringExtra("name");
        phone = i.getStringExtra("phone");
        address = i.getStringExtra("address");
        comment = i.getStringExtra("comment");

        public_or_private = i.getIntExtra("publicorprivate",pub_or_priv);

        Log.i("EditContact-MyMessage", "EditContact: public or private value :" + public_or_private);


        //set the EditText to display the pair value of key "category"
        categoryname.setText(category);
        //etc
        namename.setText(name);
        phonename.setText(phone);
        addressname.setText(address);
        commentname.setText(comment);

        //make the cursor appear at the end of the categoryname
        categoryname.setSelection(categoryname.getText().length());

        //for the Public, phoneContacts, justMe, save and cancel buttons
        publicContacts = (Button) findViewById(R.id.btnPublic);
        phoneContacts = (Button) findViewById(R.id.btnPhoneContacts);
        justMeContacts = (Button) findViewById(R.id.btnJustMe);

        publicButton();
        phoneContactsButton();
        justMeButton();

        //we get this from ViewContact, with an intent
        //Set the sharing button to be 'public' or 'phone contacts' or 'just me' colour
        //If pub_or_priv value from ViewContact is 0 then
        if(public_or_private==0)
        {
            //keep the slightly rounded shape, when the button is pressed, and change colour
            justMeContacts.setBackgroundResource(R.drawable.justmecontacts_buttonshapepressed);
        }

        if(public_or_private==1)
        {
            //keep the slightly rounded shape, when the button is pressed, and change colour
            phoneContacts.setBackgroundResource(R.drawable.phonecontacts_buttonshapepressed);
        }

        if(public_or_private==2)
        {
            //keep the slightly rounded shape, when the button is pressed, and change colour
            publicContacts.setBackgroundResource(R.drawable.publiccontacts_buttonshapepressed);
        }

        //load the asynctask stuff here
        LoadContact loadContact = new LoadContact();

        //execute asynctask
        loadContact.execute();

        //selectPhoneContacts is an empty array list that will hold our SelectPhoneContact info
        selectPhoneContacts = new ArrayList<SelectPhoneContact>();

        recyclerView = (RecyclerView) findViewById(R.id.rv);

        //for the cancel button
        cancel = (Button) findViewById(R.id.cancel);

        //call the cancel function
        cancelButton();


    }

    //for the SAVE button
    private void saveContactButton() {

        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                System.out.println("you clicked it, save");

                //close the populistolistview class
                //(we'll be opening it again, will close now so it will be refreshed)
                PopulistoListView.fa.finish();

                pDialog = new ProgressDialog(EditContact.this);
                // Showing progress dialog for the review being saved
                pDialog.setMessage("Saving...");
                pDialog.show();

                try {
                    System.out.println("we're in the try part");

                    //get the checked contacts from the adapter
                    int count = PopulistoContactsAdapter.checkedContactsAsArrayList.size();

                    //for each phone number in the array list...
                    for (int i = 0; i < count; i++) {

                            // make each checked contact
                            // into an individual
                            // JSON OBJECT called checkedContact
                            JSONObject checkedContact = new JSONObject();

                            //for  contacts that are checked (they can only be matching contacts)...
                            // checkedContact OBJECT will be of the form {"checkedContact":"+353123456"}
                            checkedContact.put("checkedContact", PopulistoContactsAdapter.checkedContactsAsArrayList.get(i));

                            // Add checkedContact JSON Object to checkedContacts jsonArray
                            //The JSON ARRAY will be of the form
                            // [{"checkedContact":"+3531234567"},{"checkedContact":"+353868132813"}]
                            //we will be posting this JSON Array to Php, further down below
                            checkedContacts.put(checkedContact);
                            Log.i("Adapter1", "EditContact: checkedcontact JSONObject :" + checkedContact);

                    }

                    //add phone owner's number to the checkedContacts JSON Array
                    //First, new JSON Object called phoneOwner
                    JSONObject phoneOwner = new JSONObject();

                    //add the phone number
                    phoneOwner.put("checkedContact", phoneNoofUserCheck);
                    System.out.println("NewContact: phoneOwner: " + phoneOwner);

                    //add it to the Array
                    checkedContacts.put(phoneOwner);

                    Log.i("Adapter1", "EditContact: checkedContacts JSON Array " + checkedContacts);



                } catch (Exception e) {
                    System.out.println("EditContact: there's a problem here unfortunately");
                    e.printStackTrace();
                }


                //post the review_id in the current activity to EditContact.php and from that
                //get associated values - category, name, phone etc...
                StringRequest stringRequest = new StringRequest(Request.Method.POST, EditContact_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Toast.makeText(EditContact.this, response, Toast.LENGTH_LONG).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(EditContact.this, "there's a problem saving this page", Toast.LENGTH_LONG).show();

                            }

                        }) {

                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        //post the phone number to php to get the user_id in the user table
                        params.put("phonenumberofuser", phoneNoofUserCheck);
                        params.put("review_id", review_id);
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

                        //this is the JSON Array of checked contacts
                        //it will be of the form
                        //[{"checkedContact":"+3531234567"},{"checkedContact":"+353868132813"}]
                        params.put("checkedContacts", checkedContacts.toString());

                        Log.i("Adapter1", "public_or_private value when saved is: " + public_or_private);


                        return params;

                    }


                };


                AppController.getInstance().addToRequestQueue(stringRequest);

                //when saved, go to the PopulistoListView class and update with
                //the edited values
                Intent j = new Intent(EditContact.this, PopulistoListView.class);

                EditContact.this.startActivity(j);

                // clear the checkbox state of checked contacts, we only want to keep it when the app resumes
                //SharedPreferences preferences = getSharedPreferences("sharedPrefsFile", 0);
                //preferences.edit().clear().commit();

                finish();
                //hide the dialogue box when page is saved
                hidePDialog();
            }


        });

    }

    private void cancelButton() {

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //for the cancel button, clear the checkbox state
                SharedPreferences preferences = getSharedPreferences("sharedPrefsFile", 0);
                preferences.edit().clear().commit();

                finish();
            }
        });
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


    //******for the phone contacts in the listview

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

                System.out.println("ViewContact: phoneNumberofContact : " + phoneNumberofContact);
                System.out.println("ViewContact: phoneNameofContact : " + phoneNameofContact);

                SelectPhoneContact selectContact = new SelectPhoneContact();

                //if a phone number is in our array of matching contacts
                if (PopulistoContactsAdapter.MatchingContactsAsArrayList.contains(phoneNumberofContact))

                {
                    // insert the contact at the beginning of the listview
                    selectPhoneContacts.add(0, selectContact);
                    // checkBoxforContact.setVisibility(View.VISIBLE);

                    //In SelectContact class, so getItemViewType will know which layout to show
                    //:checkbox or Invite Button
                    selectContact.setType_row("1");

                } else {
                    // insert it at the end (default)
                    selectPhoneContacts.add(selectContact);
                    selectContact.setType_row("2");

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



            PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, EditContact.this, 2);

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager((new LinearLayoutManager(EditContact.this)));

            //we need to notify the listview that changes may have been made on
            //the background thread, doInBackground, like adding or deleting contacts,
            //and these changes need to be reflected visibly in the listview. It works
            //in conjunction with selectContacts.clear()
            adapter.notifyDataSetChanged();

        }
    }


/*    @Override
    protected void onResume() {

        super.onResume();

        //  selectPhoneContacts.clear();

        //  EditContact.LoadContact loadContact = new EditContact.LoadContact();


        //  loadContact.execute();
//        adapter.notifyDataSetChanged();
        Toast.makeText(EditContact.this, "resuming!", Toast.LENGTH_SHORT).show();


    }*/

    //need this to change radio button to Phone Contacts,
    //if a checkbox is changed to false
    public abstract class radioButtontoPhoneContactsEdit
    {
        public void update() {}
    }


    public void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
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



    //for the Public Contacts button
    private void publicButton() {

        publicContacts.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//              keep the slightly rounded shape, when the button is pressed
                publicContacts.setBackgroundResource(R.drawable.publiccontacts_buttonshapepressed);

//               keep the slightly rounded shape of the others, but still grey
                phoneContacts.setBackgroundResource(R.drawable.buttonshape);
                justMeContacts.setBackgroundResource(R.drawable.buttonshape);

                //set sharing to Public
                // This will be uploaded to server to review table,
                //public_or_private column, if saved in this state
                public_or_private = 2;

                Toast.makeText(EditContact.this, String.valueOf(public_or_private), Toast.LENGTH_LONG).show();

                PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, EditContact.this, 2);

                recyclerView.setAdapter(adapter);

                //loop through the matching contacts
                int count = PopulistoContactsAdapter.MatchingContactsAsArrayList.size();

                //clear checkedContactsAsArrayList and then add all matching contacts again
                PopulistoContactsAdapter.checkedContactsAsArrayList.clear();

                //i is the number of matching contacts that there are
                for (int i = 0; i < count; i++) {

                    //add the matching contacts
                    PopulistoContactsAdapter.checkedContactsAsArrayList.add(i,PopulistoContactsAdapter.MatchingContactsAsArrayList.get(i));

                    //set them to be checked
                    PopulistoContactsAdapter.theContactsList.get(i).setSelected(true);

                        //we need to notify the recyclerview that changes may have been made
                        adapter.notifyDataSetChanged();

                }
                Log.i("Adapter1", "checkedContactsAsArrayList is: " + PopulistoContactsAdapter.checkedContactsAsArrayList);
                //Log.i("EditContact-MyMessage", "List is: " + existing_values);
                Log.i("Adapter1", "number in Matching Contacts is " + PopulistoContactsAdapter.MatchingContactsAsArrayList.size());
                Log.i("Adapter1", "number in checkedContactsAsArrayList is " + PopulistoContactsAdapter.checkedContactsAsArrayList.size());


            }
        });

    }


    //for the phone Contacts button
    private void phoneContactsButton() {

        phoneContacts.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //keep the slightly rounded shape, when the button is pressed
                phoneContacts.setBackgroundResource(R.drawable.phonecontacts_buttonshapepressed);


//               keep the slightly rounded shape of the others, but still grey
                publicContacts.setBackgroundResource(R.drawable.buttonshape);
                justMeContacts.setBackgroundResource(R.drawable.buttonshape);

                //set sharing to Phone Contacts
                // This will be uploaded to server to review table,
                //public_or_private column, if saved in this state
                public_or_private = 1;

                Toast.makeText(EditContact.this, String.valueOf(public_or_private), Toast.LENGTH_LONG).show();

                PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, EditContact.this,2);

                recyclerView.setAdapter(adapter);

                //loop through the matching contacts
                int count = PopulistoContactsAdapter.MatchingContactsAsArrayList.size();

                //clear checkedContactsAsArrayList and then add all matching contacts again
                PopulistoContactsAdapter.checkedContactsAsArrayList.clear();

                //i is the number of matching contacts that there are
                for (int i = 0; i < count; i++) {

                    PopulistoContactsAdapter.checkedContactsAsArrayList.add(i,PopulistoContactsAdapter.MatchingContactsAsArrayList.get(i));

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

                //keep the slightly rounded shape, when the button is pressed
                justMeContacts.setBackgroundResource(R.drawable.justmecontacts_buttonshapepressed);

//               keep the slightly rounded shape of the others, but still grey
                publicContacts.setBackgroundResource(R.drawable.buttonshape);
                phoneContacts.setBackgroundResource(R.drawable.buttonshape);

                //set sharing to Just Me
                // This will be uploaded to server to review table,
                //public_or_private column, if saved in this state
                public_or_private = 0;
                Toast.makeText(EditContact.this, String.valueOf(public_or_private), Toast.LENGTH_LONG).show();

                PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, EditContact.this,2);

                recyclerView.setAdapter(adapter);

                //reset the size of the array to 0
                PopulistoContactsAdapter.checkedContactsAsArrayList.clear();

                //loop through the matching contacts
               int count = PopulistoContactsAdapter.MatchingContactsAsArrayList.size();

                for (int i = 0; i < count; i++) {

                    //uncheck all matching contacts, we want it to be 'Just Me'
                    PopulistoContactsAdapter.theContactsList.get(i).setSelected(false);
                    //we need to notify the recyclerview that changes may have been made*/
                    adapter.notifyDataSetChanged();
                }
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

        //when checked boxes are more than 0, change public_or_private to 1,
        //so phone contacts button will be selected
        public_or_private =1;

    }

    //this is called from PopulistoContactsAdapter
    //change the colour of the justMe button
    public void changeColorofJustMe() {
        //keep the slightly rounded shape, when the button is pressed
        justMeContacts.setBackgroundResource(R.drawable.justmecontacts_buttonshapepressed);

        //keep the slightly rounded shape of the others, but still grey
        publicContacts.setBackgroundResource(R.drawable.buttonshape);
        phoneContacts.setBackgroundResource(R.drawable.buttonshape);

        //when checked boxes are 0, change public_or_private to 0,
        //so justme button will be selected
        public_or_private =0;

    }




}