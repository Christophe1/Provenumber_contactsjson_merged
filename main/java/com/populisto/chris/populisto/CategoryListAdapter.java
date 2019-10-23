package com.populisto.chris.populisto;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.populisto.chris.populisto.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 30/05/2018.
 */

public class CategoryListAdapter extends ArrayAdapter<CategoryList> {

  //existingCategoryList is a breakdown of our jsonString
  private List<CategoryList> existingCategoryList;
  private List<CategoryList> tempCategoryList;
  private List<CategoryList> suggestionCategoryList;

  public CategoryListAdapter(@NonNull Context context, int resource, @NonNull List<CategoryList> objects) {

    super(context, resource, objects);
    existingCategoryList = objects;
    tempCategoryList = new ArrayList<>(existingCategoryList);
    //when user types, get the data from tempCategoryList and filter it
    suggestionCategoryList = new ArrayList<>();
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

    if (convertView == null)
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.categorylist_dropdown_layout, parent, false);

    TextView textView = (TextView) convertView.findViewById(R.id.simple_text);

    //make existingCategoryList an object of CategoryList class
    CategoryList categoryList = existingCategoryList.get(position);

    textView.setText(categoryList.getName());

    return convertView;
  }

  @NonNull
  @Override
  public Filter getFilter() {
    return categoryFilter;
  }

  Filter categoryFilter = new Filter() {

    @Override
    public CharSequence convertResultToString(Object resultValue) {

      //we want to convert the categoryList value to a string
      CategoryList categoryList = (CategoryList) resultValue;

      return categoryList.getName();
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

      FilterResults filterResults = new FilterResults();

      //contraint is the text being typed
      if (constraint != null && constraint.length() > 0) {

        //clear suggestionCategoryList if it exists already
        suggestionCategoryList.clear();
        //convert constraint to lower case
        constraint = constraint.toString().trim().toLowerCase();

        //for every user in our tempCategoryList
        for (CategoryList categoryList : tempCategoryList) {

          //if the categoryList name contains the text we have typed
          if (categoryList.getName().toLowerCase().contains(constraint)) {
            //then add it to the suggestionCategoryList
            suggestionCategoryList.add(categoryList);
          }
        }

        filterResults.count = suggestionCategoryList.size();
        filterResults.values = suggestionCategoryList;

      }
      return filterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

      ArrayList<CategoryList> uList = (ArrayList<CategoryList>) results.values;

      if (results != null && results.count > 0) {

        //clear the data
        clear();

        for (CategoryList u : uList) {

          add(u);
          notifyDataSetChanged();
        }
      }
    }

  };

}