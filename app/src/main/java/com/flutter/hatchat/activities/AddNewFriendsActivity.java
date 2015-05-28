package com.flutter.hatchat.activities;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.flutter.hatchat.R;
import com.flutter.hatchat.database.ContactsDataService;
import com.flutter.hatchat.model.Contact;
import com.flutter.hatchat.model.ContactListViewAdapter;
import com.flutter.hatchat.model.ContactRowItem;

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
    protected void onCreate(Bundle savedInstanceState) {
        //TODO FINISH THIS CLASS
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_friends);
        contactListView = (ListView) findViewById(R.id.contactListView);
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
        //updateView(v, selected);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
