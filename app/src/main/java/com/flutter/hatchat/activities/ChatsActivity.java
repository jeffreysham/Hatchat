package com.flutter.hatchat.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.flutter.hatchat.R;
import com.flutter.hatchat.database.ContactsDataService;
import com.flutter.hatchat.model.ChatListViewAdapter;
import com.flutter.hatchat.model.Contact;
import com.flutter.hatchat.model.ContactListViewAdapter;
import com.flutter.hatchat.model.Message;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ChatsActivity extends ActionBarActivity {

    private ChatListViewAdapter listViewAdapter;
    private ListView chatListView;
    private List<Message> userSenderMessageList;
    private List<Message> userRecipientMessageList;
    private List<Message> displayedMessageList;
    private List<Contact> contactList;

    private ContactsDataService contactsDataService;

    ServiceConnection contactsServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ContactsDataService.ContactBinder binder = (ContactsDataService.ContactBinder) service;
            contactsDataService = binder.getService();
            userSenderMessageList = contactsDataService.getUserSenderMessageList();
            userRecipientMessageList = contactsDataService.getUserRecipientMessageList();
            contactList = contactsDataService.getContactList();
            createDisplayedMessageList();
            displayMessages();
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
        unbindService(contactsServiceConnection);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_list);
        chatListView = (ListView) findViewById(R.id.chat_list);
        EditText inputSearch = (EditText) findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ChatsActivity.this.listViewAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void createDisplayedMessageList() {
        List<Contact> currentMessageContacts = new ArrayList<>();
        for (int i = 0; i < contactList.size(); i++) {
            Contact tempContact = contactList.get(i);
            if (tempContact.getIsMessaging()) {
                currentMessageContacts.add(tempContact);
            }
        }

        for (int j = 0; j < currentMessageContacts.size(); j++) {
            //Get the contacts to display in the right order
            Contact contact = currentMessageContacts.get(j);
            Date date = new Date();
            String content = "";
            for (int i = 0; i < userRecipientMessageList.size(); i++) {
                //Get the Message time of the first message
                Message message = userRecipientMessageList.get(i);

                if (message.getSender().equals(contact.getPhoneNumber())) {
                    date = message.getDate();
                    break;
                }
            }

            for (int i = userRecipientMessageList.size() - 1; i >= 0; i--) {
                Message message = userRecipientMessageList.get(i);

                if (message.getSender().equals(contact.getPhoneNumber())) {
                    content = message.getMessage();
                    break;
                }
            }

            Message theMessage = new Message();
            theMessage.setSender(contact.getName());
            theMessage.setDate(date);
            theMessage.setMessage(content);
            displayedMessageList.add(theMessage);
        }

        Collections.sort(displayedMessageList);


    }

    public void displayMessages() {
        listViewAdapter = new ChatListViewAdapter(this, R.layout.chat_list_item, displayedMessageList);
        chatListView.setAdapter(listViewAdapter);
        chatListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(chatListView, view, position, id);
            }
        });
    }

    private void onListItemClick(ListView l, View v, int position, long id) {
        Message message = (Message) l.getItemAtPosition(position);

        List<Message> userSenderSpecificContactList = new ArrayList<>();
        List<Message> userRecipientSpecificContactList = new ArrayList<>();

        for (int i = 0; i < userRecipientMessageList.size(); i++) {
            Message tempMessage = userRecipientMessageList.get(i);
            if (tempMessage.getSender().equals(message.getSender())) {
                userRecipientSpecificContactList.add(tempMessage);
            }
        }

        for (int i = 0; i < userSenderMessageList.size(); i++) {
            Message tempMessage = userSenderMessageList.get(i);
            if (tempMessage.getRecipient().equals(message.getSender())) {
                userSenderSpecificContactList.add(tempMessage);
            }
        }
        //TODO Finish this..
        //Go to ViewChatsActivity
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chats, menu);
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
