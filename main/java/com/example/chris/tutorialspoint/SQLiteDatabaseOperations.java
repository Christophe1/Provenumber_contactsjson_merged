package com.example.chris.tutorialspoint;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.chris.tutorialspoint.TableData.TableInfo.CAT_NAME;
import static com.example.chris.tutorialspoint.TableData.TableInfo.NAME;
import static com.example.chris.tutorialspoint.TableData.TableInfo.TABLE_NAME;



/*************************
 * Created by Chris on 28/12/2017.
 * THIS IS USED FOR SQLITE INSERTION. Not using it for the time being,
 * sticking with getting the details from server and see how fast it is.
 *
 * IT WORKS IN CONJUNCTION WITH :
 * displayMyPopulistoAdapter.java
 * displayMyPopulistoListViewjava
 * TableData.java
 * BackGroundTask.java
 */

public class SQLiteDatabaseOperations extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;

    //this is the name of the db to be created
    private static final String DB_NAME = "review.db";

    //create the query for making the table, which we get from TableData.java
    private static final String CREATE_QUERY = "CREATE TABLE " + TABLE_NAME + "(" + TableData.TableInfo.CAT_NAME + " TEXT," +
            NAME + " TEXT);";


    SQLiteDatabaseOperations(Context ctx){
        //create the database, review.db
        super(ctx, DB_NAME, null, DB_VERSION);
        System.out.println("Database has been created");
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //crete the table, review
        db.execSQL(CREATE_QUERY);
        System.out.println("table has been created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sdb, int arg1, int arg2) {


    }


    public void putInformation(SQLiteDatabase db, String cat_name, String name) {

        //write data into the database
        //SQLiteDatabase SQ = db.getReadableDatabase();
        ContentValues cv = new ContentValues();
        //In the CAT_NAME column in the review table, put the value  of the argument cat_name
        cv.put(CAT_NAME, cat_name);
        //In the NAME column in the review table, put the value of the argument name
        cv.put(NAME, name);
        //this will add 1 row of data into the table
        db.insert(TABLE_NAME, null, cv);
        System.out.println("one row inserted");

    }

    public Cursor getInformation(SQLiteDatabase db) {

        //get info to display in ListView
         {
            //make an array of the column names in the table
            String[] columns = {CAT_NAME, NAME};

             //create a cursor object to get info from the db
            Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);

            return cursor;

        }

    }
}