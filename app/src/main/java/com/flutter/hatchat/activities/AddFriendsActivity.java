package com.flutter.hatchat.activities;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flutter.hatchat.R;
import com.flutter.hatchat.database.ContactsDataService;
import com.flutter.hatchat.model.Contact;
import com.flutter.hatchat.model.ContactListViewAdapter;
import com.flutter.hatchat.model.ContactRowItem;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddFriendsActivity extends ListActivity {

    private List<ContactRowItem> contactRowItemList;
    private List<Contact> contactList;
    private ContactListViewAdapter listViewAdapter;

    private ContactsDataService contactsDataService;

    ServiceConnection contactsServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ContactsDataService.ContactBinder binder = (ContactsDataService.ContactBinder) service;
            contactsDataService = binder.getService();
            contactRowItemList = contactsDataService.getContactRowItemList();
            displayContacts();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            contactsDataService = null;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent i = new Intent(this,ContactsDataService.class);
        bindService(i, contactsServiceConnection, BIND_AUTO_CREATE);
    }

    /*@Override
    protected void onStop() {
        super.onStop();
        unbindService(contactsServiceConnection);
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(contactsServiceConnection);
    }

    public void displayContacts() {
        listViewAdapter = new ContactListViewAdapter(this, R.layout.contacts_list_item,contactRowItemList);
        setListAdapter(listViewAdapter);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    /*public void findContacts() {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        contactList = new ArrayList<Contact>();
        contactRowItemList = new ArrayList<ContactRowItem>();
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    //Query phone here.  Covered next
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", new String[]{id}, null);
                    if (pCur.moveToNext()) {
                        // Do something with phones
                        String number = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));

                        Log.i("Contact", "Name: " + name + " Number 1: " + number);

                        String realPhoneNumber = "";

                        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                        try {
                            Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(number, "US");
                            realPhoneNumber = "" + phoneNumber.getNationalNumber();
                        } catch (NumberParseException e) {
                            e.printStackTrace();
                        }

                        if (realPhoneNumber.length() > 0) {
                            Contact tempContact = new Contact();
                            tempContact.setPhoneNumber(realPhoneNumber);
                            contactList.add(tempContact);
                            ContactRowItem tempRowItem = new ContactRowItem(name, realPhoneNumber);
                            contactRowItemList.add(tempRowItem);
                        }
                    }
                    pCur.close();

                }


            }
            Collections.sort(contactRowItemList);
            displayContacts();
        }
    }*/

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ContactRowItem tempRowItem = (ContactRowItem)l.getItemAtPosition(position);
        tempRowItem.setSelected(!tempRowItem.getSelected());
        listViewAdapter.notifyDataSetChanged();

        TextView view = (TextView) findViewById(R.id.numberOfContactsTextView);
        view.setText(String.valueOf(getListView().getCheckedItemCount()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Toast.makeText(this,
                String.valueOf(getListView().getCheckedItemCount()),
                Toast.LENGTH_LONG).show();
        return true;
    }
}
