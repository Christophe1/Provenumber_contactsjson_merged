package com.example.chris.tutorialspoint;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chris.tutorialspoint.SharedReviews.SharedPopulistoReviewsAdapter;
import com.example.chris.tutorialspoint.SharedReviews.SharedReview;
import com.example.tutorialspoint.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PopulistoListView extends AppCompatActivity implements CategoriesAdapter.CategoriesAdapterListener {

    private static final String TAG = PopulistoListView.class.getSimpleName();
    public static RecyclerView recyclerView;


    // this is the php file for showing all logged in (own user's) reviews in the recyclerView.
    //First thing we see when app loads.
    // we will post the user's phone number into Php and get the matching user_id
    //and all values in the reviews
    private static final String SelectUserReviews_URL = "http://www.populisto.com/SelectUserReviews.php";

    //this is the url for loading the categories shared with the logged-in user
    //it returns a JSON Array of categories shared with the user -
    //in this format:
    //[{"cat_name":"vet",
    // "user_review_ids":[2],
    // "private_review_ids":[],
    // "public_review_ids":[],
    // "user_personal_count":1,
    // "private_count":0,
    // "public_count":0}, etc
    private static final String CategoryFilter_URL = "http://www.populisto.com/CategoryFilter.php";

    //this is for showing ALL reviews available to logged-in user when a category is searched.
    // we will post the selected (clicked on) category into Php and get the
    //reviews that are shared with the logged in user
    //It returns a JSON Array of this format: {"user_review_ids":[1,3],"private_review_ids":[2],"public_review_ids":[8,12]}
    private static final String User_Private_Public_Reviews_URL = "http://www.populisto.com/User_Private_Public_Reviews.php";

    //when searchView has focus and user types, we will be showing/filtering
    //categories
    private List<Category> categoryList = new ArrayList<Category>();
    //this is the adapter for categories, loading from the searchView
    private CategoriesAdapter mAdapter;


    //we are posting phoneNoofUser (logged-in user's own number), the key is phonenumberofuser, which we see in php
    public static final String KEY_PHONENUMBER_USER = "phonenumberofuser";

    // the key is reviewiduser, which we see in php
    //this is for user's own reviews
    //We will be posting the string selectOwnUserReviews, which will be
    // exploded, and the values for review_ids
    //will be got individually
    public static final String KEY_REVIEWID_USER = "reviewiduser";

    // the key is reviewidprivate, which we see in php
    //this is for phone contact reviews
    public static final String KEY_REVIEWID_PRIVATE = "reviewidprivate";

    // the key is reviewidpublic, which we see in php
    //this is for public reviews
    public static final String KEY_REVIEWID_PUBLIC = "reviewidpublic";

    private ProgressDialog pDialog;

    private List<Review> reviewList = new ArrayList<Review>();

    private List<SharedReview> sharedReviewList = new ArrayList<SharedReview>();

    //this is the adapter for user's reviews
    public UPopulistoListAdapter pAdapter;


    //this is the adapter for shared reviews including user's own
    public SharedPopulistoReviewsAdapter qAdapter;

    //declare an activity object so we can
    //call populistolistview and shut it down in ViewContact and NewContact
    //so that we will only have one instance of populistolistview
    public static Activity fa;


    private SearchView searchView;

    //phoneNoofUser is stored in Shared Prefs at the VerifyUserPhoneNumber user stage
    String phoneNoofUser;

    //selectOwnUserReviews is to hold the own user's reviews for a fetched category.
    //When a category is clicked we get the review ids that are shared with logged-in user for this
    //particular category
    //we fetch the review ids from php as an array like [23,65,67] and
    //remove [ and ] so 23,65,67 will be sent to User_Private_Public_Reviews.php and exploded, getting corresponding
    //categories for each of 23, 65 and 67
    String selectOwnUserReviews;

    String selectPrivateReviews;

    String selectPublicReviews;

    //to decide colour of "U" in phone_user_name
    public int pub_or_priv;

    private TextView phone_user_name;

    //public static ArrayList<String> MatchingContactsAsArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_listview_contacts);

        //set the layout for the searchview widget
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // toolbar
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_title);


        //get the own user's phone number value from shared preferences file instead
        //of from the VerifiedUserPhoneNumber class because we might not
        //be coming from that class, for example on Edit, New etc. The phone
        //number needs to be posted for this recyclerView to load properly.
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        phoneNoofUser = sharedPreferences.getString(KEY_PHONENUMBER_USER, "");


/*        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String json_array = sharedPrefs.getString("AllPhonesandNamesofContacts", "0");

        try

        {
            JSONArray jsonArray = new JSONArray(json_array);
            System.out.println("the jsonarray is :" + jsonArray);


        } catch (JSONException e) {
            Log.e("MYAPP", "unexpected JSON exception", e);
            // Do something to recover ... or kill the app.
        }*/

        // Toast.makeText(PopulistoListView.this, jsonArray, Toast.LENGTH_LONG).show();


        //we are fetching the array list MatchingContactsAsArrayList, created in VerifyUserPhoneNumber.
        //we want to put the name of the user who made the review alongside the review
/*
        SharedPreferences sharedPreferencesMatchingContactsAsArrayList = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gsonMatchingContactsAsArrayList = new Gson();
        String jsonMatchingContactsAsArrayList = sharedPreferencesMatchingContactsAsArrayList.getString("MatchingContactsAsArrayList", "");
        Type type1 = new TypeToken<ArrayList<String>>() {
        }.getType();
        MatchingContactsAsArrayList = gsonMatchingContactsAsArrayList.fromJson(jsonMatchingContactsAsArrayList, type1);
        System.out.println("PopulistoListView MatchingContactsAsArrayList :" + MatchingContactsAsArrayList);
*/


        //why isn't title being set!?
        //getSupportActionBar().setTitle("Search...");

        //cast a TextView for each of the field ids in activity_view_contact.xml
        // phone_user_name = (TextView) findViewById(R.id.phone_user_name);

        //populistolistview is the activity object
        fa = this;
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        //the adapter for all own user reviews
        pAdapter = new UPopulistoListAdapter(reviewList);

        //the adapter for filtering categories
        mAdapter = new CategoriesAdapter(this, categoryList, this);

        //the adapter for all shared reviews including user's own
        qAdapter = new SharedPopulistoReviewsAdapter(sharedReviewList, this);

        // white background notification bar
        //whiteNotificationBar(recyclerView);


        // final CustomPopulistoListAdapter adapter = new CustomPopulistoListAdapter(sharedReviewList, this);
        // recyclerView.setAdapter(adapter);

        //set the layout
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(pAdapter);

        Log.e(TAG, "phonno" + phoneNoofUser);

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
                        //Toast.makeText(PopulistoListView.this, response, Toast.LENGTH_LONG).show();
                        //System.out.println("the review list array is :" + response);

                        try {
                            //name our JSONArray responseObject
                            JSONArray responseObject = new JSONArray(response);

                            for
                                //get the number of objects in the Array
                                    (int i = 0; i < responseObject.length(); i++) {
                                //for each object in the array, name it obj
                                //each obj will consist of reviewid, category, name, phone,comment
                                JSONObject obj = responseObject.getJSONObject(i);
                                // and create a new reviewUser, getting details of user's reviews in the db
                                Review review = new Review();

                                //get 0,1 or 2 value, for Just U, private or public
                                review.setPublicorprivate(obj.getString("publicorprivate"));
                                //we are getting the reviewid so we can pull extra matching info,
                                review.setReviewid(obj.getString("reviewid"));

                                //convert public_or_private to an integer
                                pub_or_priv = Integer.parseInt(obj.getString("publicorprivate"));

                                //set the category part of the object to that matching reviewid
                                review.setCategory(obj.getString("category"));
                                //etc...
                                review.setName(obj.getString("name"));
                                review.setPhone(obj.getString("phone"));
                                review.setComment(obj.getString("comment"));

                                //  Toast.makeText(PopulistoListView.this, obj.getString("publicorprivate"), Toast.LENGTH_LONG).show();


                                //add the reviewUser to the sharedReviewList
                                reviewList.add(review);

                            }
                        } catch (JSONException e) {
                            Log.e("MYAPP", "unexpected JSON exception", e);
                            // Do something to recover ... or kill the app.
                        }


                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        pAdapter.notifyDataSetChanged();

                        // System.out.println("size of reviewlist " + sharedReviewList.size());
                        System.out.println("heree it is" + sharedReviewList.size());
                        System.out.println("heree it is" + sharedReviewList.toString());
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

    }

/*    public int getbs(){
        return pub_or_priv;
    }*/

    //this is the function for filtering categories in the searchView
    //it is called onQueryTextChange
    private void fetchContacts() {

        StringRequest request = new StringRequest(Request.Method.POST, CategoryFilter_URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        // Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();

                        //response will be like:

                        //[{"cat_name":"vet",
                        // "user_review_ids":[2],
                        // "private_review_ids":[],
                        // "public_review_ids":[],
                        // "user_personal_count":1,
                        // "private_count":0,
                        // "public_count":0}, etc

                        try {

                            //not sure exactly what is happening here
                            //something to do with converting the response to a list and setting it to
                            //our Category object...whatever that means
                            List<Category> items = new Gson().fromJson(response.toString(), new TypeToken<List<Category>>() {
                            }.getType());

                            //clear the list
                            categoryList.clear();

                            // adding contacts to contacts list
                            categoryList.addAll(items);

                            //app not crashing as much with this here
                            recyclerView.setAdapter(mAdapter);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })

        {
            @Override
            //post info to php
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //phoneNoofUser is the value we get from Android, the user's phonenumber.
                //the key,KEY_PHONENUMBER_USER, is "phonenumberofuser". When we see "phonenumberofuser" in our php,
                //put in phoneNoofUser
                params.put(KEY_PHONENUMBER_USER, phoneNoofUser);
                return params;

            }
        };
        AppController.getInstance().addToRequestQueue(request);
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


        getMenuInflater().inflate(R.menu.menu, menu);

        // Associate searchable configuration with the SearchView
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        //Clear query
        searchView.setQuery("", false);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextSubmit(String query) {

                // hidePDialog();

                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                //WILL CRASH IF UNCOMMENTED
                //recyclerView.setAdapter(mAdapter);
                //sharedReviewList.clear();

                //if the searchView is empty
                if (searchView.getQuery().length() == 0) {

                    //show the reviews, not the searched categories
                    recyclerView.setAdapter(pAdapter);
                    pAdapter.notifyDataSetChanged();
                } else {
                    //if there's text in the search box
                    fetchContacts();
                    //Log.e(TAG, "phonno2 is: " + phoneNoofUser);
                    // filter recycler view when text is changed
                    mAdapter.getFilter().filter(query);
                }
                return false;

            }
        });

        return true;


        // Inflate the menu items for use in the action bar
/*        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.new_contact:
                //start the NewContact class
                Intent intent = new Intent(PopulistoListView.this, NewContact.class);

                startActivity(intent);
                return true;
        }
        return false;
    }

/*    @Override
    protected void onResume() {

        super.onResume();
        adapter.notifyDataSetChanged();

    }*/


    @Override
    //when a fetched category in recyclerView is clicked...
    public void onContactSelected(Category category) {

        //convert [56,23,87] to a string
        selectOwnUserReviews = Arrays.toString(category.getUserReviewIds());
        //remove [ and ] so we have a string of 56,23,87
        selectOwnUserReviews = selectOwnUserReviews.substring(1, selectOwnUserReviews.length() - 1);

        //convert [56,23,87] to a string
        selectPrivateReviews = Arrays.toString(category.getPrivateReviewIds());
        //remove [ and ] so we have a string of 56,23,87
        selectPrivateReviews = selectPrivateReviews.substring(1, selectPrivateReviews.length() - 1);

        //convert [56,23,87] to a string
        selectPublicReviews = Arrays.toString(category.getPublicReviewIds());
        //remove [ and ] so we have a string of 56,23,87
        selectPublicReviews = selectPublicReviews.substring(1, selectPublicReviews.length() - 1);


        // Toast.makeText(getApplicationContext(), selectOwnUserReviews + " and " + selectPrivateReviews, Toast.LENGTH_LONG).show();

        show_own_private_public_Reviews();

    }

    //when a fetched category in recyclerView is clicked, do this function
    private void show_own_private_public_Reviews() {

        //post selectOwnUserReviews string (and private and public) to
        // User_Private_Public_Reviews_URL.php and from that
        //get the reviews details that belong to those users for the specific category
        StringRequest stringRequest = new StringRequest(Request.Method.POST, User_Private_Public_Reviews_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show();

                        //clear the list of shared reviews, start afresh on new filter
                        sharedReviewList.clear();

                        // and create a new sharedReview, getting details of user's reviews in the db
                        //  SharedReview sharedReview = new SharedReview();

   /*                     JSONArray jsonArray  = new JSONArray();

                        //username is in fact a phone number from the db
                        String username = sharedReview.getPhone_user_name();

                        int matching = jsonArray.length();
                        for (int i = 0; i < matching; i++) {

                            try {

                                JSONObject object = jsonArray.getJSONObject(i);

                                if (object.getString("phone_number").equals(username))
                                    //Toast.makeText(context_type,object.getString("phone_number"), Toast.LENGTH_SHORT).show();

                                {

                                    //phone_user_name = (object.getString("name"));

                                }
                            } catch (JSONException e) {
                                Log.e("MYAPP", "unexpected JSON exception", e);
                                // Do something to recover ... or kill the app.
                            }
                        }*/


                        //hide the 'loading' box when the page loads
                        // hidePDialog();

                        try {

                            //name our JSONObject User_Private_Public_Obj, which is response from server
                            JSONObject User_Private_Public_Obj = new JSONObject(response);

                            //Now break up the response.
                            //for the JSON Array user_review_ids
                            JSONArray own_ids = User_Private_Public_Obj.getJSONArray("user_review_ids");

                            //for the JSON Array private_review_ids
                            JSONArray private_ids = User_Private_Public_Obj.getJSONArray("private_review_ids");

                            for
                                //get the number of objects in the array own_ids
                                    (int i = 0; i < own_ids.length(); i++)

                            {

                                //for each object in the array own_ids, name it obj
                                //each obj will consist of reviewid, category, name, phone,comment
                                JSONObject obj = own_ids.getJSONObject(i);

                                // and create a new sharedReview, getting details of user's reviews in the db
                                SharedReview sharedReview = new SharedReview();

                                sharedReview.setphoneNameonPhone("phone user name damn it!");

                                sharedReview.setPhoneNumberofUserFromDB(obj.getString("username"));

                                //get 0,1 or 2 value from db, for Just U, private or public
                                sharedReview.setPublicorprivate(obj.getString("publicorprivate"));
                                //we are getting the reviewid from the db so we can pull extra matching info,
                                sharedReview.setReviewid(obj.getString("reviewid"));
                                //set the category part of the object to that matching reviewid
                                sharedReview.setCategory(obj.getString("category"));
                                //etc...
                                sharedReview.setName(obj.getString("name"));
                                sharedReview.setPhone(obj.getString("phone"));
                                sharedReview.setComment(obj.getString("comment"));

                                //add the sharedReview to the sharedReviewList
                                sharedReviewList.add(sharedReview);

                            }


                            for
                                //get the number of objects in User_Private_Public_Obj
                                    (int i = 0; i < private_ids.length(); i++)

                            {

                                //for each object in the array private_ids, name it obj
                                //each obj will consist of reviewid, category, name, phone,comment
                                JSONObject obj = private_ids.getJSONObject(i);

                                /*String own_ids=obj.getString("user_review_ids");
                                String private_ids=obj.getString("private_review_ids");
                                String public_ids=obj.getString("public_review_ids");*/


                                //for each object in the array, name it obj
                                //each obj will consist of reviewid, category, name, phone,comment
                                //JSONObject obj = responseObject.getJSONObject(i);
                                // and create a new sharedReview, getting details of user's reviews in the db


                                SharedReview sharedReview = new SharedReview();


                                //JSONArray jsonArray = new JSONArray();

                                //String bobby = "muttface";

                                //phoneNumberOnPhone is a phone number from the db
                                //sharedReview.setPhoneNumberofUserFromDB(bobby);


                                String phoneNameonPhone = "";





                                SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                                String json_array = sharedPrefs.getString("AllPhonesandNamesofContacts", "0");

                                JSONArray jsonArray = new JSONArray(json_array);

                                Toast.makeText(PopulistoListView.this, "wwaaas" + json_array.toString(), Toast.LENGTH_SHORT).show();


                                int matching = jsonArray.length();
                                for (int n = 0; n < matching; n++) {

                                   // String bobby = sharedReview.getPhoneNumberofUserFromDB();

                                    try {

                                        JSONObject object = jsonArray.getJSONObject(n);


                                        //if (object.getString("phone_number").contains("+353864677745"))

                                        if (object.getString("phone_number").contains(sharedReview.getPhoneNumberofUserFromDB()))
                                        //Toast.makeText(PopulistoListView.this, jsonArray.toString(), Toast.LENGTH_SHORT).show();

                                        //Toast.makeText(PopulistoListView.this,object.getString("phone_number"), Toast.LENGTH_SHORT).show();

                                        {

                                            phoneNameonPhone = (object.getString("name"));

                                        }
                                    } catch (JSONException e) {
                                        Log.e("MYAPP", "unexpected JSON exception", e);
                                        // Do something to recover ... or kill the app.
                                    }
                                }


                                sharedReview.setphoneNameonPhone(phoneNameonPhone);
                                //Toast.makeText(PopulistoListView.this, (sharedReview.setphoneNameonPhone(bobby)), Toast.LENGTH_SHORT).show();

                                //get 0,1 or 2 value, for Just U, private or public
                                sharedReview.setPublicorprivate(obj.getString("publicorprivate"));
                                //we are getting the reviewid so we can pull extra matching info,
                                sharedReview.setReviewid(obj.getString("reviewid"));
                                //set the category part of the object to that matching reviewid
                                sharedReview.setCategory(obj.getString("category"));
                                //etc...
                                sharedReview.setName(obj.getString("name"));
                                sharedReview.setPhone(obj.getString("phone"));
                                sharedReview.setComment(obj.getString("comment"));

                                //add the sharedReview to the sharedReviewList
                                sharedReviewList.add(sharedReview);

                            }


                            //set the adapter to show shared reviews
                            recyclerView.setAdapter(qAdapter);

                        } catch (JSONException e) {
                            Log.e("MYAPP", "unexpected JSON exception", e);
                            // Do something to recover ... or kill the app.
                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        qAdapter.notifyDataSetChanged();

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
                //selectOwnUserReviews is a string of_review_ids of reviews, created by logged-in user,
                // for the clicked category in filter.
                //KEY_REVIEWID_USER is "reviewiduser". When we see "reviewiduser" in our php,
                //put in selectOwnUserReviews, which will be exploded, and the values for review_ids
                //will be got individually
                params.put(KEY_REVIEWID_USER, selectOwnUserReviews);
                params.put(KEY_REVIEWID_PRIVATE, selectPrivateReviews);
                params.put(KEY_REVIEWID_PUBLIC, selectPublicReviews);

                return params;

            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

}