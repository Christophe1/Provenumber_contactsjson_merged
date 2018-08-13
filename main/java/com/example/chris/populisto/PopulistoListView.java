package com.example.chris.populisto;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chris.populisto.SharedReviews.SharedPopulistoReviewsAdapter;
import com.example.chris.populisto.SharedReviews.SharedReview;
import com.example.tutorialspoint.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.chris.populisto.PopulistoContactsAdapter.MatchingContactsAsArrayList;

public class PopulistoListView extends AppCompatActivity implements CategoriesAdapter.CategoriesAdapterListener {

  private static final String TAG = PopulistoListView.class.getSimpleName();
  public static RecyclerView recyclerView;

  //for if no categories are displayed in recyclerView
  TextView noResultsFoundView;

  //private EditText testy;


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

  //this is for showing ALL reviews available to logged-in user when a category is clicked.
  // we will post the selected (clicked on) category into Php and get the
  //reviews that are shared with the logged in user
  //It returns a JSON Array of this format: {"user_review_ids":[1,3],"private_review_ids":[2],"public_review_ids":[8,12]}
  private static final String User_Private_Public_Reviews_URL = "http://www.populisto.com/User_Private_Public_Reviews.php";

  //when searchView has focus and user types, we will be showing/filtering
  //categories
  private List<Category> categoryList = new ArrayList<Category>();
  //public List<Category> categoryListFiltered;

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

  //private ProgressDialog pDialog;

  DelayedProgressDialog progressDialog = new DelayedProgressDialog();

  private List<Review> reviewList = new ArrayList<Review>();

  private List<SharedReview> sharedReviewList = new ArrayList<SharedReview>();

  //this is the adapter for user's reviews
  public static UPopulistoListAdapter pAdapter;


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
  //When a category is clicked we get the review ids that are shared with logged-in
  // user for this
  //particular category
  //we fetch the review ids from php as an array like [23,65,67] and
  //remove [ and ] so 23,65,67 will be sent to User_Private_Public_Reviews.php
  // and exploded,
  // getting corresponding
  //categories for each of 23, 65 and 67
  String selectOwnUserReviews;

  String selectPrivateReviews;

  String selectPublicReviews;

  //to decide colour of "U" in phone_user_name
  public int pub_or_priv;

  //set a string to the the phone number from the DB,
  //the phone number of the person who made the review
  String phoneNoInDB;

  List<Category> items;
  String the_response;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.phone_listview_contacts);


    //set the layout for the toolbar
    //searchview will appear inside of this
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);


    // toolbar
    //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    //getSupportActionBar().setTitle(R.string.toolbar_title);


    //get the own user's phone number value from shared preferences file instead
    //of from the VerifiedUserPhoneNumber class because we might not
    //be coming from that class, for example on Edit, New etc. The phone
    //number needs to be posted for this recyclerView to load properly.
    SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
    phoneNoofUser = sharedPreferences.getString(KEY_PHONENUMBER_USER, "");


    //we are fetching the array list MatchingContactsAsArrayList, created in VerifyUserPhoneNumber.
    //we want to put the name of the user who made the review alongside the review
    SharedPreferences sharedPreferencesMatchingContactsAsArrayList = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    Gson gsonMatchingContactsAsArrayList = new Gson();
    String jsonMatchingContactsAsArrayList = sharedPreferencesMatchingContactsAsArrayList.getString("MatchingContactsAsArrayList", "");
    Type type1 = new TypeToken<ArrayList<String>>() {
    }.getType();
    MatchingContactsAsArrayList = gsonMatchingContactsAsArrayList.fromJson(jsonMatchingContactsAsArrayList, type1);
    System.out.println("PopulistoListView MatchingContactsAsArrayList :" + MatchingContactsAsArrayList);

    //why isn't title being set!?
    //getSupportActionBar().setTitle("Search...");

    //cast a TextView for each of the field ids in activity_view_contact.xml
    // phone_user_name = (TextView) findViewById(R.id.phone_user_name);

    //populistolistview is the activity object
    fa = this;
    recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

    noResultsFoundView = (TextView) findViewById(R.id.noResultsFoundView);


    //the adapter for all own user reviews
    pAdapter = new UPopulistoListAdapter(reviewList);

    //the adapter for filtering categories
    mAdapter = new CategoriesAdapter(this, categoryList, this);

    //the adapter for all shared reviews including user's own
    qAdapter = new SharedPopulistoReviewsAdapter(sharedReviewList, this);

    //set the layout
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

    recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    //recyclerView.setLayoutManager(mLayoutManager);

    recyclerView.setAdapter(pAdapter);

    Log.e(TAG, "phonno" + phoneNoofUser);

    //pDialog = new ProgressDialog(this);
    // Showing progress dialog before making http request to get user's reviews

    //show the "Loading" dialog
    progressDialog.show(getSupportFragmentManager(), "tag");

    //pDialog.setMessage("Loading...");
    //pDialog.show();

/*    testy = (EditText) findViewById(R.id.action_search);

    testy.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        String str = charSequence.toString();
        //if it starts with " " then don't recognise it
        if(str.equals(" "))
        {
          testy.setText("");
        }

      }

      @Override
      public void afterTextChanged(Editable editable) {

      }
    });*/

    //searchView.setOnQueryTextListener(new TextWatcher());

    //searchView.setOnQueryTextListener();

    //post the phone number of the logged in user to SelectUserReviews.php and from that
    //get the logged in user's reviews
    StringRequest stringRequest = new StringRequest(Request.Method.POST, SelectUserReviews_URL,
        new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {

            //dismiss the dialog when we get the response
            progressDialog.cancel();

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
                review.setAddress(obj.getString("address"));
                review.setComment(obj.getString("comment"));

                //set a string to the the phone number from the DB,
                //the phone number of the person who made the review
                // phoneNoInDB = phoneNoofUser;
                //set the setter to
                //the phone number of the person who made the review
                review.setPhoneNumberofUserFromDB(phoneNoofUser);

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
            Toast.makeText(PopulistoListView.this, "Trouble cantacting the server right now :(", Toast.LENGTH_LONG).show();

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

    //this is to hopefully end the VolleyTimeOut error message
    stringRequest.setRetryPolicy(new RetryPolicy() {
      @Override
      public int getCurrentTimeout() {
        return 50000;
      }

      @Override
      public int getCurrentRetryCount() {
        return 50000;
      }

      @Override
      public void retry(VolleyError error) throws VolleyError {

      }
    });

    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(stringRequest);


  }


  //this is the function for filtering categories in the searchView
  //it is called onQueryTextChange
  private void fetchCategories() {

    StringRequest request = new StringRequest(Request.Method.POST, CategoryFilter_URL,
        new Response.Listener<String>() {


          @Override
          public void onResponse(String response) {

            //hide the loading dialog when we get a response
            //hidePDialog();

            //Toast.makeText(getApplicationContext(), "loaded categoryfilter file" + response, Toast.LENGTH_SHORT).show();

            the_response = response;
            //response will be like:

            //[{"cat_name":"vet",
            // "user_review_ids":[2],
            // "private_review_ids":[3,4,5],
            // "public_review_ids":[6,7,8,9,10],
            // "user_personal_count":1,
            // "private_count":3,
            // "public_count":5}, etc

            //try {



/*              //items is a list of the category names available to the logged-in user
              items = new Gson().fromJson(response.toString(), new TypeToken<List<Category>>() {
              }.getType());*/

/*            //clear the list
            categoryList.clear();
              //adding categories to category list
              categoryList.addAll(items);*/

            //Toast.makeText(getApplicationContext(), "mAdapter, no category results", Toast.LENGTH_SHORT).show();


            // System.out.println("categoryList size is :" + CategoriesAdapter.categoryListFiltered.size());

            //app not crashing as much with this here
            // recyclerView.setAdapter(mAdapter);

            //hidePDialog();

            //if there are no category results for what is typed, with each key press...
/*              if (mAdapter.getItemCount() < 1) {

                Toast.makeText(getApplicationContext(), "mAdapter is 0, no category results", Toast.LENGTH_SHORT).show();


                recyclerView.setVisibility(View.GONE);
                noResultsFoundView.setVisibility(View.VISIBLE);

                 //hidePDialog();

              } else {

                //if there ARE category results for what is typed, with each key press...
                Toast.makeText(getApplicationContext(), "mAdapter, show results", Toast.LENGTH_SHORT).show();

                recyclerView.setVisibility(View.VISIBLE);
                noResultsFoundView.setVisibility(View.GONE);

                // hidePDialog();
              }*/

            //}
          /*catch (Exception e) {
              e.printStackTrace();
            }*/


/*            //when text changes in the searchview, show the 'Loading' dialogue
            pDialog = new ProgressDialog(PopulistoListView.this);
            // Showing progress dialog before making http request to get user's reviews
            pDialog.setMessage("Loading...");
            pDialog.show();*/

            // refreshing recycler view
            // mAdapter.notifyDataSetChanged();
          }


        }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        // error in getting json
        Log.e(TAG, "Error: " + error.getMessage());
        Toast.makeText(getApplicationContext(), "Value is null, but why? " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    //this is to hopefully end the VolleyTimeOut error message
    request.setRetryPolicy(new RetryPolicy() {
      @Override
      public int getCurrentTimeout() {
        return 50000;
      }

      //set retry to 5 seconds
      @Override
      public int getCurrentRetryCount() {
        return 50000;
      }

      @Override
      public void retry(VolleyError error) throws VolleyError {

      }
    });


    AppController.getInstance().addToRequestQueue(request);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    //hidePDialog();
    //dismiss the dialog when we get the response
    progressDialog.cancel();
  }

  //9/8/2018
/*  public void hidePDialog() {
    if (pDialog != null) {
      pDialog.dismiss();
      pDialog = null;
    }
  }*/

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


    //When searchview has focus....
    searchView.setOnSearchClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Toast.makeText(getApplicationContext(), "clickety!", Toast.LENGTH_SHORT).show();
        fetchCategories();
      }
    });


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

/*        //when text changes in the searchview, show the 'Loading' dialogue
        pDialog = new ProgressDialog(PopulistoListView.this);
        // Showing progress dialog before making http request to get user's reviews
        pDialog.setMessage("Loading...");
        pDialog.show();*/

          //items is the complete list of the category names available to the logged-in user
          items = new Gson().fromJson(the_response, new TypeToken<List<Category>>() {
          }.getType());

          //clear the list every time a key is pressed
          categoryList.clear();


          //was getting NullPointerException so put this here...
          if (items != null && items.size() > 0) {
            //adding categories to category list
            categoryList.addAll(items);
          }

          // recyclerView.setAdapter(mAdapter);

          mAdapter.getFilter().filter(query);

          // mAdapter.notifyDataSetChanged();

          recyclerView.setAdapter(mAdapter);

          if (mAdapter.getItemCount() < 1) {

            //if there's nothing to show, hide recyclerView...
            recyclerView.setVisibility(View.GONE);

            //and show the "No Results" textbox
            noResultsFoundView.setVisibility(View.VISIBLE);
           // mAdapter.notifyDataSetChanged();
            //hidePDialog();

            Toast.makeText(getApplicationContext(), "mAdapter is 0, no item results", Toast.LENGTH_SHORT).show();



          }

        if (mAdapter.getItemCount() >= 1) {

            recyclerView.setVisibility(View.VISIBLE);
            noResultsFoundView.setVisibility(View.GONE);
            //mAdapter.notifyDataSetChanged();
            // hidePDialog();

            //if there ARE category results for what is typed, with each key press...
            Toast.makeText(getApplicationContext(), "mAdapter size is:" + mAdapter.getItemCount(), Toast.LENGTH_SHORT).show();


          }


          //WILL CRASH IF UNCOMMENTED
          //recyclerView.setAdapter(mAdapter);
          //sharedReviewList.clear();
          //String str = query.toString();
          // searchView.getText().toString().trim();


    /*    String str = query.toString();
        //if it starts with " " then don't recognise it
        if(str.equals(" "))
        {
          searchView.setQuery("",false);
        }*/

          //if the searchView is empty
          if (searchView.getQuery().length() == 0) {

            // hidePDialog();

            //show the logged-in users reviews, not the searched categories
            recyclerView.setAdapter(pAdapter);
            pAdapter.notifyDataSetChanged();

            //show the recyclerview, hide the noResults textview
            recyclerView.setVisibility(View.VISIBLE);
            noResultsFoundView.setVisibility(View.GONE);

          }

          // fetchCategories();

          // else {

          //on entering the first character, show
          //our progress dialog, so user can see activity is happening.

        /*          if (searchView.getQuery().length() == 1) {

*//**//*            pDialog = new ProgressDialog(PopulistoListView.this);
          // Showing progress dialog before making http request to get user's reviews
          pDialog.setMessage("Loading...");
          pDialog.show();*//**//*

            //if there's text in the search box
            fetchCategories();

          }*//*



          //if there's text in the search box
          //fetchCategories();


          //Log.e(TAG, "phonno2 is: " + phoneNoofUser);
          // filter recycler view when text is changed
          mAdapter.getFilter().filter(query);
        }*/
          //app not crashing as much with this here
          //recyclerView.setAdapter(mAdapter);

          //maybe this will resolve the IndexOutofBounds error?
         // recyclerView.getRecycledViewPool().clear();

          // refreshing recycler view
          mAdapter.notifyDataSetChanged();


        return false;


      }
    });


    // refreshing recycler view
    //mAdapter.notifyDataSetChanged();

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
  //when a fetched category is clicked in recyclerView
  //show the list of reviews
  //shared with logged in-user including his own,
  public void onCategorySelected(Category category) {

    //show the "Loading" dialog
    progressDialog.show(getSupportFragmentManager(), "tag");

    //Own Reviews
    //convert [56,23,87] to a string
    selectOwnUserReviews = Arrays.toString(category.getUserReviewIds());
    //remove [ and ] so we have a string of 56,23,87
    selectOwnUserReviews = selectOwnUserReviews.substring(1, selectOwnUserReviews.length() - 1);

    //Personal Contact reviews
    //convert [56,23,87] to a string
    selectPrivateReviews = Arrays.toString(category.getPrivateReviewIds());
    //remove [ and ] so we have a string of 56,23,87
    selectPrivateReviews = selectPrivateReviews.substring(1, selectPrivateReviews.length() - 1);

    //Public Reviews
    //convert [56,23,87] to a string
    selectPublicReviews = Arrays.toString(category.getPublicReviewIds());
    //remove [ and ] so we have a string of 56,23,87
    selectPublicReviews = selectPublicReviews.substring(1, selectPublicReviews.length() - 1);


    Toast.makeText(getApplicationContext(), "own reviews are:" + selectOwnUserReviews, Toast.LENGTH_LONG).show();
    //System.out.println("PopulistoListView newarray :" + jsonMatchingContacts);

    show_own_private_public_Reviews();

  }

  //when a fetched category in recyclerView is clicked, do this function
  private void show_own_private_public_Reviews() {

    //Toast.makeText(getApplicationContext(), "view the clicked on review", Toast.LENGTH_LONG).show();

    //post selectOwnUserReviews string (and private and public) to
    // User_Private_Public_Reviews_URL.php and from that
    //get the reviews details that belong to those users for the specific category
    StringRequest stringRequest = new StringRequest(Request.Method.POST, User_Private_Public_Reviews_URL,
        new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {

            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            System.out.println("response is :" + response);

            //clear the list of shared reviews, start afresh on new filter
            sharedReviewList.clear();

            // and create a new sharedReview, getting details of user's reviews in the db
            //  SharedReview sharedReview = new SharedReview();

            try {

              //name our JSONObject User_Private_Public_Obj, which is response from server
              JSONObject User_Private_Public_Obj = new JSONObject(response);

              //Now break up the response.
              //for the JSON Array user_review_ids
              JSONArray own_ids = User_Private_Public_Obj.getJSONArray("user_review_ids");

              //for the JSON Array private_review_ids
              JSONArray private_ids = User_Private_Public_Obj.getJSONArray("private_review_ids");

              //for the JSON Array public_review_ids
              JSONArray public_ids = User_Private_Public_Obj.getJSONArray("public_review_ids");

              for
                //get the number of objects in the array own_ids
                  (int i = 0; i < own_ids.length(); i++)

              {

                //for each object in the array own_ids, name it obj
                //each obj will consist of reviewid, category, name, phone,comment
                JSONObject obj = own_ids.getJSONObject(i);

                // and create a new sharedReview, getting details of user's reviews in the db
                SharedReview sharedReview = new SharedReview();

                //System.out.println("tesst1");

                sharedReview.setphoneNameonPhone("U");

                //set a string to get the phone number of the logged-in user from the DB,
                //the phone number of the person who made the own_ids review
                // String reviewOwnerphoneNoInDB = obj.getString("username");

                // System.out.println("reviewOwnerphoneNoInDB :" + reviewOwnerphoneNoInDB);


                //set the setter to
                //the phone number of the person who made the review
                sharedReview.setPhoneNumberofUserFromDB(obj.getString("username"));


                //get 0,1 or 2 value from db, for Just U, private or public
                sharedReview.setPublicorprivate(obj.getString("publicorprivate"));
                //we are getting the reviewid from the db so we can pull extra matching info,
                sharedReview.setReviewid(obj.getString("reviewid"));
                //set the category part of the object to that matching reviewid
                sharedReview.setCategory(obj.getString("category"));
                //etc...
                sharedReview.setName(obj.getString("name"));
                sharedReview.setAddress(obj.getString("address"));
                sharedReview.setPhone(obj.getString("phone"));
                sharedReview.setComment(obj.getString("comment"));

                //depending on if setType_row is 1 or 2 or 3,
                //in this case it is 1 - a review that is owned
                //by logged-in user.
                //We will getType_row in SharedPopulistoReviewsAdapter.
                //We will put phoneNameOnPhone in brown, blue or green text - depending
                //on how loggedin user is sharing the review
                //We will show ViewContact - has edit,
                //delete button etc
                sharedReview.setType_row("1");

                //add the sharedReview to the sharedReviewList
                sharedReviewList.add(sharedReview);

              }


              for
                //get the number of objects in User_Private_Public_Obj
                  (int i = 0; i < private_ids.length(); i++)

              {

                //for each object in the array private_ids, name it obj
                //each obj will consist of reviewid, category, name, address, phone,comment
                JSONObject obj = private_ids.getJSONObject(i);

                SharedReview sharedReview = new SharedReview();

                //get the string from sharedpreferences, AllPhonesandNamesofContacts,
                //which we put in VerifyUserPhoneNumber,
                //it will be like [{"phone_number":"+123456","name":"Jim Smith"}, etc...]
                //we want this so we can display phone name in recyclerView
                SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                String json_array = sharedPrefs.getString("AllPhonesandNamesofContacts", "0");
                //System.out.println("all phones and names string :" + json_array);

                //convert the string above into a json array
                JSONArray jsonArray = new JSONArray(json_array);

                //System.out.println("all phones and names :" + jsonArray);

                //get 0,1 or 2 value, for Just U, private or public
                sharedReview.setPublicorprivate(obj.getString("publicorprivate"));
                //we are getting the reviewid so we can pull extra matching info,
                sharedReview.setReviewid(obj.getString("reviewid"));
                //set the category part of the object to that matching reviewid
                sharedReview.setCategory(obj.getString("category"));
                //etc...
                sharedReview.setName(obj.getString("name"));
                sharedReview.setAddress(obj.getString("address"));

                //set a string to the phone number from the DB,
                //the phone number of the person who made the review
                phoneNoInDB = obj.getString("username");

                //set the setter to the phone number string, the string is
                //the phone number of the person who made the review
                sharedReview.setPhoneNumberofUserFromDB(phoneNoInDB);
                // System.out.println("PopulistoListView newarray :" + jsonMatchingContacts);

                //jsonArray is our AllPhonesandNamesofContacts
                int matching = jsonArray.length();
                for (int n = 0; n < matching; n++) {

                  try {
                    //for every object in AllPhonesandNamesofContacts,
                    //name it "object"
                    JSONObject object = jsonArray.getJSONObject(n);

                    //if the phone_number in AllPhonesandNamesofContacts equals
                    //the phone number in the DB
                    if (object.getString("phone_number").equals(phoneNoInDB)) {

                      //then rip out the other part of the object, the name in Contacts
                      //of the person who made the review
                      sharedReview.setphoneNameonPhone(object.getString("name"));

/*                      String convertedToString = object.getString("name");
                      System.out.println("convertedToString:" + convertedToString);*/


                    }

                  } catch (JSONException e) {
                    Log.e("MYAPP", "unexpected JSON exception", e);
                    // Do something to recover ... or kill the app.
                  }
                }


                sharedReview.setPhone(obj.getString("phone"));
                sharedReview.setComment(obj.getString("comment"));

                //depending on if setType_row is 1 or 2 or 3,
                //in this case it is 2 - a review that is shared with logged-in user
                //and in phone contacts of logged-in user.
                //We will getType_row in SharedPopulistoReviewsAdapter.
                //We will put phoneNameOnPhone in blue text - for Phone Contact
                //if the review is 1 (belongs to logged-in user)
                //then we would show ViewContact
                //In this case, "2", we will be showing SharedViewContact, no edit,
                //delete button etc
                sharedReview.setType_row("2");

                //add the sharedReview to the sharedReviewList
                sharedReviewList.add(sharedReview);

              }

              for
                //get the number of objects in the array public_ids
                  (int i = 0; i < public_ids.length(); i++)

              {

                //for each object in the array public_ids, name it obj
                //each obj will consist of reviewid, category, name, phone,comment
                JSONObject obj = public_ids.getJSONObject(i);

                // and create a new sharedReview, getting details of user's reviews in the db
                SharedReview sharedReview = new SharedReview();

                //If public review, mask the number
                String maskNumber = (obj.getString("username"));

                //before masking, get the number so it can be passed to ViewContact
                // in SharedPopulistoReviewsAdapter
                //so we know what stuff to show in ViewContact
                sharedReview.setPhoneNumberofUserFromDB(maskNumber);

                //now mask the number
                maskNumber = maskNumber.substring(0, maskNumber.length() - 4);
                maskNumber = maskNumber + "****";

                //for public reviews, we'll show the review maker's phone number - masked
                sharedReview.setphoneNameonPhone(maskNumber);
                //System.out.println("masknumber:" + maskNumber);


                //get 0,1 or 2 value from db, for Just U, private or public
                sharedReview.setPublicorprivate(obj.getString("publicorprivate"));
                //we are getting the reviewid from the db so we can pull extra matching info,
                sharedReview.setReviewid(obj.getString("reviewid"));
                //set the category part of the object to that matching reviewid
                sharedReview.setCategory(obj.getString("category"));
                //etc...
                sharedReview.setName(obj.getString("name"));
                sharedReview.setAddress(obj.getString("address"));
                sharedReview.setPhone(obj.getString("phone"));
                sharedReview.setComment(obj.getString("comment"));

                //depending on if setType_row is 1 or 2 or 3,
                //in this case it is 3 - a review that is public and
                //not in phone contacts of logged-in user.
                //We will getType_row in SharedPopulistoReviewsAdapter.
                //We will put phoneNameOnPhone in green text - for Public
                //if the review is 1 (belongs to logged-in user)
                //then we would show ViewContact
                //In this case, "3", we will be showing SharedViewContact, no edit,
                //delete button etc
                sharedReview.setType_row("3");

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
            //for shared reviews including user's own
            qAdapter.notifyDataSetChanged();

            //dismiss the dialog
            progressDialog.cancel();

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