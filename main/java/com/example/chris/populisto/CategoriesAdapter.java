package com.example.chris.populisto;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutorialspoint.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 06/04/2018.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder>
    implements Filterable {
  private Context context;
  private List<Category> categoryList;
  //categoryListFiltered are the names in the filtered list
  public List<Category> categoryListFiltered;
  private CategoriesAdapterListener listener;
  //private List<Category> categoryListFiltered = new ArrayList<>();


  public class MyViewHolder extends RecyclerView.ViewHolder {
    //'sharedWith' is the box for holding (1,0,0)....
    public TextView name, sharedWith;
    // public ImageView thumbnail;

    public MyViewHolder(View view) {
      super(view);
      name = (TextView) view.findViewById(R.id.name);
      sharedWith = (TextView) view.findViewById(R.id.sharedWith);
      // thumbnail = view.findViewById(R.id.thumbnail);

      view.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          // send selected contact in callback
          listener.onCategorySelected(categoryListFiltered.get(getAdapterPosition()));
        }
      });
    }
  }


  public CategoriesAdapter(Context context, List<Category> categoryList, CategoriesAdapterListener listener) {
    this.context = context;
    this.listener = listener;
    this.categoryList = categoryList;
    this.categoryListFiltered = categoryList;
    //categoryListFiltered = new ArrayList<>();
  }

  @Override
  public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.user_row_item_fetch, parent, false);

    return new MyViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(MyViewHolder holder, final int position) {

    final Category category = categoryListFiltered.get(position);

    holder.name.setText(category.getName());
    holder.sharedWith.setText(Html.fromHtml("(" + "<font color='#DA850B'>" + "U" + "</font>" + "," + "<font color='#0A7FDA'>" + category.getPrivateCount() + "</font>" + "," + "<font color='#009900'>" + category.getPublicCount() + "</font>" + ")"));
  }

  @Override
  public int getItemCount() {
    Toast.makeText(context, "size of list is " + categoryListFiltered.size(), Toast.LENGTH_SHORT).show();

    return categoryListFiltered.size();
  }


  @Override
  public Filter getFilter() {
    return new Filter() {
      @Override
      protected FilterResults performFiltering(CharSequence charSequence) {

        //the text entered in the search box
        String charString = charSequence.toString();

        //if searchbox is empty...
        if (charString.isEmpty()) {

          //set the filtered list to the whole category list
          categoryListFiltered = categoryList;
        } else {
          List<Category> filteredList = new ArrayList<>();

          //for every row in the categoryList
          for (Category row : categoryList) {

            //split the php title into separate words
            String[] title_in_php = row.getName().split(" ");

            //for every split word
            for (String split_title : title_in_php) {

              //with every character typed in the searchbox see if it exists
              //at the start of each word in the row.
              //For example, "red onions"
              //with our 'split' function above
              //if the search term starts with "r" or "o", the row will be added
              //to the list.
              if (split_title.toLowerCase().startsWith(charString.toLowerCase())) {

                //...if the row has already been added to
                //filteredList don't add it again
                if (!filteredList.contains(row)) {
                  filteredList.add(row);

                }

              }

              //5/8/2018
/*              else if

                //this is for if the title starts with the search term.
                //so if the search term starts with "r", "red onions"
                //will be added to the list UNLESS it has already been
                //added (in the if statement above)
                  (row.getName().startsWith(charString.toLowerCase())) {

                //add the row to the list unless it has been added already
                if (!filteredList.contains(row)) {
                  filteredList.add(row);

                }
              }*/
              
            }

          }

          categoryListFiltered = filteredList;
        }

        FilterResults filterResults = new FilterResults();

        filterResults.values = categoryListFiltered;
        return filterResults;
      }

      @Override
      protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        categoryListFiltered = (ArrayList<Category>) filterResults.values;

        //System.out.println("categoryListFiltered :" + categoryListFiltered.size());

        //refresh the list with filtered data
        notifyDataSetChanged();
      }
    };
  }


  public interface CategoriesAdapterListener {
    void onCategorySelected(Category category);
  }


}


