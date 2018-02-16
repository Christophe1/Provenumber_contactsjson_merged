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
    //an arraylist of all contacts phone numbers, which we will get from VerifyUserPhoneNumber
    //ArrayList <String> allPhonesofContacts;
    //an arraylist of all contacts names, which we will get from VerifyUserPhoneNumber
    //ArrayList <String> allNamesofContacts;
    String MatchingContactsAsString;
    //ArrayList<String> MatchingContactsAsArrayList;
    ArrayList<String> checkedContactsAsArrayList;
    String phoneNumberofContact;
    String phoneNameofContact;
    //ListView listView;
    //SelectPhoneContactAdapter adapter;
    PopulistoContactsAdapter adapter;
    String phoneNoofUserCheck;

    //For the recycler view, containing the phone contacts
    RecyclerView recyclerView;

    //this is the review of the current activity
    String review_id;

    Button save;
    Button cancel;


    private EditText categoryname;
    private EditText namename;
    private EditText phonename;
    private EditText addressname;
    private EditText commentname;

    //string for getting intent info from ContactView class
    String categoryid, category, name, phone, address, comment;

    //int for getting intent info for the check radio button, will be 0 or 1
    int pub_or_priv;

    //for the radio buttons
    RadioButton rbu1;
    RadioButton rbu2;

    //depends on radio button selected
    //int public_or_private;

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

        //********************
        //selectPhoneContacts is an empty array list that will hold our SelectPhoneContact info
        selectPhoneContacts = new ArrayList<SelectPhoneContact>();

        //listView = (ListView) findViewById(R.id.listviewPhoneContacts);

        //rbu1 = (RadioButton) findViewById(R.id.PhoneContacts);
       // rbu2 = (RadioButton) findViewById(R.id.Public);

        //final Button btnCheckAll = (Button) findViewById(R.id.btnCheckAll);

        //we want to get the phone number of the current user for posting
        //as a checkedcontact, phoneOwner.put("checkedContact", phoneNoofUserCheck);
        //so, they will always see their own reviews

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

        pub_or_priv = i.getIntExtra("publicorprivate",pub_or_priv);
        System.out.println("EditContact: public or private value :" + pub_or_priv);


        //set the EditText to display the pair value of key "category"
        categoryname.setText(category);
        //etc
        namename.setText(name);
        phonename.setText(phone);
        addressname.setText(address);
        commentname.setText(comment);

        //make the cursor appear at the end of the categoryname
        categoryname.setSelection(categoryname.getText().length());

        //we get this from ViewContact, with an intent
        //Set the radio button to be 'public' or 'phone contacts'
        //If pub_or_priv value from ViewContact is 0 then
/*        if(pub_or_priv==0)
            //set the radio button to phone contacts
            rbu1.setChecked(true);
        else
            //otherwise, if it's 1, make it public
            rbu2.setChecked(true);*/

        //load the asynctask stuff here
        LoadContact loadContact = new LoadContact();

        //execute asynctask
        loadContact.execute();

        //selectPhoneContacts is an empty array list that will hold our SelectPhoneContact info
        selectPhoneContacts = new ArrayList<SelectPhoneContact>();

        recyclerView = (RecyclerView) findViewById(R.id.rv);


        //If Public radio button is selected then check all the boxes
        //and change the button text to 'Clear All'
        //listen for which radio button is clicked
   /*     RadioGroup radioGroup = (RadioGroup) findViewById(R.id.SharedWith);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int SelectWho) {
                // find which radio button is selected
                if (SelectWho == R.id.Public) {
                    Toast.makeText(EditContact.this, "Public", Toast.LENGTH_LONG).show();
                    //If 'public' is selected, set pub_or_priv = 1
                    //this will save the review as public
                    // public_or_private=1;
                    //call the function to check all checkboxes in NewContact
                    //loop through the Matching Contacts
                    int count = PopulistoContactsAdapter.MatchingContactsAsArrayList.size();
                    for (int i = 0; i < count; i++) {
                        LinearLayout itemLayout = (LinearLayout) listView.getChildAt(i); // Find by under LinearLayout
                        CheckBox checkbox = (CheckBox) itemLayout.findViewById(R.id.checkBoxContact);
                        checkbox.setChecked(true);
                        btnCheckAll.setText("Clear All");
                    }

                }
                //  else
                //If 'phone contacts' is selected, set pub_or_priv = 0
                //this will save the review as private
                // public_or_private=0;
                //System.out.println("EditContact, after onCheckChange, should be 0: " + public_or_private);
            }
        });*/

        //Select All / Clear All Button
        //Check all or clear all checkboxes
  /*      btnCheckAll.setOnClickListener(new View.OnClickListener() {

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

                int count = PopulistoContactsAdapter.MatchingContactsAsArrayList.size();
                for (int i = 0; i < count; i++) {
                    LinearLayout itemLayout = (LinearLayout) listView.getChildAt(i); // Find by under LinearLayout
                    CheckBox checkbox = (CheckBox) itemLayout.findViewById(R.id.checkBoxContact);
                    checkbox.setChecked(toCheck);
                }
            }
        });
*/


        //*****************************************
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

                    //if the public radio button is selected
                    //then in the public_or_private column of the review
                    //table put 1, t make it public
                    //otherwise, if PhoneContacts is selected,
                    //put 0, to make it private

/*                    RadioGroup rg1 =(RadioGroup)findViewById(R.id.SharedWith);
                    if (rg1.getCheckedRadioButtonId()==R.id.Public){
                        System.out.println("It is public");
                        pub_or_priv =1;
                    }
                    if (rg1.getCheckedRadioButtonId()==R.id.PhoneContacts){
                        System.out.println("It is private");
                        pub_or_priv =0;
                    }*/
                    // the user will be able to check contacts who to share the review
                    // with, from their matching contacts list
                    int count = PopulistoContactsAdapter.newcheckedContactsAsArrayList.size();
                    // CheckBox fee_checkbox;
                    for (int i = 0; i < count; i++) {
                        //  LinearLayout itemLayout = (LinearLayout)recyclerView.getChildAt(i);
                        // CheckBox checkbox = (CheckBox)itemLayout.findViewById(R.id.checkBoxContact);

             /*           Object position = checkbox.getTag();
                        if (position instanceof Integer) {
                            Toast.makeText(EditContact.this, "whawha", Toast.LENGTH_LONG).show();

                            //SelectPhoneContact contact = checkedContactsAsArrayList.get((Integer)position);
                        }*/
                        // SelectPhoneContact contact = (SelectPhoneContact) checkbox.getTag();
                        // fee_checkbox = (CheckBox)findViewById(R.id.checkBoxContact);
                        //   if(count.get(i)==true){
                        //for  contacts that are checked (they can only be matching contacts)...
                        // if (fee_checkbox.isChecked()) {

                        //for each Matching Contacts row in the listview
                        //LinearLayout itemLayout = (LinearLayout)listView.getChildAt(i); // Find by under LinearLayout
                        //for each Matching Contacts checkbox in the listview
                        //CheckBox checkbox = (CheckBox)itemLayout.findViewById(R.id.checkBoxContact);
                        //get the other data related to the selected contact - name and number
                        // SelectPhoneContact contact = (SelectPhoneContact) checkbox.getTag();

                        // make each checked contact in selectPhoneContacts
                        // into an individual
                        // JSON object called checkedContact
                        JSONObject checkedContact = new JSONObject();

                        //if that checkbox is checked, then get the phone number
                        //if(checkbox.isChecked()) {
                /*        if(checkbox.isChecked()) {
                            Log.d("Item " + String.valueOf(i), checkbox.getTag().toString());
                            Toast.makeText(EditContact.this, contact.getPhone(), Toast.LENGTH_LONG).show();
*/
                        // checkedContact will be of the form {"checkedContact":"+353123456"}
                        checkedContact.put("checkedContact", PopulistoContactsAdapter.theContactsList.get(i).getPhone());

                        // Add checkedContact JSON Object to checkedContacts jsonArray
                        //The JSON Array will be of the form
                        // [{"checkedContact":"+3531234567"},{"checkedContact":"+353868132813"}]
                        //we will be posting this JSON Array to Php, further down below
                        checkedContacts.put(checkedContact);
                        System.out.println("EditContact: checkedcontact JSONObject :" + checkedContact);
                        //  }

                   // }

                    //add phone owner's number to the checkedContacts JSON Array
                    //new JSON Object called phoneOwner
                    JSONObject phoneOwner = new JSONObject();

                    //add the phone number
                    phoneOwner.put("checkedContact", phoneNoofUserCheck);
                    System.out.println("NewContact: phoneOwner: " + phoneOwner);

                    //add it to the Array
                    checkedContacts.put(phoneOwner);

                    System.out.println("checkedContacts JSON Array " + checkedContacts);


                    }
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
                        params.put("public_or_private", String.valueOf(pub_or_priv));

                        //this is the JSON Array of checked contacts
                        //it will be of the form
                        //[{"checkedContact":"+3531234567"},{"checkedContact":"+353868132813"}]
                        params.put("checkedContacts", checkedContacts.toString());

                        return params;

                    }


                };


                AppController.getInstance().addToRequestQueue(stringRequest);

                //when saved, go to the PopulistoListView class and update with
                //the edited values
                Intent j = new Intent(EditContact.this, PopulistoListView.class);
/*                j.putExtra("category", categoryname.getText().toString());
                j.putExtra("name", namename.getText().toString());
                j.putExtra("phone", phonename.getText().toString());
                j.putExtra("address", addressname.getText().toString());
                j.putExtra("comment", commentname.getText().toString());*/

                //    j.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//startactivity only once with intent
                // android:launchMode="singleInstance"
                // android:launchMode="singleInstance"
                //https://stackoverflow.com/questions/8132939/how-to-keep-a-single-activity-instance-but-be-able-to-update-the-view

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

            //System.out.println("EditContact postexecute1: checkedContactsAsArrayList is " + checkedContactsAsArrayList);


            //we want to delete the old selectContacts from the listview when the Activity loads
            //because it may need to be updated and we want the user to see the updated listview,
            //like if the user adds new names and numbers to their phone contacts.
            //selectPhoneContacts.clear();


            //we are fetching the array list allPhonesofContacts, created in VerifyUserPhoneNumber.
            //with this we will put all phone numbers of contacts on user's phone into our ListView in ViewContact activity
            /*SharedPreferences sharedPreferencesallPhonesofContacts = PreferenceManager.getDefaultSharedPreferences(getApplication());
            Gson gson = new Gson();
            String json = sharedPreferencesallPhonesofContacts.getString("allPhonesofContacts", "");
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            PopulistoContactsAdapter.allPhonesofContacts = gson.fromJson(json, type);
            System.out.println("ViewContact: allPhonesofContacts :" + PopulistoContactsAdapter.allPhonesofContacts);
*/
            //we are fetching the array list allNamesofContacts, created in VerifyUserPhoneNumber.
            //with this we will put all phone names of contacts on user's phone into our ListView in ViewContact activity
            /*SharedPreferences sharedPreferencesallNamesofContacts = PreferenceManager.getDefaultSharedPreferences(getApplication());
            Gson gsonNames = new Gson();
            String jsonNames = sharedPreferencesallNamesofContacts.getString("allNamesofContacts", "");
            Type typeNames = new TypeToken<ArrayList<String>>() {
            }.getType();
            allNamesofContacts = gsonNames.fromJson(jsonNames, typeNames);
            System.out.println("ViewContact: allNamesofContacts :" + allNamesofContacts);

            System.out.println("ViewContact:the amount in allPhonesofContacts :" + allPhonesofContacts.size());
            System.out.println("ViewContact:the amount in allNamesofContacts :" + allNamesofContacts.size());
*/

            //we are fetching the array list MatchingContactsAsArrayList, created in VerifyUserPhoneNumber.
            //With that we'll put our
            //matching contacts at the top of the listview, display check boxes beside them etc...
 /*           SharedPreferences sharedPreferencesMatchingContactsAsArrayList = PreferenceManager.getDefaultSharedPreferences(getApplication());
            Gson gsonMatchingContactsAsArrayList = new Gson();
            String jsonMatchingContactsAsArrayList = sharedPreferencesMatchingContactsAsArrayList.getString("MatchingContactsAsArrayList", "");
            Type type1 = new TypeToken<ArrayList<String>>() {
            }.getType();
            MatchingContactsAsArrayList = gsonMatchingContactsAsArrayList.fromJson(jsonMatchingContactsAsArrayList, type1);
            System.out.println("ViewContact MatchingContactsAsArrayList :" + MatchingContactsAsArrayList);
*/


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

            //*********set the Matching Contacts to checked, by default ************
            //loop through the matching contacts
 /*           int count = PopulistoContactsAdapter.checkedContactsAsArrayList.size();
            System.out.println("EditContact postexecute: checkedContactsAsArrayList is " + PopulistoContactsAdapter.checkedContactsAsArrayList);

           for (int number2 = 0; number2 < count; number2++) {

                //if a phone number is in our array of checked contacts
                if (PopulistoContactsAdapter.theContactsList.contains(checkedContactsAsArrayList)) {

                    PopulistoContactsAdapter.theContactsList.get(number2).setSelected(true);
                }
            }*/

            // adapter = new SelectPhoneContactAdapter(selectPhoneContacts, EditContact.this,2);

            //listView.setAdapter(adapter);

          /*  adapter.radioButtontoPhoneContactsEdit = new EditContact.radioButtontoPhoneContactsEdit() {
                //@Override
                public void update() {
                    EditContact.this.rbu1.setChecked(true);
                }
            };*/

            //*********set the Matching Contacts to checked, by default ************
            //loop through the matching contacts
  /*          int count = PopulistoContactsAdapter.checkedContactsAsArrayList.size();

            for (int i = 0; i < count; i++) {

                //check all matching contacts, we want it to be 'Phone Contacts' by default
                PopulistoContactsAdapter.theContactsList.get(i).setSelected(true);

                //we need to notify the recyclerview that changes may have been made
                adapter.notifyDataSetChanged();
            }*/


            //we need to notify the listview that changes may have been made on
            //the background thread, doInBackground, like adding or deleting contacts,
            //and these changes need to be reflected visibly in the listview. It works
            //in conjunction with selectContacts.clear()
            adapter.notifyDataSetChanged();



            //********************

            //this function measures the height of the listview, with all the contacts, and loads it to be that
            //size. We need to do this because there's a problem with a listview in a scrollview.
            //The function is in GlobalFunctions
           // GlobalFunctions.justifyListViewHeightBasedOnChildren(EditContact.this,listView);

        }
    }


    @Override
    protected void onResume() {

        super.onResume();

        //  selectPhoneContacts.clear();

        //  EditContact.LoadContact loadContact = new EditContact.LoadContact();


        //  loadContact.execute();
//        adapter.notifyDataSetChanged();
        Toast.makeText(EditContact.this, "resuming!", Toast.LENGTH_SHORT).show();


    }

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




}