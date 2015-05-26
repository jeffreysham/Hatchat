package com.flutter.hatchat.application;

import android.app.Application;

import com.digits.sdk.android.Digits;
import com.flutter.hatchat.model.Contact;
import com.flutter.hatchat.model.Message;
import com.flutter.hatchat.model.User;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Jeffrey Sham on 5/22/2015.
 */
public class HatChatApp extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "POIAFygJJJ70eoYpfznJmjIgs";
    private static final String TWITTER_SECRET = "uuZABikd3HkOVX3lSNc7TZDBiZZPSjjyznnsCJPWN4kR00aevh";

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits());
        Parse.enableLocalDatastore(this);

        ParseObject.registerSubclass(Contact.class);
        ParseUser.registerSubclass(User.class);
        ParseObject.registerSubclass(Message.class);

        Parse.initialize(this, "UWvPqv4YjfKsD8shypFuNMR9Ci5z2N05apvOpMXf", "sJU25a90mzO5WbqXfsOedeTU9LH4sjfFFdUE5mUz");
    }
}
