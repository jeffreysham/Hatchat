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
public class ContactListViewAdapter extends ArrayAdapter<ContactRowItem>{
    private Context context;

    public ContactListViewAdapter(Context context, int resource, List<ContactRowItem> items) {
        super(context, resource, items);
        this.context = context;
    }

    private class ContactViewHolder {
        TextView contactNameText;
        TextView contactNumberText;
        ImageView itemClickedImageView;
        ImageView ownsAppImageView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ContactViewHolder holder;
        ContactRowItem rowItem = getItem(position);
        LayoutInflater rowViewInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            Log.i("run", "convertView==null");
            convertView = rowViewInflater.inflate(R.layout.contacts_list_item,null);
            holder = new ContactViewHolder();
            holder.contactNameText = (TextView) convertView.findViewById(R.id.contactNameTextView);
            holder.contactNumberText = (TextView)convertView.findViewById(R.id.contactNumberTextView);
            holder.itemClickedImageView = (ImageView) convertView.findViewById(R.id.itemClickedImageView);
            holder.ownsAppImageView = (ImageView) convertView.findViewById(R.id.contactHasAppImageView);
            convertView.setTag(holder);
        } else {
            Log.i("run","convertView!=null");
            holder = (ContactViewHolder) convertView.getTag();
        }

        holder.contactNameText.setText(rowItem.getName());
        holder.contactNumberText.setText(rowItem.getPhoneNumber());

        if (rowItem.getSelected()) {
            //Selected Picture
            holder.itemClickedImageView.setImageResource(R.mipmap.ic_launcher);
        } else {
            //Did not select picture
            holder.itemClickedImageView.setImageResource(R.color.abc_background_cache_hint_selector_material_light);
        }

        if (rowItem.getHasApp()) {
            //Owns app
            holder.ownsAppImageView.setImageResource(R.mipmap.ic_launcher);
        } else {
            //Does not own app
            holder.ownsAppImageView.setImageResource(R.color.abc_background_cache_hint_selector_material_light);
        }

        return convertView;
    }
}
