package com.example.chris.tutorialspoint;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutorialspoint.R;

import java.util.List;

/**
 * Created by Chris on 07/01/2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    //make a List containing info about SelectPhoneCOntact objects
    public List<SelectPhoneContact> theContactsList;
    //  private ArrayList<SelectPhoneContact> arraylist;

    //we will run through different logic in this custom adapter based on the activity that is passed to it
    private int whichactivity;
    Context context_type;


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



    public RecyclerViewAdapter(List<SelectPhoneContact>selectPhoneContacts, Context context, int activity) {

        theContactsList =selectPhoneContacts;
        whichactivity = activity;
        context_type = context;

        // this.arraylist = new ArrayList<SelectPhoneContact>();
        // this.arraylist.addAll(theContactsList);
        // System.out.println("this.arraylist" + this.arraylist);

    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.recycler_blueprint, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);


        //if the activity is NewContact
        if (whichactivity == 1) {
            //if a checkbox is checked
            viewHolder.check.setOnClickListener(new View.OnClickListener() {
                @Override
                //when a checkbox in the Listview is clicked
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    SelectPhoneContact data = (SelectPhoneContact) cb.getTag();
                    //if it is set to unchecked
                 /*   if (cb.isChecked() == false)
                    //need this to change radio button to Phone Contacts,
                    //if a checkbox is changed to false
                    {
                        radioButtontoPhoneContacts.update();
                    }*/
                    Toast.makeText(context_type,
                            "Clicked on Checkbox: " + data.getPhone() +
                                    " is " + cb.isChecked(),
                            Toast.LENGTH_LONG).show();
                    data.setSelected(cb.isChecked());
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
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder viewHolder, int position){
        SelectPhoneContact selectPhoneContact = theContactsList.get(position);
        TextView title = viewHolder.title;
        title.setText(selectPhoneContact.getName());

        TextView phone = viewHolder.phone;
        phone.setText(selectPhoneContact.getPhone());

    }

    @Override
    public int getItemCount(){
/*        if(theContactsList == null)
            return 0;*/
        return theContactsList.size();
    }
}

