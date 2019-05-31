package com.populisto.chris.populisto.SharedReviews;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.populisto.chris.populisto.ViewContact;
import com.populisto.chris.populisto.R;

import java.util.List;

//DESCRIPTION OF ACTIVITY
//This is for showing a list in the recyclerView of reviews available to
//the logged-in user when he does a search, both his own and those shared with him
//When a cell is clicked we will show ViewContact or SharedViewContact
//whichever getItemViewType is set to, 1 or 2 or 3

public class SharedPopulistoReviewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  //private Activity activity;
  //private LayoutInflater inflater;
  public static List<SharedReview> the_Shared_reviews;
  //Context context;
 // Context context_type;

 //we will be passing as an intent which view to show in SharedViewContact based on this value
  Integer getItemViewType;

/*
    SharedPreferences sharedPrefs = context.getSharedPreferences("MyData", Context.MODE_PRIVATE);
    String language = sharedPrefs.getString("AllPhonesandNamesofContacts", "");
*/


/*
    SharedPreferences sharedPrefs;
    String text;
*/

/*    sharedPrefs = context.getSharedPreferences("MyData", MODE_PRIVATE);
    text = sharedPrefs.getString(PREFS_KEY, null);*/
/*    Context context;
    SharedPreferences pref = context.getSharedPreferences("MyData", MODE_PRIVATE);
    String json_array = pref.getString("AllPhonesandNamesofContacts", null);
    public void myMethod() {
        try
        {
            JSONArray jsonArray = new JSONArray(json_array);
        } catch (JSONException e) {
            Log.e("MYAPP", "unexpected JSON exception", e);
            // Do something to recover ... or kill the app.
        }
    }*/

  public static class ReviewHolder extends RecyclerView.ViewHolder {

    //In each populisto_list_row show the items you want to have appearing
    public TextView phone_user_name, date_created, category, name, address, phone, comment;
    public View separator;

    public ReviewHolder(View itemView) {
      super(itemView);

      phone_user_name = (TextView) itemView.findViewById(R.id.phone_user_name);
      date_created = (TextView) itemView.findViewById(R.id.date_created);
      category = (TextView) itemView.findViewById(R.id.category);
      name = (TextView) itemView.findViewById(R.id.name);
      address = (TextView) itemView.findViewById(R.id.address);
      //phone = (TextView) itemView.findViewById(R.id.phone);
      comment = (TextView) itemView.findViewById(R.id.comment);
      separator = (View) itemView.findViewById(R.id.separator);

      //fade to white...
      Shader myShader = new LinearGradient(
          //start gradient at point (0,40) down to (0,0)
          0, 40, 0, 0,
          Color.WHITE, Color.BLACK,
          Shader.TileMode.CLAMP);
      comment.getPaint().setShader(myShader);


    }

  }

  @Override
  public int getItemViewType(int position) {
    //for each row in recyclerview, get the getType_row
    //what layout is shown will depend on whether getType_row is "1" or "2"
    //this is to decide to show ViewContact or SharedViewContact,
    //for next activity
    return Integer.parseInt(the_Shared_reviews.get(position).getType_row());
  }


  public SharedPopulistoReviewsAdapter(List<SharedReview> sharedReviews) {

    the_Shared_reviews = sharedReviews;
   // context_type = context;


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

    final SharedReview r = the_Shared_reviews.get(position);

    //pubOrPriv is based on publicorprivate in getPublicorprivate
    // taken from the server, which is 0,1 or 2,depending on shared status of the review
    //We want pubOrPriv so we can decide the colour of "Just U" in ViewContact
    final int pubOrPriv = Integer.parseInt(r.getPublicorprivate());

    //shared_status will be Just U, Private or Public
    //String shared_status ="U";

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
    }

    //If setType_row = 2 in PopulistoListView,
    //if the review is by a phone contact, then make the
    // phoneNameOnPhone in blue text
    //(getItemViewType is above, in SharedPopulistoReviewsAdapter)
    if (viewHolder.getItemViewType() == 2) {
      ((ReviewHolder) viewHolder).phone_user_name.setTextColor(Color.parseColor("#0A7FDA"));

    }

    //If setType_row = 3 in PopulistoListView,
    //if the review is public by a user not in logged-in user's phone contacts,
    // then make the phoneNameOnPhone in green text
    //(getItemViewType is above, in SharedPopulistoReviewsAdapter)
    if (viewHolder.getItemViewType() == 3) {
      ((ReviewHolder) viewHolder).phone_user_name.setTextColor(Color.parseColor("#2AB40E"));

    }

    //set progressbar to invisible, cause we have a response
    //((ReviewHolder) viewHolder).container2.setVisibility(View.VISIBLE);
    //set the details in the recyclerView cell
    ((ReviewHolder) viewHolder).phone_user_name.setText(r.getPhoneNameonPhone());
    ((ReviewHolder) viewHolder).date_created.setText(r.getDate_created());
    ((ReviewHolder) viewHolder).category.setText("Categoryyy: " + r.getCategory());
    ((ReviewHolder) viewHolder).name.setText("Name: " + r.getName());
    ((ReviewHolder) viewHolder).address.setText("Address: " + r.getAddress());
//    ((ReviewHolder) viewHolder).phone.setText("Phone: " + r.getPhone());
    ((ReviewHolder) viewHolder).comment.setText("Comment: " + r.getComment());

    //set an onClick listener for the cell, if it's clicked anywhere
    ((ReviewHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {

      @Override
      //When the review is clicked in PopulistoListView
      //then show that review, either ViewContact or SharedContact
      public void onClick(View v) {

        Toast.makeText(v.getContext(), "SharedPopulistoReviewsAdapter", Toast.LENGTH_LONG).show();


        SharedReview sharedReview = (SharedReview) SharedPopulistoReviewsAdapter.getItem(position);

        //If getItemViewType = 1, which means getType_row = 1,
        // then the logged-in user owns the review
        //so load ViewContact -
        //with edit, delete button, list of contacts review is shared with etc...
        if (viewHolder.getItemViewType() == 1) {

          //Load the ViewContact activity
          Intent i = new Intent(v.getContext(), ViewContact.class);

          //pass the review_id to ViewContact class
          //the key is "review_id"
          i.putExtra("review_id", sharedReview.getReviewid());

          //pass the intent value of pubOrPriv to ViewContact
          i.putExtra("UPuborPrivVal", pubOrPriv);
          //pass these values as an intent to ViewContact class
          i.putExtra("date_created", sharedReview.getDate_created());
          i.putExtra("category", sharedReview.getCategory());
          i.putExtra("name", sharedReview.getName());
          i.putExtra("phone", sharedReview.getPhone());
          i.putExtra("address", sharedReview.getAddress());
          i.putExtra("comment", sharedReview.getComment());

            //Pass as intent to ViewContact, decide what colour to show "U"
            getItemViewType = 1;

          i.putExtra("getItemViewType", getItemViewType);

          //start the ViewContact activity
          v.getContext().startActivity(i);

          //If getType_row is 2 or 3, show SharedViewContact
        } else {

          //grab these details as an intent and pass
          //to the SharedViewContact activity
          Intent i = new Intent(v.getContext(), SharedViewContact.class);
          //pass the review_id to SharedViewContact class
          //the key is "review_id"
          i.putExtra("review_id", sharedReview.getReviewid());
          i.putExtra("date_created", sharedReview.getDate_created());
          i.putExtra("category", sharedReview.getCategory());
          i.putExtra("name", sharedReview.getName());
          i.putExtra("phone", sharedReview.getPhone());
          i.putExtra("address", sharedReview.getAddress());
          i.putExtra("comment", sharedReview.getComment());

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
  public static SharedReview getItem(int position) {
    return the_Shared_reviews.get(position);
  }


  @Override
  public int getItemCount() {

    //System.out.println("CustomPopulistoListAdapter: here it is" + the_Shared_reviews.size());
    return the_Shared_reviews.size();

  }

}