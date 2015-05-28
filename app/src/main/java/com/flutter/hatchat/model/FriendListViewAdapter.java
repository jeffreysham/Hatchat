package com.flutter.hatchat.model;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.flutter.hatchat.R;

import java.util.List;

/**
 * Created by Jeffrey Sham on 5/25/2015.
 */
public class FriendListViewAdapter extends ArrayAdapter<Contact>{
    private Context context;

    public FriendListViewAdapter(Context context, int resource, List<Contact> items) {
        super(context, resource, items);
        this.context = context;
    }

    private class ContactViewHolder {
        TextView friendNameText;
        ImageView removeFriendImageView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ContactViewHolder holder;
        Contact rowItem = getItem(position);
        LayoutInflater rowViewInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            Log.i("run", "convertView==null");
            convertView = rowViewInflater.inflate(R.layout.friends_list_item,null);
            holder = new ContactViewHolder();
            holder.friendNameText = (TextView) convertView.findViewById(R.id.friendNameTextView);
            holder.removeFriendImageView = (ImageView) convertView.findViewById(R.id.removeFriendImageView);

            convertView.setTag(holder);
        } else {
            Log.i("run","convertView!=null");
            holder = (ContactViewHolder) convertView.getTag();
        }

        holder.friendNameText.setText(rowItem.getName());
        holder.removeFriendImageView.setImageResource(R.mipmap.ic_launcher);

        return convertView;
    }
}
