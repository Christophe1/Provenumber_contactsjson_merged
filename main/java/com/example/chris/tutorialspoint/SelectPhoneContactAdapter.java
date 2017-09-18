

package com.example.chris.tutorialspoint;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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

import org.json.JSONArray;
import org.json.JSONObject;

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

    ArrayList <String> allPhonesofContacts;
    ArrayList <String> allNamesofContacts;

    String phoneNumberofContact;
    String phoneNameofContact;


    public SelectPhoneContactAdapter(final List<SelectPhoneContact> selectPhoneContacts, Context context) {
        theContactsList = selectPhoneContacts;
        _c = context;
        this.arraylist = new ArrayList<SelectPhoneContact>();
        this.arraylist.addAll(theContactsList);


        //  when the form loads, get the String MatchingContacts in the SharedPreferences file, created in
        // VerifyUserPhoneNumber
        // it will be of the form of a JSONArray, like [{"phone_number":"+35312345"}, {"phone_number": etc...
        // We get this string from our php file, checkcontact.php. Then we want to extract the phone nubers
        //and compare against the contacts on the user's phone.
        SharedPreferences sharedPreferencetheMatchingContacts = _c.getSharedPreferences("MyData", Context.MODE_PRIVATE);
        MatchingContactsAsString = sharedPreferencetheMatchingContacts.getString("thematchingcontacts", "");
        System.out.println("SelectPhoneContactAdapter matchingcontacts :" + MatchingContactsAsString);

//NEED TO SET UP SHAREDPREFERENCE FILES FOR THESE

        //get the Array list of all contacts' numbers on user's phone, allPhonesofContacts, from PopulistoListView
        allPhonesofContacts = getIntent().getStringArrayListExtra("allPhonesofContacts");
        System.out.println("allphonesofcontacts from NewContact" + allPhonesofContacts);

        //get the Array list of all contacts' names on user's phone, allPhonesofContacts, from PopulistoListView
        allNamesofContacts = getIntent().getStringArrayListExtra("allNamesofContacts");
        System.out.println("allnamesofcontacts from NewContact" + allNamesofContacts);

//        ***************************************

        //make an arraylist which will hold the phone_number part of the MatchingContacts string
        MatchingContactsAsArrayList = new ArrayList<String>();
        try {
            JSONArray Object = new JSONArray(MatchingContactsAsString);
            for (int x = 0; x < Object.length(); x++) {
                final JSONObject obj = Object.getJSONObject(x);
                MatchingContactsAsArrayList.add(obj.getString("phone_number"));

            }
            System.out.println("MatchingContactsAsArrayList :" + MatchingContactsAsArrayList);
            // Log.v("index valuee", MatchingContacts);
            //get the names and numbers from VerifyPhoneNumber and bring them
            //over to this class
            //Intent myIntent = ((NewContact) context).getIntent();
            //get the Arraylist of all contacts' numbers on user's phone, allPhonesofContacts, from PopulistoListView
            //MatchingContacts = ((NewContact) context).getIntent().getStringArrayListExtra("MatchingContacts");
            //System.out.println("CustomAdapter : MatchingContacts from NewContact" + MatchingContacts);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //if a phone number is in our array of matching contacts
        if (MatchingContactsAsArrayList.contains(phoneNumberofContact))

        {
            // insert the contact at the beginning of the listview
            selectPhoneContacts.add(0, selectContact);
            // checkBoxforContact.setVisibility(View.VISIBLE);

        }

        else {
            // insert it at the end (default)
            selectPhoneContacts.add(selectContact);
            //makeinvisible();
        }


        selectContact.setName(phoneNameofContact);
        //    selectContact.setPhone(phoneNumberofContact);
        selectContact.setPhone(phoneNumberofContact);
        //selectContact.setSelected(is);

    }

    return null;
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