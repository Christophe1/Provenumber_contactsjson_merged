package com.example.chris.populisto;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.example.chris.populisto.GlobalFunctions.getDateandFormat;
//import static com.example.chris.populisto.GlobalFunctions.troubleContactingServerDialog;
import static com.example.chris.populisto.PopulistoContactsAdapter.MatchingContactsAsArrayList;
import static com.example.chris.populisto.VerifyUserPhoneNumber.activity;
import static com.example.tutorialspoint.R.id.category;

public class PopulistoListView extends AppCompatActivity implements CategoriesAdapter.CategoriesAdapterListener {

  private static final String TAG = PopulistoListView.class.getSimpleName();
  public static RecyclerView recyclerView;

  //for if no categories are displayed in recyclerView
  TextView noResultsFoundView;

  //show progress bar, before response is fetched
  ProgressBar Content_Main_Progressbar;

  // this is the php file for showing all logged in (own user's) reviews in the recyclerView.
  //First thing we see when app loads.
  // we will post the user's phone number into Php and get the matching user_id
  //and all values in the reviews
  private static final String SelectUserReviews_URL = "https://www.populisto.com/SelectUserReviews.php";

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
  private static final String CategoryFilter_URL = "https://www.populisto.com/CategoryFilter.php";

  //this is for showing ALL reviews available to logged-in user when a category is clicked,
  //for that category.
  // we will post the selected (clicked on) category into Php and get the
  //reviews that are shared with the logged in user
  //It returns a JSON Array of this format: {"user_review_ids":[1,3],"private_review_ids":[2],"public_review_ids":[8,12]}
  private static final String User_Private_Public_Reviews_URL = "https://www.populisto.com/User_Private_Public_Reviews.php";

  //this is for showing 'random reviews', more appealing than recyclerView being left empty
  private static final String Random_Reviews_URL = "https://www.populisto.com/Random_Reviews.php";

  //when searchView has focus and user types, we will be showing/filtering
  //categories
  private List<Category> categoryList = new ArrayList<Category>();
  //public List<Category> categoryListFiltered;

  //this is the adapter for categories, loading from the searchView
  private CategoriesAdapter mAdapter;

  //we are posting phoneNoofUser (logged-in user's own number),
  // the key is phonenumberofuser, which we see in php
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

  //a list of reviews for uAdapter
  private List<Review> reviewList = new ArrayList<Review>();

  //a list of reviews for sharedAdapter
  private List<SharedReview> sharedReviewList = new ArrayList<SharedReview>();

  //this is the adapter for user's reviews and "random reviews"
  public static UPopulistoListAdapter uAdapter;

  //this is the adapter for shared reviews including user's own
  public SharedPopulistoReviewsAdapter sharedAdapter;

  //declare an activity object so we can
  //call populistolistview and shut it down in ViewContact and NewContact
  //so that we will only have one instance of populistolistview
  //This way, updates will be refreshed.
  public static Activity fa;

  private SearchView searchView;


  //phoneNoofUser is stored in Shared Prefs at the VerifyUserPhoneNumber user stage
  String phoneNoofUser;

  //selectOwnUserReviews is to hold the own user's reviews for a fetched category.
  //When a category is clicked we get the review ids that are shared with logged-in
  // user for this particular category
  //we fetch the review ids from php as an array like [23,65,67] and
  //remove [ and ] so 23,65,67 will be sent to User_Private_Public_Reviews.php
  // and exploded,
  // getting corresponding
  //categories for each of 23, 65 and 67
  String selectOwnUserReviews;

  //String selectOwnUserCount;

  //hold phone contact reviews, for a fetched category
  String selectPrivateReviews;

  //hold public reviews, for a fetched category
  String selectPublicReviews;

  //hold random reviews by phone contacts
  String random_reviews;

  //to decide colour of "U" in phone_user_name
  public int pub_or_priv;

  //set a string to the the phone number from the DB,
  //the phone number of the person who made the review
  String phoneNoInDB;

  List<Category> items;

  //the_response is the response of all categories available to
  //logged-in user, used throughout the activity
  String the_response;

  // Request code for READ_CONTACTS. It can be any number > 0.
  //We need this for version Android 6 or greater, READ_CONTACTS in
  //Manifest alone is not enough
  private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

  //final Context context = this;

  String date;

  String[] private_array;
  String[] public_array;

  Integer[] private_array_numbers;
  Integer[] public_array_numbers;

  List<String> accumulator;

  String[] randomReviewsStringArray;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //phone_listview_contacts.xml is the layout file for PopulistoListView.java,
    //with toolbar etc and content_main.xml -
    setContentView(R.layout.phone_listview_contacts);

    //set the layout for the toolbar
    //searchview will appear inside of this
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    //Check READ_CONTACTS Permissions, will only be asked on Marshmallow +
    //because permission is automatically granted on older versions
    PackageManager manager = getPackageManager();
    int hasPermission = manager.checkPermission("android.permission.READ_CONTACTS", "com.example.chris.populisto");
    if (hasPermission == manager.PERMISSION_DENIED) {

      //if denied, show the standard Android dialog, 'Allow access to Contacts?'
      ActivityCompat.requestPermissions(this,
          new String[]{Manifest.permission.READ_CONTACTS},
          PERMISSIONS_REQUEST_READ_CONTACTS);

      //you don't have permission
      Toast.makeText(getApplicationContext(), "No. Read contacts not granted", Toast.LENGTH_LONG).show();
    }


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

    //for when we come back to this activity from New or Edit,
    //after saving. It will be updated.
    fa = this;

    //define the recyclerView
    recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

    //"No results" text box, if no categories to show on searching
    //View is gone, by default
    noResultsFoundView = (TextView) findViewById(R.id.noResultsFoundView);

    Content_Main_Progressbar = findViewById(R.id.content_main_progressbar);


    //the adapter for all own user reviews
    uAdapter = new UPopulistoListAdapter(reviewList);

    //the adapter for filtering categories
    mAdapter = new CategoriesAdapter(this, categoryList, this);

    //the adapter for all shared reviews including user's own
    sharedAdapter = new SharedPopulistoReviewsAdapter(sharedReviewList);

    //set the layout
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

    //this stops app crashing on search, bug with Android I think
    recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    //show the logged-in user's reviews
    recyclerView.setAdapter(uAdapter);


    //Log.e(TAG, "phonno" + phoneNoofUser);

    //Toast.makeText(PopulistoListView.this, "just before call fetchcategories", Toast.LENGTH_LONG).show();

    //Toast.makeText(getApplicationContext(), "clickety!", Toast.LENGTH_SHORT).show();

    //get every review - his own ones, phone contacts and public -
    //that is shared with the logged-in user.
    //it is called when activity is created(used to be when searchview first gets focus)
      StringRequest request = new StringRequest(Request.Method.POST, CategoryFilter_URL,
          new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

              //show categories available to the logged-in user
              //the_response is defined as a global string, above,
              //so we use this, rather than just "response"
              the_response = response;

              //response will be like:

              //[{"cat_name":"vet",
              // "user_review_ids":[2],
              // "private_review_ids":[3,4,5],
              // "public_review_ids":[6,7,8,9,10],
              // "user_personal_count":1,
              // "private_count":3,
              // "public_count":5},
              //{"cat_name":"dentist",
              // "user_review_ids":[],
              // "private_review_ids":[31,40],
              // "public_review_ids":[52,60,79],
              // "user_personal_count":0,
              // "private_count":2,
              // "public_count":3}, etc...etc....

              //Toast.makeText(PopulistoListView.this, "response is: " + the_response, Toast.LENGTH_LONG).show();

              //the_response is defined as a global string, above
              System.out.println("response is: " + the_response);

              //breaking up the response into respective parts
              //so we can get values for 'random reviews'
              try {
                JSONArray responseObject = new JSONArray(response);

               // System.out.println("responseObject is: " + responseObject);

                //use StringBuilder, so we can append values
                StringBuilder private_review_ids = new StringBuilder();
                StringBuilder public_review_ids = new StringBuilder();

                for (int i = 0; i < responseObject.length(); i++) {

                  JSONObject obj = responseObject.getJSONObject(i);

                  //get the "private_review_ids" part of the response, append
                  //each value into a string private_review_ids
                  //will be of the form
                  //[][105][][21][111][113][116][173][][104][]
                  //also, do same for public_review_ids
                  private_review_ids.append(obj.getString("private_review_ids").toString());
                  public_review_ids.append(obj.getString("public_review_ids").toString());


                }

                  //System.out.println("private_review_ids: " + private_review_ids);
                  //System.out.println("public_review_ids: " + public_review_ids);

                //make private_review_ids into a single string
                String private_review_ids2 = private_review_ids.toString();

                //we only want the numbers (the review_ids) so get rid of other stuff
                //replace everything that's not a number with a comma
                private_review_ids2 = private_review_ids2.replaceAll("[^0-9]+", ",");
                //get rid of the first ",", replace it with nothing
                private_review_ids2 = private_review_ids2.replaceFirst(",", "");


                //likewise for public
                String public_review_ids2 = public_review_ids.toString();
                //we only want the numbers (the review_ids) so get rid of other stuff
                //replace everything that's not a number with a comma
                public_review_ids2 = public_review_ids2.replaceAll("[^0-9]+", ",");
                //get rid of the first ","
                public_review_ids2 = public_review_ids2.replaceFirst(",", "");


                //make a string array, with the stuff (numbers) between
                //commas into separate strings
                private_array = private_review_ids2.split(",");
                public_array = public_review_ids2.split(",");

                Toast.makeText(PopulistoListView.this, "private array is" + Arrays.toString(private_array), Toast.LENGTH_LONG).show();
                Toast.makeText(PopulistoListView.this, "public array is" + Arrays.toString(public_array), Toast.LENGTH_LONG).show();


                //[105, 21, 57, 60, 103, 108, 111, 113, 116, 173, 104]
                //System.out.println("private_array : " + Arrays.toString(private_array));
                //[10, 44]
                //System.out.println("public_array : " + Arrays.toString(public_array));


                //let's get 10 reviews, made of private first and then public
                //it will give us an array, randomReviewsStringArray
                getMixedRandomNumbers(6);

                //make a string from the return value of getMixedRandomNumbers
                random_reviews = (Arrays.toString(randomReviewsStringArray));

                Toast.makeText(PopulistoListView.this, "random_reviews are: " + random_reviews, Toast.LENGTH_LONG).show();


              }catch (JSONException e) {
                Log.e("MYAPP", "unexpected JSON exception", e);
                // Do something to recover ... or kill the app.
              }

              sendSecondRequest();

            }
          }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

          //System.out.println("onErrorResponse is " + error);

          //If there is an error (such as contacting server for example) then
          //show a message like:
          //Sorry, can't contact server right now. Is internet access enabled?, try again, Cancel
          GlobalFunctions.troubleContactingServerDialog(PopulistoListView.this);

        }
      })

      {
        @Override
        //post info to php
        protected Map<String, String> getParams() {
          Map<String, String> params = new HashMap<String, String>();
          //phoneNoofUser is the value we get from Android, the user's phonenumber.
          //We post this and get all the categories available to that user.
          //the key,KEY_PHONENUMBER_USER, is "phonenumberofuser".
          // When we see "phonenumberofuser" in our php,
          //put in phoneNoofUser
          params.put(KEY_PHONENUMBER_USER, phoneNoofUser);
          return params;

        }
      };


      AppController.getInstance().addToRequestQueue(request);

  }


  //this is for "Add New" button, U reviews, " May be of interest" heading
  private void sendSecondRequest(){

    //This is for the first activity user sees:
    //"Add New" button, U reviews, " May be of interest" heading and " May be of Interest"
    //reviews.
    //post the phone number of the logged in user to SelectUserReviews.php and from that
    //get the logged in user's reviews
    StringRequest stringRequest = new StringRequest(Request.Method.POST, SelectUserReviews_URL,
        new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {

            try {

              //for our HEADER, "Add New", just 1 row will be like this
              Review review = new Review();

              //in UPopulistoListAdapter getItemViewType will be 4
              review.setType_row("4");

              //add the review to the reviewList
              reviewList.add(review);

            } catch (Exception e) {
              e.printStackTrace();
            }

            //for U reviews
            try {
              //name our JSONArray responseObject.
              //JSONArray is an array of responseObjects.
              JSONArray responseObject = new JSONArray(response);

              for
                //for each responseObject/review
                  (int i = 0; i < responseObject.length(); i++) {

                //for each responseObject in the array, name it obj
                //1 obj = 1 review, consisting of reviewid, category, name, phone,comment...
                JSONObject obj = responseObject.getJSONObject(i);

                // and create a new object, Review, getting details of user's reviews in the db
                Review review = new Review();

                //for logged-in user's reviews, set phone name to "U"
                review.setphoneNameonPhone("U");

                //get 0,1 or 2 value, for Just U, private or public
                //this will decide the colour for "U"
                review.setPublicorprivate(obj.getString("publicorprivate"));

                //we are getting the reviewid so we can pull extra matching info,
                review.setReviewid(obj.getString("reviewid"));

                //get "publicorprivate" from server,
                //convert it to an integer, pub_or_priv
                pub_or_priv = Integer.parseInt(obj.getString("publicorprivate"));

                review.setCategory(obj.getString("category"));
                //etc...
                review.setName(obj.getString("name"));
                review.setPhone(obj.getString("phone"));
                review.setAddress(obj.getString("address"));
                review.setComment(obj.getString("comment"));

                //get the part of the object "date_created"
                date = obj.getString("date_created");

                //we only want the date stuff, not the time in seconds etc.
                //and we want it formatted like this : 11 October 2018.
                //not like "2018-11-09 08:04:37
                review.setDate_created(getDateandFormat(date));

                //in this case it is 1 - a review that is owned
                //by logged-in user.
                //We will getType_row in UPopulistoListAdapter.
                //We will show ViewContact - has edit,
                //delete button etc
                review.setType_row("1");

                //add the reviewUser to the reviewList
                reviewList.add(review);

              }}

              catch (Exception e) {
                e.printStackTrace();
              }

              try {

                //for the heading, "May be of interest:", just 1 row will be like this
                Review review = new Review();

                //in UPopulistoListAdapter getItemViewType will be 5
                review.setType_row("5");

                //add the review to the reviewList
                reviewList.add(review);


              } catch (Exception e) {
                e.printStackTrace();
              }

            //with this we will fetch info on random_reviews
              sendThirdRequest();


          }
        },
        new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {
            Toast.makeText(PopulistoListView.this, "Shouldn't see this at all now:Trouble cantacting the server right now :(", Toast.LENGTH_LONG).show();

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

  private void sendThirdRequest(){

    //post random_reviews string to Random_Reviews.php.
    //It will be a string of the form 5,22,56, made by getMixedRandomNumbers
    //then when we get values of category, comment etc from server, show those reviews in recyclerView
    //below the logged-in user's reviews
    StringRequest stringRequest = new StringRequest(Request.Method.POST, Random_Reviews_URL,
        new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {

            Toast.makeText(PopulistoListView.this, "sendThirdRequest called  " + random_reviews, Toast.LENGTH_LONG).show();
            //System.out.println("response is :" + response);

            try {

              //name our JSONObject User_Private_Public_Obj, which is response from server
              JSONObject User_Private_Public_Obj = new JSONObject(response);

              //Now break up the response from server
              //We want the JSON Array part, "private_review_ids"
              JSONArray private_ids = User_Private_Public_Obj.getJSONArray("private_review_ids");

              for
                //get the number of objects in User_Private_Public_Obj
                  (int i = 0; i < private_ids.length(); i++)

              {

                //for each object in the array private_ids, name it obj
                //each obj will consist of reviewid, category, name, address, phone,comment
                JSONObject obj = private_ids.getJSONObject(i);

                Review review = new Review();

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

                //get the part of the object "date_created" from Random_Reviews.php
                String date2 = obj.getString("date_created");

                //we only want the date stuff, not the time in seconds etc.
                //and we want it formatted like this : 11 October 2018.
                //not like "2018-11-09 08:04:37
                review.setDate_created(getDateandFormat(date2));
                //get 0,1 or 2 value, for Just U, private or public
                review.setPublicorprivate(obj.getString("publicorprivate"));
                //we are getting the reviewid so we can pull extra matching info,
                review.setReviewid(obj.getString("reviewid"));
                //set the category part of the object to that matching reviewid
                review.setCategory(obj.getString("category"));
                //etc...
                review.setName(obj.getString("name"));
                review.setAddress(obj.getString("address"));

                //set a string to the phone number from the DB,
                //the phone number of the person who made the review
                phoneNoInDB = obj.getString("username");

                //set the setter to the phone number string, the string is
                //the phone number of the person who made the review
                review.setPhoneNumberofUserFromDB(phoneNoInDB);
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

                      //then rip out the other part of the object, the name in
                      // AllPhonesandNamesofContacts
                      //of the person who made the review
                      review.setphoneNameonPhone(object.getString("name"));

                    }

                  } catch (JSONException e) {
                    Log.e("MYAPP", "unexpected JSON exception", e);
                    // Do something to recover ... or kill the app.
                  }
                }


                review.setPhone(obj.getString("phone"));
                review.setComment(obj.getString("comment"));

                //depending on if setType_row is 1 or 2 or 3,
                //in this case it is 2 - a review that is shared with logged-in user
                //and in phone contacts of logged-in user.
                //We will getType_row in UPopulistoReviewsAdapter.
                //We will put phoneNameOnPhone in blue text - for Phone Contact
                //if the review is 1 (belongs to logged-in user)
                //then we would show ViewContact
                //In this case, "2", we will be showing SharedViewContact, no edit,
                //delete button etc
                review.setType_row("2");

                //add the review to the sharedReviewList
                reviewList.add(review);

              }

              //set the adapter to show the random reviews
              recyclerView.setAdapter(uAdapter);

            } catch (JSONException e) {
              Log.e("MYAPP", "unexpected JSON exception", e);
              // Do something to recover ... or kill the app.
            }

            // notifying list adapter about data changes
            // so that it renders the list view with updated data
            //for random reviews, after user's own
            uAdapter.notifyDataSetChanged();

          }
        },
        new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {
            Toast.makeText(PopulistoListView.this, "bbb " + error.toString(), Toast.LENGTH_LONG).show();
            System.out.println("bbb " + error.toString());

          }

        }) {
      @Override
      //post info to php
      protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();

        //KEY_REVIEWID_PRIVATE is reviewidprivate (in PHP)
        //random_reviews will be a string like 3,12,34.
        //explode it in Random_Reviews.php, break it into individual strings between the commas
        //and get more info
        params.put(KEY_REVIEWID_PRIVATE, random_reviews);

        return params;

      }

    };
    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
    requestQueue.add(stringRequest);



  // notifying list adapter about data changes
  // so that it renders the recyclerView with updated data
  uAdapter.notifyDataSetChanged();


}

  //method to get random reviews from phone contacts and public
  public  String[] getMixedRandomNumbers(int size) {

    //Toast.makeText(PopulistoListView.this, "calling getMixedRandomNumbers", Toast.LENGTH_LONG).show();

    try {
      //String[] private_array = {"105", "21", "57"};
      //String[] public_array  = {"103", "44", "3"};
      List<String> priv = Arrays.asList(private_array);
      List<String> publ = Arrays.asList(public_array);
      //shuffle the order
      Collections.shuffle(priv);
      Collections.shuffle(publ);
      List<String> result = new ArrayList<>();
      //add all the phone contact reviews
      result.addAll(priv);
      //if there's not a sufficient number of phone contact reviews, add public
      for (String p : publ) {
        if (!result.contains(p)) {
          result.add(p);
        }
      }

      int i = Math.min(size, result.size());

      //make a new string consisting of "size" numbers, whatever the method
      //defines it as
      randomReviewsStringArray = result.subList(0, i).toArray(new String[i]);

      Toast.makeText(getApplicationContext(), "stream, randomReviewsStringArray:" + Arrays.toString(randomReviewsStringArray), Toast.LENGTH_SHORT).show();

    }

    catch (IndexOutOfBoundsException e) {
      System.out.println("Exception thrown : " + e);
    }

    return randomReviewsStringArray;

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


    //When searchview has focus....
    //searchView.setOnSearchClickListener(new View.OnClickListener() {
/*      @Override
      public void onClick(View v) {
        Toast.makeText(getApplicationContext(), "clickety!", Toast.LENGTH_SHORT).show();
        fetchCategories();
      }
    });*/


    // listening to search query text change
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


      @Override
      public boolean onQueryTextSubmit(String query) {



        // filter recycler view when query submitted
        mAdapter.getFilter().filter(query);
        return false;
      }

      @Override
      public boolean onQueryTextChange(String query) {

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

       // Toast.makeText(getApplicationContext(), "items are:" + categoryList, Toast.LENGTH_SHORT).show();


        // recyclerView.setAdapter(mAdapter);

        mAdapter.getFilter().filter(query);

        recyclerView.setAdapter(mAdapter);

        if (mAdapter.getItemCount() < 1) {

         // recyclerView.setAdapter(mAdapter);

          //if there's nothing to show, hide recyclerView...
          recyclerView.setVisibility(View.GONE);

          //and show the "No Results" textbox
          noResultsFoundView.setVisibility(View.VISIBLE);

          Toast.makeText(getApplicationContext(), "mAdapter is 0, no item results", Toast.LENGTH_SHORT).show();
         // Toast.makeText(getApplicationContext(), the_response, Toast.LENGTH_SHORT).show();


         // mAdapter.notifyDataSetChanged();
        }

        if (mAdapter.getItemCount() >= 1) {

          //if there ARE category results for what is typed, with each key press...
/*
          Toast.makeText(getApplicationContext(), "mAdapter size is:" + mAdapter.getItemCount(), Toast.LENGTH_SHORT).show();
          Toast.makeText(getApplicationContext(), "uAdapter size is:" + uAdapter.getItemCount(), Toast.LENGTH_SHORT).show();
          Toast.makeText(getApplicationContext(), "sharedAdapter size is:" + sharedAdapter.getItemCount(), Toast.LENGTH_SHORT).show();
*/


          recyclerView.setVisibility(View.VISIBLE);
          noResultsFoundView.setVisibility(View.GONE);

/*          recyclerView.setAdapter(mAdapter);
          mAdapter.notifyDataSetChanged();*/

          // hidePDialog();



        }


        //WILL CRASH IF UNCOMMENTED
        //recyclerView.setAdapter(mAdapter);
        //sharedReviewList.clear();
        //String str = query.toString();
        // searchView.getText().toString().trim();

        //if the searchView is empty
        if (searchView.getQuery().length() == 0) {

          //show the logged-in users reviews, not the searched categories
          recyclerView.setAdapter(uAdapter);
          uAdapter.notifyDataSetChanged();

          //show the recyclerview, hide the noResults textview
          recyclerView.setVisibility(View.VISIBLE);
          noResultsFoundView.setVisibility(View.GONE);

        }


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
      case R.id.add_new:
        //start the NewContact class
        Intent intent = new Intent(PopulistoListView.this, NewContact.class);

        startActivity(intent);
        return true;


      case R.id.contact_us:
        //start the email intent
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        //emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"harris.christophe@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Populisto, Contact Us");
        emailIntent.putExtra(Intent.EXTRA_TEXT,"");

        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent, "Choose email package:"));

    }

    return false;
  }

  @Override
  //when a fetched category is clicked in recyclerView,
  //like "veterinarian (U,1,7)"
  //show the list of reviews
  //shared with logged in-user including his own.
  public void onCategorySelected(Category category) {

    //Own Reviews
    //convert [56,23,87] to a string
    selectOwnUserReviews = Arrays.toString(category.getUserReviewIds());
    //remove [ and ] so we have a string of 56,23,87
    selectOwnUserReviews = selectOwnUserReviews.substring(1, selectOwnUserReviews.length() - 1);

    //selectOwnUserCount = category.getUserPersonalCount();

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


   // Toast.makeText(getApplicationContext(), "own reviews are:" + selectOwnUserReviews, Toast.LENGTH_LONG).show();
   // Toast.makeText(getApplicationContext(), "own reviews count:" + selectOwnUserCount, Toast.LENGTH_LONG).show();

    // Toast.makeText(getApplicationContext(), "Phone contact reviews are:" + selectPrivateReviews, Toast.LENGTH_LONG).show();
    // Toast.makeText(getApplicationContext(), "Public reviews are:" + selectPublicReviews, Toast.LENGTH_LONG).show();

    //System.out.println("PopulistoListView newarray :" + jsonMatchingContacts);

    show_own_private_public_Reviews();

  }

  //when a fetched category in recyclerView is clicked,
  // for example, "veterinarian (U,1,7)" in recyclerView is clicked on,
  // do this function
  private void show_own_private_public_Reviews() {

    //we run on UI thread to stopp progressbar flickering...
    runOnUiThread(new Runnable() {

                    @Override
                    public void run() {


                    //before we get a response, hide recyclerView...
                    recyclerView.setVisibility(View.GONE);

                    //and show the Progress bar, it's loading
                    Content_Main_Progressbar.setVisibility(View.VISIBLE);

                  }});

    //post selectOwnUserReviews string (and private and public) to
    // User_Private_Public_Reviews.php and from that
    //get the reviews details that belong to those users for the specific category
    StringRequest stringRequest = new StringRequest(Request.Method.POST, User_Private_Public_Reviews_URL,
        new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {

            //when we get response, make recyclerview visible
            recyclerView.setVisibility(View.VISIBLE);

            //and hide progressbar
            Content_Main_Progressbar.setVisibility(View.GONE);

          //  Toast.makeText(getApplicationContext(), "fetching now..", Toast.LENGTH_LONG).show();

       //     Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
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

                //for own_ids, logged-in user's reviews, set phone name to "U"
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
                //sharedReview.setDate_created("world wide web");

                //get the part of the object "date_created"
                String date1 = obj.getString("date_created");

                //we only want the date stuff, not the time in seconds etc.
                //and we want it formatted like this : 11 October 2018.
                //not like "2018-11-09 08:04:37
                sharedReview.setDate_created(getDateandFormat(date1));

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

                //get the part of the object "date_created" from each review
                //that appears in search results
                String date3 = obj.getString("date_created");

                //we only want the date stuff, not the time in seconds etc.
                //and we want it formatted like this : 11 October 2018.
                //not like "2018-11-09 08:04:37
                sharedReview.setDate_created(getDateandFormat(date3));

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

//*                      String convertedToString = object.getString("name");
                     // System.out.println("convertedToString:" + convertedToString);*//*


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

                //get the part of the object "date_created" from each review
                //that appears in search results
                String date4 = obj.getString("date_created");

                //we only want the date stuff, not the time in seconds etc.
                //and we want it formatted like this : 11 October 2018.
                //not like "2018-11-09 08:04:37
                sharedReview.setDate_created(getDateandFormat(date4));

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
              recyclerView.setAdapter(sharedAdapter);

            } catch (JSONException e) {
              Log.e("MYAPP", "unexpected JSON exception", e);
              // Do something to recover ... or kill the app.
            }

            // notifying list adapter about data changes
            // so that it renders the list view with updated data
            //for shared reviews including user's own
            sharedAdapter.notifyDataSetChanged();

            //dismiss the dialog
            //progressDialog.cancel();

          }
        },
        new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {

           // System.out.println("onErrorResponse is " + error);

            //Toast.makeText(PopulistoListView.this, error.toString(), Toast.LENGTH_LONG).show();

            //If there is an error (such as contacting server for example) then
            //show a message like:
            //Sorry, can't contact server right now. Is internet access enabled?, try again, Cancel
            GlobalFunctions.troubleContactingServerDialog(PopulistoListView.this);

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

  @Override
  //check Permissions status
  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[], int[] grantResults) {
    switch (requestCode) {

      //if Permission is granted, then continue as normal
      case PERMISSIONS_REQUEST_READ_CONTACTS: {

        if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          //getPhoneContacts();
          Toast.makeText(getApplicationContext(), "Yay! Granted now", Toast.LENGTH_LONG).show();

          //if Permission is denied, then show our custom made dialog
          //This is important for if user chooses, 'Don't Show Again',
          //It will open up SETTINGS and user can change it
        } else {
          new AlertDialog.Builder(this).
              setCancelable(false).
              // setTitle("You need to enable Read Contacts").
                  setMessage("To get full use of Populisto, allow Populisto access to your phone contacts. Click SETTINGS, tap Permissions and turn Contacts on.").
              //setIcon(R.drawable.ninja).
                  setPositiveButton("SETTINGS",
                  new DialogInterface.OnClickListener() {
                    @TargetApi(11)
                    public void onClick(DialogInterface dialog, int id) {
                      dialog.cancel();

                      //Open up the SETTINGS for the App, user can enable contacts
                      Intent intent = new Intent();
                      intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                      Uri uri = Uri.fromParts("package", getPackageName(), null);
                      intent.setData(uri);
                      startActivity(intent);
                    }
                  })
              .setNegativeButton("NOT NOW", new DialogInterface.OnClickListener() {
                @TargetApi(11)
                public void onClick(DialogInterface dialog, int id) {
                  dialog.cancel();
                }
              }).show();

        }
        return;
      }

    }
  }

  //if the user chooses to refresh the Activity, when "Try Again" button is clicked...
  public void refresh() {
    Intent intent = getIntent();
    overridePendingTransition(0, 0);
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    finish();
    overridePendingTransition(0, 0);
    startActivity(intent);
  }

  //CURRENTLY NOT BEING USED 09/11/2018
  private void showRandomSharedReviews() {
    //rather than unwelcoming empty screen, if user has no reviews to show on start up,
    //show 3 random reviews
    // and create a new sharedReview, getting details of user's reviews in the db
    SharedReview sharedReview = new SharedReview();

    //System.out.println("tesst1");

 /*   sharedReview.setphoneNameonPhone("U");

    //get 0,1 or 2 value from db, for Just U, private or public
    sharedReview.setPublicorprivate("1");
    //we are getting the reviewid from the db so we can pull extra matching info,
    sharedReview.setReviewid("6");
    //set the category part of the object to that matching reviewid
    sharedReview.setCategory("doctor");
    //etc...
    sharedReview.setName("Dr Harris");
    sharedReview.setAddress("Tallaght");
    sharedReview.setPhone("086 34 63 389");
    sharedReview.setComment("All I want for Christmas is you");
*/
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

    //set the adapter to show shared reviews
    recyclerView.setAdapter(sharedAdapter);


    //post the phone number of the logged in user to SelectUserReviews.php and from that
    //get the logged in user's reviews
    StringRequest stringRequest = new StringRequest(Request.Method.POST, SelectUserReviews_URL,
        new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {

            try {
              //name our JSONArray responseObject.
              //JSONArray is an array of responseObjects.
              //one responseObject = one review
              JSONArray responseObject = new JSONArray(response);

              for
                //for each responseObject/review
                  (int i = 0; i < responseObject.length(); i++) {

                //for each responseObject in the array, name it obj
                //each obj will consist of reviewid, category, name, phone,comment
                JSONObject obj = responseObject.getJSONObject(i);
                // and create a new object, Review, getting details of user's reviews in the db
                Review review = new Review();

                //get 0,1 or 2 value, for Just U, private or public
                review.setPublicorprivate(obj.getString("publicorprivate"));
                //we are getting the reviewid so we can pull extra matching info,
                review.setReviewid(obj.getString("reviewid"));

                //convert public_or_private to an integer
                pub_or_priv = Integer.parseInt(obj.getString("publicorprivate"));

                //set the category part of the object to that matching reviewid
                review.setDate_created(obj.getString("date_created"));

                review.setCategory(obj.getString("category"));
                //etc...
                review.setName(obj.getString("name"));
                review.setPhone(obj.getString("phone"));
                review.setAddress(obj.getString("address"));
                review.setComment(obj.getString("comment"));

                String date = obj.getString("date_created");
                SimpleDateFormat spf = new SimpleDateFormat("yyyy dd mm hh:mm:ss");
                Date newDate;
                try {
                  newDate = spf.parse(date);
                  spf = new SimpleDateFormat("dd MMM yyyy");
                  date = spf.format(newDate);
                  System.out.println("the date is" + date);

                } catch (ParseException e) {
                  e.printStackTrace();
                }

                //set a string to the phone number from the DB,
                //the phone number of the person who made the review
                // phoneNoInDB = phoneNoofUser;
                //set the setter to
                //the phone number of the person who made the review
                review.setPhoneNumberofUserFromDB(phoneNoofUser);

                //add the reviewUser to the sharedReviewList
                reviewList.add(review);

                //System.out.println("obj length is: " + obj.length());

              }

              //number of reviews....
              System.out.println("responseObject length is: " + responseObject.length());

            } catch (JSONException e) {
              Log.e("MYAPP", "unexpected JSON exception", e);
              // Do something to recover ... or kill the app.
            }

            // notifying list adapter about data changes
            // so that it renders the list view with updated data
            uAdapter.notifyDataSetChanged();

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

  //delete matching contacts in shared prefs on activity close down
/*  @Override
  public void onDestroy() {
    super.onDestroy();
  //  Toast.makeText(getApplicationContext(), "Goodbye now", Toast.LENGTH_LONG).show();
    SharedPreferences sharedPreferencesMatchingContactsAsArrayList = PreferenceManager.getDefaultSharedPreferences(getApplication());
    SharedPreferences.Editor editorMatchingContactsAsArrayList = sharedPreferencesMatchingContactsAsArrayList.edit();
    editorMatchingContactsAsArrayList.putString("MatchingContactsAsArrayList", "");
    editorMatchingContactsAsArrayList.apply();
  }*/

  }

