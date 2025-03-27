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

    Context context;
    List<ArrayList<String>> eventData;

    public TimetableAdapter(Context context, List<ArrayList<String>> eventData) {
        this.context = context;
        this.eventData = eventData;
    }

    @NonNull
    @Override
    public TimetableAdapter.MyViewTimetableHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TimetableAdapter.MyViewTimetableHolder(LayoutInflater.from(context).inflate(R.layout.timetable_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TimetableAdapter.MyViewTimetableHolder holder, int position) {

        holder.taskEventTextOutput.setText(eventData.get(position).get(0));
        holder.startTimeEventTextOutput.setText(eventData.get(position).get(1));
        holder.stopTimeEventTextOutput.setText(eventData.get(position).get(2));
        holder.placeEventTextOutput.setText(eventData.get(position).get(3));
        holder.formatEventTextOutput.setText(eventData.get(position).get(4));


    }

    @Override
    public int getItemCount() {
        return eventData.size();
    }

    public class MyViewTimetableHolder extends RecyclerView.ViewHolder {
        TextView taskEventTextOutput;
        TextView startTimeEventTextOutput;
        TextView stopTimeEventTextOutput;
        TextView placeEventTextOutput;
        TextView formatEventTextOutput;

        public MyViewTimetableHolder(@NonNull View itemView) {
            super(itemView);
            taskEventTextOutput = itemView.findViewById(R.id.nameEventTextOut);
            startTimeEventTextOutput = itemView.findViewById(R.id.startTimeEventTextOut);
            stopTimeEventTextOutput = itemView.findViewById(R.id.stopTimeEventTextOut);
            placeEventTextOutput = itemView.findViewById(R.id.placeEventTextOut);
            formatEventTextOutput = itemView.findViewById(R.id.formatEventTextOut);
        }

    }

}
