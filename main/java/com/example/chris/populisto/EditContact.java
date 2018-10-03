package com.example.chris.populisto;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

import static android.view.View.GONE;
import static com.example.chris.populisto.PopulistoContactsAdapter.allPhonesofContacts;
import static com.example.chris.populisto.PopulistoContactsAdapter.checkedContactsAsArrayList;
import static com.example.chris.populisto.PopulistoContactsAdapter.theContactsList;
import static java.lang.String.valueOf;

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

  //We get this as intent from ViewContact so we can pass it back again to ViewContact
  //if Cancel or <- is clicked (otherwise its value would be null)
  String checkedContactsfromViewContact;

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

  //string for getting intent info from ViewContact class
  //String categoryid;
  String category, name, phone, address, comment;

  //int for getting intent info for the sharing buttons in ViewContact
  int pub_or_priv;

  // private CheckBox testchecked;

  //we want to detect if editTexts have changed,
  //if so, we'll give the option to save - cancel click will say like'Really? Save changes first?'
  Boolean editTexthasChanged = false;

  //sharedprefs for holding all phone numbers of contacts
  SharedPreferences sharedPreferencesallPhonesofContacts;

  //if user has no contacts on his phone, like if no
  //permission has been given to getPhoneContacts
  TextView noContactsFound;

  //if it is 0, then show the 'No Contacts Found' textbox
  int noContactFoundCheck;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_contact);

    noContactsFound = (TextView) findViewById(R.id.noContactsFoundView);

    PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, EditContact.this, 2);

    //put in a toolbar
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    //Show the back button (???)
    //ActionBar actionbar = getSupportActionBar();
    //actionbar.setDisplayHomeAsUpEnabled(true);
    //actionbar.setDisplayShowHomeEnabled(true);

    //remove the app name from the toolbar (don't want it twice)
    //we already get it from strings.xml
    getSupportActionBar().setDisplayShowTitleEnabled(false);

    //into the toolbar, inflate the back button and Populisto title,
    //which we find in toolbar_custom_view_layout.xml
    View logo = getLayoutInflater().inflate(R.layout.toolbar_custom_view_layout, null);
    toolbar.addView(logo);

    //for the back arrow, tell it to close the activity, when clicked
    ImageView backButton = (ImageView) logo.findViewById(R.id.back_arrow_id);
    //backButton.setBackgroundColor(Color.rgb(100, 100, 50));

    //use ontouch listener, so when <- image is DOWN it changes to grey
    //for an instant
    backButton.setOnTouchListener(new View.OnTouchListener() {

      @Override
      public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN: {
            ImageView view = (ImageView) v;

            view.getDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
            view.invalidate();

            //before going back, see if changes have been made
            goBacktoViewContact();
            break;
          }
          case MotionEvent.ACTION_UP:
          case MotionEvent.ACTION_CANCEL: {
            ImageView view = (ImageView) v;
            //clear the overlay
            view.getDrawable().clearColorFilter();
            view.invalidate();
            break;
          }
        }
        return true;
      }
    });

/*    backButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        //before going back, see if changes have been made
        goBacktoViewContact();

      }
    });*/

    //get the phone number, stored in an XML file, when the user first registered the app.
    // We need the user's number so we can remove that number from checkedContactsAsArrayList
    //in PopulistoContactsAdapter, otherwise that number will be saved twice in the DB
    SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
    phoneNoofUserCheck = sharedPreferences.getString("phonenumberofuser", "");


    //cast an EditText for each of the field ids in activity_edit_contact.xml
    //can be edited and changed by the user
    categoryname = (EditText) findViewById(R.id.textViewCategory);
    namename = (EditText) findViewById(R.id.textViewName);
    phonename = (EditText) findViewById(R.id.textViewPhone);
    addressname = (EditText) findViewById(R.id.textViewAddress);
    commentname = (EditText) findViewById(R.id.textViewComment);

    //for the save button
    save = (Button) findViewById(R.id.save);
    //for the cancel button
    cancel = (Button) findViewById(R.id.cancel);


    //get the intent we created in ViewContact class, to bring the details over
    //to this class
    Intent i = this.getIntent();
    //we need to get review_id to ensure changes made are saved to correct review_id
    //we post review_id in saveContact()
    review_id = i.getStringExtra("review_id");

    category = i.getStringExtra("category");
    name = i.getStringExtra("name");
    phone = i.getStringExtra("phone");
    address = i.getStringExtra("address");
    comment = i.getStringExtra("comment");
    checkedContactsfromViewContact = i.getStringExtra("checkedContacts");

    public_or_private = i.getIntExtra("publicorprivate", pub_or_priv);

    //the original value of pub_or_priv, in case user click CANCEL
    //or <-, which we pass back to ViewContact
    pub_or_priv = public_or_private;

    //Log.i("EditContact-MyMessage", "EditContact: public or private value :" + public_or_private);


    //set the EditText to display the pair value of key "category"
    categoryname.setText(category);
    //etc
    namename.setText(name);
    phonename.setText(phone);
    addressname.setText(address);
    commentname.setText(comment);

    //make the cursor appear at the end of the categoryname
    categoryname.setSelection(categoryname.getText().length());

    //let's set up a textwatcher so if the state of any of the edittexts has changed.
    //if it has changed and user clicks 'CANCEL', we'll ask first, '
    //You've made changes here. Sure you want to cancel?'
    TextWatcher edittw = new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        // Boolean editTexthasChanged has been set to false on initialization,
        //if text has been changed in any of our editTexts then set it to true
        editTexthasChanged = true;

      }
    };

    categoryname.addTextChangedListener(edittw);
    namename.addTextChangedListener(edittw);
    phonename.addTextChangedListener(edittw);
    addressname.addTextChangedListener(edittw);
    commentname.addTextChangedListener(edittw);


    //for the Public, phoneContacts, justMe, save and cancel buttons
    publicContacts = (Button) findViewById(R.id.btnPublic);
    phoneContacts = (Button) findViewById(R.id.btnPhoneContacts);
    justMeContacts = (Button) findViewById(R.id.btnJustMe);

    //declare the buttons
    publicButton();
    phoneContactsButton();
    justMeButton();

    //If permission denied (will only be on Marshmallow +)
    PackageManager manager = getPackageManager();
    int hasPermission = manager.checkPermission("android.permission.READ_CONTACTS", "com.example.chris.populisto");
    if (hasPermission == manager.PERMISSION_DENIED) {

      //PopulistoContactsAdapter.checkedContactsAsArrayList.clear();

      //select the Just Me button
      justMeContacts.setBackgroundResource(R.drawable.justmecontacts_buttonshapepressed);

      //disable the phoneContacts button
      phoneContacts.setEnabled(false);

      //disable the public button
      publicContacts.setEnabled(false);

    }

    else {
      //we get sharing status from ViewContact, with an intent
      //Set the sharing button to be 'public' or 'phone contacts' or 'just me' colour
      //If pub_or_priv value from ViewContact is 0 then
      if (public_or_private == 0) {
        //keep the slightly rounded shape, when the button is pressed, and change colour
        justMeContacts.setBackgroundResource(R.drawable.justmecontacts_buttonshapepressed);
      }

      if (public_or_private == 1) {
        //keep the slightly rounded shape, when the button is pressed, and change colour
        phoneContacts.setBackgroundResource(R.drawable.phonecontacts_buttonshapepressed);
      }

      if (public_or_private == 2) {
        //keep the slightly rounded shape, when the button is pressed, and change colour
        publicContacts.setBackgroundResource(R.drawable.publiccontacts_buttonshapepressed);
      }
    }

      //  Toast.makeText(EditContact.this, "public_or_private value is : " + public_or_private, Toast.LENGTH_SHORT).show();

      //load the asynctask stuff here
      LoadContact loadContact = new LoadContact();

      //execute asynctask
      loadContact.execute();

      //selectPhoneContacts is an empty array list that will hold our SelectPhoneContact info,
      //the recyclerView of phone contacts, check boxes, Invite button
      selectPhoneContacts = new ArrayList<SelectPhoneContact>();

      recyclerView = (RecyclerView) findViewById(R.id.rv);



    //when the SAVE BUTTON is clicked
    //save the contact details
    save.setOnClickListener(new View.OnClickListener() {

      public void onClick(View v) {
        try {
          saveContact();
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    });


    //when the CANCEL BUTTON is clicked
    //check if editTexts have been changed
    //and checkboxes changed
    cancel.setOnClickListener(new View.OnClickListener() {

      public void onClick(View v) {
        //Toast.makeText(EditContact.this, "here it is dudia" + String.valueOf(PopulistoContactsAdapter.checkBoxhasChanged), Toast.LENGTH_SHORT).show();

        goBacktoViewContact();
        //Booleans have been set to true,
        //when text has changed in our editTexts
        //and when checkboxes have changed in our adapter
        /*if ((PopulistoContactsAdapter.checkBoxhasChanged == true) || (editTexthasChanged == true)) {

          // if Booleans are true then
          //add a dialogue box
          AlertDialog.Builder builder = new AlertDialog.Builder(recyclerView.getContext());
          builder.setMessage("Save changes you made?").setPositiveButton("Yes", dialogClickListener)
              .setNegativeButton("No", dialogClickListener).show();


        } else {

          //we need to pass an intent going back to ViewContact
          //so text boxes and pub_or_priv will be filled
          //Intent intent = new Intent();
          Intent intent = new Intent(getApplicationContext(), ViewContact.class);
          intent.putExtra("categoryfromedit", category);
          //j.putExtra("category_id",  categoryid);
          intent.putExtra("namefromedit", name);
          intent.putExtra("phonefromedit", phone);
          intent.putExtra("addressfromedit", address);
          intent.putExtra("commentfromedit", comment);
          intent.putExtra("checkedContactsfromedit", checkedContactsfromViewContact);


          intent.putExtra("publicorprivatefromedit", public_or_private);

          startActivity(intent);

          finish();
        }*/
      }

    });

/*        testchecked = (CheckBox) findViewById(R.id.checkBoxContact);

        testchecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

         @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Toast.makeText(EditContact.this, "state changed", Toast.LENGTH_LONG).show();

               }
            }
        );*/

  }


  //for the SAVE contact
  private void saveContact() {

    //If permission denied (will only be on Marshmallow +)
    PackageManager manager = getPackageManager();
    int hasPermission = manager.checkPermission("android.permission.READ_CONTACTS", "com.example.chris.populisto");
    if (hasPermission == manager.PERMISSION_DENIED) {

      PopulistoContactsAdapter.checkedContactsAsArrayList.clear();

      //set public_or_private to Just Me
      public_or_private = 0;
    }

      //close the populistolistview class
    //(we'll be opening it again, will close now so it will be refreshed)
    PopulistoListView.fa.finish();

    pDialog = new ProgressDialog(EditContact.this);
    // Showing progress dialog for the review being saved
    pDialog.setMessage("Saving...");
    pDialog.show();

    try {
      System.out.println("we're in the try part");

      //clear checkedContactsAsArrayList and then add all checked contacts again
      //PopulistoContactsAdapter.checkedContactsAsArrayList.clear();


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
      System.out.println("EditContact: phoneOwner: " + phoneOwner);

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
            Toast.makeText(EditContact.this, "page is saving: " + phoneNoofUserCheck + " + " + review_id, Toast.LENGTH_SHORT).show();

           // Toast.makeText(EditContact.this, response, Toast.LENGTH_LONG).show();
          }
        },
        new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {
            //Toast.makeText(EditContact.this, "there's a problem saving this page", Toast.LENGTH_LONG).show();
            Toast.makeText(EditContact.this, "errorlistener toast: " + phoneNoofUserCheck + " + " + review_id, Toast.LENGTH_SHORT).show();

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
        params.put("public_or_private", valueOf(public_or_private));

        //this is the JSON Array of checked contacts
        //it will be of the form
        //[{"checkedContact":"+3531234567"},{"checkedContact":"+353868132813"}]
        params.put("checkedContacts", checkedContacts.toString());

        System.out.println("EditContact: checkedContacts are: " + checkedContacts.toString());


        Log.i("Adapter1", "public_or_private value when saved is: " + public_or_private);


        return params;

      }


    };


    AppController.getInstance().addToRequestQueue(stringRequest);

    //when saved, go to the PopulistoListView class and update with
    //the edited values
    Intent j = new Intent(EditContact.this, PopulistoListView.class);


    EditContact.this.startActivity(j);

    //refresh the populistolistview adapter, so when we go back
    //to that activity the recyclerview will be refreshed
    PopulistoListView.pAdapter.notifyDataSetChanged();

    // clear the checkbox state of checked contacts, we only want to keep it when the app resumes
    //SharedPreferences preferences = getSharedPreferences("sharedPrefsFile", 0);
    //preferences.edit().clear().commit();

    finish();
    //hide the dialogue box when page is saved
    hidePDialog();
  }


  //Are you sure you want to cancel? dialogue
  DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
      switch (which) {
        case DialogInterface.BUTTON_POSITIVE:
          Toast.makeText(EditContact.this, "Save pressed", Toast.LENGTH_SHORT).show();

          //Yes button clicked
          //save the contact
          saveContact();

          //exit out of the dialogInterface
          break;

        case DialogInterface.BUTTON_NEGATIVE:
          Toast.makeText(EditContact.this, "Cancel pressed", Toast.LENGTH_SHORT).show();

          //we need to pass an intent going back to ViewContact
          //so text boxes and pub_or_priv will be filled
          //Intent intent = new Intent();
          Intent intent = new Intent(getApplicationContext(), ViewContact.class);
          intent.putExtra("reviewfromedit", review_id);
          intent.putExtra("categoryfromedit", category);
          //j.putExtra("category_id",  categoryid);
          intent.putExtra("namefromedit", name);
          intent.putExtra("phonefromedit", phone);
          intent.putExtra("addressfromedit", address);
          intent.putExtra("commentfromedit", comment);
          intent.putExtra("checkedContactsfromedit", checkedContactsfromViewContact);

          //the old value, pub_or_priv, because logged-in user is not saving
          intent.putExtra("publicorprivatefromedit", pub_or_priv);

          startActivity(intent);

          finish();
      }
    }
  };

  //If going back to ViewContact
  //then bring the intents from EditContact
  private void goBacktoViewContact() {

    //Check READ_CONTACTS Permissions
    PackageManager manager = getPackageManager();
    int hasPermission = manager.checkPermission("android.permission.READ_CONTACTS", "com.example.chris.populisto");
    //If permission denied (will only be on Marshmallow +)
    //or when checkboxes have changed in our adapter or text changes
    if ((PopulistoContactsAdapter.checkBoxhasChanged == true) || (editTexthasChanged == true) || (hasPermission == manager.PERMISSION_DENIED)) {

      // if Booleans are true then
      //add a dialogue box
      AlertDialog.Builder builder = new AlertDialog.Builder(recyclerView.getContext());
      builder.setMessage("Save changes you made?").setPositiveButton("Yes", dialogClickListener)
          .setNegativeButton("No", dialogClickListener).show();

    } else {

      //we need to pass an intent going back to ViewContact
      //so text boxes and pub_or_priv will be filled
      //Intent intent = new Intent();
      Intent intent = new Intent(getApplicationContext(), ViewContact.class);
      intent.putExtra("reviewfromedit", review_id);
      intent.putExtra("categoryfromedit", category);
      //j.putExtra("category_id",  categoryid);
      intent.putExtra("namefromedit", name);
      intent.putExtra("phonefromedit", phone);
      intent.putExtra("addressfromedit", address);
      intent.putExtra("commentfromedit", comment);
      intent.putExtra("checkedContactsfromedit", checkedContactsfromViewContact);


      intent.putExtra("publicorprivatefromedit", public_or_private);

      Toast.makeText(EditContact.this, "review_id is: " + review_id, Toast.LENGTH_SHORT).show();

      startActivity(intent);

      finish();

    }

  }


  //code for the '<', back button. Go back to ViewContact
/*  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Toast.makeText(EditContact.this, "back pressed!", Toast.LENGTH_SHORT).show();

    Intent j = new Intent(getApplicationContext(), ViewContact.class);
        j.putExtra("category",  categoryname.getText());
        //j.putExtra("category_id",  categoryid);
        j.putExtra("name", namename.getText());
        j.putExtra("phone",  phonename.getText());
        j.putExtra("address",  addressname.getText());
        j.putExtra("comment",  commentname.getText());
    startActivity(j);
    finish();*/
/*
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    return true;
  }
*/

  //******for the phone contacts in the listview

  // Load data in background
  class LoadContact extends AsyncTask<Void, Void, Void> {
    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {



      //If permission denied (will only be on Marshmallow +)
      PackageManager manager = getPackageManager();
      int hasPermission = manager.checkPermission("android.permission.READ_CONTACTS", "com.example.chris.populisto");
      if (hasPermission == manager.PERMISSION_DENIED) {
        System.out.println("1. NoContactFoundCheck value:" + noContactFoundCheck);

        //and show the "No Results" textbox
        noContactFoundCheck = 1;

      }

      else {

        //don't show the No Contacts found box
        noContactFoundCheck = 0;

      }

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

      //if READ_CONTACTS is  disabled
      if (noContactFoundCheck == 1) {
        System.out.println("2. NoContactFoundCheck value:" + noContactFoundCheck);

        noContactsFound.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(GONE);
      } else {

        noContactFoundCheck = 0;
        noContactsFound.setVisibility(GONE);

        //we need to notify the listview that changes may have been made on
        //the background thread, doInBackground, like adding or deleting contacts,
        //and these changes need to be reflected visibly in the listview. It works
        //in conjunction with selectContacts.clear()
        adapter.notifyDataSetChanged();
      }
    }
  }


    @Override
    protected void onResume() {

        super.onResume();

/*          selectPhoneContacts.clear();

          EditContact.LoadContact loadContact = new EditContact.LoadContact();


          loadContact.execute();
        adapter.notifyDataSetChanged();*/
        Toast.makeText(EditContact.this, "resuming!", Toast.LENGTH_SHORT).show();


    }


  public void hidePDialog() {
    if (pDialog != null) {
      pDialog.dismiss();
      pDialog = null;
    }
  }


  //for the backbutton, remove the saved checkbox state
  //20/3/2018: DOES THIS STILL APPLY?
  //@Override
/*  public void onBackPressed() {
    // your code.
    Integer i = null;
    SharedPreferences preferences = getSharedPreferences("sharedPrefsFile", 0);
    preferences.edit().clear().commit();
    finish();
  }*/


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
        //Toast.makeText(EditContact.this, valueOf(public_or_private), Toast.LENGTH_LONG).show();

        PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, EditContact.this, 2);

        recyclerView.setAdapter(adapter);

        System.out.println("checkedContactsAsArrayList before clear:" + checkedContactsAsArrayList.size());

        //reset the size of the array to 0
/*        PopulistoContactsAdapter.checkedContactsAsArrayList.clear();

        System.out.println("checkedContactsAsArrayList after clear:" + checkedContactsAsArrayList.size());*/

        Toast.makeText(EditContact.this, "Publico: checkedcontacts cleared,contactToBeChecked called : ", Toast.LENGTH_SHORT).show();


        //If permission denied (will only be on Marshmallow +)
        PackageManager manager = getPackageManager();
        int hasPermission = manager.checkPermission("android.permission.READ_CONTACTS", "com.example.chris.populisto");
        if (hasPermission == manager.PERMISSION_DENIED) {

          //show No Results textbox
          noContactFoundCheck = 1;
        }

        else {

          checkedContactsAsArrayList.clear();

          noContactFoundCheck = 0;
          //loop through the matching contacts
          int count = PopulistoContactsAdapter.MatchingContactsAsArrayList.size();

          //i is the number of matching contacts that there are
          for (int i = 0; i < count; i++) {

            //add the matching contacts
            PopulistoContactsAdapter.checkedContactsAsArrayList.add(i, PopulistoContactsAdapter.MatchingContactsAsArrayList.get(i));

            //set them to be checked
            PopulistoContactsAdapter.theContactsList.get(i).setSelected(true);

            //we need to notify the recyclerview that changes may have been made
            adapter.notifyDataSetChanged();


          }

          //if checkboxes of contacts have been changed by clicking the button,
          //then set the boolean to be true
          PopulistoContactsAdapter.checkBoxhasChanged = true;

        }
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

        //keep the slightly rounded shape of the others, but still grey
        publicContacts.setBackgroundResource(R.drawable.buttonshape);
        justMeContacts.setBackgroundResource(R.drawable.buttonshape);

        //set sharing to Phone Contacts
        // This will be uploaded to server to review table,
        //public_or_private column, if saved in this state
        public_or_private = 1;
        //Toast.makeText(EditContact.this, "public_or_private is:" + valueOf(public_or_private), Toast.LENGTH_LONG).show();

        PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, EditContact.this, 2);

        recyclerView.setAdapter(adapter);

        //reset the size of the array to 0
        //PopulistoContactsAdapter.checkedContactsAsArrayList.clear();
        //Toast.makeText(EditContact.this, "Phone contacts: checkedcontacts cleared,contactToBeChecked called  : ", Toast.LENGTH_SHORT).show();


        //clear checkedContactsAsArrayList and then add all matching contacts again
        //PopulistoContactsAdapter.checkedContactsAsArrayList.clear();

        //If permission denied (will only be on Marshmallow +)
        PackageManager manager = getPackageManager();
        int hasPermission = manager.checkPermission("android.permission.READ_CONTACTS", "com.example.chris.populisto");
        if (hasPermission == manager.PERMISSION_DENIED) {

          System.out.println("NewContact: it is not in shared prefs" + allPhonesofContacts);
          //and show the "No Results" textbox
          noContactFoundCheck = 1;

        } else

        {


          noContactFoundCheck = 0;

/*
          //keep the slightly rounded shape, when the button is pressed
          phoneContacts.setBackgroundResource(R.drawable.phonecontacts_buttonshapepressed);

          //keep the slightly rounded shape of the others, but still grey
          publicContacts.setBackgroundResource(R.drawable.buttonshape);
          justMeContacts.setBackgroundResource(R.drawable.buttonshape);

          //set sharing to Phone Contacts
          // This will be uploaded to server to review table,
          //public_or_private column, if saved in this state
          public_or_private = 1;

          Toast.makeText(EditContact.this, "public_or_private is:" + valueOf(public_or_private), Toast.LENGTH_LONG).show();
*/


          //loop through the matching contacts
          int count = PopulistoContactsAdapter.MatchingContactsAsArrayList.size();

          //i is the number of matching contacts that there are
          for (int i = 0; i < count; i++) {

            PopulistoContactsAdapter.checkedContactsAsArrayList.add(i, PopulistoContactsAdapter.MatchingContactsAsArrayList.get(i));

            //check all matching contacts, we want it to be 'Phone Contacts'
            PopulistoContactsAdapter.theContactsList.get(i).setSelected(true);

            //we need to notify the recyclerview that changes may have been made
            adapter.notifyDataSetChanged();

            //if checkboxes of contacts have been changed by clicking the button,
            //then set the boolean to be true
            PopulistoContactsAdapter.checkBoxhasChanged = true;
          }
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

        //keep the slightly rounded shape of the others, but still grey
        publicContacts.setBackgroundResource(R.drawable.buttonshape);
        phoneContacts.setBackgroundResource(R.drawable.buttonshape);

        //set sharing to Just Me
        // This will be uploaded to server to review table,
        //public_or_private column, if saved in this state
        public_or_private = 0;
        Toast.makeText(EditContact.this, valueOf(public_or_private), Toast.LENGTH_LONG).show();

        PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, EditContact.this, 2);

        recyclerView.setAdapter(adapter);

        //reset the size of the array to 0
        PopulistoContactsAdapter.checkedContactsAsArrayList.clear();
        Toast.makeText(EditContact.this, "Just me: checkedcontacts cleared : ", Toast.LENGTH_SHORT).show();


        //If permission denied (will only be on Marshmallow +)
        //then show 'No Contacts Available' because
        PackageManager manager = getPackageManager();
        int hasPermission = manager.checkPermission("android.permission.READ_CONTACTS", "com.example.chris.populisto");
        if (hasPermission == manager.PERMISSION_DENIED) {

          //show the No Results Textbox
          noContactFoundCheck = 1;

        }


        else {
          //loop through the matching contacts
          int count = PopulistoContactsAdapter.MatchingContactsAsArrayList.size();

          for (int i = 0; i < count; i++) {

            //uncheck all matching contacts, we want it to be 'Just Me'
            theContactsList.get(i).setSelected(false);
            //we need to notify the recyclerview that changes may have been made*/
            adapter.notifyDataSetChanged();

            //if checkboxes of contacts have been changed by clicking the button,
            //then set the boolean to be true
            PopulistoContactsAdapter.checkBoxhasChanged = true;
          }

          //don't show the No Contacts Available box
          noContactFoundCheck = 0;

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
    public_or_private = 1;

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
    public_or_private = 0;

    //if checkboxes of contacts have been changed by clicking the button,
    //then set the boolean to be true
    PopulistoContactsAdapter.checkBoxhasChanged = true;


  }

  public void onDestroy() {

    super.onDestroy();
    PopulistoContactsAdapter.checkBoxhasChanged = false;
  }

}