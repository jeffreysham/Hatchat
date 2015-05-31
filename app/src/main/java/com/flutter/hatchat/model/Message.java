package com.flutter.hatchat.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jeffrey Sham on 5/22/2015.
 */
@ParseClassName("Message")
public class Message extends ParseObject implements Comparable{
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

    public void setDate(Date date) {
        put("date", date);
    }

    public Date getDate() {
        return getDate("date");
    }

    @Override
    public int compareTo(Object another) {
        if (another instanceof Message) {
            Message other = (Message) another;
            return getDate().compareTo(other.getDate());
        }
        return 0;
    }
}
