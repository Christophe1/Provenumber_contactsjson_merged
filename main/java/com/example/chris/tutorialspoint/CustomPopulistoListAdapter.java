package com.example.chris.tutorialspoint;


import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.example.tutorialspoint.R;

import static com.example.tutorialspoint.R.layout.populisto_list_row;

public class CustomPopulistoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private LayoutInflater inflater;
    public static List<Review> the_reviews;
  //  Context context_type;

    public static class ReviewHolder extends RecyclerView.ViewHolder {

        //In each populisto_list_row show the items you want to have appearing
        public TextView category, name,phone, comment;

        public ReviewHolder(View itemView){
            super(itemView);
             category = (TextView) itemView.findViewById(R.id.category);
             name = (TextView) itemView.findViewById(R.id.name);
             phone = (TextView) itemView.findViewById(R.id.phone);
             comment = (TextView) itemView.findViewById(R.id.comment);

        }

    }

    public CustomPopulistoListAdapter(List<Review> reviews, Activity activity) {

        this.activity = activity;
        the_reviews = reviews;


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
        ((ReviewHolder) viewHolder).category.setText("Category: " + r.getCategory());
        ((ReviewHolder) viewHolder).name.setText("Name: " + r.getName());
        ((ReviewHolder) viewHolder).phone.setText("Phone: " + r.getPhone());
        ((ReviewHolder) viewHolder).comment.setText("Your Comment: " + r.getComment());

        //set an onClick listener for the row, if it's clicked anywhere
        ((ReviewHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //position is the number of the row
                Toast.makeText(v.getContext(),position + " cheers!", Toast.LENGTH_SHORT).show();

            }

        });
    }


    @Override
    public int getItemCount() {

        System.out.println("CustomPopulistoListAdapter: here it is" + the_reviews.size());
        return the_reviews.size();

    }


}







