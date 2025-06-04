package com.example.assistant.ui.plans.viewmodel;

import static java.lang.Integer.parseInt;
import android.widget.EditText;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.assistant.ui.plans.model.Plan;
import com.example.assistant.repository.GetDataRepository;
import com.example.assistant.repository.NewObjectRepository;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditPlanViewModel extends ViewModel {

    private MutableLiveData<Plan> plan = new MutableLiveData<>();
    private MutableLiveData<String> url = new MutableLiveData<>();
    private MutableLiveData<Integer> id = new MutableLiveData<>();
    private GetDataRepository getDataRepository = new GetDataRepository();

    private MutableLiveData<String> score = new MutableLiveData<>();
    private MutableLiveData<List<ArrayList<String>>> categories = new MutableLiveData<>();
    private MutableLiveData<List<ArrayList<String>>> tasks = new MutableLiveData<>();
    private NewObjectRepository repository = new NewObjectRepository();
    private MutableLiveData<Boolean> saveResult = new MutableLiveData<>();

    private MutableLiveData<String> getResult = new MutableLiveData<>();


    public LiveData<Plan> getPlan() {
        return plan;
    }

    public void setPlanName(String planName) {
        Plan currentPlan = plan.getValue();
        if (currentPlan != null) {
            currentPlan.setName(planName);
            plan.setValue(currentPlan);
        }
    }

    public String getPlanName() {
        Plan currentPlan = plan.getValue();
        return currentPlan.getName();
    }

    public void setPlanDetails(String planDetails) {
        Plan currentPlan = plan.getValue();
        if (currentPlan != null) {
            currentPlan.setDetails(planDetails);
            plan.setValue(currentPlan);
        }
    }

    public String getPlanDetails() {
        Plan currentPlan = plan.getValue();
        return currentPlan.getDetails();
    }


    public void setStartDate(String startDate) {
        Plan currentPlan = plan.getValue();
        if (currentPlan != null) {
            currentPlan.setStartDate(startDate);
            plan.setValue(currentPlan);
        }
    }

    public String getStartDate() {
        Plan currentPlan = plan.getValue();
        return changeDateFormat(currentPlan.getStartDate(), "from server");
    }

    public void setStopDate(String stopDate) {
        Plan currentPlan = plan.getValue();
        if (currentPlan != null) {
            currentPlan.setStopDate(stopDate);
            plan.setValue(currentPlan);
        }
    }

    public String getStopDate() {
        Plan currentPlan = plan.getValue();
        return changeDateFormat(currentPlan.getStopDate(), "from server");
    }

    public void setStatus(int status) {
        Plan currentPlan = plan.getValue();
        if (currentPlan != null) {
            currentPlan.setStatus(status);
            plan.setValue(currentPlan);
        }
    }

    public void setUrl(String urlPlan) {
        url.setValue(urlPlan);
    }

    //Получаем id плана
    public void setIdEditPlan(int idPlan) {
        id.setValue(idPlan);
    }

    public String getScore() {
        return score.getValue();
    }

    public  List<ArrayList<String>> getCategories() {
        return categories.getValue();
    }

    public  List<ArrayList<String>> getTasks() {
        return tasks.getValue();
    }
    public EditPlanViewModel() {
        plan.setValue(new Plan(0, 0, "", "", "", "", 0));
    }

    public void onSaveBtnClicked() {
        if (isInputDataValid()) {
            System.out.println("SUCCESS!");
            saveEditPlan();
        }
        else
            System.out.println("NO :(");
    }

    public boolean isInputDataValid() {
        Plan currentPlan = plan.getValue();
        return currentPlan != null &&
                !currentPlan.getName().isEmpty() &&
                !currentPlan.getDetails().isEmpty() &&
                !currentPlan.getStartDate().isEmpty() &&
                !currentPlan.getStopDate().isEmpty() &&
                checkDateFormat(currentPlan.getStartDate()) &&
                checkDateFormat(currentPlan.getStopDate()) &&
                !isDateStartAfterDateStop(currentPlan.getStartDate(), currentPlan.getStopDate());
    }


    public LiveData<Boolean> getSaveResult() {
        return saveResult;
    }
    public void saveEditPlan() {
        ArrayList<String> allJsonData = new ArrayList<>();
        Plan currentPlan = plan.getValue();
        plan.setValue(new Plan(id.getValue(), 1, currentPlan.getName(),
                currentPlan.getDetails(),
                changeDateFormat(currentPlan.getStartDate(), "to server"),
                changeDateFormat(currentPlan.getStopDate(), "to server"),
                currentPlan.getStatus()));
        allJsonData.add(new Gson().toJson(plan.getValue()));

        repository.sendDataObj(allJsonData, url.getValue(), "PUT").
                observeForever(result -> saveResult.setValue(result));
    }

    public String changeDateFormat(String dateStr, String param) {
        SimpleDateFormat inputFormat;
        SimpleDateFormat outputFormat;
        if (param.equals("from server")) {
            inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            outputFormat = new SimpleDateFormat("dd.MM.yyyy");
        }
        else {
            inputFormat = new SimpleDateFormat("dd.MM.yyyy");
            outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        }
        String formattedDate = "";

        try {
            Date date = inputFormat.parse(dateStr);
            formattedDate = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formattedDate;
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

    public boolean isDateStartAfterDateStop(String start, String stop) {
        DateTimeFormatter dtfDATE = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault());
        LocalDate dateFromLD = LocalDate.parse(start, dtfDATE);
        LocalDate dateToLD = LocalDate.parse(stop, dtfDATE);
        return dateFromLD.isAfter(dateToLD);
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



    /*
        Загрузка данных редактируемого (выбранного) плана
     */
    public void loadPlanData() {
        String editPlanUrl = url.getValue() + "/" + id.getValue();
        getDataRepository.getDataObj(editPlanUrl).observeForever(result -> {
            getResult.setValue(result);
        });
    }


    /*
        Передача данных плана в модель
     */
    public void setPlan() {
        String json = getResult.getValue();

        try {
            JSONObject jsonObject = new JSONObject(json);

            setPlanName(jsonObject.getString("name"));
            setPlanDetails(jsonObject.getString("details"));
            setStatus(parseInt(jsonObject.getString("status")));
            setStartDate(jsonObject.getString("start_date"));
            setStopDate(jsonObject.getString("stop_date"));
            score.setValue(jsonObject.getString("done_points") + " / " + jsonObject.getString("goal_points"));

            //categories
            List<ArrayList<String>> array = new ArrayList<>();
            JSONArray jsonArrayCtg = jsonObject.getJSONArray("categories");
            for (int i = 0; i < jsonArrayCtg.length(); i++) {
                JSONObject ctgData = jsonArrayCtg.getJSONObject(i);
                ArrayList<String> oneCtg = new ArrayList<>();
                oneCtg.add(ctgData.getString("title"));
                oneCtg.add(ctgData.getString("color"));
                array.add(oneCtg);
            }
            categories.setValue(array);

            //tasks
            array = new ArrayList<>();
            JSONArray jsonArrayTasks = jsonObject.getJSONArray("tasks");
            for (int i = 0; i < jsonArrayTasks.length(); i++) {
                JSONObject taskData = jsonArrayTasks.getJSONObject(i);
                ArrayList<String> oneTask = new ArrayList<>();
                oneTask.add(taskData.getString("id"));
                oneTask.add(taskData.getString("name"));
                oneTask.add(taskData.getString("status"));
                array.add(oneTask);
            }
            tasks.setValue(array);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public LiveData<String> getResult() {
        return getResult;
    }


}
