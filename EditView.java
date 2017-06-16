package com.example.chris.tutorialspoint;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.tutorialspoint.R;

public class EditView extends AppCompatActivity {

    Button save;

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

        //make the cursor appear at the end of the text
        categoryfield.setSelection(categoryfield.getText().length());

        //for the save button ******************************
        save = (Button) findViewById(R.id.save);

/*        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                System.out.println("you clicked it, save");

                Intent i = new Intent(ContactView.this, EditView.class);
                i.putExtra("category",  categoryfromListView);
                //   i.putExtra("maxhoras",  item.get_maxhoras());
                startActivity(i);


            }
        });*/

    }
}
