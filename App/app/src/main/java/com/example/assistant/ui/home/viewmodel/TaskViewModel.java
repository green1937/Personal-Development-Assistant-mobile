package com.example.assistant.ui.home.viewmodel;

import static java.lang.Integer.parseInt;


import android.content.Context;
import android.text.format.DateUtils;
import android.widget.EditText;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.assistant.ui.wheel.model.Category;
import com.example.assistant.ui.home.model.NewTask;
import com.example.assistant.ui.home.model.Repeat;
import com.example.assistant.ui.home.model.Task;
import com.example.assistant.repository.GetDataRepository;
import com.example.assistant.repository.NewTaskRepository;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskViewModel extends ViewModel {

    private MutableLiveData<Task> task = new MutableLiveData<>();
    private MutableLiveData<NewTask> editTask = new MutableLiveData<>();
    private MutableLiveData<Category> category = new MutableLiveData<>();
    private MutableLiveData<Repeat> repeat = new MutableLiveData<>();
    private MutableLiveData<Integer> idEditTask = new MutableLiveData<>();
    private MutableLiveData<String> url = new MutableLiveData<>();
    private MutableLiveData<String> urlEditTask = new MutableLiveData<>();
    private MutableLiveData<String> estimate = new MutableLiveData<>();

    private MutableLiveData<String> repeatInterval = new MutableLiveData<>();

    private MutableLiveData<String> numOfRepeats = new MutableLiveData<>();

    private MutableLiveData<String> urlCtg = new MutableLiveData<>();
    private MutableLiveData<List<ArrayList<String>>> allCategories = new MutableLiveData<>();
    private MutableLiveData<List<ArrayList<String>>> allPlans = new MutableLiveData<>();
    private MutableLiveData<String> itemCtg = new MutableLiveData<>();
    private MutableLiveData<String> itemPlan = new MutableLiveData<>();

    private MutableLiveData<String> urlPlans = new MutableLiveData<>();

    private NewTaskRepository repository = new NewTaskRepository();
    private GetDataRepository getDataRepository = new GetDataRepository();
    private MutableLiveData<Boolean> saveResult = new MutableLiveData<>();

    private MutableLiveData<Boolean> isTaskRepeat = new MutableLiveData<>();
    private MutableLiveData<Integer> flagSpinnerWhenRepeatEnd = new MutableLiveData<>();
    private MutableLiveData<int[]> flagsWeek = new MutableLiveData<>();
    private MutableLiveData<String> spinnerRepeatTimeParam = new MutableLiveData<>();

    private MutableLiveData<String> getCtgResult = new MutableLiveData<>();
    private MutableLiveData<String> getPlanResult = new MutableLiveData<>();
    private MutableLiveData<String> getEditTaskResult = new MutableLiveData<>();

    private MutableLiveData<String> token = new MutableLiveData<>();


    public void setToken(String t) {
        token.setValue(t);
    }




    public LiveData<Task> getTask() {
        return task;
    }
    public LiveData<Repeat> getRepeat() {
        return repeat;
    }

    public void setStartDate(String startDate) {
        Task currentTask = task.getValue();
        if (currentTask != null) {
            currentTask.setStartDate(startDate);
            task.setValue(currentTask);
        }
    }

    public void setStopDate(String stopDate) {
        Task currentTask = task.getValue();
        if (currentTask != null) {
            currentTask.setStopDate(stopDate);
            task.setValue(currentTask);
        }
    }
    public void setFlagWeek(int[] flags) {
        flagsWeek.setValue(flags);
    }

    public void setSpinnerTimeParam(String param) {
        spinnerRepeatTimeParam.setValue(param);
    }


    public void setEstimate(String score) {
        estimate.setValue(score);
    }


    public void setItemCtg(String item) {
        itemCtg.setValue(item);
    }

    public void setItemPlan(String item) {
        itemPlan.setValue(item);
    }


    public void setRepeatInterval(String interval) {
        repeatInterval.setValue(interval);
    }

    public void setNumOfRepeats(String number) {
        numOfRepeats.setValue(number);
    }

    public void setIsTaskRepeat(Boolean isRepeat) {
        isTaskRepeat.setValue(isRepeat);
    }

    public void setFlagSpinnerWhenRepeatEnd(Integer flagSpinner) {
        flagSpinnerWhenRepeatEnd.setValue(flagSpinner);
    }
    public void setIdEditTask(int id) {
        idEditTask.setValue(id);
    }

    public void setEditUrl(String urlEdit) {
        urlEditTask.setValue(urlEdit);
    }

    public void setUrl(String urlTask) {
        url.setValue(urlTask);
    }
    public void setUrlCtg(String url1) {
        urlCtg.setValue(url1);
    }
    public void setUrlPlans(String url2) {
        urlPlans.setValue(url2);
    }

    public String getItemCtg() {
        return itemCtg.getValue();
    }

    public String getItemPlan() {
        return itemPlan.getValue();
    }

    public LiveData<String> getCtgResult() {
        return getCtgResult;
    }


    public LiveData<String> getPlanResult() {
        return getPlanResult;
    }

    public LiveData<String> getEditTaskResult() {
        return getEditTaskResult;
    }

    public LiveData<Boolean> getSaveResult() {
        return saveResult;
    }



    public TaskViewModel() {
        repeat.setValue(new Repeat(0,"", null, "", "", 0));
        category.setValue(new Category(0));
        task.setValue(new Task("", "", 0, category.getValue(), "", "", "", "", null, repeat.getValue()));
    }


    public void loadNameAllCtgForTask() {
        getDataRepository.getDataObj(urlCtg.getValue(), token.getValue()).observeForever(result -> getCtgResult.setValue(result));
    }

    public void loadNameAllPlanForTask() {
        getDataRepository.getDataObj(urlPlans.getValue(), token.getValue()).observeForever(result -> getPlanResult.setValue(result));
    }


    public void loadEditTaskData() {
        getDataRepository.getDataObj(urlEditTask.getValue(), token.getValue()).observeForever(result -> getEditTaskResult.setValue(result));
    }

    public void setEditTask() {
        String json = getEditTaskResult.getValue();
        Task currentTask = task.getValue();
        if (currentTask!=null) {
            try {
                JSONObject jsonObject = new JSONObject(json);

                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");

                currentTask.setStatus(parseInt(jsonObject.getString("status")));
                currentTask.setName(jsonObject.getString("name"));
                currentTask.setDescription(jsonObject.getString("description"));
                currentTask.setStartDate(changeDateFormat(jsonObject.getString("start_date"), inputFormat, outputFormat));
                currentTask.setStartTime(jsonObject.getString("start_time"));
                currentTask.setStopDate(changeDateFormat(jsonObject.getString("stop_date"), inputFormat, outputFormat));
                currentTask.setStopTime(jsonObject.getString("stop_time"));
                currentTask.setEstimate(parseInt(jsonObject.getString("estimate")));

                //repeat
                Repeat currentRepeat = repeat.getValue();
                if (!jsonObject.getString("repeat").equals("null") && currentRepeat != null) {
                    JSONObject repeatObject = jsonObject.getJSONObject("repeat");

                    currentRepeat.setTerm(repeatObject.getString("term"));

                    // Получаем days
                    JSONArray daysArray = repeatObject.getJSONArray("days");
                    int[] arr = new int[daysArray.length()];
                    for (int i = 0; i < daysArray.length(); i++) {
                        arr[i] = daysArray.getInt(i);
                    }

                    currentRepeat.setDays(arr);
                    currentRepeat.setRepeatInterval(parseInt(repeatObject.getString("repeat_interval")));
                    currentRepeat.setStart(changeDateFormat(repeatObject.getString("start"), inputFormat, outputFormat));
                    currentRepeat.setStart(changeDateFormat(repeatObject.getString("end"), inputFormat, outputFormat));
                    currentRepeat.setNumberOfRepeat(parseInt(repeatObject.getString("number_of_repeats")));
                }
                else currentRepeat = null;


                //plan
                if (!jsonObject.getString("plan").equals("null")) {
                    JSONObject planObject = jsonObject.getJSONObject("plan");
                    currentTask.setPlanId((long) parseInt(planObject.getString("id")));
                    itemPlan.setValue(planObject.getString("name"));
                }

                //category
                JSONObject categoryObject = jsonObject.getJSONObject("task_category");
                itemCtg.setValue(categoryObject.getString("title"));
                Category currentCtg = category.getValue();
                currentCtg.setId(parseInt(categoryObject.getString("id")));

                repeat.setValue(currentRepeat);
                category.setValue(currentCtg);
                currentTask.setRepeat(repeat.getValue());
                currentTask.setTaskCategory(category.getValue());
                task.setValue(currentTask);
                System.out.println("THIS IS -----EDIT----- TASK DATA " + new Gson().toJson(task.getValue()));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public ArrayList<String> getNameAllCtgForTask() {
        ArrayList<String> nameAllCategories = new ArrayList<>();
        List<ArrayList<String>> allCtg = new ArrayList<>();
        String json = getCtgResult.getValue();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject ctgData = jsonArray.getJSONObject(i);
                ArrayList<String> oneCategory = new ArrayList<>();

                oneCategory.add(ctgData.getString("id"));
                oneCategory.add(ctgData.getString("title"));

                nameAllCategories.add(ctgData.getString("title"));

                allCtg.add(oneCategory);
                allCategories.setValue(allCtg);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return nameAllCategories;
    }


    public ArrayList<String> getNameAllPlanForTask() {
        ArrayList<String> nameAllPlans = new ArrayList<>();
        List<ArrayList<String>> allP = new ArrayList<>();
        String json = getPlanResult.getValue();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject planData = jsonArray.getJSONObject(i);
                ArrayList<String> onePlan = new ArrayList<>();

                onePlan.add(planData.getString("id"));
                onePlan.add(planData.getString("name"));

                nameAllPlans.add(planData.getString("name"));

                allP.add(onePlan);
                allPlans.setValue(allP);
            }
            nameAllPlans.add(0, "Нет плана");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return nameAllPlans;
    }

    public void onSaveBtnClicked() {
        if (isInputDataValid()) {
            System.out.println("SUCCESS!");
            if (idEditTask.getValue()==null) saveNewTask();
            else saveEditTask();
        }
        else
            System.out.println("NO :(");
    }

    public void saveEditTask() {
        ArrayList<String> allJsonData = new ArrayList<>();
        Category ctgTask = new Category(getIdObj(itemCtg.getValue(), allCategories.getValue()));
        Long planId = null;
        if(!itemPlan.getValue().equals("Нет плана"))
            planId = Long.valueOf(getIdObj(itemPlan.getValue(), allPlans.getValue()));
        else
            planId = null;

        Task currentTask = task.getValue();
        editTask.setValue(new NewTask(idEditTask.getValue(), currentTask.getStatus(), currentTask.getName(), currentTask.getDescription(), parseInt(estimate.getValue()), ctgTask, currentTask.getStartDate(), currentTask.getStopDate(), currentTask.getStartTime(), currentTask.getStopTime(), planId));

        System.out.println("Save EDIT task data  ---- task   ---------  " + new Gson().toJson(editTask.getValue()));

        allJsonData.add(new Gson().toJson(editTask.getValue()));
        String jsonData = new Gson().toJson(editTask.getValue());
        repository.sendDataObj(jsonData, url.getValue(), "PUT", token.getValue()).observeForever(result -> saveResult.setValue(result));
    }

    public void saveNewTask() {
        ArrayList<String> allJsonData = new ArrayList<>();
        Category ctgTask = new Category(getIdObj(itemCtg.getValue(), allCategories.getValue()));
        Long planId = null;
        if(!itemPlan.getValue().equals("Нет плана"))
            planId = Long.valueOf(getIdObj(itemPlan.getValue(), allPlans.getValue()));
        else
            planId = null;

        Task currentTask = task.getValue();
        task.setValue(new Task(currentTask.getName(), currentTask.getDescription(), parseInt(estimate.getValue()), ctgTask, currentTask.getStartDate(), currentTask.getStopDate(), currentTask.getStartTime(), currentTask.getStopTime(), planId, repeat.getValue()));

        System.out.println("SAVE  ---- task   ---------  " + new Gson().toJson(task.getValue()));

        allJsonData.add(new Gson().toJson(task.getValue()));
        String jsonData = new Gson().toJson(task.getValue());
        repository.sendDataObj(jsonData, url.getValue(), "POST", token.getValue()).observeForever(result -> {
            saveResult.setValue(result);
        });


    }


    public static int getIdObj(String nameObj, List<ArrayList<String>> allData) {
        for (int i=0; i<allData.size(); i++) {
            if (allData.get(i).get(1).equals(nameObj)) {
                return parseInt(allData.get(i).get(0));
            }
        }
        return 0;
    }




    /*
        Функция, отвечающая за проверку параметров
        сохранение созданной задачи.
     */

    public boolean isInputDataValid() {
        Task currentTask = task.getValue();
        // Оценка не может быть пустой и нельзя указывать только время без даты
        if (currentTask.getName().equals("") || estimate.getValue().equals("")
                || (currentTask.getStartDate().equals("") && !currentTask.getStartTime().equals(""))
                || (currentTask.getStopDate().equals("") && !currentTask.getStopTime().equals(""))) {
            System.out.println("------------- ERROR 1 " + currentTask.getName() + estimate.getValue());
            return false;
        }

        int score = Integer.parseInt(estimate.getValue());

        // Валидация оценки и дат
        if (!isValidScore(score)) {
            System.out.println("------------- ERROR 2-1");
            return false;
        }

        if (checkDateTimeFormat(currentTask.getStartDate(), "date") == 1 ||  checkDateTimeFormat(currentTask.getStopDate(), "date") == 1) {
            System.out.println("------------- ERROR 2-2");
            return false;
        }


        if (checkDateTimeFormat(currentTask.getStartTime(), "time") == 1 || checkDateTimeFormat(currentTask.getStopTime(), "time") == 1) {
            System.out.println("------------- ERROR 2-3");
            return false;
        }



        // Проверка последовательности дат
        if (!isInvalidInput(currentTask.getStartDate(), currentTask.getStopDate()) && isDateFromAfterDateTo(currentTask.getStartDate(), currentTask.getStopDate())) {
            System.out.println("------------- ERROR 3");
            return false;
        }

        if(!isInvalidInput(currentTask.getStartDate(), currentTask.getStopDate(), currentTask.getStartTime(), currentTask.getStopTime()) && currentTask.getStartDate().equals(currentTask.getStopDate()) && isTimeFromAfterTimeTo(currentTask.getStartTime(), currentTask.getStopTime())) {
            System.out.println("------------- ERROR 4");
            return false;
        }

        // Форматирование дат
        formatDates(currentTask);

        // Сохранение повторяющихся данных
        if (Boolean.TRUE.equals(isTaskRepeat.getValue())) {
            if (checkRepeat())
                return true;
            else
                return false;
        }
        else {
            repeat.setValue(null);
        }
        return true;

    }




    public boolean checkRepeat() {
        Repeat currentRepeat = repeat.getValue();
        int[] flagWeek = flagsWeek.getValue();

        //  Проверка на пустоту ввода: кол-ва повторов, даты окончания повторов, после какого кол-ва повторов заканчивать, дата начала повторов
        if (repeatInterval.getValue().equals("") || (currentRepeat.getStop().isEmpty() && flagSpinnerWhenRepeatEnd.getValue() == 1) || (numOfRepeats.getValue().equals("") && flagSpinnerWhenRepeatEnd.getValue()==2) || currentRepeat.getStart().equals("")) {
            return false;
        }
        //  Если эти поля заполнены, то --- смотрим выбраны ли дни недели, если повторы на неделе
        else {
            if (spinnerRepeatTimeParam.getValue().equals("Неделя") && checkDaysWeek(flagWeek) == 1) {
                System.out.println("---- Ошибка сохранения! Не выбран ни один день недели!");
                return false;
            }
            else {
                // Проверка на кооректность введенной даты
                if ((flagSpinnerWhenRepeatEnd.getValue() == 1 && !checkDateFormat(currentRepeat.getStop())) || (!checkDateFormat(currentRepeat.getStart()))) { // Проверка на дату
                    return false;
                }
                else {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("dd.MM.yyyy");
                    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

                    String end = null;
                    Integer numR = 0;
                    String term = "week";
                    String start = changeDateFormat(currentRepeat.getStart(), inputFormat, outputFormat);
                    ArrayList<Integer> days = getDaysForRepeat(flagWeek);

                    if (spinnerRepeatTimeParam.getValue().equals("Месяц")) term = "month";
                    if (spinnerRepeatTimeParam.getValue().equals("Год")) term = "year";

                    if (flagSpinnerWhenRepeatEnd.getValue() != 0 && !currentRepeat.getStop().equals("")) {
                        end = changeDateFormat(currentRepeat.getStop(), inputFormat, outputFormat);
                        if (flagSpinnerWhenRepeatEnd.getValue() != 1) numR =  parseInt(numOfRepeats.getValue());
                    }

                    int[] arr = new int[days.size()];

                    for (int i = 0; i < days.size(); i++) {
                        arr[i] = days.get(i);
                    }

                    repeat.setValue(new Repeat(parseInt(repeatInterval.getValue()), term, arr, start, end, numR));
                }
            }
        }
        return true;
    }


    protected boolean isInvalidInput(String... fields) {
        for (String field : fields) {
            if (field.isEmpty()) return true;
        }
        return false;
    }

    protected boolean isValidScore(int score) {
        return score >= 1 && score <= 100;
    }

    protected boolean isDateFromAfterDateTo(String dateFrom, String dateTo) {
        DateTimeFormatter dtfDATE = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault());
        LocalDate dateFromLD = LocalDate.parse(dateFrom, dtfDATE);
        LocalDate dateToLD = LocalDate.parse(dateTo, dtfDATE);
        return dateFromLD.isAfter(dateToLD);
    }

    protected boolean isTimeFromAfterTimeTo(String timeFrom, String timeTo) {
        DateTimeFormatter dtfTIME = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());
        LocalTime timeFromLD = LocalTime.parse(timeFrom, dtfTIME);
        LocalTime timeToLD = LocalTime.parse(timeTo, dtfTIME);
        return timeFromLD.isAfter(timeToLD);
    }



    protected int checkDateTimeFormat(String param1, String param2) {
        SimpleDateFormat sdfDATE = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        SimpleDateFormat sdfTIME = new SimpleDateFormat("HH:mm", Locale.getDefault());

        int flag = 0;
        if (!param1.equals("")) {
            if (param2.equals("date")) {
                sdfDATE.setLenient(false);
                try { sdfDATE.parse(param1); }
                catch (ParseException e) { flag = 1; }
            }

            if (param2.equals("time")) {
                sdfTIME.setLenient(false);
                try { sdfTIME.parse(param1); }
                catch (ParseException e) { flag = 1; }
            }
        }
        else flag = 2;

        return flag;
    }


    public int checkDaysWeek(int[] flagsForWeek) {
        int flag = 1;
        for (int i=0; i<7; i++) {
            if (flagsForWeek[i] == 1) {  // Проверка на выбранный день
                flag = 0;
                break;
            }
        }
        return flag;
    }


    protected ArrayList<Integer> getDaysForRepeat(int[] flagWeek) {
        ArrayList<Integer> daysRepeat = new ArrayList<>();
        for (int i =0; i<7; i++) {
            if (flagWeek[i] == 1) {
                daysRepeat.add(i);
            }
        }
        return daysRepeat;
    }

    public static String changeDateFormat(String dateStrWithComma, SimpleDateFormat inputFormat, SimpleDateFormat outputFormat) {
        String formattedDate = "";
        if (dateStrWithComma != null && !dateStrWithComma.equals("") && !dateStrWithComma.equals("null")) {

            try {
                Date date = inputFormat.parse(dateStrWithComma);
                formattedDate = outputFormat.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return formattedDate;
    }

    public void formatDates(Task currentTask) {
        SimpleDateFormat sdfDATE = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        DateFormat formatForDateVariant2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            if (!currentTask.getStartDate().isEmpty()) {
                currentTask.setStartDate(formatForDateVariant2.format(sdfDATE.parse(currentTask.getStartDate())));
            }
            if (!currentTask.getStopDate().isEmpty()) {
                currentTask.setStopDate(formatForDateVariant2.format(sdfDATE.parse(currentTask.getStopDate())));
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkDateFormat(String param) {
        SimpleDateFormat sdfDATE = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        boolean flag = true;
        if (!param.equals("")) {
            sdfDATE.setLenient(false);
            try { sdfDATE.parse(param); }
            catch (ParseException e) { flag = false; }
        }
        return flag;
    }


    public void setInitialDate(int year, int monthOfYear, int dayOfMonth, EditText editDate) {
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

    public void setInitialTime(Context context, EditText editTime, Calendar timeCld) {
        editTime.setText(DateUtils.formatDateTime(context, timeCld.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
    }

    // В ViewModel
    public void handleDateSet(Calendar calendar, EditText editText, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        // Дополнительная логика
        setInitialDate(year, monthOfYear+1, dayOfMonth, editText);
    }


    public void handleTimeSet(Calendar calendar, EditText editText, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        setInitialTime(editText.getContext(), editText, calendar);
    }


}
