package com.example.mchs.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
                String selectedCategory = spinner.getSelectedItem().toString(); // Get selected category
                new HttpRequestTask().execute(username, msg, selectedCategory);

            }
        });

        return root;
    }

    private void showTip(String category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Подсказка для категории " + category);

        // Здесь вы можете установить сообщение в зависимости от категории
        String message = "Это подсказка для категории " + category;
        builder.setMessage(message);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Закройте диалог, когда пользователь нажмет OK
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class HttpRequestTask extends AsyncTask<String, Void, String> {
        private String selectedCategory;

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String msg = params[1];
            String category = params[2]; // Get category from params
            selectedCategory = params[2];

            JSONObject json = new JSONObject();

            try {
                json.put("username", username);
                json.put("msg", msg);
                json.put("category", category); // Include category in JSON
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
                // Получить выбранную категорию

                // Показать всплывающее окно с подсказкой для выбранной категории
                showTip(selectedCategory);
            }
        }
    }
}



