package com.birulia.apps.dmitrytwitterclient;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.birulia.apps.dmitrytwitterclient.adapters.TweetsArrayAdapter;
import com.birulia.apps.dmitrytwitterclient.fragments.PostTweetDialogFragment;
import com.birulia.apps.dmitrytwitterclient.models.Tweet;
import com.birulia.apps.dmitrytwitterclient.models.User;
import com.birulia.apps.dmitrytwitterclient.utils.TwitterClient;
import com.codepath.apps.dmitrytwitterclient.R;
import com.codepath.apps.dmitrytwitterclient.databinding.ActivityTimeLineBinding;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimeLineActivity extends AppCompatActivity implements DialogInterface.OnDismissListener{


    private TwitterClient client;
    private TweetsArrayAdapter tweetsAdapter;
    private ArrayList<Tweet> tweets;
    private RecyclerView rvTweets;
    private ActivityTimeLineBinding binding;
    private long max_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_time_line);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_time_line);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = binding.actionBar;
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        client = TwitterApplication.getRestClient();

        tweets = new ArrayList<>();
        tweetsAdapter = new TweetsArrayAdapter(this, tweets);
        rvTweets = binding.rvTweets;
        // Attach the adapter to the recyclerview to populate items
        rvTweets.setAdapter(tweetsAdapter);
        // Set layout manager to position the items
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        // Attach the layout manager to the recycler view
        rvTweets.setHasFixedSize(true);
        rvTweets.setLayoutManager(layoutManager);

        // Add the scroll listener
        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
               populateTimeline();
            }
        });
        populateTimeline();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.twitter_client_header, menu);

        // Set up Search Action
        MenuItem postTweet = menu.findItem(R.id.compose_tweet);
        // Set up Post Tweet Action
        MenuItemCompat.getActionView(postTweet).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // launch fragment
                showPostTweetFragment();
            }
        });


        client.getMyProfile(new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    User myself = new User(response);
                                    Toolbar toolbar = binding.actionBar;
                                    TextView tvTitle = (TextView) toolbar.getChildAt(0);
                                    tvTitle.setText(myself.getScreenName());
                                    tvTitle.setTextColor(Color.parseColor("#ffffff"));
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    // log exception
                                    Log.w("TwitterApp", "failed to extract profile details");

                                }
                            }
        );

        return super.onCreateOptionsMenu(menu);
    }

    private void showPostTweetFragment(){
        FragmentManager fm = getSupportFragmentManager();
        PostTweetDialogFragment filterDialogFragment = PostTweetDialogFragment.newInstance("New tweet");
        filterDialogFragment.setCancelable(false);
        filterDialogFragment.show(fm, "fragment_post_tweet");
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        // dialog was dismissed apply filters
        tweets.clear();
        tweetsAdapter.notifyDataSetChanged();
        max_id = 0;
        populateTimeline();
    }

    public void populateTimeline() {

        client.getHomeTimeLine(max_id, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                tweets.addAll(Tweet.fromJSONArray(response));
                tweetsAdapter.notifyDataSetChanged();
                updateMaxId(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // log exception
                Log.w("TwitterApp", "failed to retrieve tweets");
            }
        });
    }

    private void updateMaxId(JSONArray jsonTweets){

        for (int i = 0; i < jsonTweets.length(); i++) {
            try{
                JSONObject t = jsonTweets.getJSONObject(i);
                if (t.getLong("id") < max_id){
                    max_id = t.getLong("id");
                }
            }
            catch (JSONException ex){
                ex.printStackTrace();
            }
        }
    }
}
