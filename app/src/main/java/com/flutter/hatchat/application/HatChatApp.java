package com.flutter.hatchat.application;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Jeffrey Sham on 5/22/2015.
 */
public class HatChatApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "UWvPqv4YjfKsD8shypFuNMR9Ci5z2N05apvOpMXf", "sJU25a90mzO5WbqXfsOedeTU9LH4sjfFFdUE5mUz");
    }
}
