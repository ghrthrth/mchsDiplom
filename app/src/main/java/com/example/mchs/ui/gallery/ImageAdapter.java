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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        sortData();
    }

    private void sortData() {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < mCategIncidents.size(); i++) {
            Card card = new Card(mPhotoUrls.get(i), mUsernames.get(i), mMsgs.get(i), mCategories.get(i), mCategIncidents.get(i));
            cards.add(card);
        }

        Collections.sort(cards, new Comparator<Card>() {
            @Override
            public int compare(Card card1, Card card2) {
                String categ1 = card1.getCategIncident();
                String categ2 = card2.getCategIncident();

                if (categ1.equals("срочно")) {
                    return -1; // Первыми будут срочные
                } else if (categ1.equals("средне") && categ2.equals("несрочно")) {
                    return -1; // Затем средне
                } else {
                    return 1; // Все остальные считаются несрочными
                }
            }
        });

        // Обновляем отсортированные данные обратно в исходные списки
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            mPhotoUrls.set(i, card.getPhotoUrl());
            mUsernames.set(i, card.getUsername());
            mMsgs.set(i, card.getMsg());
            mCategories.set(i, card.getCategory());
            mCategIncidents.set(i, card.getCategIncident());
        }
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
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_item_layout, parent, false);
        }

        // Находим представления внутри макета элемента сетки
        ImageView imageView = convertView.findViewById(R.id.grid_image);
        TextView usernameTextView = convertView.findViewById(R.id.username_text_view);
        TextView msgTextView = convertView.findViewById(R.id.msg_text_view);
        TextView categoryTextView = convertView.findViewById(R.id.category_text_view);
        TextView categIncidentTextView = convertView.findViewById(R.id.categ_incident_text_view);

        // Устанавливаем данные для каждого представления
        String photoUrl = mPhotoUrls.get(position);
        String username = mUsernames.get(position);
        String msg = mMsgs.get(position);
        String category = mCategories.get(position);
        String categIncident = mCategIncidents.get(position);

        // Загружаем изображение с помощью библиотеки Picasso или Glide
        Picasso.get().load(photoUrl).into(imageView);

        // Устанавливаем текст для текстовых представлений
        usernameTextView.setText(username);
        msgTextView.setText(msg);
        categoryTextView.setText(category);
        categIncidentTextView.setText(categIncident);

        return convertView;
    }
}
