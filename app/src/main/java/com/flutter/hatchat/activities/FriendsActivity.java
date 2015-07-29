package com.flutter.hatchat.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
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
import android.widget.ImageButton;
import android.widget.ListView;

import com.flutter.hatchat.R;
import com.flutter.hatchat.database.ContactsDataService;
import com.flutter.hatchat.database.DatabaseHandler;
import com.flutter.hatchat.model.Contact;
import com.flutter.hatchat.model.ContactRowItem;
import com.flutter.hatchat.model.FriendListViewAdapter;
import com.parse.ParseAnalytics;

import java.util.Collections;
import java.util.List;

public class FriendsActivity extends ActionBarActivity {

    private ContactsDataService contactsDataService;
    private List<Contact> friendsList;
    private List<ContactRowItem> contactRowItemList;
    private ListView friendsListView;
    private FriendListViewAdapter listViewAdapter;
    private Context context = this;
    private DatabaseHandler databaseHandler;
    private EditText inputSearch;

    /**
     * Get/use the data service
     */
    ServiceConnection contactsServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ContactsDataService.ContactBinder binder = (ContactsDataService.ContactBinder) service;
            contactsDataService = binder.getService();
            contactRowItemList = contactsDataService.getContactRowItemList();
            databaseHandler = new DatabaseHandler(context);
            friendsList = databaseHandler.getAllContacts();
            Collections.sort(friendsList);
            displayFriends();
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

    /**
     * Update the friends list
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("test", "in on Resume of Friends Activity");
        Log.i("test", "friendsList = " + friendsList);
        Log.i("test", "listViewAdapter = " + listViewAdapter);
        if (friendsList != null && listViewAdapter != null) {
            friendsList = databaseHandler.getAllContacts();
            if (friendsList.size() > 1) {
                Collections.sort(friendsList);
            }

            listViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FriendsActivity.this.listViewAdapter.getFilter().filter("");
        Log.i("Stop", "In Friends: onStop()");
        unbindService(contactsServiceConnection);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_list);
        friendsListView = (ListView) findViewById(R.id.friendsListView);

        //Search through list
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FriendsActivity.this.listViewAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddNewFriendsActivity.class);
                startActivity(intent);
            }
        });

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Display friends in list
     */
    public void displayFriends() {
        listViewAdapter = new FriendListViewAdapter(this, R.layout.friends_list_item,friendsList);
        friendsListView.setAdapter(listViewAdapter);
        friendsListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleItemClick(friendsListView, view, position, id);
            }
        });
    }

    /**
     * Removes friend at the clicked list item
     */
    private void handleItemClick(ListView l, View v, int position, long id) {
        final Contact theContact = (Contact) l.getItemAtPosition(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Remove Friend?")
                .setMessage("Are you sure you want to remove " + theContact.getName())
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeContact(theContact);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialogBuilder.setCancelable(false);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Removes the contact from the database
     */
    private void removeContact(Contact theContact) {
        databaseHandler.deleteContact(theContact);
        ContactRowItem tempItem = contactRowItemList.get(contactRowItemList.indexOf(theContact));
        tempItem.setSelected(false);
        friendsList.remove(theContact);

        List<Contact> tempList = databaseHandler.getAllContacts();
        Collections.sort(tempList);
        listViewAdapter.updateLists(friendsList, tempList);

        listViewAdapter.notifyDataSetChanged();
        ParseAnalytics.trackEventInBackground("friendRemoved");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        finish();


        return super.onOptionsItemSelected(item);
    }
}
