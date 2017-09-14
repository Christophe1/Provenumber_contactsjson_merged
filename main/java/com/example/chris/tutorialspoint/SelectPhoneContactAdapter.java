package com.example.chris.tutorialspoint;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutorialspoint.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static com.example.tutorialspoint.R.id.phone;

/**
 * Created by Chris on 23/06/2017.
 */

public class SelectPhoneContactAdapter extends BaseAdapter {

    //define a list made out of SelectPhoneContacts and call it theContactsList
    public List<SelectPhoneContact> theContactsList;
    //define an array list made out of SelectContacts and call it arraylist
    private ArrayList<SelectPhoneContact> arraylist;
  //  boolean mChecked[];
 //   boolean itemChecked[];
    Context _c;


    //define a ViewHolder to hold our name and number info, instead of constantly querying
    // findviewbyid. Makes the ListView run smoother
    ViewHolder v;

    public SelectPhoneContactAdapter(final List<SelectPhoneContact> selectPhoneContacts, Context context) {
        theContactsList = selectPhoneContacts;
        _c = context;
        this.arraylist = new ArrayList<SelectPhoneContact>();
        this.arraylist.addAll(theContactsList);
      //  itemChecked = new boolean[theContactsList.size()];

    }


    @Override
    public int getCount() {
        System.out.println("the amount in arraylist :" + arraylist.size());
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
        TextView title, phone, lookup;
        CheckBox check;
    }

    @Override
    public View getView( int i, View convertView, ViewGroup viewGroup) {

        //we're naming our convertView as view
        View view = convertView;


        if (view == null) {

            v = new ViewHolder();


            //if there is nothing there (if it's null) inflate the view with the layout
            LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.phone_inflate_listview, null);


            //      So, for example, title is cast to the name id, in phone_inflate_listview,
//        phone is cast to the id called no etc
            v.title = (TextView) view.findViewById(R.id.name);
            v.phone = (TextView) view.findViewById(R.id.no);
            v.check = (CheckBox) view.findViewById(R.id.checkBoxContact);
            //       v.lookup = (TextView) view.findViewById(R.id.lookup);
//       v.check.setTag(Integer.valueOf(i)); // set the tag so we can identify the correct row in the listener
//        v.check.setChecked(mCheckStates.get(i, false)); // set the status as we stored it
//        v.check.setOnCheckedChangeListener(mListener); // set the listener




            view.setTag(v);

/*
            v.title.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v ;
                    SelectPhoneContact selectPhoneContact = (SelectPhoneContact) cb.getTag();
*/
/*                    Toast.makeText(getApplicationContext(),
                            "Clicked on Checkbox: " + cb.getText() +
                                    " is " + cb.isChecked(),
                            Toast.LENGTH_LONG).show();*//*

                    selectPhoneContact.setSelected(cb.isChecked());
                }
            });
*/

            // v.check.setOnCheckedChangeListener((NewContact) _c);

            //or else use the view (what we can see in each row) that is already there
        } else {
            view = convertView;

        }


//        store the holder with the view
        final SelectPhoneContact data = (SelectPhoneContact) theContactsList.get(i);

        //in the listview for contacts, set the name
        v.title.setText(data.getName());
       // v.title.setText(getItem(i));
        //in the listview for contacts, set the number
        v.phone.setText(data.getPhone());


        v.check.setChecked(false);
/*
        v.check.setChecked(itemChecked[i]);

        v.check
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        itemChecked[i] = isChecked;
                    }
                });*/

         //view.setTag(data);

        // Return the completed view to render on screen
        return view;

    }

/*    CompoundButton.OnCheckedChangeListener mListener = new CompoundButton.OnCheckedChangeListener() {

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mChecked[(Integer)buttonView.getTag()] = isChecked; // get the tag so we know the row and store the status
            System.out.println("getview position :" + mChecked);
        }
    };*/

    }



