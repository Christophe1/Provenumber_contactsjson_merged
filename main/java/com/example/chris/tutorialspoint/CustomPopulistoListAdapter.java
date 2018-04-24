package com.example.chris.tutorialspoint;


import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutorialspoint.R;

public class CustomPopulistoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private LayoutInflater inflater;
    public static List<Review> the_reviews;
  //  Context context_type;

    public static class ReviewHolder extends RecyclerView.ViewHolder {

        //In each populisto_list_row show the items you want to have appearing
        public TextView phone_user_name, category, name,phone, comment;

        public ReviewHolder(View itemView){
            super(itemView);
             phone_user_name = (TextView) itemView.findViewById(R.id.phone_user_name);
             category = (TextView) itemView.findViewById(R.id.category);
             name = (TextView) itemView.findViewById(R.id.name);
             phone = (TextView) itemView.findViewById(R.id.phone);
             comment = (TextView) itemView.findViewById(R.id.comment);

        }

    }

    public CustomPopulistoListAdapter(List<Review> reviewUsers, Activity activity) {

        this.activity = activity;
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
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        Review r = the_reviews.get(position);
        ((ReviewHolder) viewHolder).phone_user_name.setText(r.getPhone_user_name());
        ((ReviewHolder) viewHolder).category.setText("Category: " + r.getCategory());
        ((ReviewHolder) viewHolder).name.setText("Name: " + r.getName());
        ((ReviewHolder) viewHolder).phone.setText("Phone: " + r.getPhone());
        ((ReviewHolder) viewHolder).comment.setText("Your Comment: " + r.getComment());

        //set an onClick listener for the row, if it's clicked anywhere
        ((ReviewHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            //When the review is clicked in PopulistoListView
            //then show that review
            public void onClick(View v) {

                //position is the number of the row
                Toast.makeText(v.getContext(),position + " cheers!", Toast.LENGTH_SHORT).show();

                Review reviewUser = (Review) CustomPopulistoListAdapter.getItem(position);

                //we want to pass the review_id of the reviewUser being clicked
                //to the ViewContact activity, and from there post it and get more
                //info for that reviewUser - address, comments etc
                Intent i = new Intent(v.getContext(), ViewContact.class);
                //pass the review_id to ViewContact class
                i.putExtra("review_id", reviewUser.getReviewid());
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

        //System.out.println("CustomPopulistoListAdapter: here it is" + the_reviewUsers.size());
        return the_reviews.size();

    }


}







