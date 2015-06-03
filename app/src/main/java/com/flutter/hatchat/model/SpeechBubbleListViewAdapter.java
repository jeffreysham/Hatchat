package com.flutter.hatchat.model;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flutter.hatchat.R;

import java.util.List;

/**
 * Created by Jeffrey Sham on 5/30/2015.
 */
public class SpeechBubbleListViewAdapter extends ArrayAdapter<Message> {
    private Context context;
    private List<Message> messageList;
    private String phoneNumber;

    public SpeechBubbleListViewAdapter(Context context, int resource, List<Message> items, String phoneNumber) {
        super(context, resource, items);
        this.context = context;
        this.messageList = items;
        this.phoneNumber = phoneNumber;
    }

    private class MessageViewHolder {
        TextView messageText;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        MessageViewHolder holder;
        Message rowItem = messageList.get(position);
        LayoutInflater rowViewInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            Log.i("run", "convertView==null");
            convertView = rowViewInflater.inflate(R.layout.speech_bubble_list_item,null);
            holder = new MessageViewHolder();
            holder.messageText = (TextView) convertView.findViewById(R.id.message_text);
            convertView.setTag(holder);
        } else {
            Log.i("run","convertView!=null");
            holder = (MessageViewHolder) convertView.getTag();
        }

        if (rowItem != null) {
            holder.messageText.setText(rowItem.getMessage());
        }

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.messageText.getLayoutParams();
        if (rowItem.getSender().equals(phoneNumber)) {
            holder.messageText.setBackgroundResource(R.drawable.speech_bubble_green);
            layoutParams.gravity = Gravity.RIGHT;
        } else {
            holder.messageText.setBackgroundResource(R.drawable.speech_bubble_orange);
            layoutParams.gravity = Gravity.LEFT;
        }

        holder.messageText.setLayoutParams(layoutParams);

        return convertView;
    }

}

