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
                        List<String> photoUrls = new ArrayList<>();
                        for (int i = 0; i < photoUrlsArray.length(); i++) {
                            String photoUrl = photoUrlsArray.getString(i);
                            photoUrls.add(photoUrl);
                        }
                        displayPhotosInGrid(photoUrls);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void displayPhotosInGrid(List<String> photoUrls) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GridView gridView = binding.gridView;
                ImageAdapter adapter = new ImageAdapter(getContext(), photoUrls);
                gridView.setAdapter(adapter);
            }
        });
    }

}
