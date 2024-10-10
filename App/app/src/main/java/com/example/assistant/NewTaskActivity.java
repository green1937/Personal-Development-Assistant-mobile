package com.example.assistant;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NewTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        backToOption();  // Возвращение назад

        saveTask();  // Сохранение задачи

        openRepeatSettings();  // Переход на страницу настроек повтора



    }


    /*
        Возвращение назад на страницу выбора типа создания (голосая заметка или задача).
     */
    protected void backToOption() {
        ImageButton backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewTaskActivity.this, OptionToAddNoteActivity.class));
            }
        });
    }


    /*
        Функция, отвечающая за сохранение созданной задачи.

        1. В случае, если ошибок никаких нет, то задача успешно сохраняется,
           и пользователь переходит на главный экран.
        2. В противном случае, задача не сохраняется и пользователь получает сообщение
           об ошибке сохранения.


        ! Пока реализован только переход.
     */
    protected void saveTask() {
        ImageButton saveTaskBtn = findViewById(R.id.tickBtn);
        saveTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewTaskActivity.this, MainActivity.class));
            }
        });
    }


    protected void openRepeatSettings() {
        TextView repeatTaskSettings = findViewById(R.id.repeatTaskText);
        repeatTaskSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewTaskActivity.this, RepeatTaskSettActivity.class));
            }
        });

    }
}
