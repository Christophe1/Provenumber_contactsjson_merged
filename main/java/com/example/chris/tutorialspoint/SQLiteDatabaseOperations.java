package com.example.chris.tutorialspoint;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.chris.tutorialspoint.TableData.TableInfo.CAT_NAME;
import static com.example.chris.tutorialspoint.TableData.TableInfo.NAME;
import static com.example.chris.tutorialspoint.TableData.TableInfo.TABLE_NAME;


/**
 * Created by Chris on 26/12/2017.
 */

public class SQLiteDatabaseOperations extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;

    //this is the name of the db to be created
    private static final String DB_NAME = "review.db";

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
        cv.put(CAT_NAME, cat_name);
        cv.put(NAME, name);

        //this will add 1 row of data into the table
        db.insert(TABLE_NAME, null, cv);
        System.out.println("one row inserted");

    }

    public Cursor getInformation(SQLiteDatabase db) {

        //SQLiteDatabase SQ = dop.getReadableDatabase();
        {
            //make an array of the column names in the table
            String[] columns = {CAT_NAME, NAME};

            Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);

            return cursor;

        }

    }
}