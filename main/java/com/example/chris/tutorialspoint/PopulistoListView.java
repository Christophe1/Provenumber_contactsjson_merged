package com.example.chris.tutorialspoint;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

import java.util.ArrayList;
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
    private CustomListAdapter adapter;
    //private TextView textphonenumber;
    //private String strphone;
//private int reviewid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_contacts);

        // textphonenumber = (TextView) findViewById(R.id.textView3);

        listView = (ListView) findViewById(R.id.list);
        adapter = new CustomListAdapter(this, reviewList);
        listView.setAdapter(adapter);

        //get the phone number value from shared preferences file instead
        //of from the VerifiedUserPhoneNumber class because we might not
        //be coming from that class, for example on Edit, New etc. The phone
        //number needs to be posted for this listview to load properly.
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        final String phoneNoofUser = sharedPreferences.getString("phonenumberofuser", "");

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

       // Intent j = this.getIntent();
       // phoneNoofUser = j.getStringExtra("phonenumberofuser");
        //Toast.makeText(ContactView.this, review_id, Toast.LENGTH_SHORT).show();

        // textphonenumber.setText(phoneNoofUser);

        //post the phone number of the logged in user to SelectUserReviews.php and from that
        //get the logged in user's reviews
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SelectUserReviews_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hide the 'loading' box when the page loads
                        hidePDialog();
                        //toast the response of SelectUserReviews.php, which has been converted to a
                        //JSON array in the Php file with JSON encode
                        Toast.makeText(PopulistoListView.this, response, Toast.LENGTH_LONG).show();

                        //break up the JSON Array into parts
                        //we need to sort out this error we keep getting in logcat
                        final int numberOfItemsInResp = response.length();
                        for (int i = 0; i < numberOfItemsInResp; i++) {
                            try {
                                JSONArray responseObject = new JSONArray(response);
                                JSONObject obj = responseObject.getJSONObject(i);
                                Review review = new Review();
                                review.setCategory(obj.getString("category"));
                                review.setName(obj.getString("name"));
                                review.setPhone(obj.getString("phone"));
                                review.setComment(obj.getString("comment"));
                                //we are getting the review id so we can pull extra needed info, like Address etc
                                review.setReviewid(obj.getString("reviewid"));
                                // Toast.makeText(PopulistoListView.this, responseObject.toString(), Toast.LENGTH_LONG).show();

                                reviewList.add(review);

                            } catch (JSONException e) {
                                Log.e("MYAPP", "unexpected JSON exception", e);
                                // Do something to recover ... or kill the app.
                            }
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
                //to the ContactView activity, so we can post it and get more
                //info for that review - address, comments etc
                   Intent i = new Intent(PopulistoListView.this, ViewContact.class);
                    i.putExtra("review_id",  review.getReviewid());
                    startActivity(i);
              //  }
              //  else{*/
                    Toast.makeText(PopulistoListView.this, review.getReviewid(), Toast.LENGTH_SHORT).show();
                    //}

                //}
            }


        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.new_contact:
                Intent intent = new Intent(PopulistoListView.this, NewContact.class);
                startActivity(intent);
                return true;
        }
        return false;
    }

    }

