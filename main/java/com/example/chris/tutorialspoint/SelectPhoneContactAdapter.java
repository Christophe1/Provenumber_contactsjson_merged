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
import java.util.List;
import java.util.Locale;

/**
 * Created by Chris on 23/06/2017.
 */

public class SelectPhoneContactAdapter extends BaseAdapter {

    //define a list made out of SelectContacts and call it theContactsList
    public List<SelectPhoneContact> theContactsList;
    //define an array list made out of SelectContacts and call it arraylist
    private ArrayList<SelectPhoneContact> arraylist;
    Context _c;

    //define a ViewHolder to hold our name and number info, instead of constantly querying
    // findviewbyid. Makes the ListView run smoother
    ViewHolder v;

//    RoundImage roundedImage;

    public SelectPhoneContactAdapter(List<SelectPhoneContact> selectPhoneContacts, Context context) {
        theContactsList = selectPhoneContacts;
        _c = context;
        this.arraylist = new ArrayList<SelectPhoneContact>();
        this.arraylist.addAll(theContactsList);
    }


    @Override
    public int getCount() {
        return theContactsList.size();
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
        CheckBox checkbox;
        TextView title, phone, lookup;
//        CheckBox check;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        //we're naming our convertView as view
        View view = convertView;

        if (view == null) {

            //if there is nothing there (if it's null) inflate the layout for each row
            LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.phone_inflate_listview, null);
//            Log.e("Inside", "here--------------------------- In view1");


            //or else use the view (what we can see in each row) that is already there
        } else {
            view = convertView;
//            Log.e("Inside", "here--------------------------- In view2");
        }


        //        we are making a cell format in the ListView, which will contain info like
//        number, name... the layout for this, with name, no, pic etc...
//        is contained in phone_inflate_listview.xmlew.xml, which describes how each cell data
//        loads into the listview
        //Viewholder stores component views together so we can
        // immediately access them without the need to lookup repeatedly.
        // Saves on resources, makes the listview smoother. It saves on having to look up findviewbyid all the time
        v = new ViewHolder();

//      So, for example, title is cast to the name id, in phone_inflate_listview,
//        phone is cast to the id called no etc
        v.title = (TextView) view.findViewById(R.id.name);
//        v.check = (CheckBox) view.findViewById(R.id.check);
        v.phone = (TextView) view.findViewById(R.id.no);
//        set text size to 0dp, and height to 0dp, user doesn't need to see it
        //v.lookup = (TextView) view.findViewById(R.id.lookup);
        v.checkbox = (CheckBox) view.findViewById(R.id.checkBoxContact);

//        store the holder with the view
        final SelectPhoneContact data = (SelectPhoneContact) theContactsList.get(i);
        v.title.setText(data.getName());
        v.checkbox.setChecked(data.getCheckedBox());
        v.phone.setText(data.getPhone());
        //v.lookup.setText(data.getLookup());

//        set a tag for a string we're calling getthename, and make it equal to the name of the contact
        //same for getthenumber and getthelookup
     //   v.checkbox.setTag(R.string.getthename,data.getName());
     //   v.checkbox.setTag(R.string.getthenumber,data.getPhone());
    //    v.checkbox.setTag(R.string.getthelookup,data.getLookup());


      //   Set checkbox listener android
        // need this here so the listview remembers which checkbox is clicked,
        //when scrolled
/*        v.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) view;
                if (checkBox.isChecked()) {
                    data.setCheckedBox(true);
                  } else {
                    data.setCheckedBox(false);
                }
            }
        });*/

        view.setTag(data);

        return view;
    }

    // Filter Class. This is for search
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
//        theContactsList is our list of contacts
        theContactsList.clear();
//        If there is nothing in the searchview, if the charText length is 0,
//        then show all the contacts
        if (charText.length() == 0) {
            theContactsList.addAll(arraylist);
//            or else....
        } else {
            for (SelectPhoneContact wp : arraylist) {
//                If a contact's name matches the input thus far, which is charText,
//                then include it in the listview.
                if ((wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) || (wp.getPhone().toLowerCase(Locale.getDefault())
                        .contains(charText)))
                {


//                    int flag = 0;
//
//                    for(int i=0;i<arraylist.size();i++){
//
//                        if(!arraylist.get(i).getPhone().trim().equals(name)){
//                            flag = 1;
//
//                        }else{
//                            flag =0;
//                            break;
//                        }
//
//                    }
//                    if(flag == 1){
//                        arraylist.add(new SelectPhoneContact(name, phoneNumber));
//                    }


                    theContactsList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}
