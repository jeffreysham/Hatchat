package com.flutter.hatchat.activities;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.EditText;

import com.digits.sdk.android.AuthCallback;

import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.flutter.hatchat.R;
import com.flutter.hatchat.database.ContactsDataService;
import com.flutter.hatchat.model.ContactRowItem;
import com.flutter.hatchat.model.User;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;


public class LoginActivity extends ActionBarActivity {

    private Context context = this;
    private ContactsDataService contactsDataService;

    //Maintains the service until it gets to AddFriendsActivity
    ServiceConnection contactsServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ContactsDataService.ContactBinder binder = (ContactsDataService.ContactBinder) service;
            contactsDataService = binder.getService();

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
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Uses Digits in order to do phone number authentication
        final DigitsAuthButton digitsAuthButton = (DigitsAuthButton) findViewById(R.id.auth_button);
        digitsAuthButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession digitsSession, String phoneNumber) {
                Log.i("Success", "Success");

                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser == null && phoneNumber != null) {
                    User tempUser = new User();

                    String realPhoneNumber = "";

                    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                    try {
                        Phonenumber.PhoneNumber number = phoneUtil.parse(phoneNumber, "US");
                        realPhoneNumber = "" + number.getNationalNumber();
                    } catch (NumberParseException e) {
                        e.printStackTrace();
                    }
                    if (realPhoneNumber.length() > 0) {
                        //Phone Number without country code
                        tempUser.setPhoneNumber(realPhoneNumber);
                        tempUser.setUsername(realPhoneNumber);
                        tempUser.setPassword(realPhoneNumber);
                        tempUser.setEmail(realPhoneNumber + "@example.com");
                    }
                    /**else {
                        //Phone Number with country code
                        //Should not ever go here
                        tempUser.setPhoneNumber(phoneNumber);
                        tempUser.setUsername(phoneNumber);
                        tempUser.setPassword(phoneNumber);
                        tempUser.setEmail(phoneNumber + "@example.com");
                    }**/

                    tempUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                //Success, go to app
                                Intent intent = new Intent(context, AddFriendsActivity.class);
                                startActivity(intent);

                            } else {
                                //Failure
                                //Show Alert

                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                                alertDialog.setTitle("Error");
                                alertDialog.setMessage("There was a problem signing up.")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                LoginActivity.this.finish();
                                            }

                                        });
                                AlertDialog dialog = alertDialog.create();
                                dialog.show();
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    //For some reason the user was logged out, need to log them back in
                    TelephonyManager tMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                    String mPhoneNumber = tMgr.getLine1Number();

                    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                    try {
                        Phonenumber.PhoneNumber number = phoneUtil.parse(mPhoneNumber, "US");
                        mPhoneNumber = "" + number.getNationalNumber();
                    } catch (NumberParseException e) {
                        e.printStackTrace();
                    }

                    if (mPhoneNumber.length() > 0) {
                        ParseUser.logInInBackground(mPhoneNumber, mPhoneNumber);
                        Intent intent = new Intent(context, HomeScreenActivity.class);
                        startActivity(intent);
                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

                        final EditText editText = new EditText(context);
                        editText.setHint("Your phone number");

                        dialog.setTitle("Login")
                                .setMessage("Please enter your phone number")
                                .setView(editText)
                                .setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String theEnteredNumber = editText.getText().toString();
                                        ParseUser.logInInBackground(theEnteredNumber, theEnteredNumber, new LogInCallback() {
                                            @Override
                                            public void done(ParseUser parseUser, ParseException e) {
                                                if (parseUser == null) {
                                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                                                    alertDialog.setTitle("Error");
                                                    alertDialog.setMessage("There was a problem logging in.")
                                                            .setCancelable(false)
                                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    LoginActivity.this.finish();
                                                                }

                                                            });
                                                    AlertDialog dialog = alertDialog.create();
                                                    dialog.show();
                                                } else {
                                                    Intent intent = new Intent(context, HomeScreenActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        });
                                    }
                                });
                    }

                }

            }

            @Override
            public void failure(DigitsException e) {
                Log.i("Failure", "Failure");
                //Show Alert

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Error");
                alertDialog.setMessage("There was a problem signing up.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LoginActivity.this.finish();
                            }

                        });
                AlertDialog dialog = alertDialog.create();
                dialog.show();
                e.printStackTrace();
            }
        });


    }
}
