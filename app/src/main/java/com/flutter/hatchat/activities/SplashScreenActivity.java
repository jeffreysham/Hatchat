package com.flutter.hatchat.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.flutter.hatchat.R;
import com.flutter.hatchat.database.ContactsDataService;
import com.flutter.hatchat.database.ParseQueries;
import com.flutter.hatchat.model.Contact;
import com.flutter.hatchat.model.ContactRowItem;
import com.flutter.hatchat.model.Message;
import com.flutter.hatchat.model.User;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SplashScreenActivity extends ActionBarActivity {

    private ContactsDataService contactsDataService;
    private List<ContactRowItem> contactRowItemList;

    ServiceConnection contactsServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ContactsDataService.ContactBinder binder = (ContactsDataService.ContactBinder) service;
            contactsDataService = binder.getService();

            //Get Contact Data and put in the Service
            FindContactsInBackground f = new FindContactsInBackground();
            f.execute();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            contactsDataService = null;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
    }

    //Gets the messages sent by the user
    public void getUserSenderMessages(final String phoneNumber) {
        ParseQuery<Message> query = ParseQueries.createUserSenderMessagesQuery(phoneNumber);
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> list, ParseException e) {
                if (list.size() > 0) {
                    contactsDataService.storeUserSenderMessages(list);
                }
                getUserRecipientMessages(phoneNumber);

            }
        });
    }

    //Gets the messages that are sent to the user
    public void getUserRecipientMessages(String phoneNumber) {
        ParseQuery<Message> query = ParseQueries.createUserRecipientMessagesQuery(phoneNumber);
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> list, ParseException e) {
                if (list.size() > 0) {
                    contactsDataService.storeUserRecipientMessages(list);
                }
                finishSplashActivity();
            }
        });
    }

    //Gets the contacts that the user selected to randomly message
    public void getDataFromServer() {

        final ParseUser currentUser = ParseUser.getCurrentUser();
        Log.i("Tag", "In getDataFromServer");

        if (currentUser != null) {

            ParseQuery relationQuery = ParseQueries.createContactsQuery(currentUser);

            relationQuery.findInBackground(new FindCallback<Contact>() {
                @Override
                public void done(List<Contact> list, ParseException e) {
                    Log.i("Tag", "In getDataFromServer:done()");
                    if (list != null && list.size() > 0) {

                        //Sets up the contact and contact row item information
                        for (int i = 0; i < contactRowItemList.size(); i++) {
                            Contact tempContact = new Contact();
                            ContactRowItem item = contactRowItemList.get(i);
                            tempContact.setPhoneNumber(item.getPhoneNumber());

                            int index = list.indexOf(tempContact);

                            if (index > -1) {
                                item.setSelected(true);
                                Contact theContact = list.get(index);
                                theContact.setHasApp(item.getHasApp());
                                theContact.saveInBackground();
                            }
                        }
                        Collections.sort(list);
                        contactsDataService.storeContacts(list);
                    }

                    contactsDataService.storeContactRowItems(contactRowItemList);
                    getUserSenderMessages(currentUser.getString("phoneNumber"));
                }
            });


        } else {
            contactsDataService.storeContactRowItems(contactRowItemList);
            finishSplashActivity();
        }

    }

    //Decides where to go after the splash activity
    public void finishSplashActivity() {
        //Go to Login Activity or go to main screen
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, HomeScreenActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent i = new Intent(this,ContactsDataService.class);
        bindService(i, contactsServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(contactsServiceConnection);
        finish();
    }

    private class FindContactsInBackground extends AsyncTask <Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            findUsers();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... params) {
            findAndStoreContacts();
            Collections.sort(contactRowItemList);

            return null;
        }
    }

    //Finds the contacts on the phone and stores them as contact row items
    public void findAndStoreContacts() {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        contactRowItemList = new ArrayList<ContactRowItem>();
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    //Query phone here.  Covered next
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    if (pCur.moveToNext()) {
                        // Do something with phones
                        String number = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));
                        
                        String realPhoneNumber = "";

                        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                        try {
                            Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(number, "US");
                            realPhoneNumber = "" + phoneNumber.getNationalNumber();
                        } catch (NumberParseException e) {
                            e.printStackTrace();
                        }

                        if (realPhoneNumber.length() > 0) {
                            ContactRowItem tempRowItem = new ContactRowItem(name, realPhoneNumber);
                            contactRowItemList.add(tempRowItem);
                        }
                    }
                    pCur.close();
                }


            }
        }
        cur.close();
    }

    public void findUsers() {
        ParseQuery query = ParseQueries.createUsersQuery();
        Log.i("Tag", "In findUsers");
        query.findInBackground(new FindCallback() {
            @Override
            public void done(List list, ParseException e) {

            }

            @Override
            public void done(Object o, Throwable throwable) {

                List list = (List) o;
                if (o != null && list.size() > 0) {
                    for (int i = 0; i < contactRowItemList.size(); i++) {
                        User tempUser = new User();
                        tempUser.setPhoneNumber(contactRowItemList.get(i).getPhoneNumber());
                        if (list.contains(tempUser)) {
                            contactRowItemList.get(i).setHasApp(true);
                        }
                    }
                }

                //Get Data from server
                getDataFromServer();

            }
        });
    }
}
