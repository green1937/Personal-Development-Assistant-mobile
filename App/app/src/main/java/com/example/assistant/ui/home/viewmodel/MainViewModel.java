package com.example.assistant.ui.home.viewmodel;

import android.widget.EditText;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.assistant.repository.GetDataRepository;
import com.example.assistant.repository.NewObjectRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainViewModel extends ViewModel {
    private MutableLiveData<String> url = new MutableLiveData<>();
    private MutableLiveData<String> urlNote = new MutableLiveData<>();
    private GetDataRepository getDataRepository = new GetDataRepository();
    private NewObjectRepository repository = new NewObjectRepository();
    private MutableLiveData<String> getResult = new MutableLiveData<>();
    private MutableLiveData<Boolean> getResultNewNote = new MutableLiveData<>();
    private MutableLiveData<Boolean> getResultEditNote = new MutableLiveData<>();
    private MutableLiveData<String> getPhraseResult = new MutableLiveData<>();
    private MutableLiveData<String> nameDayOfWeek = new MutableLiveData<>();

    private MutableLiveData<List<ArrayList<String>>> tasks = new MutableLiveData<>();
    private MutableLiveData<List<ArrayList<String>>> events = new MutableLiveData<>();
    private MutableLiveData<String> note = new MutableLiveData<>();
    private MutableLiveData<String> dateNote = new MutableLiveData<>();

    private MutableLiveData<String> token = new MutableLiveData<>();


    DateFormat formatForDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    DateFormat formatForDateServer = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public void setUrl(String urlTuna, String date) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat outputFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String formattedDate = outputFormat.format(Objects.requireNonNull(inputFormat.parse(date)));
            url.setValue(urlTuna + formattedDate);
            dateNote.setValue(outputFormat2.format(Objects.requireNonNull(inputFormat.parse(date))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setToken(String t) {
        token.setValue(t);
    }

    public void setNoteUrl(String urlN) {
        urlNote.setValue(urlN);
    }


    //  Получение текущей даты в формате ДД.ММ.ГГГГ
    public String getTodayData() {
        Date currDate = new Date();
        return formatForDate.format(currDate);
    }

    /*
        Получение даты отличной на некоторое кол-во дней от текущей даты в формате ДЕНЬ.МЕСЯЦ.ГОД.
        Например, для "завтра" - это плюс один день, а для "вчера" - это минус один день
    */
    public String getAnotherDate(int a) {
        try {
            Date date = formatForDate.parse(getTodayData());
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DATE, a);  // сколько дней прибавить к дате
            date = c.getTime();
            return formatForDate.format(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


    /*
        Получение параметров даты (день недели, номер дня недели, неделя)
        из EditText, в который поступает дата из выпадающего списка или из календаря
     */
    private void getDataParameters(String currDate) {
        String [] weeksDay = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье" };
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        formatter = formatter.withLocale(Locale.getDefault());
        LocalDate date = LocalDate.parse(currDate, formatter);

        DayOfWeek day = date.getDayOfWeek();
        int valueDayOfWeek = day.getValue() - 1;  // Порядковый номер дня недели (отсчет начинается с 0)
        nameDayOfWeek.setValue(weeksDay[valueDayOfWeek]);
    }
    public LiveData<Boolean> getResultNewNote() {
        return getResultNewNote;
    }

    public LiveData<Boolean> getResultEditNote() {
        return getResultEditNote;
    }



    //  Результат загрузки данных
    public LiveData<String> getResult() {
        return getResult;
    }

    //  Результат загрузки данных
    public LiveData<String> getPhraseResult() {
        return getPhraseResult;
    }

    // Все расписание
    public LiveData<List<ArrayList<String>>> getEvents() {
        return events;
    }

    // Все задачи
    public LiveData<List<ArrayList<String>>> getTasks() {
        return tasks;
    }

    public LiveData<String> getNote() {
        return note;
    }

    // Загрузка данных Главного экрана на день (Задачи, Распсиание, Заметки)
    public void loadHomeData() {
        System.out.println("TOKEN in VIEWMODEL = " + token.getValue());
        getDataRepository.getDataObj(url.getValue(), token.getValue()).observeForever(result -> {
            getResult.setValue(result);
        });
    }

    public void loadPhrase() {
        getDataRepository.getDataObj("https://api.forismatic.com/api/1.0/?method=getQuote&format=jsonp&jsonp=parseQuote", "phrase").observeForever(result -> {
            getPhraseResult.setValue(result);
        });
    }


    /*
        Получение расписания и задач на выбранную дату
     */
    public void getDataFromJSON(String date) {
        String json = getResult.getValue();
        try {
            // Получение всех задач
            String[] tasksCtgForMainPage = { "На день", "Просрочено", "Скоро дедлайн", "Бессрочно"};
            List<ArrayList<String>> allTasks = new ArrayList<>();
            allTasks.add(new ArrayList<>(Collections.singleton(tasksCtgForMainPage[0])));

            JSONObject jsonObject = new JSONObject(json);
            JSONObject todayObject = jsonObject.getJSONObject("today");
            String[] todayTaskStr = {"fixed_tasks", "done_tasks", "late_tasks", "soon_tasks", "free_tasks"};

            for (int i=0; i<2; i++) {
                JSONArray tasksArray = todayObject.getJSONArray(todayTaskStr[i]);

                for (int j=0; j<tasksArray.length(); j++) {
                    JSONObject taskExampleData = tasksArray.getJSONObject(j);
                    ArrayList<String> oneTask = new ArrayList<>();

                    oneTask.add(taskExampleData.getString("id"));
                    oneTask.add(taskExampleData.getString("name"));
                    oneTask.add(taskExampleData.getString("status"));

                    allTasks.add(oneTask);
                }
            }

            getDataParameters(date);
            JSONArray eventArray = todayObject.getJSONArray("day_classes");
            List<ArrayList<String>> allEvents = new ArrayList<>();
            allEvents.add(new ArrayList<>(Collections.singleton(nameDayOfWeek.getValue())));

            for (int j=0; j<eventArray.length(); j++) {
                JSONObject eventData = eventArray.getJSONObject(j);
                ArrayList<String> oneEvent = new ArrayList<>();

                oneEvent.add(eventData.getString("id"));
                oneEvent.add(eventData.getString("name"));
                oneEvent.add(eventData.getString("start_time"));
                oneEvent.add(eventData.getString("stop_time"));


                String placeEvent = eventData.getString("place");
                if (placeEvent.length() > 5 && placeEvent.substring(0,4).equals("http")) {
                    placeEvent = "ссылка на мероприятие";
                }

                oneEvent.add(placeEvent);
                oneEvent.add(eventData.getString("format"));
                allEvents.add(oneEvent);
            }

            for (int i=2; i<5; i++) {
                allTasks.add(new ArrayList<>(Collections.singleton(tasksCtgForMainPage[i-1])));
                JSONArray tasksArray = jsonObject.getJSONArray(todayTaskStr[i]);

                for (int j=0; j<tasksArray.length(); j++) {
                    JSONObject taskExampleData = tasksArray.getJSONObject(j);
                    ArrayList<String> oneTask = new ArrayList<>();

                    oneTask.add(taskExampleData.getString("id"));
                    oneTask.add(taskExampleData.getString("name"));
                    oneTask.add(taskExampleData.getString("status"));

                    allTasks.add(oneTask);
                }
            }

            note.setValue(todayObject.getString("text_note"));
            events.setValue(allEvents);
            tasks.setValue(allTasks);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void createNote(String text) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode()
                .put("user_id", 1)
                .put("assigned_day", dateNote.getValue())
                .put("text", text);
        System.out.println("JSON DATA NEW NOTE = " + json.toString());
        repository.sendDataObj(Collections.singletonList(json.toString()), urlNote.getValue(),
                "POST", token.getValue()).observeForever(result -> getResultNewNote.setValue(result));
    }

    /*
        Возвращает фразу дня и ее автора
     */
    public ArrayList<String> getPhraseText() {
        ArrayList<String> quoteText = new ArrayList<>();
        String json = getPhraseResult.getValue();
        try {
            // Удаляем "parseQuote(" и ")"
            String jsonString = json.substring(11, json.length() - 1);

            // Преобразуем JSON строку в объект
            JSONObject jsonObject = new JSONObject(jsonString);

            // Получаем значение quoteText
            quoteText.add(jsonObject.getString("quoteText"));
            quoteText.add(jsonObject.getString("quoteAuthor"));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return quoteText;
    }


    public void setInitialDate1(int year, int monthOfYear, int dayOfMonth, EditText editDate) {
        String dateForEndStr;
        if (dayOfMonth < 10 && monthOfYear < 10) {
            dateForEndStr = "0" + dayOfMonth + "." + "0" + monthOfYear + "." + year;
        }
        else {
            if (dayOfMonth > 9 && monthOfYear < 10) {
                dateForEndStr = dayOfMonth + "." + "0" + monthOfYear + "." + year;
            } else if (dayOfMonth < 10 && monthOfYear > 9) {
                dateForEndStr = "0" + dayOfMonth + "." + monthOfYear + "." + year;
            } else {
                dateForEndStr = dayOfMonth + "." + monthOfYear + "." + year;
            }
        }
        editDate.setText(dateForEndStr);
    }


    public int noteVisibility(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        int flag = 0;
        try {
            Date currDate = new Date();
            LocalDate localDate1 = LocalDate.parse(date, formatter);
            LocalDate localDate2 = LocalDate.parse(formatForDate.format(currDate), formatter);

            if(localDate1.equals(localDate2)) flag = 0;
            else {
                if (localDate1.isAfter(localDate2)) flag = 1;
                else flag = 3;
            }

        } catch (DateTimeParseException e) {
            System.err.println("Ошибка при парсинге даты: " + e.getMessage());
        }
        System.out.println("FLAG NOTE = " + flag);
        return flag;
    }

}
