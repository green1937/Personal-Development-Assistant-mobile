package com.example.assistant.repository;

import android.content.Context;
import android.util.Log;

import com.example.assistant.R;
import com.example.assistant.utils.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimetableRepository {
    private String url;

    public TimetableRepository(Context context) {
        url = context.getResources().getString(R.string.urlTuna) + "events";
    }

    public void getTimetableData(Callback callback) {
        new Thread(() -> {
            try {
                String json = getJsonFromUrl(url);
                if (json != null) {
                    List<List<ArrayList<String>>> data = parseJson(json);
                    System.out.println("This is DATA in REpository -------------- " + data);
                    callback.onSuccess(data);
                } else {
                    callback.onFailure("Ошибка загрузки данных");
                }
            } catch (Exception e) {
                callback.onFailure("Ошибка загрузки данных");
            }
        }).start();
    }


    private String getJsonFromUrl(String urlString) {
        String json = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("tuna-skip-browser-warning", "true");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();

            if (inputStream == null) {
                Log.d("DEBUG", "inputStream == null");
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            if (buffer.length() == 0) return null;
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
        return json;
    }


    private List<List<ArrayList<String>>> parseJson(String json) {
        List<List<ArrayList<String>>> allEventsWithTwoWeek = new ArrayList<>();
        try {
            List<List<List<ArrayList<String>>>> allEvents = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("days");
            for (int i = 0; i < jsonArray.length(); i++) {
                List<List<ArrayList<String>>> fullWeek = new ArrayList<>();
                List<ArrayList<String>> fullEvent;

                JSONObject weekData = jsonArray.getJSONObject(i);

                fullEvent = addEventInWeek(weekData, "odd_week");  // Нечетная неделя
                fullWeek.add(fullEvent);

                fullEvent = addEventInWeek(weekData, "even_week");  // Четная неделя
                fullWeek.add(fullEvent);

                allEvents.add(fullWeek);  // Расписание на две недели
            }
            allEventsWithTwoWeek = gelAllEventWithTwoWeek(allEvents);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return allEventsWithTwoWeek;
    }




    /*
        Получение в массив данных каждого мероприятия нечетной/четной недели
    */
    protected List<ArrayList<String>> addEventInWeek(JSONObject weekData, String weekName) throws JSONException {
        List<ArrayList<String>> fullEvent = new ArrayList<>();

        if (!weekData.get(weekName).toString().equals("null")) {

            JSONArray jsonArray2 = (JSONArray) weekData.get(weekName);

            for (int j = 0; j < jsonArray2.length(); j++) {
                ArrayList<String> eventExample = new ArrayList<>();
                JSONObject eventExampleData = jsonArray2.getJSONObject(j);

                eventExample.add(eventExampleData.getString("id"));
                eventExample.add(eventExampleData.getString("name"));
                eventExample.add(eventExampleData.getString("start_time"));
                eventExample.add(eventExampleData.getString("stop_time"));

                String placeEvent = eventExampleData.getString("place");
                if (placeEvent.length() > 5 && placeEvent.substring(0,4).equals("http")) {
                    placeEvent = "ссылка на мероприятие";
                }

                eventExample.add(placeEvent);
                eventExample.add(eventExampleData.getString("format"));
                fullEvent.add(eventExample);
            }
        }
        return fullEvent;
    }


    /*
        Функция отвечает за передачу данных в RecyclerView и
        отображение в зависимости от выбора недели (нечетной/четной)
     */
    protected List<List<ArrayList<String>>> gelAllEventWithTwoWeek(List<List<List<ArrayList<String>>>> allEvents) {
        List<List<ArrayList<String>>> allEventsInTwoWeek = new ArrayList<>();
        List<ArrayList<String>> allEventsInWeek = new ArrayList<>();
        allEventsInWeek = cycleByWeek(0, allEvents);
        allEventsInTwoWeek.add(allEventsInWeek);
        allEventsInWeek = cycleByWeek(1, allEvents);
        allEventsInTwoWeek.add(allEventsInWeek);

        // При нажатии на кнопку меняется ее текста "четная <-> нечетная"
        /*
        textParameterOddEvenWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (textParameterOddEvenWeek.getText().equals("Нечетная неделя")) {
                    textParameterOddEvenWeek.setText("Четная неделя");  // Смена текста
                    List<ArrayList<String>> allEvents = cycleByWeek(1);
                }
                else {
                    textParameterOddEvenWeek.setText("Нечетная неделя");
                    List<ArrayList<String>> allEvents = cycleByWeek(0);
                }
            }
        });
         */
        return allEventsInTwoWeek;
    }


    /*
        Цикл по всем дням недели (пн, вт, ср ...) отдельно для нечетной/четной
     */
    protected List<ArrayList<String>> cycleByWeek(int param, List<List<List<ArrayList<String>>>> allEvents) {
        String[] weeksDay = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"};

        List<ArrayList<String>> fullWeekWithDayOfWeekForRV = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            ArrayList<String> dd = new ArrayList<>(Collections.singleton(weeksDay[i])); //День недели
            fullWeekWithDayOfWeekForRV.add(dd);
            List<ArrayList<String>> eventData = allEvents.get(i).get(param);  // Мероприятия
            fullWeekWithDayOfWeekForRV.addAll(eventData);  // Добавление мероприятия по одному
        }

        return fullWeekWithDayOfWeekForRV;
    }



}