package com.flutter.hatchat.activities;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.flutter.hatchat.R;
import com.flutter.hatchat.database.DatabaseHandler;
import com.flutter.hatchat.model.Contact;
import com.parse.ParseAnalytics;

import java.util.List;
import java.util.Random;

/**
 * Implementation of App Widget functionality.
 */
public class WriteNewMessageWidget2 extends AppWidgetProvider {

    public static String CALL_ACTION = "Call_Action";

    private RemoteViews views;
    private List<Contact> contactList;
    private DatabaseHandler databaseHandler;

    //TODO: test widget
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            Intent messageIntent = new Intent(context, WriteNewMessageActivity.class);
            Intent callIntent = new Intent(context, WriteNewMessageWidget2.class);
            callIntent.setAction(CALL_ACTION);

            PendingIntent pendingMessageIntent = PendingIntent.getActivity(context,0,messageIntent,0);
            PendingIntent pendingCallIntent = PendingIntent.getActivity(context,0,callIntent,0);
            views = new RemoteViews(context.getPackageName(), R.layout.write_new_message_widget2);

            views.setOnClickPendingIntent(R.id.sendButton, pendingMessageIntent);
            views.setOnClickPendingIntent(R.id.callButton, pendingCallIntent);
            views.setOnClickPendingIntent(R.id.editText, pendingMessageIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        databaseHandler = new DatabaseHandler(context);

        contactList = databaseHandler.getAllContacts();

        databaseHandler.close();
        if (intent.getAction().equals(CALL_ACTION)) {
            callUser(context);
        }
    }

    //Calls a random user
    public void callUser(Context context) {
        if (contactList.size() > 0) {
            Random ran = new Random();
            int randomNum = ran.nextInt(contactList.size());
            Contact theContact = contactList.get(randomNum);

            try {
                //TODO: test calling
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + theContact.getPhoneNumber()));
                context.startActivity(intent);

                ParseAnalytics.trackEventInBackground("callSent");

            } catch (Exception e) {
                Toast.makeText(context,
                        "Call failed, please try again later!",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
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
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

