package com.example.chris.tutorialspoint;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.tutorialspoint.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.id;

/**
 * Created by Chris on 23/06/2017.
 */

public class SelectPhoneContactAdapter extends BaseAdapter {

    //define a list made out of SelectPhoneContacts and call it theContactsList
    public List<SelectPhoneContact> theContactsList;
    //define an array list made out of SelectContacts and call it arraylist
    private ArrayList<SelectPhoneContact> arraylist;
    Context _c;

    //define a ViewHolder to hold our name and number info, instead of constantly querying
    // findviewbyid. Makes the ListView run smoother
    ViewHolder viewHolder;

    public SelectPhoneContactAdapter(final List<SelectPhoneContact> selectPhoneContacts, Context context) {
        theContactsList = selectPhoneContacts;
        _c = context;
        this.arraylist = new ArrayList<SelectPhoneContact>();
        this.arraylist.addAll(theContactsList);

    }



    @Override
    public int getCount() {
        System.out.println("the amount in arraylist :" + theContactsList.size());
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
        TextView title, phone;
        CheckBox check;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        //we're naming our convertView as view
        View view = convertView;

        viewHolder = new ViewHolder();

        if (view == null) {

            //if there is nothing there (if it's null) inflate the view with the layout
            LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.phone_inflate_listview, null);

            //or else use the view (what we can see in each row) that is already there
        } else {
            view = convertView;
        }


//      So, for example, title is cast to the name id, in phone_inflate_listview,
//        phone is cast to the id called no etc
        viewHolder.title = (TextView) view.findViewById(R.id.name);
        viewHolder.phone = (TextView) view.findViewById(R.id.no);

        viewHolder.check = (CheckBox) view.findViewById(R.id.checkBoxContact);
        viewHolder.check.setVisibility(View.GONE);

        //remember the state of the checkbox
        viewHolder.check.setOnCheckedChangeListener((NewContact) _c);


//        store the holder with the view
        final SelectPhoneContact data = (SelectPhoneContact) theContactsList.get(i);
        //in the listview for contacts, set the name
        viewHolder.title.setText(data.getName());
        //in the listview for contacts, set the number
        viewHolder.phone.setText(data.getPhone());

        viewHolder.check.setChecked(data.isSelected());
        viewHolder.check.setTag(data);

        view.setTag(data);

        // Return the completed view to render on screen
        return view;
    }

}
