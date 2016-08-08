package com.birulia.apps.dmitrytwitterclient.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.birulia.apps.dmitrytwitterclient.models.Tweet;
import com.bumptech.glide.Glide;
import com.codepath.apps.dmitrytwitterclient.R;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


public class TweetsArrayAdapter extends RecyclerView.Adapter<TweetsArrayAdapter.ViewHolder> {

    // Store a member variable for the contacts
    private List<Tweet> tweets;
    // Store the context for easy access
    private Context tweetContext;

    // Pass in the contact array into the constructor
    public TweetsArrayAdapter(Context context, List<Tweet> _tweets) {
        tweets = _tweets;
        tweetContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return tweetContext;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView ivProfileImage;
        public TextView tvBody;
        public TextView tvCreatedAt;
        public TextView tvUsername;
        public String imgURL;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvCreatedAt = (TextView) itemView.findViewById(R.id.tvCreatedAt);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            imgURL = "";
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // Show Fragment with all hte details
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View tweetView = inflater.inflate(R.layout.tweet_item, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Tweet tweet = tweets.get(position);

        // Set item views based on your views and data model
        TextView tvBody = viewHolder.tvBody;
        TextView tvCreatedAt = viewHolder.tvCreatedAt;
        TextView tvUsername = viewHolder.tvUsername;

        tvBody.setText(tweet.getBody());
        tvCreatedAt.setText(tweet.getCreatedAt());
        tvUsername.setText(tweet.getUser().getScreenName());

        ImageView ivProfileImage = viewHolder.ivProfileImage;
        String imageUrl = tweet.getUser().getProfileImageURL();
        Glide.with(getContext())
                .load(imageUrl)
                .bitmapTransform(new RoundedCornersTransformation(getContext(), 5, 2))
                .error(R.drawable.empty_profile)
                .placeholder(R.drawable.empty_profile)
                .into(ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }


}
