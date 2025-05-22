package com.example.assistant.viewmodel;

import static java.lang.Integer.parseInt;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.assistant.model.Repeat;
import com.example.assistant.model.Task;
import com.example.assistant.repository.NewObjectRepository;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class NewTaskViewModel extends ViewModel {

    private MutableLiveData<Task> task = new MutableLiveData<>();
    private MutableLiveData<Repeat> repeat = new MutableLiveData<>();
    private MutableLiveData<String> url = new MutableLiveData<>();
    private MutableLiveData<String> estimate = new MutableLiveData<>();

    private MutableLiveData<String> repeatInterval = new MutableLiveData<>();

    private MutableLiveData<String> numOfRepeats = new MutableLiveData<>();

    private MutableLiveData<String> urlCtg = new MutableLiveData<>();
    private MutableLiveData<String> urlPlans = new MutableLiveData<>();

    private NewObjectRepository repository = new NewObjectRepository();
    private MutableLiveData<Boolean> saveResult = new MutableLiveData<>();

    private MutableLiveData<Boolean> isTaskRepeat = new MutableLiveData<>();
    private MutableLiveData<Integer> flagSpinnerWhenRepeatEnd = new MutableLiveData<>();
    private MutableLiveData<int[]> flagsWeek = new MutableLiveData<>();
    private MutableLiveData<String> spinnerRepeatTimeParam = new MutableLiveData<>();






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


    public void setUrl(String urlTask) {
        url.setValue(urlTask);
    }
    public void setUrlCtg(String url1) {
        urlCtg.setValue(url1);
    }
    public void setUrlPlans(String url2) {
        urlPlans.setValue(url2);
    }

    public NewTaskViewModel() {
        repeat.setValue(new Repeat(0,"", null, "", "", 0));
        task.setValue(new Task("", "", 0, null, "", "", "", "", null, repeat.getValue()));
    }

    public void onSaveBtnClicked() {
        if (isInputDataValid()) {
            System.out.println("SUCCESS!");
            saveNewTask();
        }
        else
            System.out.println("NO :(");
    }


    public void saveNewTask() {
        if (Boolean.FALSE.equals(isTaskRepeat.getValue())) repeat.setValue(null);
        System.out.println("SAVE  ---- task   ---------  " + new Gson().toJson(task.getValue()));
        System.out.println("SAVE  ---- repeat   ---------  " + new Gson().toJson(repeat.getValue()));



    }





    public boolean isInputDataValid() {
        System.out.println("start CLICK!");


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
        if (!isValidScore(score) || (checkDateTimeFormat(currentTask.getStartDate(), "date") == 1 ||  checkDateTimeFormat(currentTask.getStopDate(), "date") == 1
                || checkDateTimeFormat(currentTask.getStartTime(), "time") == 1 || checkDateTimeFormat(currentTask.getStopTime(), "time") == 1)) {
            System.out.println("------------- ERROR 2");
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
        System.out.println("------------- FORAMTED DATE");


        // Сохранение повторяющихся данных
        if (Boolean.TRUE.equals(isTaskRepeat.getValue())) {
            System.out.println("------------- REPEAT YES!");
            if (checkRepeat()) {
                System.out.println("NEW REPEAT 2  ---------  " + new Gson().toJson(repeat.getValue()));
                return true;
            }
            else {
                return false;
            }
        }
        return true;

    }




    public boolean checkRepeat() {
        Repeat currentRepeat = repeat.getValue();
        int[] flagWeek = flagsWeek.getValue();

        //  Проверка на пустоту ввода: кол-ва повторов, даты окончания повторов, после какого кол-ва повторов заканчивать, дата начала повторов
        if (repeatInterval.getValue().equals("") || (currentRepeat.getStop().equals("") && flagSpinnerWhenRepeatEnd.getValue() == 1) || (numOfRepeats.getValue().equals("") && flagSpinnerWhenRepeatEnd.getValue()==2) || currentRepeat.getStart().equals("")) {
            return false;
        }
        //  Если эти поля заполнены, то --- смотрим выбраны ли дни недели, если повторы на неделе
        else {
            if (spinnerRepeatTimeParam.getValue().equals("Неделя") && checkDaysWeek(flagWeek) == 1) {
                System.out.println("CHECK NEWTASK VIEWMODEL       ---- Ошибка сохранения! Не выбран ни один день недели!");
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

                    if (flagSpinnerWhenRepeatEnd.getValue() != 0) {
                        end = changeDateFormat(currentRepeat.getStop(), inputFormat, outputFormat);
                        if (flagSpinnerWhenRepeatEnd.getValue() != 1) numR =  parseInt(numOfRepeats.getValue());
                    }

                    int[] arr = new int[days.size()];

                    for (int i = 0; i < days.size(); i++) {
                        arr[i] = days.get(i);
                    }

                    repeat.setValue(new Repeat(parseInt(repeatInterval.getValue()), term, arr, start, end, numR));
                    System.out.println("NEW REPEAT 1  ---------  " + new Gson().toJson(repeat.getValue()));
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

        try {
            Date date = inputFormat.parse(dateStrWithComma);
            formattedDate = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
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

}
