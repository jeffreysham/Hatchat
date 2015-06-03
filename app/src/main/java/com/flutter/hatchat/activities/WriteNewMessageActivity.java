package com.flutter.hatchat.activities;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.flutter.hatchat.R;
import com.flutter.hatchat.database.ContactsDataService;
import com.flutter.hatchat.database.ParseQueries;
import com.flutter.hatchat.model.Contact;
import com.flutter.hatchat.model.Message;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WriteNewMessageActivity extends ActionBarActivity {

    private ContactsDataService contactsDataService;
    private EditText editText;
    private List<Contact> contactList;
    private Button sendButton;
    private Context context = this;

    //Get/use the data service
    ServiceConnection contactsServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ContactsDataService.ContactBinder binder = (ContactsDataService.ContactBinder) service;
            contactsDataService = binder.getService();
            contactList = contactsDataService.getContactList();
            setUpSendButton();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            contactsDataService = null;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_message_screen);
        editText = (EditText) findViewById(R.id.editText);
        sendButton = (Button) findViewById(R.id.sendButton);
    }

    //Sets up the on click listener
    public void setUpSendButton() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
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

    //Sends the message to the server and to the contact. Also sends a push notification
    //if the user has the application
    public void sendMessage() {
        List<Contact> contactListToSend = new ArrayList<>();
        for (int i = 0; i < contactList.size(); i++) {
            Contact theContact = contactList.get(i);
            if (!theContact.getIsMessaging()) {
                contactListToSend.add(theContact);
            }
        }

        if (contactListToSend.size() > 0) {
            Random ran = new Random();
            int randomNum = ran.nextInt(contactListToSend.size());
            Contact theContact = contactListToSend.get(randomNum);

            //Make message object regardless of if the contact has the app or not
            theContact.setIsMessaging(true);

            theContact.saveInBackground();

            final String theMessageToSend = editText.getText().toString().trim();

            final Message theMessage = new Message();
            theMessage.setDate();
            theMessage.setMessage(theMessageToSend);
            theMessage.setSender(ParseUser.getCurrentUser().getUsername());
            theMessage.setRecipient(theContact.getPhoneNumber());
            theMessage.saveInBackground();

            if (!theContact.getHasApp()) {
                //If the contact does not have the app
                //send SMS
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(theContact.getPhoneNumber(), null, "Someone messaged you on Hatchat! Download in the app store!", null, null);
                    Toast.makeText(getApplicationContext(), "SMS Sent!",
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again later!",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                //The contact has the app
                ParseQuery<ParseUser> tempUserQuery = ParseQueries.createUsersQuery();
                tempUserQuery.whereEqualTo("phoneNumber", theContact.getPhoneNumber());
                tempUserQuery.getFirstInBackground(new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (parseUser != null) {
                            JSONObject object = new JSONObject();
                            try {
                                object.put("status", "new message");
                                object.put("sender", theMessage.getSender());
                            } catch (JSONException exception) {
                                exception.printStackTrace();
                            }


                            ParsePush push = new ParsePush();
                            push.setQuery(ParseQueries.createPushQuery(parseUser.getObjectId()));
                            push.setMessage(theMessageToSend);
                            push.setData(object);
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
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Message")
                    .setMessage("You are already in a conversation with all of your selected friends!")
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
}
