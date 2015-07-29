package com.flutter.hatchat.activities;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import com.flutter.hatchat.R;
import com.flutter.hatchat.database.ContactsDataService;
import com.flutter.hatchat.database.DatabaseHandler;
import com.flutter.hatchat.model.Contact;
import com.flutter.hatchat.model.ContactRowItem;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.parse.ParseAnalytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SplashScreenActivity extends ActionBarActivity {

    private ContactsDataService contactsDataService;
    private List<ContactRowItem> contactRowItemList;
    private Context context = this;

    ServiceConnection contactsServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ContactsDataService.ContactBinder binder = (ContactsDataService.ContactBinder) service;
            contactsDataService = binder.getService();

            //Get Contact Data and put in the Service
            FindContactsInBackground f = new FindContactsInBackground();
            f.execute();
            /*if (!isNetworkAvailable()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("No internet access")
                        .setMessage("Please turn on internet for better experience.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alert.create().show();
                FindContactsInBackground f = new FindContactsInBackground();
                f.execute();
            } else {
                //Get Contact Data and put in the Service
                FindContactsInBackground f = new FindContactsInBackground();
                f.execute();
            }*/
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
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    public void getDataFromDatabase() {
        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        int contactCount = 0;
        try {
            contactCount = databaseHandler.getAllContacts().size();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (contactCount != 0) {
            List<Contact> contactList = databaseHandler.getAllContacts();
            for (int i = 0; i < contactRowItemList.size(); i++) {
                Contact tempContact = new Contact();
                ContactRowItem item = contactRowItemList.get(i);
                tempContact.setPhoneNumber(item.getPhoneNumber());

                int index = contactList.indexOf(tempContact);

                if (index > -1) {
                    item.setSelected(true);
                }
            }

            contactsDataService.storeContactRowItems(contactRowItemList);
            finishSplashActivity(true);
        } else {
            contactsDataService.storeContactRowItems(contactRowItemList);
            finishSplashActivity(false);
        }


    }

    //Decides where to go after the splash activity
    public void finishSplashActivity(boolean goToHomeScreen) {

        if (goToHomeScreen) {
            Intent intent = new Intent(this, HomeScreenActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, AddFriendsActivity.class);
            startActivity(intent);
        }

    }

    /*private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        Intent i = new Intent(this,ContactsDataService.class);
        bindService(i, contactsServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Stop", "In Splash: onStop()");
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
            //findUsers();
            getDataFromDatabase();
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
                        String number = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        
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
}
