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

    //we will run through different logic in this custom adapter based on the activity that is passed to it
    //NewContact, ViewContact or EditContact
    private int whichactivity;

    Context context_type;

    //for remembering checked boxes in 'New', if phone ges to sleep
    SharedPreferences.Editor editor;


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
        //for each row in recyclerview, get the getType_row, set in NewContact.java
        return Integer.parseInt(theContactsList.get(position).getType_row());
    }


    public PopulistoContactsAdapter(List<SelectPhoneContact> selectPhoneContacts, Context context, int activity) {
        //selectPhoneContacts = new ArrayList<SelectPhoneContact>();

         theContactsList = selectPhoneContacts;
        whichactivity = activity;
        context_type = context;


/*        matchingContacts.add("+3531234567");
        matchingContacts.add("+353868132813");
        matchingContacts.add("+353863366715");
        matchingContacts.add("+353858716422");*/
        // }
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

            return new MatchingContact(itemView);

        } else {

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

        if (viewHolder.getItemViewType() == 1)

        {

            ((MatchingContact) viewHolder).title.setText(selectPhoneContact.getName());
            ((MatchingContact) viewHolder).phone.setText(selectPhoneContact.getPhone());


            CheckBox check = ((MatchingContact) viewHolder).check;

            //get the number position of the checkbox in the recyclerview
            check.setTag(position);

            //initialize a sharedpreferences file called "sharedPrefs", which will be private, for our app only
            //we are doing this so the checkbox state in the listview will be saved, so user can come back to it if the phone sleeps
            SharedPreferences sharedPrefs = context_type.getSharedPreferences("sharedPrefsFile", Context.MODE_PRIVATE);


            //if the activity is NewContact
            if (whichactivity == 1) {
                //if a checkbox is checked
                ((MatchingContact) viewHolder).check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    //when a checkbox in the Listview is clicked
                    public void onClick(View v) {
                        //make new instance of the checkbox, cb, otherwise I was getting errors
                        CheckBox cb = (CheckBox) v;

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


        } else {

            ((nonMatchingContact) viewHolder).title.setText(selectPhoneContact.getName());
            ((nonMatchingContact) viewHolder).phone.setText(selectPhoneContact.getPhone());

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

