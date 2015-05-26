package com.flutter.hatchat.database;

import com.flutter.hatchat.model.Contact;
import com.parse.Parse;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

/**
 * Created by Jeffrey Sham on 5/23/2015.
 */
public class ParseQueries {

    public static ParseQuery createContactsQuery(ParseUser currentUser) {
        ParseRelation<Contact> relation = currentUser.getRelation("contacts");
        return relation.getQuery();
    }

    public static ParseQuery createMessagesQuery(String userPhoneNumber) {
        return null;
    }

    public static ParseQuery createUsersQuery() {
        return ParseUser.getQuery();
    }

}
