package com.example.chris.tutorialspoint;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.tutorialspoint.R;

public class ContactView extends AppCompatActivity {

    String categoryfromListView;
    private TextView categoryfield;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_view);

        //cast our textview
        categoryfield = (TextView) findViewById(R.id.textViewCategory);

        Intent i = this.getIntent();
        //categoryListView, get it from category in PopulistoListView activity
        categoryfromListView = i.getStringExtra("category");
        categoryfield.setText(categoryfromListView);
/*        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

}
