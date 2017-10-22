

package com.example.chris.tutorialspoint;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutorialspoint.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.id;
import static android.R.attr.targetActivity;

/**
 * Created by Chris on 23/06/2017.
 */

public class SelectPhoneContactAdapter extends BaseAdapter {

    //define a list made out of SelectPhoneContacts and call it theContactsList
    public List<SelectPhoneContact> theContactsList;
    //define an array list made out of SelectContacts and call it arraylist
    private ArrayList<SelectPhoneContact> arraylist;
    Context _c;
    ArrayList<String> MatchingContactsAsArrayList;
    ArrayList<String> checkedContactsAsArrayList;
    ArrayList <String> allNamesofContacts;
    String contactToCheck;
    //we will run through different logic in this custom adapter based on the activity that is passed to it
    private int whichactivity;

    String phoneNumberofContact;
    String[] phoneNumberofContactStringArray;
    String ContactsString;

    public SelectPhoneContactAdapter(final List<SelectPhoneContact> selectPhoneContacts, Context context, int activity) {
        theContactsList = selectPhoneContacts;
        _c = context;
        this.arraylist = new ArrayList<SelectPhoneContact>();
        this.arraylist.addAll(theContactsList);
        whichactivity = activity;

        //we are fetching the array list MatchingContactsAsArrayList, created in VerifyUserPhoneNumber.
        //with this we will put a checkbox beside the matching contacts
        SharedPreferences sharedPreferencesMatchingContactsAsArrayList = PreferenceManager.getDefaultSharedPreferences(_c);
        Gson gsonMatchingContactsAsArrayList = new Gson();
        String jsonMatchingContactsAsArrayList = sharedPreferencesMatchingContactsAsArrayList.getString("MatchingContactsAsArrayList", "");
        Type type1 = new TypeToken<ArrayList<String>>() {
        }.getType();
        MatchingContactsAsArrayList = gsonMatchingContactsAsArrayList.fromJson(jsonMatchingContactsAsArrayList, type1);
        System.out.println("SelectPhoneContactAdapter MatchingContactsAsArrayList :" + MatchingContactsAsArrayList);

        //we are fetching the array list checkedContactsAsArrayList, created in ViewContact.
        //with this we will put a tick in the checkboxes of contacts the review is being shared with
        SharedPreferences sharedPreferencescheckedContactsAsArrayList = PreferenceManager.getDefaultSharedPreferences(_c);
        Gson gsoncheckedContactsAsArrayList = new Gson();
        String jsoncheckedContactsAsArrayList = sharedPreferencescheckedContactsAsArrayList.getString("checkedContactsAsArrayList", "");
        Type type2 = new TypeToken<ArrayList<String>>() {
        }.getType();
        checkedContactsAsArrayList = gsoncheckedContactsAsArrayList.fromJson(jsoncheckedContactsAsArrayList, type2);
        System.out.println("SelectPhoneContactAdapter checkedContactsAsArrayList :" + checkedContactsAsArrayList);

        //for every value in the checkedContactsAsArrayList array list, call it contactToCheck
        //We will be checking which values of contactToCheck are in the MatchingContactsAsArrayList,
        //further down
//        for (int i = 0; i < checkedContactsAsArrayList.size(); i++) {
//            contactToCheck = checkedContactsAsArrayList.get(i);
//            System.out.println("SelectPhoneContactAdapter contactToCheck :" + contactToCheck);
//
//                                        }
                                 }

    @Override
    public int getCount() {
        System.out.println("the amount in arraylist :" + arraylist.size());
        return arraylist.size();
    }

    @Override
    public Object getItem(int i) {
        return arraylist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)

    static class ViewHolder {
        //        In each cell in the listview show the items you want to have
//        Having a ViewHolder caches our ids, instead of having to call and load each one again and again
        TextView title, phone;
        CheckBox check;
        Button invite;
    }

    //CONVERTVIEW IS NOT RECYCLING VIEWS AS INTENDED BECAUSE OF THE
    //justifyListViewHeightBasedOnChildren FUNCTION IN NEWCONTACT
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {



        System.out.println("getView number is :" + i + "convertView is : " + convertView);

        ViewHolder viewHolder = null;

        if (convertView == null) {

            //if there is nothing there (if it's null) inflate the view with the layout
            LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.phone_inflate_listview, null);

            viewHolder = new ViewHolder();
            //      So, for example, title is cast to the name id, in phone_inflate_listview,
            //      phone is cast to the id called no etc
            viewHolder.title = (TextView) convertView.findViewById(R.id.name);
            viewHolder.phone = (TextView) convertView.findViewById(R.id.no);
            viewHolder.invite = (Button)  convertView.findViewById(R.id.btnInvite);
            viewHolder.check = (CheckBox) convertView.findViewById(R.id.checkBoxContact);
            //remember the state of the checkbox
            viewHolder.check.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) _c);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();
            //viewHolder.check.setChecked(true);
        }
//        store the holder with the view
        final SelectPhoneContact data = (SelectPhoneContact) arraylist.get(i);
        //in the listview for contacts, set the name
        viewHolder.title.setText(data.getName());
        //in the listview for contacts, set the number
        viewHolder.phone.setText(data.getPhone());
        //viewHolder.check.setChecked(true);
        ////*********************

        //for every phone number in the MatchingContactsAsArrayList array list...
        for (int number = 0; number < MatchingContactsAsArrayList.size(); number++) {

            //if a phone number is in our array of matching contacts
            if (MatchingContactsAsArrayList.contains(data.getPhone()))

            {
                //if a matching contact, no need to show the Invite button
                viewHolder.invite.setVisibility(View.GONE);
                System.out.println("it's a match: phoneNumberofContact is : " + data.getPhone());
                //once a matching contact is found, no need to keep looping x number of time, move onto next contact
                break;

            }

            else {
                //if not a matching contact, no need to show the check box
                viewHolder.check.setVisibility(View.GONE);

            }

        }




       // For the ViewContact, which has int activity = 0
        if(whichactivity == 0) {
            //This is for ViewContact, to display the contact the review is shared with
            //for every phone number in the checkedContactsAsArrayList array list...
            for (int number2 = 0; number2 < checkedContactsAsArrayList.size(); number2++) {

                //if a phone number is in our array of checked contacts
                if (checkedContactsAsArrayList.contains(data.getPhone())) {
                    //check the box
                    viewHolder.check.setChecked(true);
                }
            }
            //disable the checkbox
            viewHolder.check.setEnabled(false);
        }




        //this is for NewContact, the user can select contacts to check
        //which has int activity = 1
//        if(whichactivity == 0) {
//            viewHolder.check.setChecked(data.isSelected());
//        }


        //if (convertView != null) {
            //make checkboxes in the listview ticked by default

        //}

        viewHolder.check.setTag(data);


        // Return the completed view to render on screen

        return convertView;


}
}
