package com.flutter.hatchat.activities;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.flutter.hatchat.R;
import com.flutter.hatchat.database.ContactsDataService;

public class HomeScreenActivity extends ActionBarActivity {

    private Context context = this;

    private ContactsDataService contactsDataService;

    /**
     * Maintains the service for the whole app
     */
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        //Go to WriteNewMessageActivity
        Button writeNewMessageButton = (Button) findViewById(R.id.writeNewMessageButton);
        writeNewMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = WriteNewMessageFragment.newInstance();
                newFragment.show(getFragmentManager(), "dialog");
            }
        });

        //Go to FriendsActivity
        Button friendsButton = (Button) findViewById(R.id.viewFriendsButton);
        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FriendsActivity.class);
                startActivity(intent);
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
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Stop", "In Home: onDestroy()");
        unbindService(contactsServiceConnection);
    }
}
