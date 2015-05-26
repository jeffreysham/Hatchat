package com.flutter.hatchat.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

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
}
