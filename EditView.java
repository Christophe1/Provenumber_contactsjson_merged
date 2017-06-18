package com.example.chris.tutorialspoint;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tutorialspoint.R;

import java.util.HashMap;
import java.util.Map;

public class EditView extends AppCompatActivity {

    // this is the php file name where to select from.
    // we will post the category, name, phone, address and comment into Php and
    // save with matching review_id
    private static final String EditReview_URL = "http://www.populisto.com/EditReview.php";

    //this is the review of the current activity
    String review_id;

    Button save;

    private EditText categoryname;
    private EditText namename;
    private EditText phonename;
    private EditText addressname;
    private EditText commentname;

    //string for getting intent info from ContactView class
    String category, name, phone, address, comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_view);


        //cast an EditText for each of the field ids in activity_edit_view.xml
        //can be edited and changed by the user
        categoryname = (EditText) findViewById(R.id.textViewCategory);
        namename = (EditText) findViewById(R.id.textViewName);
        phonename = (EditText) findViewById(R.id.textViewPhone);
        addressname = (EditText) findViewById(R.id.textViewAddress);
        commentname = (EditText) findViewById(R.id.textViewComment);

        //get the intent we created in ContactView class
        Intent i = this.getIntent();
        //we need to get review_id to ensure changes made are saved to correct review_id
        review_id = i.getStringExtra("review_id");
        //get the key, "category", in ContactView activity
        category = i.getStringExtra("category");
        //etc..
        name = i.getStringExtra("name");
        phone = i.getStringExtra("phone");
        address = i.getStringExtra("address");
        comment = i.getStringExtra("comment");

        //set the EditText to display the pair value of key "category"
        categoryname.setText(category);
        //etc
        namename.setText(name);
        phonename.setText(phone);
        addressname.setText(address);
        commentname.setText(comment);

        //make the cursor appear at the end of the categoryname
        categoryname.setSelection(categoryname.getText().length());







        //for the save button ******************************
        save = (Button) findViewById(R.id.save);

       save.setOnClickListener(new View.OnClickListener() {
           public void onClick(View view) {
               System.out.println("you clicked it, save");

               //post the review_id in the current activity to EditReview.php and from that
               //get associated values - category, name, phone etc...
               StringRequest stringRequest = new StringRequest(Request.Method.POST, EditReview_URL,
                       new Response.Listener<String>() {
                           @Override
                           public void onResponse(String response) {
                               Toast.makeText(EditView.this, response, Toast.LENGTH_LONG).show();
                           }
                       },
                       new Response.ErrorListener() {
                           @Override
                           public void onErrorResponse(VolleyError error) {

                           }

                       }) {

                   protected Map<String, String> getParams() {
                       Map<String, String> params = new HashMap<String, String>();
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
                       return params;

                   }



               };


           AppController.getInstance().addToRequestQueue(stringRequest);

                //when saved, go back to the ViewReview class and update with
               //the edited values
               Intent j = new Intent(EditView.this,ViewReview.class);
                j.putExtra("category", categoryname.getText().toString());
               j.putExtra("name", namename.getText().toString());
               j.putExtra("phone", phonename.getText().toString());
               j.putExtra("address", addressname.getText().toString());
               j.putExtra("comment", commentname.getText().toString());

               startActivity(j);

       }



       });
    }
}