package com.flutter.hatchat.model;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.flutter.hatchat.database.ParseQueries;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Jeffrey Sham on 6/2/2015.
 */
public class CustomParsePushBroadcastReceiver extends ParsePushBroadcastReceiver{

    private static final String TAG = "CustomPushReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent == null) {
                Log.d(TAG, "Receiver intent null");
            } else {
                JSONObject object = new JSONObject(intent.getExtras().getString("com.parse.Data"));

                if (object.has("status") && object.has("sender")) {
                    String status = object.getString("status");
                    final String sender = object.getString("sender");

                    if (status.equals("new message")) {
                        ParseUser currentUser = ParseUser.getCurrentUser();
                        if (currentUser != null) {
                            ParseQuery<Contact> contactQuery = ParseQueries.createContactsQuery(currentUser);
                            contactQuery.findInBackground(new FindCallback<Contact>() {
                                @Override
                                public void done(List<Contact> list, ParseException e) {
                                    if (list.size() > 0) {
                                        if (!list.contains(sender)) {
                                            ParseUser currentUser = ParseUser.getCurrentUser();
                                            ParseRelation<Contact> relation = currentUser.getRelation("contacts");
                                            Contact contact = new Contact();
                                            contact.setPhoneNumber(sender);
                                            contact.setName(sender);
                                            contact.setIsMessaging(true);

                                            relation.add(contact);
                                            currentUser.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        Log.i("Save", "Saved contact");
                                                    } else {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            });
                                        }
                                    }
                                }
                            });

                        }
                    }

                }

            }
        } catch (JSONException e) {
            Log.d(TAG, "JSONException: " + e.getMessage());
        }

    }
}
