package com.flutter.hatchat.model;

import com.parse.ParseObject;
import com.parse.ParseClassName;
/**
 * Created by Jeffrey Sham on 5/22/2015.
 */
@ParseClassName("Contact")
public class Contact extends ParseObject{

    public String getPhoneNumber() {
        return getString("phoneNumber");
    }

    public void setPhoneNumber(String phoneNumber) {
        put("phoneNumber", phoneNumber);
    }
}
