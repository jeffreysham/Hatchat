package com.flutter.hatchat.activities;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.flutter.hatchat.R;
import com.flutter.hatchat.database.DatabaseHandler;
import com.flutter.hatchat.model.Contact;
import com.parse.ParseAnalytics;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class WriteNewMessageFragment extends DialogFragment {

    private EditText editText;
    private List<Contact> contactList;
    private ImageButton sendButton;
    private ImageButton callButton;
    private DatabaseHandler databaseHandler;

    public static WriteNewMessageFragment newInstance() {
        return new WriteNewMessageFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.write_message_screen, container, false);

        databaseHandler = new DatabaseHandler(rootView.getContext());

        contactList = databaseHandler.getAllContacts();

        sendButton = (ImageButton) rootView.findViewById(R.id.sendButton);
        callButton = (ImageButton) rootView.findViewById(R.id.callButton);
        editText = (EditText) rootView.findViewById(R.id.editText);

        getDialog().setTitle("New Chat");

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

        return rootView;
    }

    public void callUser() {
        if (contactList.size() > 0) {
            Random ran = new Random();
            int randomNum = ran.nextInt(contactList.size());
            Contact theContact = contactList.get(randomNum);

            try {
                //TODO: test calling
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + theContact.getPhoneNumber()));
                startActivity(intent);

                ParseAnalytics.trackEventInBackground("callSent");

            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Call failed, please try again later!",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getDialog().getContext());
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Please add more people to your contacts.")
                    .setCancelable(false)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            getDialog().cancel();
                        }
                    });
            AlertDialog dialog = alertDialog.create();
            dialog.show();
        }



    }

    //Sends the message to the server and to the contact. Also sends a push notification
    //if the user has the application
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
                    Toast.makeText(getActivity().getApplicationContext(), "SMS Sent to " + theContact.getName() + "!",
                            Toast.LENGTH_SHORT).show();

                    ParseAnalytics.trackEventInBackground("messageSent");
                } catch (Exception e) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "SMS failed, please try again later!",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getDialog().getContext());
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
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getDialog().getContext());
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Please add more people to your contacts.")
                    .setCancelable(false)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            getDialog().cancel();
                        }
                    });
            AlertDialog dialog = alertDialog.create();
            dialog.show();

        }
    }
}
