package com.populisto.chris.populisto;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.populisto.chris.populisto.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 07/01/2018.
 */

public class PopulistoContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  //for changing the colour of 'Phone COntacts' button
  private Context mContext;


  //make a List containing info about SelectPhoneContact objects
  //theContactsList is ALL phone contacts on logged-in user's phone
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

  //we want to detect if checkboxes have changed,
  //if so, we'll give the option to save, if cancel is clicked
  public static Boolean checkBoxhasChanged = false;

  public class MatchingContact extends RecyclerView.ViewHolder {

    //In each recycler_blueprint show the items you want to have appearing
    public TextView title, phone;
    public CheckBox check;
    //public Button invite;


    public MatchingContact(final View itemView) {
      super(itemView);
      //title is cast to the name id, in recycler_blueprint,
      //phone is cast to the id called no etc
      title = (TextView) itemView.findViewById(R.id.name);
      phone = (TextView) itemView.findViewById(R.id.no);
     // invite = (Button) itemView.findViewById(R.id.btnInvite);
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

    //If permission denied (will only be on Marshmallow +)
    PackageManager manager = context_type.getPackageManager();
    int hasPermission = manager.checkPermission("android.permission.READ_CONTACTS", "com.populisto.chris.populisto");
    if (hasPermission == manager.PERMISSION_DENIED) {

      System.out.println("it is not in shared prefs" + allPhonesofContacts);

    }
      else {

      System.out.println("gettype row is: " + theContactsList.get(position).getType_row());
      return Integer.parseInt(theContactsList.get(position).getType_row());
    }
    return 1;
  }

  public PopulistoContactsAdapter(List<SelectPhoneContact> selectPhoneContacts, Context context, int activity) {
    //selectPhoneContacts = new ArrayList<SelectPhoneContact>();

    // checkAccumulator = 0;

    theContactsList = selectPhoneContacts;

    this.mContext = context;
    whichactivity = activity;
    context_type = context;

    //we are fetching the array list allPhonesofContacts, created in VerifyUserPhoneNumber.
    //with this we will put all phone numbers of contacts on user's phone into our recyclerView in NewContact activity
    SharedPreferences sharedPreferencesallPhonesofContacts = PreferenceManager.getDefaultSharedPreferences(context_type);


    if (!sharedPreferencesallPhonesofContacts.contains("allPhonesofContacts")) {
      System.out.println("it is not in shared prefs" + allPhonesofContacts);

    } else {
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

      //we are fetching the array list MatchingContactsAsArrayList, created in VerifyUserPhoneNumber.
      //with this we will put a checkbox beside the matching contacts
      SharedPreferences sharedPreferencesMatchingContactsAsArrayList = PreferenceManager.getDefaultSharedPreferences(context_type);

      if (sharedPreferencesMatchingContactsAsArrayList.contains("MatchingContactsAsArrayList")) {
        Gson gsonMatchingContactsAsArrayList = new Gson();
        String jsonMatchingContactsAsArrayList = sharedPreferencesMatchingContactsAsArrayList.getString("MatchingContactsAsArrayList", "");
        Type type1 = new TypeToken<ArrayList<String>>() {
        }.getType();
        MatchingContactsAsArrayList = gsonMatchingContactsAsArrayList.fromJson(jsonMatchingContactsAsArrayList, type1);
        System.out.println("SelectPhoneContactAdapter MatchingContactsAsArrayList :" + MatchingContactsAsArrayList);
      }

    }
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

      //we are FETCHING the array list checkedContactsAsArrayList, committed in ViewContact.
      //with this we will put a tick in the checkboxes of contacts the review is being shared with
      SharedPreferences sharedPreferencescheckedContactsAsArrayList = PreferenceManager.getDefaultSharedPreferences(context_type);
      Gson gsoncheckedContactsAsArrayList = new Gson();
      String jsoncheckedContactsAsArrayList = sharedPreferencescheckedContactsAsArrayList.getString("checkedContactsAsArrayList", "");
      Type type2 = new TypeToken<ArrayList<String>>() {
      }.getType();
      checkedContactsAsArrayList = gsoncheckedContactsAsArrayList.fromJson(jsoncheckedContactsAsArrayList, type2);
      System.out.println("SelectPhoneContactAdapter checkedContactsAsArrayList :" + checkedContactsAsArrayList);

      //if the row is a matching contact
      if (viewHolder.getItemViewType() == 1)

      {
        System.out.println("it's 1");
        //in the title textbox in the row, put the corresponding name etc...
        ((MatchingContact) viewHolder).title.setText(selectPhoneContact.getName());
        ((MatchingContact) viewHolder).phone.setText(selectPhoneContact.getPhone());
        ((MatchingContact) viewHolder).check.setChecked(theContactsList.get(position).getSelected());
        ((MatchingContact) viewHolder).check.setTag(position);

        //disable the check box, can't be changed
        ((MatchingContact) viewHolder).check.setEnabled(false);


      } else {

        System.out.println("it's 2");
        //if getItemViewType == 2, then show the invite button layout
        ((nonMatchingContact) viewHolder).title.setText(selectPhoneContact.getName());
        ((nonMatchingContact) viewHolder).phone.setText(selectPhoneContact.getPhone());
      }


      //This is for ViewContact, to display the contact the review is shared with
      //for every phone number in the checkedContactsAsArrayList array list...
      for (int number2 = 0; number2 < checkedContactsAsArrayList.size(); number2++) {
        // Log.i("MyMessage", "checkedContactsAsArrayList is: " + checkedContactsAsArrayList);
        System.out.println("PopulistoContactsAdapter: checkedContactsAsArrayList is: " + checkedContactsAsArrayList);

        //if a phone number is in our array of checked contacts
        if (checkedContactsAsArrayList.contains(selectPhoneContact.getPhone())) {
          //check the box
          ((MatchingContact) viewHolder).check.setChecked(true);
        }
      }

    }


    //if the activity is NewContact
    if (whichactivity == 1) {

      //if the row is a matching contact
      if (viewHolder.getItemViewType() == 1)

      {


        //in the title textbox in the row, put the corresponding name, phone number,
        //and checkbox, checked, the review will be shared by default
        ((MatchingContact) viewHolder).title.setText(selectPhoneContact.getName());
        ((MatchingContact) viewHolder).phone.setText(selectPhoneContact.getPhone());
        ((MatchingContact) viewHolder).check.setChecked(theContactsList.get(position).getSelected());
        ((MatchingContact) viewHolder).check.setTag(position);

        Toast.makeText(context_type, "matching contacts arre:" + MatchingContactsAsArrayList, Toast.LENGTH_SHORT).show();
        Toast.makeText(context_type, "checked checkboxes are:" + checkedContactsAsArrayList, Toast.LENGTH_SHORT).show();

        //for the onClick of the check box
        ((MatchingContact) viewHolder).check.setOnClickListener(new View.OnClickListener() {

          @Override
          public void onClick(View v) {

            //***********NEED THIS TO PRESERVE CHECKBOX STATE, otherwise
            //can be lost on scroll
            if (theContactsList.get(position).getSelected()) {
              theContactsList.get(position).setSelected(false);
              //Toast.makeText(context_type, theContactsList.get(position).getPhone() + " unclicked!", Toast.LENGTH_SHORT).show();
              //***************************

              //remove the checked number from the arraylist
              checkedContactsAsArrayList.remove(theContactsList.get(position).getPhone());
              Toast.makeText(context_type, "checkboxes are:" + checkedContactsAsArrayList, Toast.LENGTH_SHORT).show();

            } else {

              //if clicked
              //position is the row number that the clicked checkbox exists in

              //***********NEED THIS TO PRESERVE CHECKBOX STATE, otherwise
              //can be lost on scroll
              theContactsList.get(position).setSelected(true);
              //Toast.makeText(context_type, theContactsList.get(position).getPhone() + " clicked!", Toast.LENGTH_SHORT).show();
              //***************************

              //add the number to the arrayList
              checkedContactsAsArrayList.add(theContactsList.get(position).getPhone());
              Toast.makeText(context_type, "checkboxes are:" + checkedContactsAsArrayList, Toast.LENGTH_SHORT).show();

            }

            //if count is 0, nothing selected, then show 'Just Me'
            if (checkedContactsAsArrayList.size() == 0) {

              if (mContext instanceof NewContact) {
                ((NewContact) mContext).changeColorofJustMe();


              }

            } else {

              //select 'Phone Contacts' button in NewContact.java
              //if count is more than 0
              if (mContext instanceof NewContact) {


                ((NewContact) mContext).changeColourOfPhoneContacts();
                //  Toast.makeText(context_type, "The count is " + checkedContactsAsArrayList.size(), Toast.LENGTH_SHORT).show();

              }
            }
          }


        });

      }


      else {

        //for people on logged-in user's phone who aren't populisto users
        ((nonMatchingContact) viewHolder).title.setText(selectPhoneContact.getName());
        ((nonMatchingContact) viewHolder).phone.setText(selectPhoneContact.getPhone());

      }


    }


    //if the activity is EditContact
    if (whichactivity == 2) {

      //we need to remove the logged-in user's phone number from checkedContactsAsArrayList
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
            //Integer pos = (Integer) ((MatchingContact) viewHolder).check.getTag();

            //if the checkbox is checked
            if (((MatchingContact) viewHolder).check.isChecked())

            {
              //we want to add the phone number of the checked row into our arraylist.
              //this arraylist will be passed to EditContact class when Save is clicked and
              //saved in review_shared table on server.

              //add the checked number into the arraylist
              checkedContactsAsArrayList.add(theContactsList.get(position).getPhone());

            } else {
              //remove the checked number from the arraylist
              checkedContactsAsArrayList.remove(theContactsList.get(position).getPhone());

            }

            Toast.makeText(context_type, "checkedContactsAsArrayList: " + checkedContactsAsArrayList, Toast.LENGTH_SHORT).show();

            //*************

            //NEED THIS TO PRESERVE CHECKBOX STATE, otherwise
            //can be lost on scroll

            if (theContactsList.get(position).getSelected()) {
              theContactsList.get(position).setSelected(false);
              //    Toast.makeText(context_type, theContactsList.get(pos).getSelected() + " unclicked!", Toast.LENGTH_SHORT).show();

            } else {

              theContactsList.get(position).setSelected(true);
              //   Toast.makeText(context_type, theContactsList.get(pos).getSelected() + " clicked!", Toast.LENGTH_SHORT).show();
              //theContactsList.get(pos).getPhone();

            }

            //***************

            Toast.makeText(context_type, "The count is " + checkedContactsAsArrayList.size(), Toast.LENGTH_SHORT).show();
            System.out.println("EditContact: checkedContactsAsArrayList: " + checkedContactsAsArrayList);

            //if count is 0, nothing selected, then show 'Just Me'
            if (checkedContactsAsArrayList.size() == 0) {

              if (mContext instanceof EditContact) {
                ((EditContact) mContext).changeColorofJustMe();

                //  Toast.makeText(context_type, "count is 0!yeah", Toast.LENGTH_SHORT).show();

              }

            } else {

              //change the colour of 'Phone Contacts' button in EditContact.java
              if (mContext instanceof EditContact) {
                ((EditContact) mContext).changeColourOfPhoneContacts();

                //  Toast.makeText(context_type, "The count is " + checkedContactsAsArrayList.size(), Toast.LENGTH_SHORT).show();

              }
            }

            checkBoxhasChanged = true;
            //Toast.makeText(context_type, "here it is dudia" + String.valueOf(checkBoxhasChanged), Toast.LENGTH_SHORT).show();

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


               /* ((MatchingContact) viewHolder).check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                  @Override
                   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {*/
        //   checkBoxhasChanged = true;
        //  Toast.makeText(context_type, "here it is dudia" + String.valueOf(checkBoxhasChanged), Toast.LENGTH_SHORT).show();
                 /*   }
                 }
                );*/


      } else {
        //if it's a nonMatching contact
        ((nonMatchingContact) viewHolder).title.setText(selectPhoneContact.getName());
        ((nonMatchingContact) viewHolder).phone.setText(selectPhoneContact.getPhone());

      }


    }

  }


  @Override
  public int getItemCount() {
    System.out.println("here it is, thecontactlist" + theContactsList.size());

    return theContactsList.size();
  }


}