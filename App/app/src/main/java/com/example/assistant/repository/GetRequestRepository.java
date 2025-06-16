package com.example.assistant.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetRequestRepository {

    public LiveData<String> sendDataToServer(String jsonData, String urlWheel, String token) {
        final MutableLiveData<String> result = new MutableLiveData<>();

        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL serverUrl = new URL(urlWheel);
                connection = (HttpURLConnection) serverUrl.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("tuna-skip-browser-warning", "true");

                // Добавляем заголовок с токеном
                connection.setRequestProperty("Authorization", "Bearer " + token);

                // Отправка данных
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonData.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    StringBuilder response = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                    }

                    result.postValue(response.toString()); // postValue для асинхронного обновления
                }
            } catch (IOException e) {
                Log.e("SEND_ERROR", "Ошибка при отправке данных", e);
                result.postValue(null);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();

        return result;
    }
}
