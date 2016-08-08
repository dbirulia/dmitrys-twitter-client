package com.birulia.apps.dmitrytwitterclient.models;


import android.annotation.TargetApi;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

public class Tweet {

    public Long getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    private Long id;
    private String body;
    private String createdAt;
    private User user;



    public Tweet(JSONObject jsonTweet){
        try {
            this.id = jsonTweet.getLong("id");
            this.body = jsonTweet.getString("text");
            this.createdAt = getRelativeTimeAgo(jsonTweet.getString("created_at"));
            this.user = new User(jsonTweet.getJSONObject("user"));


        }
        catch (JSONException ex){
            ex.printStackTrace();
        }
    }

    public static ArrayList<Tweet> fromJSONArray  (JSONArray jsonTweets) {
        ArrayList<Tweet> newsArticles = new ArrayList<>();

        for (int i = 0; i < jsonTweets.length(); i++) {
            try{
                newsArticles.add(new Tweet(jsonTweets.getJSONObject(i)));
            }
            catch (JSONException ex){
                ex.printStackTrace();
            }
        }
        return newsArticles;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

}
