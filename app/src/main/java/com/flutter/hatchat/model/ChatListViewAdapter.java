package com.flutter.hatchat.model;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.flutter.hatchat.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Jeffrey Sham on 5/30/2015.
 */
public class ChatListViewAdapter extends ArrayAdapter<Message> {
    private Context context;
    private ChatListFilter filter;
    private List<Message> originalList;
    private List<Message> filteredList;
    private List<Contact> contactList;

    public ChatListViewAdapter(Context context, int resource, List<Message> items, List<Contact> contacts) {
        super(context, resource, items);
        this.context = context;
        this.filteredList = items;
        this.originalList = new ArrayList<>();
        this.originalList.addAll(items);
        this.contactList = contacts;
    }

    private class MessageViewHolder {
        TextView timeText;
        TextView friendNameText;
        TextView recentMessageText;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final MessageViewHolder holder;
        Message rowItem = filteredList.get(position);
        LayoutInflater rowViewInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            Log.i("run", "convertView==null");
            convertView = rowViewInflater.inflate(R.layout.chat_list_item,null);
            holder = new MessageViewHolder();
            holder.timeText = (TextView) convertView.findViewById(R.id.timeTextView);
            holder.friendNameText = (TextView) convertView.findViewById(R.id.chatNameTextView);
            holder.recentMessageText = (TextView) convertView.findViewById(R.id.chatMessageTextView);
            convertView.setTag(holder);
        } else {
            Log.i("run","convertView!=null");
            holder = (MessageViewHolder) convertView.getTag();
        }

        if (rowItem != null) {
            //TODO do the 24 hour countdown thing...

            holder.timeText.setText(rowItem.getDate().getHours() + ":" + (rowItem.getDate().getMinutes()));

            Contact tempContact = new Contact();
            tempContact.setPhoneNumber(rowItem.getSender());

            int index = contactList.indexOf(tempContact);

            if (index >= 0) {
                holder.friendNameText.setText(contactList.get(index).getName());
            } else {
                holder.friendNameText.setText(rowItem.getSender());
            }

            holder.recentMessageText.setText(rowItem.getMessage());
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ChatListFilter();
        }
        return filter;
    }

    private class ChatListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            String prefix = constraint.toString().toLowerCase().trim();

            if (prefix == null || prefix.length() == 0) {
                results.values = originalList;
                results.count = originalList.size();
            } else {
                ArrayList<Message> newList = new ArrayList<>();

                for (int i = 0; i < originalList.size(); i++) {
                    Message message = originalList.get(i);
                    String name = message.getSender().toLowerCase();
                    String messageContent = message.getMessage().toLowerCase();

                    Contact tempContact = new Contact();
                    tempContact.setPhoneNumber(name);

                    int index = contactList.indexOf(tempContact);
                    tempContact = contactList.get(index);
                    String tempContactName = tempContact.getName().toLowerCase();

                    if (name.contains(prefix) || messageContent.contains(prefix) || tempContactName.contains(prefix)) {
                        newList.add(message);
                    }
                }

                results.values = newList;
                results.count = newList.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (ArrayList<Message>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0; i < filteredList.size(); i++) {
                Message message = (Message) filteredList.get(i);
                add(message);
            }
            notifyDataSetInvalidated();
        }
    }
}
