package com.flutter.hatchat.model;

import android.graphics.Bitmap;

/**
 * Created by Jeffrey Sham on 5/22/2015.
 */
public class Contact implements Comparable{
    private String phoneNumber;
    private String name;
    private Bitmap photo;

    public Contact() {
        this.name = "";
        this.phoneNumber = "";
        this.photo = null;
    }

    public Contact(String phoneNumber, String name) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.photo = null;
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

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
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
