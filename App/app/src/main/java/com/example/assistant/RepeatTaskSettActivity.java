package com.example.assistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RepeatTaskSettActivity extends AppCompatActivity {
    String[] paramRS = { "Неделя", "Месяц", "Год"};
    String itemRS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeat_task);

        backToNewTask();  // Переход обратно на страни

        saveRepeatSettings();  // Сохранение настроек повтора задачи

        spinnerRepeat();  // Вывод данных в выпадающем списке


    }


    protected void backToNewTask() {
        ImageButton backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RepeatTaskSettActivity.this, NewTaskActivity.class));
            }
        });
    }

    protected void saveRepeatSettings() {
        ImageButton saveBtn = findViewById(R.id.tickBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RepeatTaskSettActivity.this, NewTaskActivity.class));
            }
        });
    }


    /*
        Вывод данных в выпадающем списке (неделя, месяц, год)
     */
    protected void spinnerRepeat() {
        Spinner repeatSpinner = findViewById(R.id.repeatSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, paramRS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeatSpinner.setAdapter(adapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Получаем выбранный объект
                itemRS = (String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        repeatSpinner.setOnItemSelectedListener(itemSelectedListener);
    }
}

