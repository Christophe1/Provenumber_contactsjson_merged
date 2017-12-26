package com.example.chris.tutorialspoint;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.chris.tutorialspoint.TableData.TableInfo.CAT_NAME;
import static com.example.chris.tutorialspoint.TableData.TableInfo.NAME;
import static com.example.chris.tutorialspoint.TableData.TableInfo.TABLE_NAME;


/**
 * Created by Chris on 26/12/2017.
 */

public class DatabaseOperations extends SQLiteOpenHelper {

    public static final int database_version = 1;
    //this is the query to create table in the db, will just be called once to create
    public String CREATE_QUERY = "CREATE TABLE " + TABLE_NAME + "("+ TableData.TableInfo.CAT_NAME+ " TEXT,"+
            NAME+" TEXT);";


    public DatabaseOperations(Context context){

        super(context, TableData.TableInfo.DATABASE_NAME, null, database_version);

    }

    @Override
    public void onCreate(SQLiteDatabase sdb){

        //crete the table, review
        sdb.execSQL(CREATE_QUERY);
        System.out.println("table has been created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sdb, int arg1, int arg2){


    }



    public void putInformation(DatabaseOperations dop, String cat_name, String name){

        //write data into the database
        SQLiteDatabase SQ = dop.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CAT_NAME,cat_name);
        cv.put(NAME,name);
        long k = SQ.insert(TABLE_NAME, null, cv);
        System.out.println("one row inserted");

    }

}
