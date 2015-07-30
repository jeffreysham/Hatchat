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
public class FriendListViewAdapter extends ArrayAdapter<Contact>{
    private Context context;
    private FriendsListFilter filter;
    private List<Contact> originalList;
    private List<Contact> filteredList;

    public FriendListViewAdapter(Context context, int resource, List<Contact> items) {
        super(context, resource, items);
        this.context = context;
        this.filteredList = items;
        this.originalList = new ArrayList<>();
        this.originalList.addAll(items);
    }

    private class FriendViewHolder {
        TextView friendNameText;
        ImageView removeFriendImageView;
        ImageView photoImageView;
    }

    public void updateLists(List<Contact> filteredList, List<Contact> originalList) {
        this.filteredList = filteredList;
        this.originalList = originalList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        FriendViewHolder holder;
        Contact rowItem = filteredList.get(position);
        LayoutInflater rowViewInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            Log.i("run", "convertView==null");
            convertView = rowViewInflater.inflate(R.layout.friends_list_item,null);
            holder = new FriendViewHolder();
            holder.friendNameText = (TextView) convertView.findViewById(R.id.friendNameTextView);
            holder.removeFriendImageView = (ImageView) convertView.findViewById(R.id.removeFriendImageView);
            holder.photoImageView = (ImageView) convertView.findViewById(R.id.friendImageView);
            convertView.setTag(holder);
        } else {
            Log.i("run","convertView!=null");
            holder = (FriendViewHolder) convertView.getTag();
        }
        if (rowItem != null) {
            holder.friendNameText.setText(rowItem.getName());
            holder.removeFriendImageView.setImageResource(R.drawable.hatchat_icon);

            if (rowItem.getPhoto() == null) {
                holder.photoImageView.setImageResource(R.drawable.default_user_photo);
            } else {
                holder.photoImageView.setImageBitmap(rowItem.getPhoto());
            }

        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FriendsListFilter();
        }
        return filter;
    }

    private class FriendsListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            String prefix = constraint.toString().toLowerCase().trim();

            if (prefix == null || prefix.length() == 0) {
                results.values = originalList;
                results.count = originalList.size();
            } else {
                ArrayList<Contact> newList = new ArrayList<>();

                for (int i = 0; i < originalList.size(); i++) {
                    Contact contact = originalList.get(i);
                    String name = contact.getName().toLowerCase();

                    if (name.contains(prefix)) {
                        newList.add(contact);
                    }
                }

                results.values = newList;
                results.count = newList.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (ArrayList<Contact>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0; i < filteredList.size(); i++) {
                Contact contact = (Contact) filteredList.get(i);
                add(contact);
            }
            notifyDataSetInvalidated();
        }
    }
}
