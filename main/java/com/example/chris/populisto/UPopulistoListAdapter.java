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

import com.example.chris.populisto.SharedReviews.SharedViewContact;
import com.example.tutorialspoint.R;

//DESCRIPTION OF ACTIVITY
//This is for showing a list in the recyclerView of reviews created by
//the logged-in user, "U", AND also random reviews.
//When a cell is clicked we will show ViewContact, with EDIT and DELETE, or SharedViewContact
//for random reviews. This will depend on what
//getItemViewType is set to, 1 or 2 or 3
public class UPopulistoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //private Activity activity;
   // private LayoutInflater inflater;
    public static List<Review> the_reviews;

    //we will be passing as an intent which view to show in SharedViewContact based on this value
    Integer getItemViewType;

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

    @Override
    public int getItemViewType(int position) {
        //for each row in recyclerview, get the getType_row
        //what layout is shown will depend on whether getType_row is "1" or "2"
        //this is to decide to show ViewContact or SharedViewContact,
        //for next activity
        return Integer.parseInt(the_reviews.get(position).getType_row());
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

        final Review r = the_reviews.get(position);

        //pubOrPriv is based on publicorprivate in getPublicorprivate
        // taken from the server, which is 0,1 or 2,depending on shared status of the review
        final int pubOrPriv = Integer.parseInt(r.getPublicorprivate());


        //If the review being shown in recyclerView belongs to
        //logged-in user then the colour of "U" will be dependant
        //on the shared status of that review: Just U, Phone Contacts or Public (0,1 or 2)

        //If the review is owned by the logged-in user...
        if (viewHolder.getItemViewType() == 1) {

            //If the review owned by logged-in user is not shared with anybody, only himself
            if (pubOrPriv == 0) {
                //change colour of "U" depending on value
                //if it is "Just U"
                ((ReviewHolder) viewHolder).phone_user_name.setTextColor(Color.parseColor("#DA850B"));
            }
            //If logged-in user review is shared with Phone Contacts
            if (pubOrPriv == 1) {
                ((ReviewHolder) viewHolder).phone_user_name.setTextColor(Color.parseColor("#0A7FDA"));
            }
            //If logged-in user review is shared with Public
            if (pubOrPriv == 2) {
                ((ReviewHolder) viewHolder).phone_user_name.setTextColor(Color.parseColor("#2AB40E"));
            }

            ((ReviewHolder) viewHolder).phone_user_name.setText("U");
        }

        //If setType_row = 2 in PopulistoListView,
        //if the review is by a phone contact, then make the
        // phoneNameOnPhone in blue text
        if (viewHolder.getItemViewType() == 2) {
            ((ReviewHolder) viewHolder).phone_user_name.setTextColor(Color.parseColor("#0A7FDA"));

        }

        //If setType_row = 3 in PopulistoListView,
        //if the review is public by a user not in logged-in user's phone contacts,
        // then make the phoneNameOnPhone in green text
        if (viewHolder.getItemViewType() == 3) {
            ((ReviewHolder) viewHolder).phone_user_name.setTextColor(Color.parseColor("#2AB40E"));

        }

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
               // Toast.makeText(v.getContext(), position + " cheers!", Toast.LENGTH_SHORT).show();

                Toast.makeText(v.getContext(), "UPopulistoListAdapter", Toast.LENGTH_LONG).show();


                Review reviewUser = (Review) UPopulistoListAdapter.getItem(position);

                //If getItemViewType = 1, which means getType_row = 1,
                // then the logged-in user owns the review
                //so load ViewContact -
                //with edit, delete button, list of contacts review is shared with etc...
                if (viewHolder.getItemViewType() == 1) {

                //we want to pass the review_id of the reviewUser being clicked
                //to the ViewContact activity, and from there post it and get more
                //info for that reviewUser - address, comments etc
                Intent i = new Intent(v.getContext(), ViewContact.class);
                //pass the review_id to ViewContact class
                //the key is "review_id"
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

                    //Pass as intent to ViewContact, decide what colour to show "U"
                    getItemViewType = 1;

                    i.putExtra("getItemViewType", getItemViewType);


                    //pass the PhoneNumberofUserFromDB to ViewContact class
                //the key is "PhoneNumberofUserFromDB"
                //i.putExtra("PhoneNumberofUserFromDB", reviewUser.getPhoneNumberofUserFromDB());
                v.getContext().startActivity(i);
            }

                //If getType_row is 2 or 3, show SharedViewContact
                else {

                    //grab these details as an intent and pass
                    //to the SharedViewContact activity
                    Intent i = new Intent(v.getContext(), SharedViewContact.class);
                    //pass the review_id to SharedViewContact class
                    //the key is "review_id"
                    i.putExtra("review_id", reviewUser.getReviewid());

                    i.putExtra("category", reviewUser.getCategory());
                    i.putExtra("name", reviewUser.getName());
                    i.putExtra("phone", reviewUser.getPhone());
                    i.putExtra("address", reviewUser.getAddress());
                    i.putExtra("comment", reviewUser.getComment());

                    //show the review maker's name from logged-in user's phone, or else masked number
                    i.putExtra("PhoneNameonPhone", r.getPhoneNameonPhone());

                    //pass getItemViewType value to SharedViewContact with an intent
                    //it will make phoneNameonPhone in BLUE text
                    if (viewHolder.getItemViewType() == 2) {
                        //BLUE text for name
                        getItemViewType = 2;
                    }

                    //pass getItemViewType value to SharedViewContact with an intent
                    //it will make phoneNameonPhone in GREEN text
                    if (viewHolder.getItemViewType() == 3) {
                        //GREEN text for name
                        getItemViewType = 3;
                    }

                    i.putExtra("getItemViewType", getItemViewType);

                    //start the SharedViewContact activity
                    v.getContext().startActivity(i);

                }

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







