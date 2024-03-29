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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flutter.hatchat.R;
import com.flutter.hatchat.database.ContactsDataService;
import com.flutter.hatchat.database.DatabaseHandler;
import com.flutter.hatchat.model.Contact;
import com.flutter.hatchat.model.ContactListViewAdapter;
import com.flutter.hatchat.model.ContactRowItem;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddFriendsActivity extends ActionBarActivity {

    private List<ContactRowItem> contactRowItemList;
    private List<Contact> contactList;
    private ContactListViewAdapter listViewAdapter;
    private ListView contactListView;
    private DatabaseHandler database;
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
            database = new DatabaseHandler(context);
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

        //Search through the list
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

        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Add Contacts");
                alertDialog.setMessage("Are you sure you want to add these contacts?")
                        .setCancelable(false)
                        .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveContacts();
                            }

                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = alertDialog.create();
                dialog.show();
            }
        });

        ParseObject object = new ParseObject("user");
        object.saveEventually();
    }

    /**
     * Start the service connection
     */
    @Override
    protected void onStart() {
        super.onStart();
        Intent i = new Intent(this,ContactsDataService.class);
        bindService(i, contactsServiceConnection, BIND_AUTO_CREATE);
    }

    /**
     * Stop the service connection
     */
    @Override
    protected void onStop() {
        super.onStop();
        AddFriendsActivity.this.listViewAdapter.getFilter().filter("");
        unbindService(contactsServiceConnection);
        finish();
    }

    /**
     * Show the contacts in the list
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

        updateView(v, selected);

        Contact tempContact = new Contact(tempRowItem.getPhoneNumber(), tempRowItem.getName());
        tempContact.setPhoto(tempRowItem.getPhoto());
        if (selected) {
            contactList.add(tempContact);
        } else {
            contactList.remove(tempContact);
        }

        TextView view = (TextView) findViewById(R.id.numberOfContactsTextView);
        view.setText(String.valueOf(contactListView.getCheckedItemCount()));
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

    /**
     * Saves the contacts. Needs at least 15 contacts initially
     */
    public void saveContacts(){
        if (contactListView.getCheckedItemCount() >= 15) {
            Collections.sort(contactList);
            for (int i = 0; i < contactList.size(); i++) {
                Contact contact = contactList.get(i);
                database.addContact(contact);
            }

            database.close();

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
