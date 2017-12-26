package com.example.chris.tutorialspoint;

import android.provider.BaseColumns;

/**
 * Created by Chris on 26/12/2017.
 */

public final class TableData {

    public TableData(){

    }

    public static abstract class TableInfo {

        //These are column names inside the Table

        public static final String CAT_NAME = "cat_name";
        public static final String NAME = "name";
        public static final String PHONE = "phone";
        public static final String ADDRESS = "address";
        public static final String COMMENT = "comment";
        public static final String DATABASE_NAME = "pApp";
        public static final String TABLE_NAME = "review";

    }

}
