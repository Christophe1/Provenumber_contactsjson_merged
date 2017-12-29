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



/**
 * Created by Chris on 28/12/2017.
 */

public class DisplayMyPopulistoListView extends AppCompatActivity {
//just for testing
   // ListView listView ;

    //for SQLiteDatabaseOperations
    Context ctx = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_listview_contacts);

        //listView = (ListView) findViewById(R.id.list);
/*
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

        //execute the AsyncTask, do stuff in the background

        BackGroundTask backGroundTask = new BackGroundTask(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            backGroundTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "get info");
        else

        backGroundTask.execute("get info");

        //DisplayMyPopulistoListView.ShowMyPopulisto showMyPopulisto = new DisplayMyPopulistoListView.ShowMyPopulisto();
        //showMyPopulisto.execute();

    }









}