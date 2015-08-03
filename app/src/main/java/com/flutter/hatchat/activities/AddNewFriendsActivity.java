package com.flutter.hatchat.activities;

import android.content.ComponentName;
import android.content.Context;
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

import com.flutter.hatchat.R;
import com.flutter.hatchat.database.ContactsDataService;
import com.flutter.hatchat.database.DatabaseHandler;
import com.flutter.hatchat.model.Contact;
import com.flutter.hatchat.model.ContactListViewAdapter;
import com.flutter.hatchat.model.ContactRowItem;
import com.parse.ParseAnalytics;

import java.util.List;

public class AddNewFriendsActivity extends ActionBarActivity {

    private List<ContactRowItem> contactRowItemList;
    private ContactListViewAdapter listViewAdapter;
    private ListView contactListView;
    private DatabaseHandler databaseHandler;
    private ContactsDataService contactsDataService;
    private Context context = this;

    /**
     * Get/use the data service.
     */
    ServiceConnection contactsServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ContactsDataService.ContactBinder binder = (ContactsDataService.ContactBinder) service;
            contactsDataService = binder.getService();
            contactRowItemList = contactsDataService.getContactRowItemList();
            databaseHandler = new DatabaseHandler(context);
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
        AddNewFriendsActivity.this.listViewAdapter.getFilter().filter("");
        Log.i("Stop", "In AddNewFriends: onStop()");
        databaseHandler.close();
        unbindService(contactsServiceConnection);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_new_list);
        contactListView = (ListView) findViewById(R.id.friendsListView);

        //Search through list
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

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Displays the contacts in the list
     */
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

    /**
     * Select contacts. Add them to the contact list and update the list.
     */
    private void onListItemClick(ListView l, View v, int position, long id) {
        ContactRowItem tempRowItem = (ContactRowItem)l.getItemAtPosition(position);

        boolean selected = !tempRowItem.getSelected();

        tempRowItem.setSelected(selected);
        //listViewAdapter.notifyDataSetChanged();
        updateView(v, selected);

        Contact tempContact = new Contact(tempRowItem.getPhoneNumber(), tempRowItem.getName());
        tempContact.setPhoto(tempRowItem.getPhoto());
        if (selected) {
            databaseHandler.addContact(tempContact);
        } else {
            databaseHandler.deleteContact(tempContact);
        }

        ParseAnalytics.trackEventInBackground("friendListAltered");

    }

    /**
     * This updates the list to show that the contact was selected/unselected
     */
    private void updateView(View view, boolean selected) {
        ImageView imageView = (ImageView) view.findViewById(R.id.itemClickedImageView);
        if (selected) {
            //Selected Picture
            imageView.setImageResource(R.drawable.hatchat_icon);
        } else {
            //Did not select picture
            imageView.setImageResource(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();

        return super.onOptionsItemSelected(item);
    }

}
