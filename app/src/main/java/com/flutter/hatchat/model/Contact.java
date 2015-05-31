package com.flutter.hatchat.model;

import com.parse.ParseObject;
import com.parse.ParseClassName;
/**
 * Created by Jeffrey Sham on 5/22/2015.
 */
@ParseClassName("Contact")
public class Contact extends ParseObject implements Comparable{

    public String getPhoneNumber() {
        return getString("phoneNumber");
    }

    public void setPhoneNumber(String phoneNumber) {
        put("phoneNumber", phoneNumber);
    }

    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }

    public boolean getIsMessaging() {
        return getBoolean("isMessaging");
    }

    public void setIsMessaging(boolean isMessaging) {
        put("isMessaging", isMessaging);
    }

    public boolean getHasApp() {
        return getBoolean("hasApp");
    }

    public void setHasApp(boolean hasApp) {
        put("hasApp", hasApp);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Contact) {
            Contact tempContact = (Contact) o;
            return getPhoneNumber().equals(tempContact.getPhoneNumber());
        } else if (o instanceof ContactRowItem) {
            ContactRowItem rowItem = (ContactRowItem) o;
            return getPhoneNumber().equals(rowItem.getPhoneNumber());
        }
        return false;

    }

    @Override
    public int compareTo(Object another) {
        if (another instanceof ContactRowItem) {
            ContactRowItem other = (ContactRowItem) another;
            return this.getPhoneNumber().compareTo(other.getPhoneNumber());
        } else if (another instanceof Contact) {
            Contact other = (Contact) another;
            return this.getName().compareTo(other.getName());
        }
        return 0;
    }
}
