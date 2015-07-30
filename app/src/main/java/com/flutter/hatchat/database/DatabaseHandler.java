package com.flutter.hatchat.database;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.flutter.hatchat.model.Contact;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ContactsManagerNew";

    // Contacts table name
    private static final String TABLE_CONTACTS = "Contacts";

    // Contacts Table Columns names
    private static final String KEY_NAME = "name";
    private static final String KEY_PH_NO = "phone_number";
    private static final String KEY_PHOTO = "photo";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_NAME + " TEXT,"
                + KEY_PH_NO + " TEXT PRIMARY KEY,"
                + KEY_PHOTO + " BLOB" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    public void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName()); // Contact Name
        values.put(KEY_PH_NO, contact.getPhoneNumber()); // Contact Phone

        if (contact.getPhoto() != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            contact.getPhoto().compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapData = bos.toByteArray();

            values.put(KEY_PHOTO, bitmapData);
        } else {
            byte[] bitmapData = null;
            values.put(KEY_PHOTO, bitmapData);
        }


        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    public Contact getContact(String phonenumber) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[]{KEY_NAME, KEY_PH_NO, KEY_PHOTO},
                KEY_PH_NO + "=?",
                new String[]{phonenumber}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Contact contact = new Contact(cursor.getString(1), cursor.getString(0));

        byte[] data = cursor.getBlob(2);
        if (data != null) {
            contact.setPhoto(convertBlobToBitmap(data));
        } else {
            contact.setPhoto(null);
        }

        // return contact
        return contact;
    }

    private Bitmap convertBlobToBitmap(byte[] data) {
        InputStream inputStream = null;
        if (data != null) {
            inputStream = new ByteArrayInputStream(data);
        }

        if (inputStream != null) {

            Bitmap bitmap = null;
            try
            {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                bitmap = BitmapFactory.decodeStream(bufferedInputStream);
                inputStream.close();
                bufferedInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }
        return null;
    }

    // Getting All Contacts
    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setName(cursor.getString(0));
                contact.setPhoneNumber(cursor.getString(1));

                byte[] data = cursor.getBlob(2);
                if (data != null) {
                    contact.setPhoto(convertBlobToBitmap(data));
                } else {
                    contact.setPhoto(null);
                }

                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    // Updating single contact
    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());

        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_PH_NO + " = ?",
                new String[] { contact.getPhoneNumber() });
    }

    // Deleting single contact
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_PH_NO + " = ?",
                new String[] { contact.getPhoneNumber() });
        db.close();
    }

}
