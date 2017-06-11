package com.example.chris.tutorialspoint;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.example.tutorialspoint.R;

public class EditView extends AppCompatActivity {

    private EditText categoryfield;
    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_view);

        //cast our edittext
        categoryfield = (EditText) findViewById(R.id.textViewCategory);
        Intent i = this.getIntent();
        //categoryListView, get the category in ContactView activity
        category = i.getStringExtra("category");
        categoryfield.setText(category);
    }
}
