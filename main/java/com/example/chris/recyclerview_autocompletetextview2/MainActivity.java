package com.example.chris.recyclerview_autocompletetextview2;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
/*
import android.support.v7.widget.DividerItemDecoration;
*/
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ContactsAdapter.ContactsAdapterListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<Contact> contactList;

    private List<Review> reviewList = new ArrayList<Review>();

    private PopulistoListAdapter pAdapter;

    private ContactsAdapter mAdapter;
    private SearchView searchView;

    // url to fetch contacts json
    // private static final String URL = "https://api.androidhive.info/json/contacts.json";

    private static final String AllReviews_URL = "http://www.populisto.com/AllReviews.php";
    private static final String SearchCategories_URL = "http://www.populisto.com/AllCategories.php";
  //private static final String SearchCategories_URL = "https://api.androidhive.info/json/contacts.json";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_title);

        //Show the back button (???)
     /*   ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);*/

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        contactList = new ArrayList<>();

        //for populisto reviews of the user
        pAdapter = new PopulistoListAdapter(reviewList, this);

        //for filterable
        mAdapter = new ContactsAdapter(this, contactList, this);

       // final PopulistoListAdapter adapter = new PopulistoListAdapter(reviewList, this);


        // white background notification bar
        whiteNotificationBar(recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
      //  recyclerView.setItemAnimator(new DefaultItemAnimator());
      //  recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));

          recyclerView.setAdapter(pAdapter);
      //  recyclerView.setAdapter(mAdapter);

       // fetchContacts();

        JsonArrayRequest request = new JsonArrayRequest(AllReviews_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), "Couldn't fetch the results! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        //create a new instance of the Contact class
                       // Contact contact = new Contact();

                        //In Contact class, so getItemViewType will know which layout to show
                       // contact.setType_row("1");

                        List<Review> items = new Gson().fromJson(response.toString(), new TypeToken<List<Review>>() {
                        }.getType());

                        // adding contacts to contacts list
                        //contactList.clear();
                        reviewList.addAll(items);

                        // refreshing recycler view
                        pAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "...4.php", Toast.LENGTH_LONG).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        MyApplication.getInstance().addToRequestQueue(request);

    }

    /**
     * fetches json by making http calls
     */
    private void fetchContacts() {

          recyclerView.setAdapter(mAdapter);

        JsonArrayRequest request = new JsonArrayRequest(SearchCategories_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), "Couldn't fetch the results! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        //create a new instance of the Contact class
                        //Contact contact = new Contact();

                        //In Contact class, so getItemViewType will know which layout to show
                        //contact.setType_row("2");

                        List<Contact> items = new Gson().fromJson(response.toString(), new TypeToken<List<Contact>>() {
                        }.getType());

                        // adding contacts to contacts list
                        reviewList.clear();
                        contactList.clear();
                        contactList.addAll(items);

                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "...fetch.php", Toast.LENGTH_LONG).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        MyApplication.getInstance().addToRequestQueue(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                //call fetchContacts function when first letter has been
                //entered into the searchView
                fetchContacts();
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    public void onContactSelected(Contact contact) {
        Toast.makeText(getApplicationContext(), "Selected: " + contact.getName(), Toast.LENGTH_LONG).show();

    //   Toast.makeText(getApplicationContext(), "Selected: " + contact.getName() + ", " + contact.getPhone(), Toast.LENGTH_LONG).show();
    }
}