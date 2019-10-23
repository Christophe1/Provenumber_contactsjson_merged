package com.populisto.chris.populisto;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import androidx.appcompat.app.AlertDialog;
import android.widget.Toast;

import com.populisto.chris.populisto.R;

import static android.graphics.Color.rgb;
import static com.populisto.chris.populisto.VerifyUserPhoneNumber.activity;

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

    //change border colour on sharing state
    public static void sharing_border_colour(Activity activity, String hex_colour) {

        //for boxes
        LayerDrawable layerDrawable1 = (LayerDrawable) activity.findViewById(R.id.textViewCategory).getBackground();
        LayerDrawable layerDrawable2 = (LayerDrawable) activity.findViewById(R.id.textViewName).getBackground();
        LayerDrawable layerDrawable3 = (LayerDrawable) activity.findViewById(R.id.textViewPhone).getBackground();
        LayerDrawable layerDrawable4 = (LayerDrawable) activity.findViewById(R.id.textViewAddress).getBackground();
        LayerDrawable layerDrawable5 = (LayerDrawable) activity.findViewById(R.id.textViewComment).getBackground();

        GradientDrawable gradientDrawable1 = (GradientDrawable) layerDrawable1
            .findDrawableByLayerId(R.id.textbox_shape);
        GradientDrawable gradientDrawable2 = (GradientDrawable) layerDrawable2
            .findDrawableByLayerId(R.id.textbox_shape);
        GradientDrawable gradientDrawable3 = (GradientDrawable) layerDrawable3
            .findDrawableByLayerId(R.id.textbox_shape);
        GradientDrawable gradientDrawable4 = (GradientDrawable) layerDrawable4
            .findDrawableByLayerId(R.id.textbox_shape);
        GradientDrawable gradientDrawable5 = (GradientDrawable) layerDrawable5
            .findDrawableByLayerId(R.id.textbox_shape);

        // Change stroke color to BLUE. (Assumes 2px stroke width.)
        gradientDrawable1.setStroke(2, Color.parseColor(hex_colour));
        gradientDrawable2.setStroke(2, Color.parseColor(hex_colour));
        gradientDrawable3.setStroke(2, Color.parseColor(hex_colour));
        gradientDrawable4.setStroke(2, Color.parseColor(hex_colour));
        gradientDrawable5.setStroke(2, Color.parseColor(hex_colour));


    }

    //for changing colour on btnTryAgain TextView click....
/*    public static class CustomTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch(motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    ((TextView)view).setTextColor(rgb(0,0,255)); //blue
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    ((TextView)view).setTextColor(0xFF000000); //black
                    break;
             }

            return true;
        }
    }*/



}





