package com.flutter.hatchat.model;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.flutter.hatchat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeffrey Sham on 5/25/2015.
 */
public class ContactListViewAdapter extends ArrayAdapter<ContactRowItem>{
    private Context context;
    private ContactsListFilter filter;
    private List<ContactRowItem> originalList;
    private List<ContactRowItem> filteredList;

    public ContactListViewAdapter(Context context, int resource, List<ContactRowItem> items) {
        super(context, resource, items);
        this.context = context;
        this.filteredList = items;
        this.originalList = new ArrayList<>();
        this.originalList.addAll(items);
    }

    private class ContactViewHolder {
        TextView contactNameText;
        TextView contactNumberText;
        ImageView itemClickedImageView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ContactViewHolder holder;
        ContactRowItem rowItem = filteredList.get(position);
        LayoutInflater rowViewInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            Log.i("run", "convertView==null");
            convertView = rowViewInflater.inflate(R.layout.contacts_list_item,null);
            holder = new ContactViewHolder();
            holder.contactNameText = (TextView) convertView.findViewById(R.id.contactNameTextView);
            holder.contactNumberText = (TextView)convertView.findViewById(R.id.contactNumberTextView);
            holder.itemClickedImageView = (ImageView) convertView.findViewById(R.id.itemClickedImageView);

            convertView.setTag(holder);
        } else {
            Log.i("run","convertView!=null");
            holder = (ContactViewHolder) convertView.getTag();
        }

        if (rowItem != null) {
            holder.contactNameText.setText(rowItem.getName());
            holder.contactNumberText.setText(rowItem.getPhoneNumber());

            if (rowItem.getSelected()) {
                //Selected Picture
                holder.itemClickedImageView.setImageResource(R.drawable.hatchat_icon);
            } else {
                //Did not select picture
                holder.itemClickedImageView.setImageResource(0);
            }
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ContactsListFilter();
        }
        return filter;
    }

    private class ContactsListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            String prefix = constraint.toString().toLowerCase().trim();

            if (prefix == null || prefix.length() == 0) {
                results.values = originalList;
                results.count = originalList.size();
            } else {
                ArrayList<ContactRowItem> newList = new ArrayList<>();

                for (int i = 0; i < originalList.size(); i++) {
                    ContactRowItem contactRowItem = originalList.get(i);
                    String value = contactRowItem.getName().toLowerCase();
                    String phoneNumber = contactRowItem.getPhoneNumber();
                    if (value.contains(prefix) || phoneNumber.contains(prefix)) {
                        newList.add(contactRowItem);
                    }
                }

                results.values = newList;
                results.count = newList.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (ArrayList<ContactRowItem>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0; i < filteredList.size(); i++) {
                ContactRowItem contactRowItem = (ContactRowItem) filteredList.get(i);
                add(contactRowItem);
            }
            notifyDataSetInvalidated();
        }
    }
}
