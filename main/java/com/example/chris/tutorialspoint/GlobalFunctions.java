package com.example.chris.tutorialspoint;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;

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
    public static void makeActionOverflowMenuShown(Context context) {
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

    }


    //this is the function we call to measure the height of the listview
    //we need this because there are problems with a listview within a scrollview
    public static void justifyListViewHeightBasedOnChildren (Context context, ListView listView) {

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
    }

    //this is the function that clears all checkboxes in a viewgroup, the listview
    //would be better if it just cleared MatchingContacts rather than everything
    public static void uncheckAllChildrenCascade(ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View v = vg.getChildAt(i);
            if (v instanceof CheckBox) {
                ((CheckBox) v).setChecked(false);
            } else if (v instanceof ViewGroup) {
                uncheckAllChildrenCascade((ViewGroup) v);
            }
        }
    }

    //this is the function that checks all checkboxes in a viewgroup, the listview
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
    }

}





