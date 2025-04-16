/*
    Адаптер для работы с расписанием мероприятий на главном экране
 */

package com.example.assistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.MyViewTimetableHolder> {
    
    private final int TYPE_ITEM1 = 0;
    private final int TYPE_ITEM2 = 1;

    Context context;

    List<ArrayList<String>> eventData;

    public TimetableAdapter(Context context, List<ArrayList<String>> eventData) {
        this.context = context;
        this.eventData = eventData;
    }

    @NonNull
    @Override
    public TimetableAdapter.MyViewTimetableHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case TYPE_ITEM1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timetable_text_view, parent, false);
                break;
            case TYPE_ITEM2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timetable_item_view, parent, false);
        }
        return new TimetableAdapter.MyViewTimetableHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewTimetableHolder holder, int position) {
        int type = getItemViewType(position);

        switch (type) {

            case TYPE_ITEM1:
                holder.dayOfWeekTextOutput.setText(eventData.get(position).get(0)); //День недели
                break;

            case TYPE_ITEM2:
                holder.taskEventTextOutput.setText(eventData.get(position).get(0));
                holder.startTimeEventTextOutput.setText(eventData.get(position).get(1));
                holder.stopTimeEventTextOutput.setText(eventData.get(position).get(2));
                holder.placeEventTextOutput.setText(eventData.get(position).get(3));
                holder.formatEventTextOutput.setText(eventData.get(position).get(4));
                break;
        }




    }

    @Override
    public int getItemCount() {
        return eventData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (eventData.get(position).size() == 1) {
            System.out.println("dayOfWeek " + position);
            return TYPE_ITEM1;
        }
        else {
            System.out.println("events " + position);
            return TYPE_ITEM2;
        }

    }
    public class MyViewTimetableHolder extends RecyclerView.ViewHolder {
        TextView dayOfWeekTextOutput;
        TextView taskEventTextOutput;
        TextView startTimeEventTextOutput;
        TextView stopTimeEventTextOutput;
        TextView placeEventTextOutput;
        TextView formatEventTextOutput;

        public MyViewTimetableHolder(@NonNull View itemView) {
            super(itemView);
            dayOfWeekTextOutput = itemView.findViewById(R.id.dayOfWeekTextOut);
            taskEventTextOutput = itemView.findViewById(R.id.nameEventTextOut);
            startTimeEventTextOutput = itemView.findViewById(R.id.startTimeEventTextOut);
            stopTimeEventTextOutput = itemView.findViewById(R.id.stopTimeEventTextOut);
            placeEventTextOutput = itemView.findViewById(R.id.placeEventTextOut);
            formatEventTextOutput = itemView.findViewById(R.id.formatEventTextOut);
        }

    }

}
