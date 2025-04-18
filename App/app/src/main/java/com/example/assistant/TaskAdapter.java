package com.example.assistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {

    private final int TYPE_ITEM1 = 0;
    private final int TYPE_ITEM2 = 1;

    Context context;
    List<ArrayList<String>> taskData;

    public TaskAdapter(Context context, List<ArrayList<String>> taskData) {
        this.context = context;
        this.taskData = taskData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case TYPE_ITEM1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_text_view, parent, false);
                break;
            case TYPE_ITEM2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_view, parent, false);
        }
        return new TaskAdapter.MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        int type = getItemViewType(position);

        switch (type) {

            case TYPE_ITEM1:
                holder.tasksGroupTextOutput.setText(taskData.get(position).get(0)); //День недели
                break;

            case TYPE_ITEM2:
                holder.taskNameTextOutput.setText(taskData.get(position).get(0));
                break;
        }

        /*
            Переход на экран редактирвоания и просмотра всей задачи с полями:название, категория, дата, время, описание и прочее.

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditTaskActivity.class);
                intent.putExtra("name", name);  //?
                //Для остальных переменных также
                context.startActivity(intent);
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return taskData.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (taskData.get(position).size() == 1) {
            return TYPE_ITEM1;
        }
        else {
            return TYPE_ITEM2;
        }

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tasksGroupTextOutput;
        TextView taskNameTextOutput;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tasksGroupTextOutput = itemView.findViewById(R.id.tasksGroupTextOut);
            taskNameTextOutput = itemView.findViewById(R.id.taskNameTextOut);
        }

    }

}
