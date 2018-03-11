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

import com.android.volley.toolbox.NetworkImageView;
import com.example.tutorialspoint.R;

import static com.example.tutorialspoint.R.layout.populisto_list_row;

public class CustomPopulistoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Review> reviews;


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
        this.reviews = reviews;

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
         Review r = reviews.get(position);
        ((ReviewHolder) viewHolder).category.setText("Category: " + r.getCategory());
        ((ReviewHolder) viewHolder).name.setText("Name: " + r.getCategory());
        ((ReviewHolder) viewHolder).phone.setText("Phone: " + r.getCategory());
        ((ReviewHolder) viewHolder).comment.setText("Your Comment: " + r.getCategory());

      //  Bitmap myBitmap = BitmapFactory.decodeFile(f.get(position));
       // holder.imageview.setImageBitmap(myBitmap);

    }

    @Override
    public int getItemCount() {

        return reviews.size();
    }


}







