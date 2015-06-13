package com.flutter.hatchat.activities;

import android.content.ComponentName;
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
import android.widget.ListView;

import com.flutter.hatchat.R;
import com.flutter.hatchat.database.ContactsDataService;
import com.flutter.hatchat.database.ParseQueries;
import com.flutter.hatchat.model.ChatListViewAdapter;
import com.flutter.hatchat.model.Contact;
import com.flutter.hatchat.model.Message;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
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
    private List<Contact> contactList;
    private List<Message> displayedMessageList;

    private ContactsDataService contactsDataService;

    /**
     * Get/use data service
     */
    ServiceConnection contactsServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ContactsDataService.ContactBinder binder = (ContactsDataService.ContactBinder) service;
            contactsDataService = binder.getService();
            userSenderMessageList = contactsDataService.getUserSenderMessageList();
            Log.i("user messages", userSenderMessageList.toString());
            userRecipientMessageList = contactsDataService.getUserRecipientMessageList();
            Log.i("recipient messages", userRecipientMessageList.toString());
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
        //TODO Test Messaging with more than 1 phone
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_list);

        chatListView = (ListView) findViewById(R.id.chat_list);

        //Search through list
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

    /**
     * Gets the messages that the user has sent
     */
    public void getUserSenderMessages(final String phoneNumber) {
        ParseQuery<Message> query = ParseQueries.createUserSenderMessagesQuery(phoneNumber);
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> list, ParseException e) {
                if (list.size() > 0) {
                    contactsDataService.storeUserSenderMessages(list);
                }
                getUserRecipientMessages(phoneNumber);

            }
        });
    }

    /**
     * Gets the messages that other people have sent to the user
     */
    public void getUserRecipientMessages(String phoneNumber) {
        ParseQuery<Message> query = ParseQueries.createUserRecipientMessagesQuery(phoneNumber);
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> list, ParseException e) {
                if (list.size() > 0) {
                    contactsDataService.storeUserRecipientMessages(list);
                }
            }
        });
    }

    /**
     * Updates the list
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (contactsDataService != null) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            getUserSenderMessages(currentUser.getString("phoneNumber"));
            listViewAdapter.notifyDataSetChanged();
        }

    }

    /**
     * Gets the information to display each list item
     */
    public void createDisplayedMessageList() {
        displayedMessageList = new ArrayList<>();

        for (int j = 0; j < contactList.size(); j++) {
            //Get the contacts to display in the right order
            Contact contact = contactList.get(j);
            Date date = null;
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

            if (date != null && content.length() > 0) {
                Message theMessage = new Message();
                theMessage.setSender(contact.getPhoneNumber());
                theMessage.setDate(date);
                theMessage.setMessage(content);
                displayedMessageList.add(theMessage);
            }

        }

        if (displayedMessageList.size() > 1) {
            Collections.sort(displayedMessageList);
        }

        Log.i("messages", displayedMessageList.toString());

    }

    /**
     * Displays the list information
     */
    public void displayMessages() {
        listViewAdapter = new ChatListViewAdapter(this, R.layout.chat_list_item, displayedMessageList, contactList);
        chatListView.setAdapter(listViewAdapter);
        chatListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(chatListView, view, position, id);
            }
        });
    }

    /**
     * Handles the clicking of the list item. Goes to new activity to enable chatting
     */
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

        List<Message> userSpecificMessageList = new ArrayList<>();
        userSpecificMessageList.addAll(userRecipientSpecificContactList);
        userSpecificMessageList.addAll(userSenderSpecificContactList);

        if (userSpecificMessageList.size() > 1) {
            Collections.sort(userSpecificMessageList);
        }

        Log.i("user specific messages", userSpecificMessageList.toString());
        contactsDataService.storeUserSpecificMessages(userSpecificMessageList);
        Intent intent = new Intent(this, SpeechBubbleActivity.class);
        startActivity(intent);
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
        if (id == R.id.refresh) {
            onResume();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
