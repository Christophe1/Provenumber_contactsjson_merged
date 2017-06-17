package com.example.chris.tutorialspoint;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tutorialspoint.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.tutorialspoint.R.id.category;

public class ContactView extends AppCompatActivity {

    // this is the php file name where to select from.
    // we will post the review id of the review in ListView into Php and
    // get the matching details - Category, name, phone, address etc...
    private static final String ContactView_URL = "http://www.populisto.com/ContactView.php";

    //we are posting review_id, which in PHP is review_id
    public static final String KEY_REVIEW_ID = "review_id";
    //the edit button, if the user wants to edit a review
    Button edit;

    private TextView categoryname;
    private TextView namename;
    private TextView phonename;
    private TextView addressname;
    private TextView commentname;

    // temporary string to show the parsed response
    //private String jsonResponse;

    //this is the review that hs been clicked in the ListView
    String review_id;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_view);

        //********************

        Intent i = this.getIntent();
        //we want review_id of the review clicked in the ListView,
        // get it from PopulistoListView activity
        //this is the review_id we are posting to php
        review_id = i.getStringExtra("review_id");
        //Toast.makeText(ContactView.this, review_id, Toast.LENGTH_SHORT).show();


        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        //cast a TextView for each of the field ids in activity_contact_view.xml
         categoryname = (TextView) findViewById(R.id.textViewCategory);
         namename = (TextView) findViewById(R.id.textViewName);
         phonename = (TextView) findViewById(R.id.textViewPhone);
         addressname = (TextView) findViewById(R.id.textViewAddress);
         commentname = (TextView) findViewById(R.id.textViewComment);

        // textphonenumber.setText(phoneNoofUser);

        //post the review_id that has been clicked in the ListView and send it to
        // ContactView.php and from that get other review details, like name, address etc..
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ContactView_URL,
                new Response.Listener<String>() {

            @Override
                    public void onResponse(String response) {
                        //hide the 'loading' box when the page loads
                        pDialog.dismiss();
                        //toast the response of ContactView.php, which has been converted to a
                        //JSON object in the Php file with JSON encode
                        Toast.makeText(ContactView.this, response, Toast.LENGTH_LONG).show();

                        try {

                            // Parsing json object response
                            // make a JSONObject called responseObject, break it down into
                            //respective parts
                            JSONObject responseObject = new JSONObject(response);
                            String category = responseObject.getString("category");
                            String name = responseObject.getString("name");
                            String phone = responseObject.getString("phone");
                            String address = responseObject.getString("address");
                            String comment = responseObject.getString("comment");

                /*            jsonResponse = "";
                            jsonResponse += "Category: " + category + "\n\n";
                            jsonResponse += "Name: " + name + "\n\n";
                            jsonResponse += "Phone: " + phone + "\n\n";
                            jsonResponse += "Address: " + address + "\n\n";
                            jsonResponse += "Comment: " + comment + "\n\n";*/

                            //assign a textview to each of the fields in the review
                            categoryname.setText(category);
                            namename.setText(name);
                            phonename.setText(phone);
                            addressname.setText(address);
                            commentname.setText(comment);

                            //System.out.println("heree it is" + jsonResponse);
                            //Toast.makeText(ContactView.this, jsonResponse, Toast.LENGTH_LONG).show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                    }

                        // System.out.println("size of reviewlist " + reviewList.size());
                        //System.out.println("heree it is" + reviewList.toString());


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ContactView.this, error.toString(), Toast.LENGTH_LONG).show();

                    }

                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //we are posting review_id into our ContactView.php file,
                //to get matching details
                params.put(KEY_REVIEW_ID, review_id);
                return params;

            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


        //*****************

        //make a TextView called category, cast this to our TextView id
        // set the text of the TextView to our getCategory string
        //category.setText(getCategory);


        //cast our textview
/*

        Intent i = this.getIntent();
        //categoryListView, get the category for the review in PopulistoListView activity
        categoryfromListView = i.getStringExtra("category");
        categoryfield.setText(categoryfromListView);*/







        //for the edit button
        edit = (Button) findViewById(R.id.edit);

        edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                System.out.println("you clicked it, edit");
            //open the Edit Activity
                Intent i = new Intent(ContactView.this, EditView.class);
                //"category" is the key, categoryname.getText() is the
                // content to pass. etc....
                i.putExtra("category",  categoryname.getText());
                i.putExtra("name",  namename.getText());
                i.putExtra("phone",  phonename.getText());
                i.putExtra("address",  addressname.getText());
                i.putExtra("comment",  commentname.getText());
                startActivity(i);


            }
        });

       // System.out.println("the cat is " + getCategory);

    }

}
