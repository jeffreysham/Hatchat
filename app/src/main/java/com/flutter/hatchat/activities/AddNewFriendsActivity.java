package com.flutter.hatchat.activities;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
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
import com.flutter.hatchat.database.ParseQueries;
import com.flutter.hatchat.model.Contact;
import com.flutter.hatchat.model.ContactListViewAdapter;
import com.flutter.hatchat.model.ContactRowItem;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Collections;
import java.util.List;

public class AddNewFriendsActivity extends ActionBarActivity {


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
            contactList = contactsDataService.getContactList();

            displayContacts();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            contactsDataService = null;
        }

    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent i = new Intent(this,ContactsDataService.class);
        bindService(i, contactsServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeAllContacts();
        unbindService(contactsServiceConnection);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_list);
        contactListView = (ListView) findViewById(R.id.friendsListView);
        EditText inputSearch = (EditText) findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                AddNewFriendsActivity.this.listViewAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
        tempContact.setHasApp(tempRowItem.getHasApp());

        tempContact.saveInBackground();

        if (selected) {
            contactList.add(tempContact);
        } else {
            contactList.remove(tempContact);
        }

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

    public void saveContacts(){
        ParseUser currentUser = ParseUser.getCurrentUser();

        for (int i = 0; i < contactList.size(); i++) {
            Contact contact = contactList.get(i);

            ParseRelation<Contact> relation = currentUser.getRelation("contacts");
            relation.add(contact);
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.i("Save", "Saved contact");
                    } else {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    public void removeAllContacts(){
        final ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery relationQuery = ParseQueries.createContactsQuery(currentUser);

        relationQuery.findInBackground(new FindCallback<Contact>() {
            @Override
            public void done(List<Contact> list, ParseException e) {
                Log.i("Tag", "In getDataFromServer:done()");
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        Contact contact = list.get(i);
                        currentUser.getRelation("contacts").remove(contact);
                        currentUser.saveInBackground();
                    }
                }
                saveContacts();
            }
        });
    }



}
