package com.flutter.hatchat.activities;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.flutter.hatchat.R;
import com.flutter.hatchat.database.DatabaseHandler;
import com.flutter.hatchat.model.Contact;

import java.util.List;
import java.util.Random;

/**
 * Implementation of App Widget functionality.
 */
public class WriteNewMessageWidget2 extends AppWidgetProvider {

    private RemoteViews views;
    private List<Contact> contactList;
    private DatabaseHandler databaseHandler;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            Intent messageIntent = new Intent(context, WriteNewMessageActivity.class);
            Intent callIntent = new Intent(Intent.ACTION_CALL);

            databaseHandler = new DatabaseHandler(context);
            contactList = databaseHandler.getAllContacts();
            databaseHandler.close();

            if (contactList.size() > 0) {
                Random ran = new Random();
                int randomNum = ran.nextInt(contactList.size());
                Contact theContact = contactList.get(randomNum);
                callIntent.setData(Uri.parse("tel:" + theContact.getPhoneNumber()));

            }

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
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

