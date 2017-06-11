package com.example.chris.tutorialspoint;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tutorialspoint.R;

public class ContactView extends AppCompatActivity {

    Button edit;

    String categoryfromListView;
    private TextView categoryfield;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_view);

        //for the edit button
        edit = (Button) findViewById(R.id.edit);

        edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                System.out.println("you clicked it, edit");

                Intent i = new Intent(ContactView.this, EditView.class);
                i.putExtra("category",  categoryfromListView);
                //   i.putExtra("maxhoras",  item.get_maxhoras());
                startActivity(i);


            }
        });

        //cast our textview
        categoryfield = (TextView) findViewById(R.id.textViewCategory);

        Intent i = this.getIntent();
        //categoryListView, get the category for the review in PopulistoListView activity
        categoryfromListView = i.getStringExtra("category");
        categoryfield.setText(categoryfromListView);

    }

}
