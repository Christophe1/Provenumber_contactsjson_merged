package com.example.chris.tutorialspoint;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tutorialspoint.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PopulistoListView extends AppCompatActivity {

    // this is the php file name where to select from.
    // we will post the user's phone number into Php and get the matching user_id
    private static final String SelectUserReviews_URL = "http://www.populisto.com/SelectUserReviews.php";

    //we are posting phoneNoofUser, the key is phonenumberofuser, which we see in php
    public static final String KEY_PHONENUMBER_USER = "phonenumberofuser";

    // private static final String TAG = PopulistoListView.class.getSimpleName();


    // String phoneNoofUser;
    private ProgressDialog pDialog;
    private List<Review> reviewList = new ArrayList<Review>();
    private ListView listView;
    private CustomPopulistoListAdapter adapter;
    //private TextView textphonenumber;
    //private String strphone;
//private int reviewid;
    ArrayList allPhonesofContacts;
    ArrayList allNamesofContacts;
    String JsonArrayMatchingContacts;
    String jsonArrayAllPhoneContacts;
    String phoneNumberofContact;

    //declare an activity object so we can
    //call populistolistview and shut it down in ViewContact and NewContact
    //so that we will only have one instance of populistolistview
    public static Activity fa;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_listview_contacts);

        Toolbar toolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        //why isn't title being set!?
        toolbar.setTitle("Search...");

        // textphonenumber = (TextView) findViewById(R.id.textView3);

        //populistolistview is the activity object
        fa = this;
        recyclerView = (RecyclerView) findViewById(R.id.list);
        final CustomPopulistoListAdapter adapter = new CustomPopulistoListAdapter(reviewList, this);
        recyclerView.setAdapter(adapter);

        //no need for this anymore as it is dealt with in the new
        //toolbar widget.
        //always show the overflow menu. Some devices don't show it by default
        //This function is in the GlobalFunctions class
        //GlobalFunctions.makeActionOverflowMenuShown(PopulistoListView.this);

        //get the phone number value from shared preferences file instead
        //of from the VerifiedUserPhoneNumber class because we might not
        //be coming from that class, for example on Edit, New etc. The phone
        //number needs to be posted for this listview to load properly.
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        final String phoneNoofUser = sharedPreferences.getString(KEY_PHONENUMBER_USER, "");

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        //post the phone number of the logged in user to SelectUserReviews.php and from that
        //get the logged in user's reviews
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SelectUserReviews_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hide the 'loading' box when the page loads
                        hidePDialog();
                        //toast the response of SelectUserReviews.php, which has been converted to a
                        //JSON array in the SelectUserReviews.php file with JSON encode
                        Toast.makeText(PopulistoListView.this, response, Toast.LENGTH_LONG).show();
                        System.out.println("the review list array is :" + response);

                        try {
                            //name our JSONArray responseObject
                            JSONArray responseObject = new JSONArray(response);


                            for
                                //get the number of objects in the Array
                                    (int i = 0; i < responseObject.length(); i++) {
                                //for each object in the array, name it obj
                                //each obj will consist of reviewid, category, name, phone,comment
                                JSONObject obj = responseObject.getJSONObject(i);
                                // and create a new review, getting details of user's reviews in the db
                                Review review = new Review();
                                //we are getting the reviewid so we can pull extra matching info,
                                review.setReviewid(obj.getString("reviewid"));
                                //set the category part of the object to that matching reviewid
                                review.setCategory(obj.getString("category"));
                                //etc...
                                review.setName(obj.getString("name"));
                                review.setPhone(obj.getString("phone"));
                                review.setComment(obj.getString("comment"));

                                //add the review to the reviewList
                                reviewList.add(review);

                            }
                        } catch (JSONException e) {
                            Log.e("MYAPP", "unexpected JSON exception", e);
                            // Do something to recover ... or kill the app.
                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                         adapter.notifyDataSetChanged();

                        // System.out.println("size of reviewlist " + reviewList.size());
                        System.out.println("heree it is" + reviewList.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PopulistoListView.this, error.toString(), Toast.LENGTH_LONG).show();

                    }

                }) {
            @Override
            //post info to php
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //phoneNoofUser is the value we get from Android, the user's phonenumber.
                //the key is "phonenumberofuser". When we see "phonenumberofuser" in our php,
                //put in phoneNoofUser
                params.put(KEY_PHONENUMBER_USER, phoneNoofUser);
                return params;

            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //cast the getItem(position) return value to a review object
                Review review = (Review) adapter.getItem(position);
                //we want to pass the review_id of the review being clicked
                //to the ViewContact activity, and from there post it and get more
                //info for that review - address, comments etc
                Intent i = new Intent(PopulistoListView.this, ViewContact.class);
                //pass the review_id to ViewContact class
                i.putExtra("review_id", review.getReviewid());
                startActivity(i);

                Toast.makeText(PopulistoListView.this, "PopulistoListView: the review id is " + review.getReviewid(), Toast.LENGTH_SHORT).show();
               // finish();
            }


        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    public void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_contact:
                //start the NewContact class
                Intent intent = new Intent(PopulistoListView.this, NewContact.class);

                //pass allPhonesofContacts Array to NewContact, so we can display in ListView
                //intent.putExtra("allPhonesofContacts", allPhonesofContacts);

                //pass allNamesofContacts Array to NewContact, so we can display in ListView
                //intent.putExtra("allNamesofContacts", allNamesofContacts);

                //also take the names and numbers of contacts, so they'll be displayed in the list view
                //intent.putExtra("jsonArrayAllPhonesandNamesofContacts", jsonArrayAllPhonesandNamesofContacts);

                //also take the matching contacts, so they'll be displayed in the list view
                //intent.putExtra("JsonArrayMatchingContacts", JsonArrayMatchingContacts);
                startActivity(intent);
                return true;
        }
        return false;
    }

    @Override
    protected void onResume() {

        super.onResume();
        adapter.notifyDataSetChanged();

    }

}