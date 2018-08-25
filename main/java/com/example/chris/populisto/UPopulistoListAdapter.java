package com.example.chris.populisto;


import java.security.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutorialspoint.R;

public class UPopulistoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //private Activity activity;
   // private LayoutInflater inflater;
    public static List<Review> the_reviews;

    public static class ReviewHolder extends RecyclerView.ViewHolder {

        //In each populisto_list_row show the items you want to have appearing
        public TextView phone_user_name, date_created, category, address, name, phone, comment;

        public ReviewHolder(View itemView) {
            super(itemView);
            phone_user_name = (TextView) itemView.findViewById(R.id.phone_user_name);
            date_created = (TextView) itemView.findViewById(R.id.date_created);
            category = (TextView) itemView.findViewById(R.id.category);
            name = (TextView) itemView.findViewById(R.id.name);
            address = (TextView) itemView.findViewById(R.id.address);
            phone = (TextView) itemView.findViewById(R.id.phone);
            comment = (TextView) itemView.findViewById(R.id.comment);

        }

    }

    public UPopulistoListAdapter(List<Review> reviewUsers) {

        //this.activity = activity;
        the_reviews = reviewUsers;

    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        itemView = inflater.inflate(R.layout.populisto_list_row, parent, false);

        // View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.yourlayout, parent, false);
        return new ReviewHolder(itemView);
    }

    @Override
    //for a specific cell....
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

        Review r = the_reviews.get(position);

        //pubOrPriv is 0,1 or 2,depending on shared status of the review
        final int pubOrPriv = Integer.parseInt(r.getPublicorprivate());

        //shared_status will be U
        //String shared_status ="U";

        //change colour of "U" in each recyclerView cell of PopulistoListView
        // depending on value of pubOrPriv
        if(pubOrPriv==0){

            ((ReviewHolder) viewHolder).phone_user_name.setTextColor(Color.parseColor("#DA850B"));


        }

        if(pubOrPriv==1){
            ((ReviewHolder) viewHolder).phone_user_name.setTextColor(Color.parseColor("#0A7FDA"));
        }

        if(pubOrPriv==2){
            ((ReviewHolder) viewHolder).phone_user_name.setTextColor(Color.parseColor("#2AB40E"));
        }


        ((ReviewHolder) viewHolder).phone_user_name.setText("U");




        ((ReviewHolder) viewHolder).date_created.setText("Date Created: " + r.getDate_created());
        ((ReviewHolder) viewHolder).category.setText("Categoryy: " + r.getCategory());
        ((ReviewHolder) viewHolder).name.setText("Name: " + r.getName());
        ((ReviewHolder) viewHolder).address.setText("Address: " + r.getAddress());
        ((ReviewHolder) viewHolder).phone.setText("Phone: " + r.getPhone());
        ((ReviewHolder) viewHolder).comment.setText("Your Comment: " + r.getComment());

        //set an onClick listener for the row, if it's clicked anywhere
        ((ReviewHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            //When the review is clicked in PopulistoListView
            //then show that review
            public void onClick(View v) {

                //position is the number of the row
                Toast.makeText(v.getContext(), position + " cheers!", Toast.LENGTH_SHORT).show();

                Review reviewUser = (Review) UPopulistoListAdapter.getItem(position);

                //we want to pass the review_id of the reviewUser being clicked
                //to the ViewContact activity, and from there post it and get more
                //info for that reviewUser - address, comments etc
                Intent i = new Intent(v.getContext(), ViewContact.class);
                //pass the review_id to ViewContact class
                i.putExtra("review_id", reviewUser.getReviewid());

                //pass the intent value of pubOrPriv to ViewContact
                i.putExtra("UPuborPrivVal", pubOrPriv);
                //pass these values as an intent to ViewContact class
                i.putExtra("date_created", reviewUser.getDate_created());
                i.putExtra("category", reviewUser.getCategory());
                i.putExtra("name", reviewUser.getName());
                i.putExtra("phone", reviewUser.getPhone());
                i.putExtra("address", reviewUser.getAddress());
                i.putExtra("comment", reviewUser.getComment());

                //pass the PhoneNumberofUserFromDB to ViewContact class
                //the key is "PhoneNumberofUserFromDB"
                //i.putExtra("PhoneNumberofUserFromDB", reviewUser.getPhoneNumberofUserFromDB());
                v.getContext().startActivity(i);
            }

        });
    }

    //I have implemented a getItem method so
    //we can get the details about review, for the recyclerView row clicked
    public static Review getItem(int position) {
        return the_reviews.get(position);
    }


    @Override
    public int getItemCount() {

        //System.out.println("CustomPopulistoListAdapter: here it is" + the_Shared_reviews.size());
        return the_reviews.size();

    }


}







