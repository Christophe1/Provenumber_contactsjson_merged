package com.example.chris.tutorialspoint;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
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

import static com.android.volley.Request.Method.POST;

public class PopulistoListView extends AppCompatActivity {

    // this is the php file name where to select from.
    // we will post the user's phone number into Php and get the matching user_id
    private static final String SelectUserReviews_URL = "http://www.populisto.com/SelectUserReviews.php";

    //we are posting phoneNoofUser, which in PHP is phonenumberofuser
    public static final String KEY_PHONENUMBER_USER = "phonenumberofuser";

   // private static final String TAG = PopulistoListView.class.getSimpleName();


    String phoneNoofUser;
    private ProgressDialog pDialog;
    private List<Review> reviewList = new ArrayList<Review>();
    private ListView listView;
    private CustomListAdapter adapter;
    //private TextView textphonenumber;
    //private String strphone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_contacts);

        // textphonenumber = (TextView) findViewById(R.id.textView3);

        listView = (ListView) findViewById(R.id.list);
        adapter = new CustomListAdapter(this, reviewList);
        listView.setAdapter(adapter);

        Intent myIntent = this.getIntent();
        //phone number of the user, get it from VerifyPhoneNumber activity
        phoneNoofUser = myIntent.getStringExtra("keyName");
        //phoneNoofUser="JK Rowling";

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        // changing action bar color
        //getActionBar().setBackgroundDrawable(
          //new ColorDrawable(Color.parseColor("#1b1b1b")));

        // textphonenumber.setText(phoneNoofUser);

        //post the phone number of the logged in user to SelectUserReviews.php and from that
        //get the logged in user's reviews
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SelectUserReviews_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hide the 'loading' box when the page loads
                        hidePDialog();
                        //post the response of SelectUserReviews.php, which is a string
                        Toast.makeText(PopulistoListView.this, response, Toast.LENGTH_LONG).show();
                        //convert the response to a JSON array
                        for (int i = 0; i < response.length(); i++) {
                        try {
                        JSONArray responseObject = new JSONArray(response);
                            JSONObject obj = responseObject.getJSONObject(i);
                            Review review = new Review();
                            review.setCategory(obj.getString("category"));
                            review.setName(obj.getString("name"));
                            review.setPhone(obj.getString("phone"));
                            review.setComment(obj.getString("comment"));
                           // Toast.makeText(PopulistoListView.this, responseObject.toString(), Toast.LENGTH_LONG).show();

                            reviewList.add(review);

                    }
                        catch (JSONException e) {
                            Log.e("MYAPP", "unexpected JSON exception", e);
                            // Do something to recover ... or kill the app.
                        }
                    }
                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();

                       // System.out.println("size of reviewlist " + reviewList.size());
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
                params.put(KEY_PHONENUMBER_USER, phoneNoofUser);
                return params;

            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



    }

