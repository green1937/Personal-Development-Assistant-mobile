package com.example.assistant.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class NewTaskRepository {

    public LiveData<Boolean> sendDataObj(String jsonData, String urlString, String requestMethod) {
        final MutableLiveData<Boolean> result = new MutableLiveData<>();
        System.out.println("JSON DATA " + jsonData);

        new Thread(() -> {

                HttpURLConnection connection = null;
                try {
                    URL url = new URL(urlString);
                    connection = (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod(requestMethod);
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("tuna-skip-browser-warning", "true");

                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
                        os.write(input);
                    }

                    int responseCode = connection.getResponseCode();
                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        result.postValue(false);
                        System.out.println("1");
                        return;
                    }
                } catch (IOException e) {
                    result.postValue(false);
                    System.out.println("2");
                    return;
                } finally {
                    connection.disconnect();
                }

            result.postValue(true);
            System.out.println("3");
        }).start();

        System.out.println("4");
        return result;
    }
}
