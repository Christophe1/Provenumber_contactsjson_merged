package com.example.chris.tutorialspoint;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import static android.R.attr.data;

/**
 * Created by Chris on 07/01/2018.
 */

public class PopulistoContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //for changing the colour of 'Phone COntacts' button
    private Context mContext;

    //make a List containing info about SelectPhoneContact objects
    public static List<SelectPhoneContact> theContactsList;

    Context context_type;

    //we will run through different logic in this custom adapter based on the activity that is passed to it
    private int whichactivity;

    public static ArrayList<String> allPhonesofContacts;
    public static ArrayList<String> allNamesofContacts;
    public static ArrayList<String> MatchingContactsAsArrayList;
    public static ArrayList<String> checkedContactsAsArrayList;
    //   public static ArrayList<String> newcheckedContactsAsArrayList;

    //In each recycler_blueprint show the items you want to have appearing
    public TextView title, phone;
    public CheckBox check;
    public Button invite;

    public class MatchingContact extends RecyclerView.ViewHolder {

        //In each recycler_blueprint show the items you want to have appearing
        public TextView title, phone;
        public CheckBox check;
        public Button invite;

        public MatchingContact(final View itemView) {
            super(itemView);
            //title is cast to the name id, in recycler_blueprint,
            //phone is cast to the id called no etc
            title = (TextView) itemView.findViewById(R.id.name);
            phone = (TextView) itemView.findViewById(R.id.no);
            invite = (Button) itemView.findViewById(R.id.btnInvite);
            check = (CheckBox) itemView.findViewById(R.id.checkBoxContact);

        }

    }

    public class nonMatchingContact extends RecyclerView.ViewHolder {

        //In each recycler_blueprint show the items you want to have appearing
        public TextView title, phone;
        public CheckBox check;
        public Button invite;


        public nonMatchingContact(final View itemView) {
            super(itemView);
            //title is cast to the name id, in recycler_blueprint,
            //phone is cast to the id called no etc
            title = (TextView) itemView.findViewById(R.id.name);
            phone = (TextView) itemView.findViewById(R.id.no);
            invite = (Button) itemView.findViewById(R.id.btnInvite);
            check = (CheckBox) itemView.findViewById(R.id.checkBoxContact);

        }

    }

    @Override
    public int getItemViewType(int position) {
        //for each row in recyclerview, get the getType_row
        //it will either have Invite Button, or no Invite Button
        return Integer.parseInt(theContactsList.get(position).getType_row());
    }


    public PopulistoContactsAdapter(List<SelectPhoneContact> selectPhoneContacts, Context context, int activity) {
        //selectPhoneContacts = new ArrayList<SelectPhoneContact>();

        // checkAccumulator = 0;

        theContactsList = selectPhoneContacts;

        this.mContext = context;
        whichactivity = activity;
        context_type = context;

        //we are fetching the array list MatchingContactsAsArrayList, created in VerifyUserPhoneNumber.
        //with this we will put a checkbox beside the matching contacts
        SharedPreferences sharedPreferencesMatchingContactsAsArrayList = PreferenceManager.getDefaultSharedPreferences(context_type);
        Gson gsonMatchingContactsAsArrayList = new Gson();
        String jsonMatchingContactsAsArrayList = sharedPreferencesMatchingContactsAsArrayList.getString("MatchingContactsAsArrayList", "");
        Type type1 = new TypeToken<ArrayList<String>>() {
        }.getType();
        MatchingContactsAsArrayList = gsonMatchingContactsAsArrayList.fromJson(jsonMatchingContactsAsArrayList, type1);
        System.out.println("SelectPhoneContactAdapter MatchingContactsAsArrayList :" + MatchingContactsAsArrayList);

        //we are fetching the array list allPhonesofContacts, created in VerifyUserPhoneNumber.
        //with this we will put all phone numbers of contacts on user's phone into our ListView in NewContact activity
        SharedPreferences sharedPreferencesallPhonesofContacts = PreferenceManager.getDefaultSharedPreferences(context_type);
        Gson gson = new Gson();
        String json = sharedPreferencesallPhonesofContacts.getString("allPhonesofContacts", "");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        allPhonesofContacts = gson.fromJson(json, type);
        System.out.println("NewContact: allPhonesofContacts :" + allPhonesofContacts);

        //we are fetching the array list allNamesofContacts, created in VerifyUserPhoneNumber.
        //with this we will put all phone names of contacts on user's phone into our ListView in NewContact activity
        SharedPreferences sharedPreferencesallNamesofContacts = PreferenceManager.getDefaultSharedPreferences(context_type);
        Gson gsonNames = new Gson();
        String jsonNames = sharedPreferencesallNamesofContacts.getString("allNamesofContacts", "");
        Type typeNames = new TypeToken<ArrayList<String>>() {
        }.getType();
        allNamesofContacts = gsonNames.fromJson(jsonNames, typeNames);
        System.out.println("NewContact: allNamesofContacts :" + allNamesofContacts);

        System.out.println("NewContact:the amount in allPhonesofContacts :" + PopulistoContactsAdapter.allPhonesofContacts.size());
        System.out.println("NewContact:the amount in allNamesofContacts :" + allNamesofContacts.size());

        //we are fetching the array list checkedContactsAsArrayList, created in ViewContact.
        //with this we will put a tick in the checkboxes of contacts the review is being shared with
        SharedPreferences sharedPreferencescheckedContactsAsArrayList = PreferenceManager.getDefaultSharedPreferences(context_type);
        Gson gsoncheckedContactsAsArrayList = new Gson();
        String jsoncheckedContactsAsArrayList = sharedPreferencescheckedContactsAsArrayList.getString("checkedContactsAsArrayList", "");
        Type type2 = new TypeToken<ArrayList<String>>() {
        }.getType();
        checkedContactsAsArrayList = gsoncheckedContactsAsArrayList.fromJson(jsoncheckedContactsAsArrayList, type2);
        System.out.println("SelectPhoneContactAdapter checkedContactsAsArrayList :" + checkedContactsAsArrayList);


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        //if getType_row is 1...
        if (viewType == 1)

        {

            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            itemView = inflater.inflate(R.layout.recycler_blueprint, parent, false);

            //itemView.setTag();
            return new MatchingContact(itemView);

        } else {

            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            itemView = inflater.inflate(R.layout.recycler_blueprint_non_matching, parent, false);

            return new nonMatchingContact(itemView);

        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        //bind the views into the ViewHolder
        //selectPhoneContact is an instance of the SelectPhoneContact class.
        //We will assign each row of the recyclerview to contain details of selectPhoneContact:

        //The number of rows will match the number of phone contacts
        final SelectPhoneContact selectPhoneContact = theContactsList.get(position);

        // For the ViewContact, which has int activity = 0
        if (whichactivity == 0) {


            //if the row is a matching contact
            if (viewHolder.getItemViewType() == 1)

            {
                //in the title textbox in the row, put the corresponding name etc...
                ((MatchingContact) viewHolder).title.setText(selectPhoneContact.getName());
                ((MatchingContact) viewHolder).phone.setText(selectPhoneContact.getPhone());
                ((MatchingContact) viewHolder).check.setChecked(theContactsList.get(position).getSelected());
                ((MatchingContact) viewHolder).check.setTag(position);

                //disable the check box, can't be changed
                ((MatchingContact) viewHolder).check.setEnabled(false);


            } else {

                //if getItemViewType == 2, then show the invite button layout
                ((nonMatchingContact) viewHolder).title.setText(selectPhoneContact.getName());
                ((nonMatchingContact) viewHolder).phone.setText(selectPhoneContact.getPhone());
            }


            //This is for ViewContact, to display the contact the review is shared with
            //for every phone number in the checkedContactsAsArrayList array list...
            for (int number2 = 0; number2 < checkedContactsAsArrayList.size(); number2++) {
                Log.i("MyMessage", "checkedContactsAsArrayList is: " + checkedContactsAsArrayList);

                //if a phone number is in our array of checked contacts
                if (checkedContactsAsArrayList.contains(selectPhoneContact.getPhone())) {
                    //check the box
                    ((MatchingContact) viewHolder).check.setChecked(true);
                }
            }
            //disable the checkbox
            //((MatchingContact) viewHolder).check.setEnabled(false);
        }


        //if the activity is NewContact
        if (whichactivity == 1) {
            //if the row is a matching contact
            if (viewHolder.getItemViewType() == 1)

            {
                //in the title textbox in the row, put the corresponding name etc...
                ((MatchingContact) viewHolder).title.setText(selectPhoneContact.getName());
                ((MatchingContact) viewHolder).phone.setText(selectPhoneContact.getPhone());
                ((MatchingContact) viewHolder).check.setChecked(theContactsList.get(position).getSelected());
                ((MatchingContact) viewHolder).check.setTag(position);

                //for the onClick of the check box
                ((MatchingContact) viewHolder).check.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        //pos is the row number that the clicked checkbox exists in
                        Integer pos = (Integer) ((MatchingContact) viewHolder).check.getTag();

                        //***********NEED THIS TO PRESERVE CHECKBOX STATE

                        //if unclicked
                        if (theContactsList.get(pos).getSelected()) {
                            theContactsList.get(pos).setSelected(false);
                            Toast.makeText(context_type, theContactsList.get(pos).getSelected() + " unclicked!", Toast.LENGTH_SHORT).show();

                        } else {

                            //if clicked
                            theContactsList.get(pos).setSelected(true);
                            Toast.makeText(context_type, theContactsList.get(pos).getSelected() + " clicked!", Toast.LENGTH_SHORT).show();
                            // theContactsList.get(pos).getPhone();
                        }

                        //**************************

                        //we want to keep track of checked boxes, so when it is '0'
                        //'Phone Contacts' button will switch to 'Just Me'
                        int count;
                        count = 0;
                        int size = theContactsList.size();
                        for (int i = 0; i < size; i++) {
                            if (theContactsList.get(i).isSelected) {
                                count++;
                                // System.out.println("The count is " + count);
                            }
                        }
                        Toast.makeText(context_type, "The count is " + count, Toast.LENGTH_SHORT).show();
                        Log.i("MyMessage", "The count is " + count);

                        //if count is 0, nothing selected, then show 'Just Me'
                        if (checkedContactsAsArrayList.size()  == 0) {

                            if (mContext instanceof NewContact) {
                                ((NewContact) mContext).changeColorofJustMe();
                                Toast.makeText(context_type, "count is 0!", Toast.LENGTH_SHORT).show();

                            }

                        } else {

                            //change the colour of 'Phone Contacts' button in NewContact.java
                            if (mContext instanceof NewContact) {
                                ((NewContact) mContext).changeColourOfPhoneContacts();
                              //  Toast.makeText(context_type, "The count is " + checkedContactsAsArrayList.size(), Toast.LENGTH_SHORT).show();

                            }
                        }


                 /*   switch (count){
                        case 0:
                            if (mContext instanceof NewContact) {
                            ((NewContact) mContext).changeColorofJustMe();
                                Toast.makeText(context_type, "count is 0!", Toast.LENGTH_SHORT).show();
                            }
                        case 1:
                   // if(count==0)
                        //change the colour of 'Phone Contacts' button in NewContact.java
                       if (mContext instanceof NewContact) {
                            ((NewContact) mContext).changeColourOfPhoneContacts();
                           Toast.makeText(context_type, "The count is " + count , Toast.LENGTH_SHORT).show();
                       }
                    }*/

                        //change the colour of 'Phone Contacts' button in NewContact.java
              /*      if (mContext instanceof NewContact) {
                        ((NewContact) mContext).changeColourOfPhoneContacts();
                    }*/
                    }


                });

/*            CompoundButton.OnCheckedChangeListener checkListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    countCheck(isChecked);
                    Toast.makeText(context_type, checkAccumulator + " unclicked!", Toast.LENGTH_SHORT).show();
                   // Log.i("MAIN", checkAccumulator + "");
                }
            };*/

                //((MatchingContact) viewHolder).check.setOnCheckedChangeListener(checkListener);

/*            ((MatchingContact) viewHolder).check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                // ((MatchingContact) viewHolder).check.setOnClickListener(new CompoundButton.OnClickListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onCheckBoxClickListener.onCheckBoxClick(isChecked);
                }
            });*/

            } else {

                ((nonMatchingContact) viewHolder).title.setText(selectPhoneContact.getName());
                ((nonMatchingContact) viewHolder).phone.setText(selectPhoneContact.getPhone());

            }

        }


        //if the activity is EditContact
        if (whichactivity == 2) {

            //we need to remove the user's phone number from checkedContactsAsArrayList
            //otherwise it will be added twice, when saved in EditContact
            if (checkedContactsAsArrayList.contains(EditContact.phoneNoofUserCheck))

            {
                checkedContactsAsArrayList.remove(EditContact.phoneNoofUserCheck);
            }

            //if the row is a matching contact
            if (viewHolder.getItemViewType() == 1)

            {


                //in the title textbox in the recycler_blueprint row, put the corresponding name etc...
                ((MatchingContact) viewHolder).title.setText(selectPhoneContact.getName());
                ((MatchingContact) viewHolder).phone.setText(selectPhoneContact.getPhone());
                ((MatchingContact) viewHolder).check.setChecked(theContactsList.get(position).getSelected());
                ((MatchingContact) viewHolder).check.setTag(position);


                ((MatchingContact) viewHolder).check.setOnClickListener(new View.OnClickListener() {

                    @Override
                    //on checkbox click
                    public void onClick(View v) {

                        //pos is the row number that the clicked checkbox exists in
                        Integer pos = (Integer) ((MatchingContact) viewHolder).check.getTag();

                        //if the checkbox is checked
                        if (((MatchingContact) viewHolder).check.isChecked())

                        {
                            //we want to add the phone number of the checked row into our arraylist.
                            //this arraylist will be passed to EditContact class when Save is clicked and
                            //saved in review_shared table on server.
                            Toast.makeText(context_type, theContactsList.get(pos).getPhone() + "clicked", Toast.LENGTH_SHORT).show();

                            //add the checked number into the arraylist
                            checkedContactsAsArrayList.add(theContactsList.get(pos).getPhone());
                            Log.i("Adapter1", "checkedContactsAsArrayList: " + checkedContactsAsArrayList);
                           // Log.i("Adapter", "clicked checkedContactsAsArrayList: " + checkedContactsAsArrayList);

                        } else {
                            //remove the checked number from the arraylist
                            checkedContactsAsArrayList.remove(theContactsList.get(pos).getPhone());
                            Log.i("Adapter1", "checkedContactsAsArrayList: " + checkedContactsAsArrayList);
                        //    Log.i("Adapter", "unclicked checkedContactsAsArrayList: " + checkedContactsAsArrayList);

                        }
                        //*************

                        //NEED THIS TO PRESERVE CHECKBOX STATE

                        if (theContactsList.get(pos).getSelected()) {
                            theContactsList.get(pos).setSelected(false);
                             Toast.makeText(context_type, theContactsList.get(pos).getSelected() + " unclicked!", Toast.LENGTH_SHORT).show();

                        } else {

                            theContactsList.get(pos).setSelected(true);
                            Toast.makeText(context_type, theContactsList.get(pos).getSelected() + " clicked!", Toast.LENGTH_SHORT).show();
                            //theContactsList.get(pos).getPhone();

                        }

                        //************


                        //we want to keep track of checked boxes, so when it is '0'
                        //'Phone Contacts' button will switch to 'Just Me'

                        //This is for EditContact, to keep track of checked boxes

                        //if matching contacts (the ones with a checkbox beside it)
                        // a phone number is in our array of checked contacts
                        // if (MatchingContactsAsArrayList.contains(selectPhoneContact.getPhone())) {

                        //    Log.i("MyMessage", "numberr in checkedContactsAsArrayList is " + checkedContactsAsArrayList.size());

                        // }


                     /*   if (checkedContactsAsArrayList.size() == 0) {
                            Toast.makeText(context_type, "count is 0!", Toast.LENGTH_SHORT).show();
                        } else {*/

                        Toast.makeText(context_type, "The count is " + checkedContactsAsArrayList.size(), Toast.LENGTH_SHORT).show();
                        Log.i("Adapter1", "checkedContactsAsArrayList total: " + checkedContactsAsArrayList);

                        //   }


                        //if count is 0, nothing selected, then show 'Just Me'
                        if (checkedContactsAsArrayList.size()  == 0) {

                            if (mContext instanceof EditContact) {
                                ((EditContact) mContext).changeColorofJustMe();

                                Toast.makeText(context_type, "count is 0!yeah", Toast.LENGTH_SHORT).show();

                            }

                        } else {

                            //change the colour of 'Phone Contacts' button in EditContact.java
                            if (mContext instanceof EditContact) {
                                ((EditContact) mContext).changeColourOfPhoneContacts();

                                //  Toast.makeText(context_type, "The count is " + checkedContactsAsArrayList.size(), Toast.LENGTH_SHORT).show();

                            }
                        }

                    }


                });


                //This is for EditContact, to display clicked contacts the user wants to share with.
                //for every phone number in the checkedContactsAsArrayList array list...
                //We want them to stay checked on scroll

                //if a phone number is in our array of checked contacts
                if (checkedContactsAsArrayList.contains(selectPhoneContact.getPhone())) {
                    //check the box
                    ((MatchingContact) viewHolder).check.setChecked(true);

                    Integer pos = (Integer) ((MatchingContact) viewHolder).check.getTag();

                    //NEED THIS TO PRESERVE CHECKBOX STATE ON RECYCLER SCROLL

                        theContactsList.get(pos).setSelected(true);

                }


            } else {
                //if it's a nonMatching contact
                ((nonMatchingContact) viewHolder).title.setText(selectPhoneContact.getName());
                ((nonMatchingContact) viewHolder).phone.setText(selectPhoneContact.getPhone());

            }







        }

        Log.i("EditContact-MyMessage", "whichactivity=2, checkedContactsAsArrayList is: " + checkedContactsAsArrayList);
        Log.i("EditContact-MyMessage", "whichactivity=2,number in checkedContactsAsArrayList is " + checkedContactsAsArrayList.size());


    }


    @Override
    public int getItemCount() {

        return theContactsList.size();
    }

/*    public interface OnCheckBoxClickListener {
        void onCheckBoxClick(boolean ischecked);
    }
    public void SetOnCheckBoxClickListener(final OnCheckBoxClickListener onCheckBoxClickListener) {
        this.onCheckBoxClickListener = onCheckBoxClickListener;
    }*/

/*    private void countCheck(boolean isChecked) {
        checkAccumulator += isChecked ? 1 : -1 ;
    }*/


}