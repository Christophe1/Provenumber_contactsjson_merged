package com.example.chris.tutorialspoint;

import android.app.Activity;
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

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    //make a List containing info about SelectPhoneContact objects
    public List<SelectPhoneContact> theContactsList;
    //  private ArrayList<SelectPhoneContact> arraylist;

    //we will run through different logic in this custom adapter based on the activity that is passed to it
    private int whichactivity;
    Context context_type;

    //we will be bringing the matching contacts to this activity, from shared preferences
    ArrayList<String> MatchingContactsAsArrayList;

    //for remembering checked boxes in 'New', if phone ges to sleep
    SharedPreferences.Editor editor;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        //In each recycler_blueprint show the items you want to have appearing
        public TextView title, phone;
        public CheckBox check;
        public Button invite;


        public ViewHolder(final View itemView) {
            super(itemView);
            //title is cast to the name id, in recycler_blueprint,
            //phone is cast to the id called no etc
            title = (TextView) itemView.findViewById(R.id.name);
            phone = (TextView) itemView.findViewById(R.id.no);
            invite = (Button) itemView.findViewById(R.id.btnInvite);
            check = (CheckBox) itemView.findViewById(R.id.checkBoxContact);


        }


    }


    public RecyclerViewAdapter(List<SelectPhoneContact> selectPhoneContacts, Context context, int activity) {

        //public RecyclerViewAdapter(List<SelectPhoneContact>selectPhoneContacts, Context context, int activity) {

        theContactsList = selectPhoneContacts;
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


        // this.arraylist = new ArrayList<SelectPhoneContact>();
        // this.arraylist.addAll(theContactsList);
        // System.out.println("this.arraylist" + this.arraylist);

    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.recycler_blueprint, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder viewHolder, final int position) {
        //bind the views into the ViewHolder
        //selectPhoneContact is an instance of the SelectPhoneContact class.
        //We will assign each row of the recyclerview to contain details of selectPhoneContact:

        //The number of rows will match the number of contacts in our contacts list
        final SelectPhoneContact selectPhoneContact = theContactsList.get(position);

        //a text view for the name, set it to the matching selectPhoneContact
        TextView title = viewHolder.title;
        title.setText(selectPhoneContact.getName());

        //a text view for the number, set it to the matching selectPhoneContact
        TextView phone = viewHolder.phone;
        phone.setText(selectPhoneContact.getPhone());

        CheckBox check = viewHolder.check;

        //get the number position of the checkbox in the recyclerview
        check.setTag(position);

        //initialize a sharedpreferences file called "sharedPrefs", which will be private, for our app only
        //we are doing this so the checkbox state in the listview will be saved, so user can come back to it if the phone sleeps
        SharedPreferences sharedPrefs = context_type.getSharedPreferences("sharedPrefsFile", Context.MODE_PRIVATE);


        //if the activity is NewContact
        if (whichactivity == 1) {
            //if a checkbox is checked
            viewHolder.check.setOnClickListener(new View.OnClickListener() {
                @Override
                //when a checkbox in the Listview is clicked
                public void onClick(View v) {
                    //make new instance of the checkbox, cb, otherwise I was getting errors
                    CheckBox cb = (CheckBox) v;

                    //Integer pos = (Integer) viewHolder.check.getTag();

                    if (cb.isChecked() == true) {
                        Toast.makeText(context_type,
                                "Clicked on Checkbox: " + position + " " + cb.isChecked() + selectPhoneContact.getPhone(),
                                Toast.LENGTH_SHORT).show();
                    }

                    if (cb.isChecked() == false) {

                        Toast.makeText(context_type,
                                "Clicked on Checkbox: " + cb.isChecked(),
                                Toast.LENGTH_SHORT).show();
                    }
                    //CheckBox cb = (CheckBox) v;
                    // SelectPhoneContact data = (SelectPhoneContact) cb.getTag();
                    //if it is set to unchecked
                    //if (cb.isChecked() == false)
                    //need this to change radio button to Phone Contacts,
                    //if a checkbox is changed to false
                    //{
                    //   System.out.println("It's false");
                    //radioButtontoPhoneContacts.update();
                    //}
/*                Toast.makeText(context_type,
                        "Clicked on Checkbox: " + data.getPhone() +
                                " is " + cb.isChecked(),
                        Toast.LENGTH_LONG).show();
                data.setSelected(cb.isChecked());*/

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

            //initialize the SharedPreferences editor
            editor = sharedPrefs.edit();

            //in the recyclerview for checkboxes, get the checkbox values from the sharedpreferences file
            check.setChecked(sharedPrefs.getBoolean("CheckValue" + position, false));

            //when the checkbox changes, edit those changes and commit,
            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    //edit and commit the checkbox status to the sharedpreferences file
                    //"CheckValue"+1 is the key, isChecked is the value
                    editor.putBoolean("CheckValue" + position, isChecked);
                    editor.commit();

                }

            });
        }

        //for every phone number in the MatchingContactsAsArrayList array list...
        for (int number = 0; number < MatchingContactsAsArrayList.size(); number++) {

            //if a phone number is in our array of matching contacts
            if (MatchingContactsAsArrayList.contains(selectPhoneContact.getPhone()))

            {
                //if a matching contact, no need to show the Invite button
                viewHolder.invite.setVisibility(View.GONE);
                System.out.println("it's a match: phoneNumberofContact is : " + selectPhoneContact.getPhone());
                //once a matching contact is found, no need to keep looping x number of time, move onto next contact
                break;

            } else {
                //if not a matching contact, no need to show the check box
                viewHolder.check.setVisibility(View.GONE);

            }

        }

       // viewHolder.check.setTag(selectPhoneContact);

       // return viewHolder;

    }

    @Override
    public int getItemCount() {
/*        if(theContactsList == null)
            return 0;*/
        return theContactsList.size();
    }
}

