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

    //Get Contacts of the current user
    public static ParseQuery createContactsQuery(ParseUser currentUser) {
        ParseRelation<Contact> relation = currentUser.getRelation("contacts");
        return relation.getQuery();
    }

    //Get the messages that the user sent
    public static ParseQuery createUserSenderMessagesQuery(String userPhoneNumber) {
        ParseQuery query = ParseQuery.getQuery("Message");
        query.whereEqualTo("sender", userPhoneNumber);
        query.orderByAscending("createdAt");
        return query;
    }

    //Get the messages that were sent to the user
    public static ParseQuery createUserRecipientMessagesQuery(String userPhoneNumber) {
        ParseQuery query = ParseQuery.getQuery("Message");
        query.whereEqualTo("recipient", userPhoneNumber);
        query.orderByAscending("createdAt");
        return query;
    }

    //Gets all the users
    public static ParseQuery createUsersQuery() {
        return ParseUser.getQuery();
    }

    //Creates a push query
    public static ParseQuery createPushQuery(String tempObjectId) {
        ParseQuery query = ParseInstallation.getQuery();
        query.whereEqualTo("userId", tempObjectId);
        return query;
    }

}
