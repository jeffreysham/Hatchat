package com.flutter.hatchat.model;

import com.parse.ParseObject;
import com.parse.ParseClassName;
/**
 * Created by Jeffrey Sham on 5/22/2015.
 */
public class Contact implements Comparable{
    private String phoneNumber;
    private String name;

    public Contact() {
        this.name = "";
        this.phoneNumber = "";
    }

    public Contact(String phoneNumber, String name) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Contact) {
            Contact tempContact = (Contact) o;
            return getPhoneNumber().equals(tempContact.getPhoneNumber());
        } else if (o instanceof ContactRowItem) {
            ContactRowItem rowItem = (ContactRowItem) o;
            return getPhoneNumber().equals(rowItem.getPhoneNumber());
        } else if (o instanceof String) {
            return getPhoneNumber().equals(o);
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
