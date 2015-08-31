package com.flutter.hatchat.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.flutter.hatchat.R;
import com.flutter.hatchat.database.DatabaseHandler;
import com.flutter.hatchat.model.Contact;
import com.parse.ParseAnalytics;

import java.util.List;
import java.util.Random;

public class WriteNewMessageActivity extends ActionBarActivity {

    private EditText editText;
    private List<Contact> contactList;
    private ImageButton sendButton;
    private ImageButton callButton;
    private DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_message_screen);
        databaseHandler = new DatabaseHandler(this);

        contactList = databaseHandler.getAllContacts();

        databaseHandler.close();

        sendButton = (ImageButton) findViewById(R.id.sendButton);
        callButton = (ImageButton) findViewById(R.id.callButton);
        editText = (EditText)findViewById(R.id.editText);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callUser();
            }
        });

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //Calls a random user
    public void callUser() {
        if (contactList.size() > 0) {
            Random ran = new Random();
            int randomNum = ran.nextInt(contactList.size());
            Contact theContact = contactList.get(randomNum);

            try {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + theContact.getPhoneNumber()));
                startActivity(intent);

                ParseAnalytics.trackEventInBackground("callSent");
                removeContact(theContact);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        "Call failed, please try again later!",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Please add more people to your contacts.")
                    .setCancelable(false)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog dialog = alertDialog.create();
            dialog.show();
        }



    }

    private void removeContact(Contact theContact) {
        databaseHandler = new DatabaseHandler(this);
        databaseHandler.deleteContact(theContact);
        contactList.remove(theContact);
        databaseHandler.close();
    }

    //Sends the message to the contact
    public void sendMessage() {

        if (contactList.size() > 0) {
            Random ran = new Random();
            int randomNum = ran.nextInt(contactList.size());
            Contact theContact = contactList.get(randomNum);

            final String theMessageToSend = editText.getText().toString().trim();

            if (theMessageToSend.length() > 0) {
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(theContact.getPhoneNumber(), null, "Hatchat: " + theMessageToSend, null, null);
                    Toast.makeText(getApplicationContext(), "SMS Sent to " + theContact.getName() + "!",
                            Toast.LENGTH_SHORT).show();

                    ParseAnalytics.trackEventInBackground("messageSent");
                    removeContact(theContact);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again later!",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Please write a message before sending.")
                        .setCancelable(false)
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = alertDialog.create();
                dialog.show();
            }


        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Please add more people to your contacts.")
                    .setCancelable(false)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog dialog = alertDialog.create();
            dialog.show();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_message, menu);
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
