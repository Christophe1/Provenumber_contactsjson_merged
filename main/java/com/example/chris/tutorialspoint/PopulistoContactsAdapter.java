package com.example.chris.tutorialspoint;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.List;

/**
 * Created by Chris on 07/01/2018.
 */

public class PopulistoContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //make a List containing info about SelectPhoneContact objects
    public List<SelectPhoneContact> theContactsList;

   // public int matchingcontact;

    Context context_type;

    //ArrayList<String> matchingContacts = new ArrayList<String>();
    //ArrayList<SelectPhoneContact> selectPhoneContacts;
    //we will be bringing the matching contacts to this activity, from shared preferences
   // ArrayList<String> MatchingContactsAsArrayList;

   // ArrayList <String> allPhonesofContacts;
   // public String phoneNumberofContact;

    public  class MatchingContact extends RecyclerView.ViewHolder {

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

    public class nonMatchingContact extends  RecyclerView.ViewHolder {

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
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
/*        System.out.println("testt");
       // SelectPhoneContact selectPhoneContact = new SelectPhoneContact();
        //for every phone number in the MatchingContactsAsArrayList array list...
        for (int number = 0; number < MatchingContactsAsArrayList.size(); number++) {
            phoneNumberofContact = allPhonesofContacts.get(number);
            //if a phone number is in our array of matching contacts
            if (MatchingContactsAsArrayList.contains(phoneNumberofContact)) {
                System.out.println("populistocontactsadapter: the number is " + phoneNumberofContact);
                position = 1;
            }
            }*/
            // matchingcontact=position;

            //if (selectPhoneContact.

        return Integer.parseInt(theContactsList.get(position).getType_row());
    }

    public PopulistoContactsAdapter(List<SelectPhoneContact> selectPhoneContacts, Context context, int activity) {
        //selectPhoneContacts = new ArrayList<SelectPhoneContact>();

       // theContactsList = selectPhoneContacts;
        context_type = context;
        //we are fetching the array list MatchingContactsAsArrayList, created in VerifyUserPhoneNumber.
        //with this we will put a checkbox beside the matching contacts
/*
        SharedPreferences sharedPreferencesMatchingContactsAsArrayList = PreferenceManager.getDefaultSharedPreferences(context_type);
        Gson gsonMatchingContactsAsArrayList = new Gson();
        String jsonMatchingContactsAsArrayList = sharedPreferencesMatchingContactsAsArrayList.getString("MatchingContactsAsArrayList", "");
        Type type1 = new TypeToken<ArrayList<String>>() {
        }.getType();
        MatchingContactsAsArrayList = gsonMatchingContactsAsArrayList.fromJson(jsonMatchingContactsAsArrayList, type1);
        System.out.println("SelectPhoneContactAdapter MatchingContactsAsArrayList :" + MatchingContactsAsArrayList);
*/

        //we are fetching the array list allPhonesofContacts, created in VerifyUserPhoneNumber.
        //with this we will put all phone numbers of contacts on user's phone into our ListView in NewContact activity
/*        SharedPreferences sharedPreferencesallPhonesofContacts = PreferenceManager.getDefaultSharedPreferences(context_type);
        Gson gson = new Gson();
        String json = sharedPreferencesallPhonesofContacts.getString("allPhonesofContacts", "");
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        allPhonesofContacts = gson.fromJson(json, type);*/

/*        matchingContacts.add("+3531234567");
        matchingContacts.add("+353868132813");
        matchingContacts.add("+353863366715");
        matchingContacts.add("+353858716422");*/
       // }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;
        if (viewType == 1)

        {

            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            itemView = inflater.inflate(R.layout.recycler_blueprint, parent, false);
            //RecyclerView.ViewHolder viewHolder = new RecyclerView.ViewHolder(contactView);

            return new MatchingContact(itemView);

        }

        else  {

            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            itemView = inflater.inflate(R.layout.recycler_blueprint_non_matching, parent, false);
            //RecyclerView.ViewHolder viewHolder = new RecyclerView.ViewHolder(contactView);

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

        if(viewHolder.getItemViewType()==1)

        {

            ((MatchingContact)viewHolder).title.setText(selectPhoneContact.getName());
            ((MatchingContact)viewHolder).phone.setText(selectPhoneContact.getPhone());

          //  if (selectPhoneContact.isMatching = true){

                //((MatchingContact)viewHolder).invite.setVisibility(View.GONE);
        }

        else {

            ((nonMatchingContact)viewHolder).title.setText(selectPhoneContact.getName());
            ((nonMatchingContact)viewHolder).phone.setText(selectPhoneContact.getPhone());

        }

        }

        //a text view for the name, set it to the matching selectPhoneContact
/*        TextView title = viewHolder.title;
        title.setText(selectPhoneContact.getName());

        //a text view for the number, set it to the matching selectPhoneContact
        TextView phone = viewHolder.phone;
        phone.setText(selectPhoneContact.getPhone());

        Button invite = viewHolder.invite;

        CheckBox check = viewHolder.check;

        for (int number = 0; number < matchingContacts.size(); number++) {

            //if a phone number is in our array of matching contacts
            if (matchingContacts.contains(selectPhoneContact.getPhone()))

            {
                //if a matching contact, no need to show the Invite button
                viewHolder.invite.setVisibility(View.GONE);
                //once a matching contact is found, no need to keep looping x number of time, move onto next contact
                break;

            } else {
                //if not a matching contact, no need to show the check box
                viewHolder.check.setVisibility(View.GONE);

            }

        }*/

   // }

    @Override
    public int getItemCount() {

        return theContactsList.size();
    }
}

