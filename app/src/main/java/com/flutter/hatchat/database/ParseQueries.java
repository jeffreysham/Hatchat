package com.flutter.hatchat.database;

import com.flutter.hatchat.model.Contact;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
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

    public static ParseQuery createUserSenderMessagesQuery(String userPhoneNumber) {
        ParseQuery query = ParseQuery.getQuery("Message");
        query.whereEqualTo("sender", userPhoneNumber);
        query.orderByAscending("createdAt");
        return query;
    }

    public static ParseQuery createUserRecipientMessagesQuery(String userPhoneNumber) {
        ParseQuery query = ParseQuery.getQuery("Message");
        query.whereEqualTo("recipient", userPhoneNumber);
        query.orderByAscending("createdAt");
        return query;
    }

    public static ParseQuery createUsersQuery() {
        return ParseUser.getQuery();
    }

    public static ParseQuery createPushQuery(String tempObjectId) {
        ParseQuery query = ParseInstallation.getQuery();
        query.whereEqualTo("userId", tempObjectId);
        return query;
    }

}
