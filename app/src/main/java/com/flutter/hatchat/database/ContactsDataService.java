package com.flutter.hatchat.database;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.flutter.hatchat.model.Contact;
import com.flutter.hatchat.model.ContactRowItem;
import com.flutter.hatchat.model.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeffrey Sham on 5/25/2015.
 */
public class ContactsDataService extends Service{
    private IBinder binder = new ContactBinder();

    List<ContactRowItem> contactRowItemList;
    List<Contact> contactList;
    List<Message> userSenderMessageList;
    List<Message> userRecipientMessageList;
    List<Message> userSpecficMessageList;

    @Override
    public void onCreate() {
        super.onCreate();
        contactRowItemList = new ArrayList<ContactRowItem>();
        contactList = new ArrayList<Contact>();
        userSenderMessageList = new ArrayList<>();
        userRecipientMessageList = new ArrayList<>();
        userSpecficMessageList = new ArrayList<>();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class ContactBinder extends Binder {
        public ContactsDataService getService() {
            return ContactsDataService.this;
        }
    }

    public void storeContactRowItems(List<ContactRowItem> contactRowItemList) {
        this.contactRowItemList = contactRowItemList;
    }

    public List<ContactRowItem> getContactRowItemList() {
        return contactRowItemList;
    }

    public void storeContacts(List<Contact> contacts) {
        this.contactList = contacts;
    }

    public List<Contact> getContactList(){
        return contactList;
    }

    public void storeUserSenderMessages(List<Message> messages) {
        this.userSenderMessageList = messages;
    }

    public List<Message> getUserSenderMessageList() {
        return userSenderMessageList;
    }

    public void storeUserRecipientMessages(List<Message> messages) {
        this.userRecipientMessageList = messages;
    }

    public List<Message> getUserRecipientMessageList() {
        return userRecipientMessageList;
    }

    public void storeUserSpecificMessages(List<Message> messages) {
        this.userSpecficMessageList = messages;
    }

    public List<Message> getUserSpecficMessageList() {
        return userSpecficMessageList;
    }
}
