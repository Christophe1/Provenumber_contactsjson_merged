package com.populisto.chris.populisto;


/*************************
 * Created by Chris on 28/12/2017.
 * THIS IS USED FOR SQLITE INSERTION. Not using it for the time being,
 * sticking with getting the details from server and see how fast it is.
 *
 * IT WORKS IN CONJUNCTION WITH :
 * displayMyPopulistoAdapter.java
 * displayMyPopulistoListViewjava
 * SQLiteDatabaseOperations.java
 * BackGroundTask.java
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
        //public static final String DATABASE_NAME = "pApp";
        public static final String TABLE_NAME = "review";

    }

}
