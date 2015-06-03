package com.flutter.hatchat.activities;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.flutter.hatchat.R;
import com.flutter.hatchat.database.ContactsDataService;
import com.flutter.hatchat.database.ParseQueries;
import com.flutter.hatchat.model.ChatListViewAdapter;
import com.flutter.hatchat.model.Contact;
import com.flutter.hatchat.model.Message;
import com.flutter.hatchat.model.SpeechBubbleListViewAdapter;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SpeechBubbleActivity extends ActionBarActivity {

    private List<Message> messageList;
    private SpeechBubbleListViewAdapter listViewAdapter;
    private ListView speechBubbleListView;
    private ContactsDataService contactsDataService;
    private EditText editText;
    private Button sendButton;
    private String otherContact;
    private String otherContactName;
    private Context context = this;

    //Get/use the data service
    ServiceConnection contactsServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ContactsDataService.ContactBinder binder = (ContactsDataService.ContactBinder) service;
            contactsDataService = binder.getService();
            messageList = contactsDataService.getUserSpecficMessageList();

            ParseUser currentUser = ParseUser.getCurrentUser();
            Message theMessage = messageList.get(0);

            if (theMessage.getSender().equals(currentUser.getString("phoneNumber"))) {
                otherContact = theMessage.getRecipient();
            } else {
                otherContact = theMessage.getSender();
            }

            //Get the name of the contact
            Contact tempContact = new Contact();
            tempContact.setPhoneNumber(otherContact);

            List<Contact> contactList = contactsDataService.getContactList();
            int index = contactList.indexOf(tempContact);

            if (index >= 0) {
                otherContactName = contactList.get(index).getName();
            } else {
                otherContactName = otherContact;
            }

            displayMessages();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            contactsDataService = null;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_bubble);
        speechBubbleListView = (ListView) findViewById(R.id.speech_list);
        editText = (EditText)findViewById(R.id.speech_edit_text);
        sendButton = (Button) findViewById(R.id.speech_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(v);
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
    }

    /**
     * Display the messages
     */
    public void displayMessages() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        this.setTitle(otherContactName);
        listViewAdapter = new SpeechBubbleListViewAdapter(this, R.layout.speech_bubble_list_item, messageList, currentUser.getString("phoneNumber"));
        speechBubbleListView.setAdapter(listViewAdapter);
    }

    /**
     * Sends the message to the other user and the server
     */
    public void sendMessage(View v) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        String newMessage = editText.getText().toString().trim();
        if (newMessage.length() > 0) {
            editText.setText("");
            Message theMessage = new Message();
            theMessage.setSender(currentUser.getString("phoneNumber"));
            theMessage.setRecipient(otherContact);
            theMessage.setDate();
            theMessage.setMessage(newMessage);
            theMessage.saveInBackground();
            addNewMessage(theMessage);

            sendMessageToServer(newMessage, theMessage);
        }
    }

    /**
     * Creates the push notification to the other user
     */
    public void sendMessageToServer(final String theMessageToSend, final Message message) {
        ParseQuery<ParseUser> tempUserQuery = ParseQueries.createUsersQuery();
        tempUserQuery.whereEqualTo("phoneNumber", otherContact);
        tempUserQuery.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser != null) {
                    ParsePush push = new ParsePush();
                    push.setQuery(ParseQueries.createPushQuery(parseUser.getObjectId()));
                    push.setMessage(theMessageToSend);
                    push.sendInBackground();
                } else {
                    //SHOULD NOT GO HERE
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Error")
                            .setMessage("There was a problem sending your message. Try again later.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    builder.create().show();
                }
            }
        });
    }

    /**
     * Adds the new message to the list
     */
    public void addNewMessage(Message message) {
        messageList.add(message);
        listViewAdapter.notifyDataSetChanged();
    }
}
