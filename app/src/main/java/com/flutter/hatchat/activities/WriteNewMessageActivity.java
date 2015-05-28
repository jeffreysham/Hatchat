package com.flutter.hatchat.activities;

import android.content.ComponentName;
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
import com.flutter.hatchat.model.Contact;
import com.flutter.hatchat.model.Message;
import com.parse.ParseUser;

import java.util.List;
import java.util.Random;

public class WriteNewMessageActivity extends ActionBarActivity {

    private ContactsDataService contactsDataService;
    private EditText editText;
    private List<Contact> contactList;
    private Button sendButton;

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

    public void sendMessage() {
        Random ran = new Random();
        int randomNum = ran.nextInt(contactList.size());
        Contact theContact = contactList.get(randomNum);
        while (!theContact.getIsMessaging()) {
            randomNum = ran.nextInt(contactList.size());
            theContact = contactList.get(randomNum);
        }

        //Make message object regardless of if the contact has the app or not
        theContact.setIsMessaging(true);

        theContact.saveInBackground();

        Message theMessage = new Message();
        theMessage.setDate();
        theMessage.setMessage(editText.getText().toString().trim());
        theMessage.setSender(ParseUser.getCurrentUser().getUsername());
        theMessage.setRecipient(theContact.getPhoneNumber());
        theMessage.saveInBackground();

        if (!theContact.getHasApp()) {
            //If the contact does not have the app
            //send SMS
            try {
                //TODO test if this sends to Play Store
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(theContact.getPhoneNumber(), null, "Someone messaged you on Hatchat! Download on the Play Store: market://details?id=com.flutter.hatchat", null, null);
                Toast.makeText(getApplicationContext(), "SMS Sent!",
                        Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        "SMS failed, please try again later!",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_write_new_message, menu);
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
