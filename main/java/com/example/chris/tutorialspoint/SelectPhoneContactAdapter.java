
package com.example.chris.tutorialspoint;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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

    Intent intent;

    SharedPreferences.Editor editor;

    //so we can set the radio button to Phone Contacts in NewContact, when a checkbox is unchecked
    public NewContact.changeRadioButtontoPhoneContacts radioButtontoPhoneContacts;

    //so we can set the radio button to Phone Contacts in EditContact, when a checkbox is unchecked
    public EditContact.radioButtontoPhoneContactsEdit radioButtontoPhoneContactsEdit;

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
        //In each cell in the listview show the items you want to have
        //Having a ViewHolder caches our ids, instead of having to call and load each one again and again
        TextView title, phone;
        CheckBox check;
        Button invite;
    }

    //CONVERTVIEW IS NOT RECYCLING VIEWS AS INTENDED BECAUSE OF THE
    //justifyListViewHeightBasedOnChildren FUNCTION IN NEWCONTACT
    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {


        //initialize a sharedpreferences file called "sharedPrefs", which will be private, for our app only
        //we are doing this so the checkbox state in the listview will be saved, so user can come back to it if the phone sleeps
        SharedPreferences sharedPrefs = _c.getSharedPreferences("sharedPrefsFile", Context.MODE_PRIVATE);


        //this is the SelectPhoneContact object; consists of textboxes, buttons, checkbox
        final SelectPhoneContact data = (SelectPhoneContact) arraylist.get(i);

        System.out.println("getView number is :" + i + "convertView is : " + convertView);

        ViewHolder holder = null;


        if (convertView == null) {

            //if there is nothing there (if it's null) inflate the view with the layout
            LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.phone_inflate_listview, null);

            holder = new ViewHolder();


            //So, for example, title is cast to the name id, in phone_inflate_listview,
            //phone is cast to the id called no etc
            holder.title = (TextView) convertView.findViewById(R.id.name);
            holder.phone = (TextView) convertView.findViewById(R.id.no);
            holder.invite = (Button) convertView.findViewById(R.id.btnInvite);
            holder.check = (CheckBox) convertView.findViewById(R.id.checkBoxContact);
            // holder.check.setTag(arraylist.get(i).getPhone());

            //if the activity is NewContact
            if (whichactivity == 1) {
                holder.check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    //when a checkbox in the Listview is clicked
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        SelectPhoneContact data = (SelectPhoneContact) cb.getTag();
                        //if it is set to unchecked
                        if (cb.isChecked() == false)
                        //need this to change radio button to Phone Contacts,
                        //if a checkbox is changed to false
                        {
                            radioButtontoPhoneContacts.update();
                        }
                        Toast.makeText(_c,
                                "Clicked on Checkbox: " + data.getPhone() +
                                        " is " + cb.isChecked(),
                                Toast.LENGTH_LONG).show();
                        data.setSelected(cb.isChecked());
                        //  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // if (data.setSelected(isChecked);) {
                        //   Toast.makeText(_c, "True ", Toast.LENGTH_LONG).show();
                        // }
                        // else {
                        //String phone_no = data.get
                        //System.out.println("Custom adapter pos " + i);
                        // Toast.makeText(_c, "phone number " + data.getPhone(), Toast.LENGTH_LONG).show();
                        // Toast.makeText(_c, "False ", Toast.LENGTH_LONG).show();
                    }
                });

            }

            //if the activity is EditContact
            if (whichactivity == 2) {
                holder.check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    //when a checkbox in the Listview is clicked
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        SelectPhoneContact data = (SelectPhoneContact) cb.getTag();
                        //if it is set to unchecked
                        if (cb.isChecked() == false)
                        //need this to change radio button to Phone Contacts,
                        //if a checkbox is changed to false
                        {
                            radioButtontoPhoneContactsEdit.update();
                        }
                        Toast.makeText(_c,
                                "Clicked on Checkbox: " + data.getPhone() +
                                        " is " + cb.isChecked(),
                                Toast.LENGTH_LONG).show();
                        data.setSelected(cb.isChecked());

                    }
                });

            }


            // if(whichactivity == 1) {
            // check the checkboxes by default, phoneContacts
            //   viewHolder.check.setChecked(true);
            // }
            //remember the state of the checkbox
            // viewHolder.check.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) _c);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
            //viewHolder.check.setChecked(true);
        }


        //in the listview for contacts, set the name
        holder.title.setText(data.getName());

        //in the listview for contacts, set the number
        holder.phone.setText(data.getPhone());

        //this is to remember the checked boxes, so if the phone goes to sleep
        //we'll still see the boxes checked by the user on resume
        //if the activity is NewContact
        if (whichactivity == 1) {

            //initialize the SharedPreferences editor
            editor = sharedPrefs.edit();

            //in the listview for checkboxes, get the checkbox values from the sharedpreferences file
            holder.check.setChecked(sharedPrefs.getBoolean("CheckValue" + i, false));

            //when the checkbox changes, edit those changes and commit,
            holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    //edit and commit the checkbox status to the sharedpreferences file
                    //"CheckValue"+1 is the key, isChecked is the value
                    editor.putBoolean("CheckValue" + i, isChecked);
                    editor.commit();
                }
            });

        }

        //if the activity is ViewContact
        //  if(whichactivity == 0) {

        //initialize the SharedPreferences editor
        // editor = sharedPrefs.edit();

        //in the listview for checkboxes, get the checkbox values from the sharedpreferences file
        // holder.check.setChecked(sharedPrefs.getBoolean("viewContactCheckValue" + i, false));
        // holder.check.setEnabled(false);

        //when the checkbox changes, edit those changes and commit,
/*            holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    //edit and commit the checkbox status to the sharedpreferences file
                    //"CheckValue"+1 is the key, isChecked is the value
                    editor.putBoolean("viewContactCheckValue" + i, isChecked);
                    editor.commit();
                }
            });*/

        // }

        ///*************************

        //for every phone number in the MatchingContactsAsArrayList array list...
        for (int number = 0; number < MatchingContactsAsArrayList.size(); number++) {

            //if a phone number is in our array of matching contacts
            if (MatchingContactsAsArrayList.contains(data.getPhone()))

            {
                //if a matching contact, no need to show the Invite button
                holder.invite.setVisibility(View.GONE);
                System.out.println("it's a match: phoneNumberofContact is : " + data.getPhone());
                //once a matching contact is found, no need to keep looping x number of time, move onto next contact
                break;

            } else {
                //if not a matching contact, no need to show the check box
                holder.check.setVisibility(View.GONE);

            }

        }


        // For the ViewContact, which has int activity = 0
        if (whichactivity == 0) {


            //This is for ViewContact, to display the contact the review is shared with
            //for every phone number in the checkedContactsAsArrayList array list...
            for (int number2 = 0; number2 < checkedContactsAsArrayList.size(); number2++) {

                //if a phone number is in our array of checked contacts
                if (checkedContactsAsArrayList.contains(data.getPhone())) {
                    //check the box
                    holder.check.setChecked(true);
                }
            }
            //disable the checkbox
            holder.check.setEnabled(false);
        }

        // For the EditContact, which has int activity = 2
        //pretty much the same as the ViewContact code above, except we
        //don't disable the checkboxes in the listview, they can be edited
        if (whichactivity == 2) {


            //This is for ViewContact, to display the contact the review is shared with
            //for every phone number in the checkedContactsAsArrayList array list...
            for (int number2 = 0; number2 < checkedContactsAsArrayList.size(); number2++) {

                //if a phone number is in our array of checked contacts
                if (checkedContactsAsArrayList.contains(data.getPhone())) {
                    //check the box
                    holder.check.setChecked(true);
                }
            }

            //this is to remember the checked boxes, so if the phone goes to sleep
            //we'll still see the boxes checked by the user on resume

            //initialize the SharedPreferences editor
            editor = sharedPrefs.edit();

            //in the listview for checkboxes, get the checkbox values from the sharedpreferences file
            //When the checkbox changes to checked it will committed, at the bottom of this code.
            //If it has been checked, show it as checked
            if (holder.check.isChecked()) {
                holder.check.setChecked(sharedPrefs.getBoolean("CheckValueEdit" + i, true));
            } else {
            //if not, show it as unchecked
                holder.check.setChecked(sharedPrefs.getBoolean("CheckValueEdit" + i, false));
            }


            //when the checkbox changes, edit those changes and commit,
            holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    //edit and commit the checkbox status to the sharedpreferences file
                    //"CheckValue"+1 is the key, isChecked is the value
                    editor.putBoolean("CheckValueEdit" + i, isChecked);
                    editor.commit();
                }
            });



        }



        //this is for NewContact, the user can select contacts to check
        //which has int activity = 1
//        if(whichactivity == 0) {
//            viewHolder.check.setChecked(data.isSelected());
//        }


        //if (convertView != null) {
        //make checkboxes in the listview ticked by default

        //}

        holder.check.setTag(data);

        return convertView;

        }
        // Return the completed view to render on screen



    }

