package com.birulia.apps.dmitrytwitterclient.models;


import org.json.JSONException;
import org.json.JSONObject;

public class User {

    private String name;
    private Long id;
    private String screenName;

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public String getScreenName() {
        return screenName;
    }

    private String profileImageURL;

    public User(JSONObject jsonUser){
        try {
            this.id = jsonUser.getLong("id");
            this.name = jsonUser.getString("name");
            this.screenName = "@" + jsonUser.getString("screen_name");
            this.profileImageURL = jsonUser.getString("profile_image_url");
        }
        catch (JSONException ex){
            ex.printStackTrace();
        }
    }
}
