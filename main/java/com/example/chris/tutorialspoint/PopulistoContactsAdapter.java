
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
import java.util.HashMap;
import java.util.List;

import static android.R.attr.data;

/**
 * Created by Chris on 07/01/2018.
 */

public class PopulistoContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder > {

   // NewContact newc;

    private Context mContext;
    //ArrayList<SelectPhoneContact> selectPhoneContacts;

    //make a List containing info about SelectPhoneContact objects
    public static List<SelectPhoneContact> theContactsList;

    //ArrayList<String> listOfStrings = new ArrayList<>(theContactsList.size());


    //private HashMap<String, Boolean> mChecked;

    Context context_type;


    private OnCheckBoxClickListener onCheckBoxClickListener;


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


    public PopulistoContactsAdapter(List<SelectPhoneContact> selectPhoneContacts, Context context) {
        //selectPhoneContacts = new ArrayList<SelectPhoneContact>();

        theContactsList = selectPhoneContacts;

        this.mContext = context;
       // whichactivity = activity;
        context_type = context;

      //  mChecked = new HashMap<>();


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

        //if the row is a matching contact
        if (viewHolder.getItemViewType() == 1)

        {
            //in the title textbox in the row, put the corresponding name etc...
            ((MatchingContact) viewHolder).title.setText(selectPhoneContact.getName());
            ((MatchingContact) viewHolder).phone.setText(selectPhoneContact.getPhone());
            ((MatchingContact) viewHolder).check.setChecked(theContactsList.get(position).getSelected());
            ((MatchingContact) viewHolder).check.setTag(position);


            ((MatchingContact) viewHolder).check.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //pos is the row number that the clicked checkbox exists in
                    Integer pos = (Integer) ((MatchingContact) viewHolder).check.getTag();

                    //NEED THIS TO PRESERVE CHECKBOX STATE
                    //because it is onClick, getSelected will always be the same value
                    //false or true, it doesn't matter
                    if (theContactsList.get(pos).getSelected()) {
                        theContactsList.get(pos).setSelected(false);
                        Toast.makeText(context_type, theContactsList.get(pos).getPhone() + " clicked!", Toast.LENGTH_SHORT).show();

                    } else {

                        theContactsList.get(pos).setSelected(true);
                        Toast.makeText(context_type, theContactsList.get(pos).getPhone() + " unclicked!", Toast.LENGTH_SHORT).show();

                    }
                }
            });

            ((MatchingContact) viewHolder).check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                // ((MatchingContact) viewHolder).check.setOnClickListener(new CompoundButton.OnClickListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    onCheckBoxClickListener.onCheckBoxClick(isChecked);

                }
            });

        }
        else {

            ((nonMatchingContact) viewHolder).title.setText(selectPhoneContact.getName());
            ((nonMatchingContact) viewHolder).phone.setText(selectPhoneContact.getPhone());

        }
    }


    @Override
    public int getItemCount() {

        return theContactsList.size();
    }

    public interface OnCheckBoxClickListener {
        void onCheckBoxClick(boolean ischecked);
    }

    public void SetOnCheckBoxClickListener(final OnCheckBoxClickListener onCheckBoxClickListener) {
        this.onCheckBoxClickListener = onCheckBoxClickListener;
    }

}

