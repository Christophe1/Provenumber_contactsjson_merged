package com.example.chris.tutorialspoint;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.tutorialspoint.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Chris on 23/06/2017.
 */

public class SelectPhoneContactAdapter extends BaseAdapter {

    //define a list made out of SelectPhoneContacts and call it theContactsList
    public List<SelectPhoneContact> theContactsList;
    //define an array list made out of SelectContacts and call it arraylist
    private ArrayList<SelectPhoneContact> arraylist;

    Context context;
    ArrayList<HashMap<String, String>> stuff;
    HashMap<String, String> resultp = new HashMap<String, String>();


    //define a ViewHolder to hold our name and number info, instead of constantly querying
    // findviewbyid. Makes the ListView run smoother
    ViewHolder v;

    public SelectPhoneContactAdapter(final List<SelectPhoneContact> selectPhoneContacts, Context context,
                                     ArrayList<HashMap<String, String>> arraylistofstuff) {

        theContactsList = selectPhoneContacts;
        this.context = context;
        this.arraylist = new ArrayList<SelectPhoneContact>();
        this.arraylist.addAll(theContactsList);
        stuff = arraylistofstuff;

    }


    @Override
    public int getCount() {
        System.out.println("the amount in arraylist :" + arraylist.size());
        return stuff.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
        //return theContactsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
       // return i;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)

    static class ViewHolder {
        //        In each cell in the listview show the items you want to have
//        Having a ViewHolder caches our ids, instead of having to call and load each one again and again
        TextView title, phone, lookup;
        CheckBox check;
    }

    @Override
    public View getView( int i, View convertView, ViewGroup parent) {

        //we're naming our convertView as view
        View view = convertView;


        if (view == null) {

            v = new ViewHolder();


            //if there is nothing there (if it's null) inflate the view with the layout
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.phone_inflate_listview, null);

            // Get the position
            resultp = stuff.get(i);

            //      So, for example, title is cast to the name id, in phone_inflate_listview,
//        phone is cast to the id called no etc
            v.title = (TextView) view.findViewById(R.id.name);
            v.phone = (TextView) view.findViewById(R.id.no);
            v.check = (CheckBox) view.findViewById(R.id.checkBoxContact);




            view.setTag(v);

        } else {
            view = convertView;

        }


//        store the holder with the view
        final SelectPhoneContact data = (SelectPhoneContact) theContactsList.get(i);

        //in the listview for contacts, set the name
        v.title.setText(data.getName());
        //in the listview for contacts, set the number
        v.phone.setText(data.getPhone());


        //v.check.setChecked(false);

        // Return the completed view to render on screen
        return view;

    }


    }



