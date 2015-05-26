package com.flutter.hatchat.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Joanna Kai on 5/22/2015.
 */
@ParseClassName("Message")
public class Message extends ParseObject{
    public void setMessage(String value) {
        put("message", value);
    }

    public String getMessage() {
        return getString("message");
    }

    public void setSender(String phoneNumber) {
        put("sender", phoneNumber);
    }

    public String getSender() {
        return getString("sender");
    }

    public void setRecipient(String phoneNumber) {
        put("recipient", phoneNumber);
    }

    public String getRecipient() {
        return getString("recipient");
    }

    public void setDate() {
        Calendar calendar = Calendar.getInstance();
        put("date", calendar.getTime());
    }

    public Date getDate() {
        return getDate("date");
    }

}
