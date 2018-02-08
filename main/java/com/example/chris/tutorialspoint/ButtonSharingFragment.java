package com.example.chris.tutorialspoint;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.tutorialspoint.R;

import java.util.ArrayList;

/**
 * Created by Chris on 01/02/2018.
 */

public class ButtonSharingFragment extends Fragment implements PlayPauseClick {

    RecyclerView recyclerView;

    Button publicContacts;
    Button phoneContacts;
    Button justMeContacts;

    // ArrayList called selectPhoneContacts that will contain SelectPhoneContact info
    ArrayList<SelectPhoneContact> selectPhoneContacts;

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment

        //selectPhoneContacts is an empty array list that will hold our SelectPhoneContact info
       // selectPhoneContacts = new ArrayList<SelectPhoneContact>();

        PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, getActivity());


         adapter.setPlayPauseClickListener(this);



        View buttonView = inflater.inflate(R.layout.sharing_buttons, parent, false);
        //return inflater.inflate(R.layout.sharing_buttons, parent, false);


        //for the Public, phoneContacts, justMe, save and cancel buttons
        publicContacts = (Button) buttonView.findViewById(R.id.btnPublic);
        phoneContacts = (Button) buttonView.findViewById(R.id.btnPhoneContacts);
        justMeContacts = (Button) buttonView.findViewById(R.id.btnJustMe);



        publicButton();
        //phoneContactsButton();
        justMeButton();

        // Defines the xml file for the fragment
        return buttonView;
    }



    //for the Public Contacts button
    private void publicButton() {

        publicContacts.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//              keep the slightly rounded shape, when the button is pressed
                publicContacts.setBackgroundResource(R.drawable.publiccontacts_buttonshapepressed);

//               keep the slightly rounded shape of the others, but still grey
                phoneContacts.setBackgroundResource(R.drawable.buttonshape);
                justMeContacts.setBackgroundResource(R.drawable.buttonshape);

                //set sharing to Public
               // public_or_private = 2;

               // PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, getActivity());

               // recyclerView.setAdapter(adapter);
                // recyclerView.setLayoutManager((new LinearLayoutManager(NewContact.this)));

                //loop through the matching contacts
                int count = PopulistoContactsAdapter.MatchingContactsAsArrayList.size();

                //i is the number of matching contacts that there are
                for (int i = 0; i < count; i++) {

                    //for all contacts, only those that are matching will be checked
                    PopulistoContactsAdapter.theContactsList.get(i).setSelected(true);

                    //we need to notify the recyclerview that changes may have been made
                 //   adapter.notifyDataSetChanged();
                }
            }

        });

    }


    //for the phone Contacts button
  /*  private void phoneContactsButton() {

        phoneContacts.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //keep the slightly rounded shape, when the button is pressed
                phoneContacts.setBackgroundResource(R.drawable.phonecontacts_buttonshapepressed);


//               keep the slightly rounded shape of the others, but still grey
                publicContacts.setBackgroundResource(R.drawable.buttonshape);
                justMeContacts.setBackgroundResource(R.drawable.buttonshape);

                //set sharing to Phone Contacts
            //    public_or_private = 1;

                PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, getActivity(),ButtonSharingFragment.this);

//                recyclerView.setAdapter(adapter);
                // recyclerView.setLayoutManager((new LinearLayoutManager(NewContact.this)));

                //loop through the matching contacts
                int count = PopulistoContactsAdapter.MatchingContactsAsArrayList.size();

                for (int i = 0; i < count; i++) {

                    //check all matching contacts, we want it to be 'Phone Contacts'
//                    PopulistoContactsAdapter.theContactsList.get(i).setSelected(true);

                    //we need to notify the recyclerview that changes may have been made
            //        adapter.notifyDataSetChanged();
                }
            }

        });

    }*/


    //for the Just Me button
    private void justMeButton() {

        justMeContacts.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

             //   PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, ButtonSharingFragment.this);


                //keep the slightly rounded shape, when the button is pressed
                justMeContacts.setBackgroundResource(R.drawable.justmecontacts_buttonshapepressed);

//               keep the slightly rounded shape of the others, but still grey
                publicContacts.setBackgroundResource(R.drawable.buttonshape);
                phoneContacts.setBackgroundResource(R.drawable.buttonshape);

                //set sharing to Just Me
             //   public_or_private = 0;

               // PopulistoContactsAdapter adapter = new PopulistoContactsAdapter(selectPhoneContacts, NewContact.this);

              //  recyclerView.setAdapter(adapter);
                // recyclerView.setLayoutManager((new LinearLayoutManager(NewContact.this)));

                //loop through the matching contacts
                int count = PopulistoContactsAdapter.MatchingContactsAsArrayList.size();

                for (int i = 0; i < count; i++) {

                    //uncheck all matching contacts, we want it to be 'Just Me'
                    PopulistoContactsAdapter.theContactsList.get(i).setSelected(false);

                    //we need to notify the recyclerview that changes may have been made
                 //   adapter.notifyDataSetChanged();
                }
            }

        });

    }


    //this is called from Adapter
    //change the colour of the button
    public void changeColourOfPhoneContacts(){
        //keep the slightly rounded shape, when the button is pressed
        phoneContacts.setBackgroundResource(R.drawable.phonecontacts_buttonshapepressed);

    }
    //this is called from PopulistoContactsAdapter
    //change the colour of the justMe button
    public void changeColorofJustMe(){
        //keep the slightly rounded shape, when the button is pressed
        justMeContacts.setBackgroundResource(R.drawable.justmecontacts_buttonshapepressed);

        //keep the slightly rounded shape of the others, but still grey
        publicContacts.setBackgroundResource(R.drawable.buttonshape);
        phoneContacts.setBackgroundResource(R.drawable.buttonshape);

    }


/*    public interface colourchange {
        phoneContacts.setBackgroundResource(R.drawable.phonecontacts_buttonshapepressed);

        //This method can be any parameters, I'm pasing color here since you need the color code
        public void changecolour(View itemView, int position, int color);
    }*/


    @Override
    public void imageButtonOnClick() {
        // TODO: Implement this
        //Toast.makeText(getActivity(), " howaya!", Toast.LENGTH_SHORT).show();
        Log.i("MyMessage","baaaalllls");
    }
}
