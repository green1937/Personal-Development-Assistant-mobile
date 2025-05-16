package com.example.assistant.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetDataRepository {


    public LiveData<String> getDataObj(String urlString) {
        final MutableLiveData<String> result = new MutableLiveData<>();
        System.out.println("URL in GET Repo " + urlString);

        new Thread(() -> {
            String json = null;
            String message = "true";
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                // Заголовок для обхода tuna browser warning
                urlConnection.setRequestProperty("tuna-skip-browser-warning", "true");

                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();

                if (inputStream == null) {
                    Log.d("DEBUG", "inputStream == null");
                    message = "false";
                    return;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                if (buffer.length() == 0) return;
                json = buffer.toString();

            } catch (IOException e) {
                Log.e("DEBUG", "IOException при получении JSON", e);
            } finally {
                if (urlConnection != null) urlConnection.disconnect();
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e("DEBUG", "Ошибка закрытия reader", e);
                    }
                }
            }
            System.out.println("REPO   json " + json);
            result.postValue(json);
        }).start();

        return result;
    }
}
