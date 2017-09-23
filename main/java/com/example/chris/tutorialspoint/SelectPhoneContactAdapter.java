

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

    ArrayList<String> allPhonesofContacts;
    ArrayList <String> allNamesofContacts;

    String phoneNumberofContact;
    String[] phoneNumberofContactStringArray;
    String ContactsString;

    public SelectPhoneContactAdapter(final List<SelectPhoneContact> selectPhoneContacts, Context context) {
        theContactsList = selectPhoneContacts;
        _c = context;
        this.arraylist = new ArrayList<SelectPhoneContact>();
        this.arraylist.addAll(theContactsList);

        //we are fetching the array list allPhonesofContacts, created in VerifyUserPhoneNumber.
        //with this we will put all phone numbers of contacts on user's phone into our ListView in NewContact activity
//        SharedPreferences sharedPreferencesallPhonesofContacts = PreferenceManager.getDefaultSharedPreferences(_c);
//        Gson gson = new Gson();
//        String json = sharedPreferencesallPhonesofContacts.getString("allPhonesofContacts", "");
//        Type type = new TypeToken<ArrayList<String>>() {
//        }.getType();
//        allPhonesofContacts = gson.fromJson(json, type);
//        System.out.println("SelectPhoneContactAdapter allPhonesofContacts :" + allPhonesofContacts);

        //I think we have to set the size of the string array, first of all, before adding values to it
        //phoneNumberofContactStringArray = new String[allPhonesofContacts.size()];

        //phoneNumberofContactStringArray will contain all the values in allPhonesofContacts
        //phoneNumberofContactStringArray = allPhonesofContacts.toArray(phoneNumberofContactStringArray);

        //pass phoneNumberofContactStringArray to NewContact, so we can do a for loop:
        //for every string value in the phoneNumberofContactStringArray we will call it phoneNumberofContact
        //It looks like Shared Preferences only works easily with strings so best way to bring the
        // string array in Shared Preferences is with Gson.
//        SharedPreferences sharedPrefsphoneNumberofContactStringArray = PreferenceManager.getDefaultSharedPreferences(_c);
//        SharedPreferences.Editor editorphoneNumberofContactStringArray = sharedPrefsphoneNumberofContactStringArray.edit();
//        Gson gsonphoneNumberofContactStringArray = new Gson();
//
//        String jsonphoneNumberofContactStringArray = gsonphoneNumberofContactStringArray.toJson(phoneNumberofContactStringArray);
//
//        editorphoneNumberofContactStringArray.putString("phoneNumberofContactStringArray", jsonphoneNumberofContactStringArray);
//        editorphoneNumberofContactStringArray.commit();
//        System.out.println("SelectPhoneContactAdapter phoneNumberofContactStringArray :" + Arrays.toString(phoneNumberofContactStringArray));

//        Bundle bundle=new Bundle();
//        bundle.putStringArray("DATA", phoneNumberofContactStringArray);
//        Intent sendIntent=new Intent(_c, NewContact.class);
//        sendIntent.putExtras(bundle);
       // _c.startActivity(sendIntent);


       // saveArray(phoneNumberofContactStringArray,ContactsString,_c);
        //System.out.println("SelectPhoneContactAdapter ContactsString :" + ContactsString);


        //for every string value in the phoneNumberofContactStringArray call it phoneNumberofContact
//        for (int i = 0; i < allPhonesofContacts.size(); i++) {
//
//            phoneNumberofContact = allPhonesofContacts.get(i);
//
//            {
//                System.out.println("SelectPhoneContactAdapter: phoneNumberofContact : " + phoneNumberofContact);
//
//                System.out.println("SelectPhoneContactAdapter: the phone numbers are : " + phoneNumberofContactStringArray[i]);
//            }
//
//
//            SelectPhoneContact selectContact = new SelectPhoneContact();
//
//                selectPhoneContacts.add(selectContact);
//
//                selectContact.setPhone(phoneNumberofContact);
//        }
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

        viewHolder.check.setChecked(data.isSelected());
        viewHolder.check.setTag(data);

        // Return the completed view to render on screen

        return convertView;

    }
}
