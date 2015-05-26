package com.flutter.hatchat.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Jeffrey Sham on 5/22/2015.
 */

@ParseClassName("User")
public class User extends ParseUser{
    public String getPhoneNumber() {
        return getString("phoneNumber");
    }

    public void setPhoneNumber(String phoneNumber) {
        put("phoneNumber", phoneNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof User) {
            User tempUser = (User) o;
            return getPhoneNumber().equals(tempUser.getPhoneNumber());
        } else if (o instanceof ParseUser) {
            ParseUser tempUser = (ParseUser) o;
            return getPhoneNumber().equals(tempUser.get("phoneNumber"));
        }
        return false;
    }
}
