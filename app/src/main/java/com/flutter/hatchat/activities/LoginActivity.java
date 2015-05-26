package com.flutter.hatchat.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.digits.sdk.android.AuthCallback;

import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.flutter.hatchat.R;
import com.flutter.hatchat.model.User;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class LoginActivity extends ActionBarActivity {

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    Intent intent = new Intent(context, AddFriendsActivity.class);
                    startActivity(intent);
                }

            }

            @Override
            public void failure(DigitsException e) {
                Log.i("Failure", "Failure");
                //Show Alert
            }
        });


    }
}
