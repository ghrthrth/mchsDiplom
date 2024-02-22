package com.example.mchs.ui.gallery;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mPhotoUrls;

    public ImageAdapter(Context context, List<String> photoUrls) {
        mContext = context;
        mPhotoUrls = photoUrls;
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
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(400, 400));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setRotation(90);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.get().load(mPhotoUrls.get(position)).into(imageView);

        return imageView;
    }
}

