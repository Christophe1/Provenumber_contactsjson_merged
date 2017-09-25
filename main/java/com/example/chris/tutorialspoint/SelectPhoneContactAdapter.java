

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.tutorialspoint.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
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
    ArrayList <String> allNamesofContacts;

    String phoneNumberofContact;
    String[] phoneNumberofContactStringArray;
    String ContactsString;

    public SelectPhoneContactAdapter(final List<SelectPhoneContact> selectPhoneContacts, Context context) {
        theContactsList = selectPhoneContacts;
        _c = context;
        this.arraylist = new ArrayList<SelectPhoneContact>();
        this.arraylist.addAll(theContactsList);

        //we are fetching the array list MatchingContactsAsArrayList, created in NewContact.
        //with this we will put a checkbox beside the matching contacts
        SharedPreferences sharedPreferencesMatchingContactsAsArrayList = PreferenceManager.getDefaultSharedPreferences(_c);
        Gson gsonMatchingContactsAsArrayList = new Gson();
        String jsonMatchingContactsAsArrayList = sharedPreferencesMatchingContactsAsArrayList.getString("MatchingContactsAsArrayList", "");
        Type type1 = new TypeToken<ArrayList<String>>() {
        }.getType();
        MatchingContactsAsArrayList = gsonMatchingContactsAsArrayList.fromJson(jsonMatchingContactsAsArrayList, type1);
        System.out.println("SelectPhoneContactAdapter MatchingContactsAsArrayList :" + MatchingContactsAsArrayList);

    }

    @Override
    public int getCount() {
        System.out.println("the amount in arraylist :" + theContactsList.size());
        return arraylist.size();
    }

    @Override
    public Object getItem(int i) {
        return theContactsList.get(i);
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
    }

    //CONVERTVIEW IS NOT RECYCLING VIEWS AS INTENDED BECAUSE OF THE
    //justifyListViewHeightBasedOnChildren FUNCTION IN NEWCONTACT
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        System.out.println("getView number is :" + i + "convertView is : " + convertView);
        //we're naming our convertView as view
        //  View view = convertView;
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

            viewHolder.check = (CheckBox) convertView.findViewById(R.id.checkBoxContact);
            // viewHolder.check.setVisibility(View.GONE);

            //remember the state of the checkbox
            viewHolder.check.setOnCheckedChangeListener((NewContact) _c);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();

        }
//        store the holder with the view
        final SelectPhoneContact data = (SelectPhoneContact) theContactsList.get(i);
        //in the listview for contacts, set the name
        viewHolder.title.setText(data.getName());
        //in the listview for contacts, set the number
        viewHolder.phone.setText(data.getPhone());

        ////*********************

        //for every phone number in the MatchingContactsAsArrayList array list...
        for (int number = 0; number < MatchingContactsAsArrayList.size(); number++) {

            //if a phone number is in our array of matching contacts
            if (MatchingContactsAsArrayList.contains(data.getPhone()))

            {
                viewHolder.check.setVisibility(View.VISIBLE);
                System.out.println("it's a match: phoneNumberofContact is : " + data.getPhone());
                //once a matching contact is found, no need to keep looping x number of time, move onto next contact
                break;

            }

            else {

                viewHolder.check.setVisibility(View.INVISIBLE);

            }

        }


        viewHolder.check.setChecked(data.isSelected());



        viewHolder.check.setTag(data);

        // Return the completed view to render on screen

        return convertView;

    }
}
