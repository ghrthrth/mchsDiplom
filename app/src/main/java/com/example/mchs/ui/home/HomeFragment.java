package com.example.mchs.ui.home;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mchs.databinding.FragmentHomeBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    String[] array = {"Пожар", "Сработала сигнализация", "Застрял кот на дереве"};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Spinner spinner = binding.spinner;
        final TextView name = binding.name;
        final TextView message = binding.message;
        final Button send = binding.send;

        List<String> list = Arrays.asList(array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = name.getText().toString();
                String msg = message.getText().toString();
                new HttpRequestTask().execute(username, msg);

            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class HttpRequestTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String msg = params[1];

            JSONObject json = new JSONObject();

            try {
                json.put("username", username);
                json.put("msg", msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, json.toString());
            Request request = new Request.Builder()
                    .url("https://claimbe.store/mchs/index.php")
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                // Вывести сообщение об ошибке, если есть
                Toast.makeText(getContext(), "Ошибка: " + result, Toast.LENGTH_SHORT).show();
            } else {
                // Вывести сообщение об успешной записи данных
                Toast.makeText(getContext(), "Данные успешно записаны", Toast.LENGTH_SHORT).show();
            }
        }
    }
}



