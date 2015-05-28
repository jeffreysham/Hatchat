package com.flutter.hatchat.model;

/**
 * Created by Jeffrey Sham on 5/25/2015.
 */
public class ContactRowItem implements Comparable{
    private String phoneNumber;
    private String name;
    private boolean selected;
    private boolean hasApp;

    public ContactRowItem (String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.selected = false;
        this.hasApp = false;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean getSelected() {
        return selected;
    }

    public boolean getHasApp() {
        return hasApp;
    }

    public void setHasApp(boolean hasApp) {
        this.hasApp = hasApp;
    }

    @Override
    public int compareTo(Object another) {
        if (another instanceof ContactRowItem) {
            ContactRowItem other = (ContactRowItem) another;
            return this.name.compareTo(other.name);
        } else if (another instanceof Contact) {
            Contact other = (Contact) another;
            return this.phoneNumber.compareTo(other.getPhoneNumber());
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Contact) {
            Contact tempContact = (Contact) o;
            return getPhoneNumber().equals(tempContact.getPhoneNumber());
        } else if (o instanceof ContactRowItem) {
            ContactRowItem rowItem = (ContactRowItem) o;
            return getName().equals(rowItem.getName());
        }
        return false;

    }
}
