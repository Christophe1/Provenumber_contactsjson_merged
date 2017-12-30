package com.example.chris.tutorialspoint;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tutorialspoint.R;

import java.util.ArrayList;
import java.util.List;



/*************************
 * Created by Chris on 28/12/2017.
 * THIS IS USED FOR SQLITE INSERTION. Not using it for the time being,
 * sticking with getting the details from server and see how fast it is.
 *
 * IT WORKS IN CONJUNCTION WITH :
 * displayMyPopulistoListView.java
 * TableData.java
 * SQLiteDatabaseOperations.java
 * BackGroundTask.java
 */

public class DisplayMyPopulistoAdapter extends ArrayAdapter {

    List list = new ArrayList<>();

    public DisplayMyPopulistoAdapter(Context context, int resource) {
        super(context, resource);


    }

    public void add(Review object) {
        list.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        DisplayMyPopulistoHolder displayMyPopulistoHolder;
        if (row == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.populisto_list_row, parent, false);
            displayMyPopulistoHolder = new DisplayMyPopulistoHolder();
            displayMyPopulistoHolder.tx_cat_name = (TextView) row.findViewById(R.id.category);
            displayMyPopulistoHolder.tx_name = (TextView) row.findViewById(R.id.name);
            row.setTag(displayMyPopulistoHolder);


        } else {
            displayMyPopulistoHolder = (DisplayMyPopulistoHolder)row.getTag();

        }

        Review review = (Review)getItem(position);
        displayMyPopulistoHolder.tx_cat_name.setText(review.getCategory().toString());
        displayMyPopulistoHolder.tx_name.setText(review.getName().toString());

        return row;
    }

    static class DisplayMyPopulistoHolder {

       TextView tx_cat_name, tx_name ;
    }
}
