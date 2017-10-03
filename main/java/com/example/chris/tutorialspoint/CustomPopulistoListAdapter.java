package com.example.chris.tutorialspoint;


import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.example.tutorialspoint.R;

import static com.example.tutorialspoint.R.layout.populisto_list_row;

//this is for PopulistoListView.java
public class CustomPopulistoListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Review> reviews;
  //  ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomPopulistoListAdapter(Activity activity, List<Review> reviews) {
        this.activity = activity;
        this.reviews = reviews;
    }

    //public String reviewid;

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Object getItem(int location) {
        return reviews.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(populisto_list_row, null);

//        if (imageLoader == null)
//            imageLoader = AppController.getInstance().getImageLoader();
//        NetworkImageView thumbNail = (NetworkImageView) convertView
//                .findViewById(R.id.thumbnail);
        TextView category = (TextView) convertView.findViewById(R.id.category);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView phone = (TextView) convertView.findViewById(R.id.phone);
        //TextView address = (TextView) convertView.findViewById(R.id.address);
        //TextView genre = (TextView) convertView.findViewById(R.id.genre);
        TextView comment = (TextView) convertView.findViewById(R.id.comment);
        //TextView reviewid = (TextView) convertView.findViewById(R.id.reviewid);
        // getting movie data for the row
        Review r = reviews.get(position);

        // thumbnail image
//		thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);

        // category
        //set the category id in xml to the mysql value that getCategory gives to us etc....
        category.setText("Category: " + r.getCategory());

        // name
        name.setText("Name: " + r.getName());

        // phone
        phone.setText("Phone: " + r.getPhone());

        // comment
        comment.setText("Your comment: " + r.getComment());

        // reviewid
        //reviewid.setText(r.getReviewid());

        // genre
/*		String genreStr = "";
		for (String str : m.getGenre()) {
			genreStr += str + ", ";
		}
		genreStr = genreStr.length() > 0 ? genreStr.substring(0,
				genreStr.length() - 2) : genreStr;
		genre.setText(genreStr);*/

        // release year
        //comment.setText(String.valueOf(m.getYear()));

        return convertView;
    }


}