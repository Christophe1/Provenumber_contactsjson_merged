package com.example.chris.populisto;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import static com.example.chris.populisto.VerifyUserPhoneNumber.activity;

/**
 * Created by Chris on 14/10/2017.
 */

public class GlobalFunctions {

    public static void simpleMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public void simpleMessage2() {
        System.out.println("simpleMessage2 text goes here");
        // Toast.makeText(this, "waaaaaw", Toast.LENGTH_LONG).show();

    }

    //always show the overflow menu, some models of phone don't show it by default
/*    public static void makeActionOverflowMenuShown(Context context) {
        //devices with hardware menu button (e.g. Samsung Note) don't show action overflow menu
        try {
            ViewConfiguration config = ViewConfiguration.get(context);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            //Log.d(TAG, e.getLocalizedMessage());
        }

    }*/


    //*******9/11/2018*************
    //this is the function we call to measure the height of the listview
    //we need this because there are problems with a listview within a scrollview
  /*  public static void justifyListViewHeightBasedOnChildren (Context context, ListView listView) {

        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();

        System.out.println("the getcount is " + adapter.getCount());
        System.out.println("the height is " + par.height);
    }*/

    //this is the function that clears all checkboxes in a viewgroup, the listview
    //would be better if it just cleared MatchingContacts rather than everything
/*    public static void uncheckAllChildrenCascade(ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View v = vg.getChildAt(i);
            if (v instanceof CheckBox) {
                ((CheckBox) v).setChecked(false);
            } else if (v instanceof ViewGroup) {
                uncheckAllChildrenCascade((ViewGroup) v);
            }
        }
    }*/

/*    //this is the function that checks all checkboxes in a viewgroup, the listview
    //would be better if it just checked MatchingContacts rather than everything

    public static void checkAllChildrenCascade(ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View v = vg.getChildAt(i);
            if (v instanceof CheckBox) {
                ((CheckBox) v).setChecked(true);
            } else if (v instanceof ViewGroup) {
                checkAllChildrenCascade((ViewGroup) v);
            }
        }
    }*/

    //********************

    public static void troubleContactingServerDialog(Context context) {

        //If there is an error (such as contacting server for example) then
        //show a message like:
        //Sorry, can't contact server right now. Is internet access enabled?, try again, Cancel
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder
            //.setTitle("Delete entry")
            //prevent box being dismissed on back key press or touch outside
            .setCancelable(false)
            .setMessage("Sorry, can't contact server right now. Is internet access enabled?")
            .setPositiveButton("Try Now", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    //refresh the activity, if the user choses "Try Now"
                    activity.recreate();
                }
            })
            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    //close the app
                    activity.finish();
                    System.exit(0);
                }
            })
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();

    }

    //get the date from server column, and then format correctly like "9 December 2018"
    public static String getDateandFormat(String TimefromServer) {

        //split the string, e.g:  2018-11-10 04:30:01, when we get to space, " "
        //so we just have 2018-11-10
        String[] parts = TimefromServer.split(" ");
        String first_part = parts[0];

        //split the new string at hyphens
        String[]date_month_year = first_part.split("-");

        String year = date_month_year[0];
        String month = date_month_year[1];
        String date = date_month_year[2];

        String new_date = date.replaceFirst("^0+(?!$)", "");

        System.out.println("the year is" + year);
        System.out.println("the month is" + month);
        System.out.println("the date is" + date);

        String month_in_words = "";

        switch(month) {

            case "01" :
                month_in_words = "January";
                break;

            case "02" :
                month_in_words = "February";
                break;

            case "03" :
                month_in_words = "March";
                break;

            case "04" :
                month_in_words = "April";
                break;

            case "05" :
                month_in_words = "May";
                break;

            case "06" :
                month_in_words = "June";
                break;

            case "07" :
                month_in_words = "July";
                break;

            case "08" :
                month_in_words = "August";
                break;

            case "09" :
                month_in_words = "September";
                break;

            case "10" :
                month_in_words = "October";
                break;

            case "11" :
                month_in_words = "November";
                break;

            case "12" :
                month_in_words = "December";
                break;
        }

        return new_date + " " + month_in_words + " " + year;

    }
}





