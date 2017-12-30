package com.example.chris.tutorialspoint;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.tutorialspoint.R;

import java.util.HashMap;
import java.util.Map;



/*************************
 * Created by Chris on 28/12/2017.
 * THIS IS USED FOR SQLITE INSERTION. Not using it for the time being,
 * sticking with getting the details from server and see how fast it is.
 *
 * IT WORKS IN CONJUNCTION WITH :
 * displayMyPopulistoAdapter.java
 * TableData.java
 * SQLiteDatabaseOperations.java
 * BackGroundTask.java
 */

public class DisplayMyPopulistoListView extends AppCompatActivity {
//just for testing
    //ListView listView ;

    //for SQLiteDatabaseOperations
    Context ctx = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_listview_contacts);

/*        listView = (ListView) findViewById(R.id.list);
        String[] values = new String[] { "Android List View",
                "Adapter implementation",
                "Simple List View In Android",
                "Create List View Android",
                "Android Example",
                "List View Source Code",
                "List View Array Adapter",
                "Android Example List View"
        };


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        listView.setAdapter(adapter);*/

        //always show the overflow menu. Some devices don't show it by default
        //This function is in the GlobalFunctions class
        GlobalFunctions.makeActionOverflowMenuShown(DisplayMyPopulistoListView.this);

        //create an object of the BackGroundTask class, backGroundTask
        //pass this activity, ctx, as the object
        BackGroundTask backGroundTask = new BackGroundTask(this);

        //asynctask runs differently for older and newer versions of Android
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            //executeOnExecutor ensures asynctasks can run in parallel, rather than wait for
            //the first task to finish
            backGroundTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "get info");
        else
        //execute the "get info" task: that is, populating the listview
        backGroundTask.execute("get info");

        //DisplayMyPopulistoListView.ShowMyPopulisto showMyPopulisto = new DisplayMyPopulistoListView.ShowMyPopulisto();
        //showMyPopulisto.execute();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_contact:
                //start the NewContact class
                Intent intent = new Intent(DisplayMyPopulistoListView.this, NewContact.class);

                startActivity(intent);
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        System.out.println("onBackPressed is clicked");

        super.onBackPressed();

    }
}