package com.example.assistant.ui.timetable.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.assistant.repository.GetDataRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TimetableViewModel extends ViewModel {
    private GetDataRepository getDataRepository = new GetDataRepository();
    private MutableLiveData<String> url = new MutableLiveData<>();
    private MutableLiveData<String> getResult = new MutableLiveData<>();
    private MutableLiveData<Integer> currentWeekType = new MutableLiveData<>(0); // 0 - четная, 1 - нечетная
    private MutableLiveData<List<List<ArrayList<String>>>> allEventsInTwoWeek = new MutableLiveData<>();
    private MutableLiveData<List<ArrayList<String>>> allEventsInWeek = new MutableLiveData<>();
    private MutableLiveData<List<List<ArrayList<String>>>> events = new MutableLiveData<List<List<ArrayList<String>>>>();
    private MutableLiveData<String> token = new MutableLiveData<>();



    public void setUrl(String urlTimetable) {
        url.setValue(urlTimetable);
    }

    public void setToken(String t) {
        token.setValue(t);
    }

    public LiveData<String> getResult() {
        return getResult;
    }


    public TimetableViewModel() {}




    // Получаем неделю (чет, нечет)
    public LiveData<List<ArrayList<String>>> getAllEventsInWeek() {
        int type = currentWeekType.getValue();
        allEventsInWeek.setValue(allEventsInTwoWeek.getValue().get(type));
        return allEventsInWeek;
    }


    // Передача параметра недели
    public void setCurrentWeekType(int type) {
        currentWeekType.setValue(type);
    }


    // Получаем тип недели (чет, нечет)
    public LiveData<Integer> getCurrentWeekType() {
        return currentWeekType;
    }


    public void loadTimetableData() {
        getDataRepository.getDataObj(url.getValue(), token.getValue()).observeForever(result -> {
            getResult.setValue(result);
        });
    }

    /*
        Передача данных плана в модель
     */
    public void setEvents() {
        String json = getResult().getValue();
        List<List<ArrayList<String>>> data = parseJson(json);
        events.setValue(data);

    }

    public List<List<ArrayList<String>>> getEvents() {
        return events.getValue();
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