package com.flutter.hatchat.activities;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flutter.hatchat.R;
import com.flutter.hatchat.database.ContactsDataService;
import com.flutter.hatchat.model.Contact;
import com.flutter.hatchat.model.ContactListViewAdapter;
import com.flutter.hatchat.model.ContactRowItem;
import com.flutter.hatchat.model.User;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddFriendsActivity extends ActionBarActivity {

    private List<ContactRowItem> contactRowItemList;
    private List<Contact> contactList;
    private ContactListViewAdapter listViewAdapter;
    private ListView contactListView;

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
        contactList = new ArrayList<>();
        contactListView = (ListView) findViewById(R.id.contactListView);
        EditText inputSearch = (EditText) findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                AddFriendsActivity.this.listViewAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

    public void displayContacts() {
        listViewAdapter = new ContactListViewAdapter(this, R.layout.contacts_list_item,contactRowItemList);
        contactListView.setAdapter(listViewAdapter);
        contactListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(contactListView, view, position, id);
            }
        });
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


    private void onListItemClick(ListView l, View v, int position, long id) {

        ContactRowItem tempRowItem = (ContactRowItem)l.getItemAtPosition(position);

        boolean selected = !tempRowItem.getSelected();

        tempRowItem.setSelected(selected);
        //listViewAdapter.notifyDataSetChanged();
        updateView(v, selected);

        Contact tempContact = new Contact();
        tempContact.setPhoneNumber(tempRowItem.getPhoneNumber());
        tempContact.setName(tempRowItem.getName());
        tempContact.setIsMessaging(false);
        if (selected) {
            contactList.add(tempContact);
        } else {
            contactList.remove(tempContact);
        }

        TextView view = (TextView) findViewById(R.id.numberOfContactsTextView);
        view.setText(String.valueOf(contactListView.getCheckedItemCount()));
    }

    private void updateView(View view, boolean selected) {
        ImageView imageView = (ImageView) view.findViewById(R.id.itemClickedImageView);
        if (selected) {
            //Selected Picture
            imageView.setImageResource(R.mipmap.ic_launcher);
        } else {
            //Did not select picture
            imageView.setImageResource(R.color.abc_background_cache_hint_selector_material_light);
        }
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.done) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Add Contacts");
            alertDialog.setMessage("Are you sure you want to add these contacts?")
                    .setCancelable(false)
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveContacts();
                        }

                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog dialog = alertDialog.create();
            dialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveContacts(){
        if (contactListView.getCheckedItemCount() >= 15) {
            ParseUser currentUser = ParseUser.getCurrentUser();

            for (int i = 0; i < contactList.size(); i++) {
                Contact contact = contactList.get(i);
                ParseRelation<Contact> relation = currentUser.getRelation("contacts");
                relation.add(contact);
                currentUser.saveInBackground();
            }

            //currentUser.saveInBackground();
            Toast.makeText(this, "Saved Contacts", Toast.LENGTH_SHORT).show();

            contactsDataService.storeContactRowItems(contactRowItemList);
            goToHomeScreen();
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Contacts");
            alertDialog.setMessage("Please add at least 15 contacts.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }

                    });
            AlertDialog dialog = alertDialog.create();
            dialog.show();

        }

    }

    public void goToHomeScreen(){
        Intent intent = new Intent(this, HomeScreenActivity.class);
        startActivity(intent);
    }
}
