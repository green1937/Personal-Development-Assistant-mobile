package com.example.assistant.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EventRepository {

    public LiveData<Boolean> sendEvents(List<String> jsonData, String urlEvent) {
        final MutableLiveData<Boolean> result = new MutableLiveData<>();

        new Thread(() -> {
            for (String json : jsonData) {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(urlEvent);
                    connection = (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("tuna-skip-browser-warning", "true");

                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = json.getBytes(StandardCharsets.UTF_8);
                        os.write(input);
                    }

                    int responseCode = connection.getResponseCode();
                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        result.postValue(false);
                        return;
                    }
                } catch (IOException e) {
                    result.postValue(false);
                    return;
                } finally {
                    connection.disconnect();
                }
            }
            result.postValue(true);
        }).start();

        return result;
    }
}