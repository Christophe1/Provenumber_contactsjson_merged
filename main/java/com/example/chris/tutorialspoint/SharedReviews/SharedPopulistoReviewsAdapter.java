package com.example.chris.tutorialspoint.SharedReviews;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chris.tutorialspoint.SelectPhoneContact;
import com.example.chris.tutorialspoint.UPopulistoListAdapter;
import com.example.chris.tutorialspoint.ViewContact;
import com.example.tutorialspoint.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.example.chris.tutorialspoint.PopulistoContactsAdapter.theContactsList;

public class SharedPopulistoReviewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private LayoutInflater inflater;
    public static List<SharedReview> the_Shared_reviews;
    //Context context;
    Context context_type;
    //myMethod();



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

    public SharedPopulistoReviewsAdapter(List<SharedReview> sharedReviews, Context context) {

        the_Shared_reviews = sharedReviews;
        context_type = context;



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


        SharedReview r = the_Shared_reviews.get(position);

        //String username = r.getUsername();

        //pubOrPriv is 0,1 or 2,depending on shared status of the review
        int pubOrPriv = Integer.parseInt(r.getPublicorprivate());

        //shared_status will be Just U, Private or Public
        String shared_status ="U";

        String bobby = r.setPhoneNumberofUserFromDB(obj.getString("username"));

        //get shared preference values from VerifyUserPhoneNumber,
        //we are getting jsonArrayAllPhonesandNamesofContacts as a string,
        //convert it back to a JSONArray
        SharedPreferences sharedPrefs = context_type.getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String json_array = sharedPrefs.getString("AllPhonesandNamesofContacts", "0");
        try {
            JSONArray jsonArray = new JSONArray(json_array);
            //System.out.println("SharedAdapter, the jsonarray is :" + jsonArray);
            Toast.makeText(context_type, jsonArray.toString(), Toast.LENGTH_SHORT).show();


        } catch (JSONException e) {
            Log.e("MYAPP", "unexpected JSON exception", e);
        }

//}



        if(pubOrPriv==0){
            //change colour depending on value
            ((ReviewHolder) viewHolder).phone_user_name.setTextColor(Color.parseColor("#DA850B"));
        }

        if(pubOrPriv==1){
            ((ReviewHolder) viewHolder).phone_user_name.setTextColor(Color.parseColor("#0A7FDA"));
        }

        if(pubOrPriv==2){
            ((ReviewHolder) viewHolder).phone_user_name.setTextColor(Color.parseColor("#2AB40E"));
        }



        ((ReviewHolder) viewHolder).phone_user_name.setText("user " + r.getPhoneNameonPhone());
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
                // Toast.makeText(v.getContext(),position + " cheers!", Toast.LENGTH_SHORT).show();

                //  Toast.makeText(v.getContext(), language, Toast.LENGTH_SHORT).show();


                SharedReview sharedReview = (SharedReview) SharedPopulistoReviewsAdapter.getItem(position);



                //we want to pass the review_id of the sharedReview being clicked
                //to the ViewContact activity, and from there post it and get more
                //info for that sharedReview - address, comments etc
                Intent i = new Intent(v.getContext(), ViewContact.class);
                //pass the review_id to ViewContact class
                i.putExtra("review_id", sharedReview.getReviewid());
                v.getContext().startActivity(i);
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






