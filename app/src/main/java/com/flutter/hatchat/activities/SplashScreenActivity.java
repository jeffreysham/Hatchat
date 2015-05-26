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
import com.flutter.hatchat.model.Contact;
import com.flutter.hatchat.model.ContactRowItem;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SplashScreenActivity extends ActionBarActivity {

    private ContactsDataService contactsDataService;
    private List<ContactRowItem> contactRowItemList;
    private List<Contact> contactList;

    ServiceConnection contactsServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("Tag", "In onServiceConnected");
            ContactsDataService.ContactBinder binder = (ContactsDataService.ContactBinder) service;
            contactsDataService = binder.getService();

            //Get Data from server

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

    public void getDataFromServer() {

    }

    public void finishSplashActivity() {
        //Go to Login Activity or go to main screen
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, AddFriendsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("Tag", "In onStart");
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
            Log.i("Tag", "In onPostExecute");
            Collections.sort(contactRowItemList);
            contactsDataService.storeContactRowItems(contactRowItemList);
            finishSplashActivity();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i("Tag", "In doInBackGround");
            findAndStoreContacts();
            return null;
        }
    }

    public void findAndStoreContacts() {
        Log.i("Tag", "In findAndStoreContacts");
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
    }
}
