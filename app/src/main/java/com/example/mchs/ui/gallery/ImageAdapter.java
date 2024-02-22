package com.example.mchs.ui.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mchs.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mPhotoUrls;
    private List<String> mUsernames;
    private List<String> mMsgs;
    private List<String> mCategories;
    private List<String> mCategIncidents;

    public ImageAdapter(Context context, List<String> photoUrls, List<String> usernames, List<String> msgs, List<String> categories, List<String> categIncidents) {
        mContext = context;
        mPhotoUrls = photoUrls;
        mUsernames = usernames;
        mMsgs = msgs;
        mCategories = categories;
        mCategIncidents = categIncidents;
    }

    @Override
    public int getCount() {
        return mPhotoUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;

        if (convertView == null) {
            gridView = new View(mContext);
            gridView = inflater.inflate(R.layout.grid_item_layout, null);

            // Find the views within the grid item layout
            ImageView imageView = gridView.findViewById(R.id.grid_image);
            TextView usernameTextView = gridView.findViewById(R.id.username_text_view);
            TextView msgTextView = gridView.findViewById(R.id.msg_text_view);
            TextView categoryTextView = gridView.findViewById(R.id.category_text_view);
            TextView categIncidentTextView = gridView.findViewById(R.id.categ_incident_text_view);

            // Set the data for each view
            String photoUrl = mPhotoUrls.get(position);
            String username = mUsernames.get(position);
            String msg = mMsgs.get(position);
            String category = mCategories.get(position);
            String categIncident = mCategIncidents.get(position);

            // Load the image using a library like Picasso or Glide
            Picasso.get().load(photoUrl).into(imageView);

            // Set the text for the text views
            usernameTextView.setText(username);
            msgTextView.setText(msg);
            categoryTextView.setText(category);
            categIncidentTextView.setText(categIncident);
        } else {
            gridView = convertView;
        }

        return gridView;
    }
}
