

package com.example.chris.tutorialspoint;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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
import java.util.HashMap;
import java.util.List;

import static android.R.attr.id;

/**
 * Created by Chris on 23/06/2017.
 */

public class SelectPhoneContactAdapter extends BaseAdapter {

    //define a list made out of SelectPhoneContacts and call it theContactsList
    public List<SelectPhoneContact> theContactsList;
    //define an array list made out of SelectContacts and call it arraylist
    private ArrayList<SelectPhoneContact> arraylist;
    Context _c;
    String MatchingContactsAsString;
    ArrayList<String> MatchingContactsAsArrayList;

    ArrayList<String> allPhonesofContacts;
    ArrayList<String> allNamesofContacts;

    String phoneNumberofContact;
    String phoneNameofContact;

    String[] phoneNumberofContactStringArray;
    String[] phoneNameofContactStringArray;

    public SelectPhoneContactAdapter(final List<SelectPhoneContact> selectPhoneContacts, Context context) {
        theContactsList = selectPhoneContacts;
        _c = context;
        this.arraylist = new ArrayList<SelectPhoneContact>();
        this.arraylist.addAll(theContactsList);


        //  when the activity loads, get the String MatchingContacts in the SharedPreferences file, created in
        // VerifyUserPhoneNumber
        // it will be of the form of a JSONArray, like [{"phone_number":"+35312345"}, {"phone_number": etc...
        // We get this string from our php file, checkcontact.php. Then we want to extract the phone numbers
        //and compare against the contacts on the user's phone.
        SharedPreferences sharedPreferencetheMatchingContacts = _c.getSharedPreferences("MyData", Context.MODE_PRIVATE);
        MatchingContactsAsString = sharedPreferencetheMatchingContacts.getString("thematchingcontacts", "");
        System.out.println("SelectPhoneContactAdapter matchingcontacts :" + MatchingContactsAsString);

//NEED TO SET UP SHAREDPREFERENCE FILES FOR THESE

        //get the Array list of all contacts' numbers on user's phone, allPhonesofContacts, from PopulistoListView
        // allPhonesofContacts = ((SelectPhoneContactAdapter) _c).getIntent().getStringArrayListExtra("allPhonesofContacts");
        //System.out.println("allphonesofcontacts from NewContact" + allPhonesofContacts);

        //we are fetching the array list allPhonesofContacts, created in VerifyUserPhoneNumber.
        //with this we will put all phone numbers of contacts on user's phone into our ListView in NewContact activity
        SharedPreferences sharedPreferencesallPhonesofContacts = PreferenceManager.getDefaultSharedPreferences(_c);
        Gson gson = new Gson();
        String json = sharedPreferencesallPhonesofContacts.getString("allPhonesofContacts", "");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        allPhonesofContacts = gson.fromJson(json, type);
        System.out.println("SelectPhoneContactAdapter allPhonesofContacts :" + allPhonesofContacts);

        //we are fetching the array list allNamesofContacts, created in VerifyUserPhoneNumber.
        //with this we will put all phone names of contacts on user's phone into our ListView in NewContact activity
        SharedPreferences sharedPreferencesallNamesofContacts = PreferenceManager.getDefaultSharedPreferences(_c);
        Gson gsonNames = new Gson();
        String jsonNames = sharedPreferencesallNamesofContacts.getString("allNamesofContacts", "");
        Type typeNames = new TypeToken<ArrayList<String>>() {
        }.getType();
        allNamesofContacts = gson.fromJson(jsonNames, type);
        System.out.println("SelectPhoneContactAdapter allNamesofContacts :" + allNamesofContacts);


        //allPhonesofContacts = sharedPreferencetheMatchingContacts.getString("allPhonesofContacts", "");
        //System.out.println("SelectPhoneContactAdapter allPhonesofContacts :" + allPhonesofContacts);

        //get the Array list of all contacts' names on user's phone, allPhonesofContacts, from PopulistoListView
        //allNamesofContacts = getIntent().getStringArrayListExtra("allNamesofContacts");
        // System.out.println("allnamesofcontacts from NewContact" + allNamesofContacts);

//        ***************************************

        //make an arraylist which will hold the phone_number part of the MatchingContacts string
        MatchingContactsAsArrayList = new ArrayList<String>();
        try {
            JSONArray Object = new JSONArray(MatchingContactsAsString);
            for (int x = 0; x < Object.length(); x++) {
                final JSONObject obj = Object.getJSONObject(x);
                MatchingContactsAsArrayList.add(obj.getString("phone_number"));


                // Log.v("index valuee", MatchingContacts);
                //get the names and numbers from VerifyPhoneNumber and bring them
                //over to this class
                //Intent myIntent = ((NewContact) context).getIntent();
                //get the Arraylist of all contacts' numbers on user's phone, allPhonesofContacts, from PopulistoListView
                //MatchingContacts = ((NewContact) context).getIntent().getStringArrayListExtra("MatchingContacts");
                //System.out.println("CustomAdapter : MatchingContacts from NewContact" + MatchingContacts);

            }

            System.out.println("MatchingContactsAsArrayList :" + MatchingContactsAsArrayList);


        } catch (Exception e) {
            e.printStackTrace();
        }


        //Instead of running through the cursor values again, in VerifyUserPhoneNumber, let's just import them from
        //there

        //Make a string array, phoneNumberofContactStringArray, that will be the same size
        //as the arraylist allPhonesofContacts, which has been imported from VerifyUserPhoneNumber
        phoneNumberofContactStringArray = new String[allPhonesofContacts.size()];

        //Make a string array, phoneNameofContactStringArray, that will be the same size
        //as the arraylist allNamesofContacts, which has been imported from VerifyUserPhoneNumber
        phoneNameofContactStringArray = new String[allNamesofContacts.size()];

        //phoneNumberofContactStringArray will contain all the values in allPhonesofContacts
        phoneNumberofContactStringArray = allPhonesofContacts.toArray(phoneNumberofContactStringArray);

        //phoneNameofContactStringArray will contain all the values in allNamesofContacts
        phoneNameofContactStringArray = allNamesofContacts.toArray(phoneNameofContactStringArray);

        //for every string value in the two arrays Phone and Name(the two should be same size, as every name has a phone number)
        //give them a respective name of phoneNumberofContact and phoneNameofContact
        for (int i = 0; i < phoneNumberofContactStringArray.length; i++) {

            phoneNumberofContact = phoneNumberofContactStringArray[i];
            phoneNameofContact = phoneNameofContactStringArray[i];

            {
                System.out.println("SelectPhoneContactAdapter: the phone numbers are : " + phoneNumberofContactStringArray[i]);
                System.out.println("SelectPhoneContactAdapter: the phone names are : " + phoneNameofContactStringArray[i]);
            }


            SelectPhoneContact selectContact = new SelectPhoneContact();

            //if a phone number is in our array of matching contacts
            if (MatchingContactsAsArrayList.contains(phoneNumberofContact))

            {
                // insert the contact at the beginning of the listview
                selectPhoneContacts.add(0, selectContact);
                // checkBoxforContact.setVisibility(View.VISIBLE);

            } else {
                // insert it at the end (default)
                selectPhoneContacts.add(selectContact);
                //makeinvisible();
            }


            selectContact.setName(phoneNameofContact);
            //    selectContact.setPhone(phoneNumberofContact);
            selectContact.setPhone(phoneNumberofContact);
            //selectContact.setSelected(is);

        }
    }

   // return null;


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
