package com.example.chris.populisto;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tutorialspoint.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CountryCodes extends AppCompatActivity {

    ArrayAdapter<String> adapter;
    ArrayList<String> itemCountryCode;
    ArrayList<String> itemCountryName;

    //to keep track of Country Code, going back to VerifyPhoneNumber
    String CountryCode;
  //to keep track of Country Name, going back to VerifyPhoneNumber
  String CountryName;
    //to keep track of phone number, going back to VerifyPhoneNumber
    String phoneNoofUserbeforeE164;

    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_codes);
        ListView listView=(ListView)findViewById(R.id.listview_country_codes);
        //for putting into the ListView
        itemCountryCode=new ArrayList<String>();
        //so we can get the code and name of the country
        itemCountryName=new ArrayList<String>();
        adapter=new ArrayAdapter(this, R.layout.listview_country_codes_items,R.id.txt,itemCountryName);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog until we get country list, response from server
        pDialog.setMessage("Loading...");
        pDialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               // TextView myTextView = (TextView) view.findViewById(R.id.txt);

                // Split the items2 array in two parts, code and name
                //String[] parts = items2.get(i).split(" ");
              String country_code = itemCountryCode.get(i);
              String country_number = itemCountryName.get(i);

                Intent myIntent2 = CountryCodes.this.getIntent();
                // get the string value of "PhoneNumber" in VerifyPhoneNumber
                phoneNoofUserbeforeE164 = myIntent2.getStringExtra("PhoneNumber");
                //Toast.makeText(getApplicationContext(),phoneNoofUserbeforeE164, Toast.LENGTH_SHORT).show();

                //start VerifyUserPhoneNumber activity, taking 'code' part of the JSON Array
                Intent myIntent = new Intent(CountryCodes.this, VerifyUserPhoneNumber.class);
                myIntent.putExtra("CountryCode", country_code);

                //take the 'name' part of the JSON Array
                myIntent.putExtra("CountryName", country_number);


                //VerifyUserPhoneNumber will be reset, so post back the phone number the user may have typed
                myIntent.putExtra("PhoneNumber", phoneNoofUserbeforeE164);

                CountryCodes.this.startActivity(myIntent);

                //close the Country Codes activity
                finish();
            }
        });

    }


    public void onStart(){
        super.onStart();

        //  Create json array request
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.POST,
                "https://www.populisto.com/CountryCodes.php", (JSONArray)null, new Response.Listener<JSONArray>(){
            public void onResponse(JSONArray jsonArray){

              //hide the 'loading' box when the page loads
              hidePDialog();

                // Successfully got CountryCodes.php
                // So parse it and populate the listview
                for(int i=0;i<jsonArray.length();i++){
                    try {
                        JSONObject jsonObject=jsonArray.getJSONObject(i);

                        //break the code and name values in the JSON CountryCodes.php into two parts
                        String code = jsonObject.getString("code");
                        String name = jsonObject.getString("name");

                        //add code and name into the items2 array
                        //this way we can isolate the Code separately to put in Main Activity
                        itemCountryCode.add(code);
                        //but for adding to our adapter, the ListView,
                        //we just want name
                      itemCountryName.add(name);

                      //  items.add(jsonObject.getString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Error", "Unable to parse json array");
            }
        });
        // Create request queue
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        // add json array request to the request queue
        requestQueue.add(jsonArrayRequest);
    }

    //code for the '<', back button. Go back to VerifyUserPhoneNumber, taking values
    @Override
    public void onBackPressed() {

        Intent myIntent = CountryCodes.this.getIntent();
        // get the string value of "CountryCode" in VerifyPhoneNumber
        CountryCode = myIntent.getStringExtra("CountryCode");
      // get the string value of "CountryName" in VerifyPhoneNumber
      CountryName = myIntent.getStringExtra("CountryName");
        // get the string value of "PhoneNumber" in VerifyPhoneNumber
        phoneNoofUserbeforeE164 = myIntent.getStringExtra("PhoneNumber");

        //Toast.makeText(getApplicationContext(),phoneNoofUserbeforeE164, Toast.LENGTH_LONG).show();

        //Now, post back the same CountryCode value to VerifyUserPhoneNumber
        Intent j = new Intent(getApplicationContext(), VerifyUserPhoneNumber.class);
        j.putExtra("CountryCode", CountryCode);
        //and post back the country name
        j.putExtra("CountryName", CountryName);
      //and post back the phone number
        j.putExtra("PhoneNumber", phoneNoofUserbeforeE164);
        startActivity(j);

        //close CountryCodes.java activity
        finish();
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
}
