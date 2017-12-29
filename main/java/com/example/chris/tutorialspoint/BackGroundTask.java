package com.example.chris.tutorialspoint;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tutorialspoint.R;

/**
 * Created by Chris on 28/12/2017.
 */

public class BackGroundTask extends AsyncTask<String, Review, String> {

    Context ctx;
    DisplayMyPopulistoAdapter displayMyPopulistoAdapter;
    Activity activity;
    ListView listView;
    BackGroundTask(Context ctx) {

        this.ctx = ctx;
        activity = (Activity) ctx;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(String... params) {

        String method = params[0];

        //create an object of SQLiteDatabaseOperations
        SQLiteDatabaseOperations sQLiteDatabaseOperations = new SQLiteDatabaseOperations(ctx);
        if (method.equals("add info"))

        {

            String cat_name = params[1];
            String name = params[2];
            SQLiteDatabase db = sQLiteDatabaseOperations.getWritableDatabase();
            sQLiteDatabaseOperations.putInformation(db, cat_name, name);

            return cat_name + " + " + name + " are inserted";
        }

        //SQLiteDatabaseOperations sqLiteDatabaseOperations = new SQLiteDatabaseOperations(ctx);

        //if the method name is "get info"
        else if (method.equals("get info"))

        {

            listView = (ListView) activity.findViewById(R.id.list);
            //create an object of the SQlite database
            SQLiteDatabase db = sQLiteDatabaseOperations.getReadableDatabase();

            //Now call this method using the sQLiteDatabaseOperations object,
            //and pass the SQL database object, which is db
            Cursor cursor = sQLiteDatabaseOperations.getInformation(db);
            displayMyPopulistoAdapter = new DisplayMyPopulistoAdapter(ctx, R.layout.populisto_list_row);

                    String cat_name, name;

            while (cursor.moveToNext()) {
                cat_name = cursor.getString(cursor.getColumnIndex(TableData.TableInfo.CAT_NAME));
                name = cursor.getString(cursor.getColumnIndex(TableData.TableInfo.NAME));

                //pass arguments into the review
                Review review = new Review(cat_name, name, null, null, null, null);

                publishProgress(review);
            }
                return "get info";
            }
            return null;
        }


        protected void onProgressUpdate(Review... values){

            displayMyPopulistoAdapter.add(values[0]);

        }


        @Override
        protected void onPostExecute (String result) {

            if (result.equals("get info")) {

                listView.setAdapter(displayMyPopulistoAdapter);
            } else {
                Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
            }
        }
}