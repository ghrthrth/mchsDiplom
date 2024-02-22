package com.example.mchs.ui.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.mchs.databinding.FragmentHomeBinding;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private static final int REQUEST_CODE_PERMISSION = 2;

    private FragmentHomeBinding binding;

    String[] array = {"Пожар", "Сработала сигнализация", "Застрял кот на дереве", "Умер человек", "Сантехник прорвал трубу"};

    private Uri selectedImageUri;

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
        final Button selectPhoto = binding.selectPhoto;

        List<String> list = Arrays.asList(array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_PERMISSION);
                } else {
                    openGallery();
                }
            }
        });
        final String[] ok = new String[1];

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = name.getText().toString();
                String msg = message.getText().toString();
                String selectedCategory = spinner.getSelectedItem().toString(); // Get selected category
                ok[0] = checkCategory(array);
                new HttpRequestTask().execute(username, msg, selectedCategory, ok[0]);
            }
        });

        return root;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == getActivity().RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            ImageView imageView = binding.imageView2;
            imageView.setImageURI(selectedImageUri);
        }
    }

    private String getRealPathFromUri(Uri uri) {
        String realPath;
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            realPath = cursor.getString(column_index);
            cursor.close();
            return realPath;
        } else {
            return uri.getPath(); // Возвращаем исходный путь, если не удалось получить реальный путь
        }
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

    public String checkCategory(String[] zalupa) {
        String urgent = "срочно";
        String notVeryUrgent = "средне";
        String nonUrgent = "хуйня";
        String result = "";

        for (String incident : zalupa) {
            // Add your logic here to determine the urgency level of each incident
            if (incident.contains("Пожар") || incident.contains("Умер человек")) {
                result = nonUrgent;
            } else if (incident.contains("Сработала сигнализация")) {
                result = notVeryUrgent;
            } else {
                result = urgent;
            }
        }

        // Do something with the categorized incidents
        return result;
    }

    private class HttpRequestTask extends AsyncTask<String, Void, String> {
        private String selectedCategory;
        private String categIncident;

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String msg = params[1];
            String category = params[2]; // Get category from params
            selectedCategory = params[2];
            categIncident = params[3];


            try {
                // Получите реальный путь к файлу из Uri
                String filePath = getRealPathFromUri(selectedImageUri);

                // Создайте объект MultipartBody.Builder для создания запроса с множественными частями (включая изображение)
                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("username", username)
                        .addFormDataPart("msg", msg)
                        .addFormDataPart("category", category)
                        .addFormDataPart("categIncident", categIncident);

                // Загрузите файл в запрос
                File file = new File(filePath);
                builder.addFormDataPart("photo", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));

                // Создайте объект RequestBody из MultipartBody.Builder
                RequestBody requestBody = builder.build();

                // Создайте объект запроса с использованием OkHttp
                Request request = new Request.Builder()
                        .url("https://claimbe.store/mchs/index.php") // Замените на URL вашего сервера
                        .post(requestBody)
                        .build();

                // Создайте клиент OkHttp и выполните запрос
                OkHttpClient client = new OkHttpClient();

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



