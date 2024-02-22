package com.example.mchs.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mchs.databinding.FragmentGalleryBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getPhotoUrlsFromServer();

        return root;
    }

    private void addItemsToList(JSONArray jsonArray, List<String> list) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                String item = jsonArray.getString(i);
                list.add(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void getPhotoUrlsFromServer() {
        String url = "https://claimbe.store/mchs/return.php"; // Замените на ваш URL-адрес сервера

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray photoUrlsArray = jsonObject.getJSONArray("photoUrls");
                        JSONArray usernameArray = jsonObject.getJSONArray("username");
                        JSONArray msgArray = jsonObject.getJSONArray("msg");
                        JSONArray categoryArray = jsonObject.getJSONArray("category");
                        JSONArray categIncidentArray = jsonObject.getJSONArray("categIncident");

                        List<String> photoUrls = new ArrayList<>();
                        List<String> usernames = new ArrayList<>();
                        List<String> msgs = new ArrayList<>();
                        List<String> categories = new ArrayList<>();
                        List<String> categIncidents = new ArrayList<>();

                        addItemsToList(photoUrlsArray, photoUrls);
                        addItemsToList(usernameArray, usernames);
                        addItemsToList(msgArray, msgs);
                        addItemsToList(categoryArray, categories);
                        addItemsToList(categIncidentArray, categIncidents);


                        // Now you can use the parsed data as needed
                        for (int i = 0; i < photoUrls.size(); i++) {
                            String photoUrl = photoUrls.get(i);
                            String username = usernames.get(i);
                            String msg = msgs.get(i);
                            String category = categories.get(i);
                            String categIncident = categIncidents.get(i);

                            Log.d("Photo URL", photoUrl);
                            Log.d("Username", username);
                            Log.d("Message", msg);
                            Log.d("Category", category);
                            Log.d("CategIncident", categIncident);
                        }

                        displayPhotosInGrid(photoUrls, usernames, msgs, categories, categIncidents);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void displayPhotosInGrid(List<String> photoUrls, List<String> usernames, List<String> msgs, List<String> categories, List<String> categIncidents) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GridView gridView = binding.gridView;
                ImageAdapter adapter = new ImageAdapter(getContext(), photoUrls, usernames, msgs, categories, categIncidents);
                gridView.setAdapter(adapter);
            }
        });
    }


}
