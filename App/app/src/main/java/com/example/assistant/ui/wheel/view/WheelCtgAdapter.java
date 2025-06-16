package com.example.assistant.ui.wheel.view;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assistant.R;

import java.util.ArrayList;
import java.util.List;

public class WheelCtgAdapter  extends RecyclerView.Adapter<WheelCtgAdapter.WheelHolder> {

    Context context;
    List<ArrayList<String>> ctgData;

    public WheelCtgAdapter(Context context, List<ArrayList<String>> ctgData) {
        this.context = context;
        this.ctgData = ctgData;
    }

    // Добавляем интерфейс для обработки изменений
    public interface OnWheelStatusChangeListener {
        void onStatusChanged(int position, boolean isChecked);
    }

    private WheelCtgAdapter.OnWheelStatusChangeListener listener;

    public void setOnWheelStatusChangeListener(WheelCtgAdapter.OnWheelStatusChangeListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public WheelCtgAdapter.WheelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ctg_wheel_item_view, parent, false);
        return new WheelCtgAdapter.WheelHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull WheelCtgAdapter.WheelHolder holder, int position) {
        holder.ctgNameTextOutput.setText(ctgData.get(position).get(0));
        holder.ctgColorView.setBackgroundColor(Color.parseColor(ctgData.get(position).get(1)));
    }


    @Override
    public int getItemCount() {
        return ctgData.size();
    }



    public class WheelHolder extends RecyclerView.ViewHolder {
        View ctgColorView;
        TextView ctgNameTextOutput;

        public WheelHolder(@NonNull View itemView) {
            super(itemView);
            ctgNameTextOutput = itemView.findViewById(R.id.ctgNameTextOut);
            ctgColorView = itemView.findViewById(R.id.ctgColorView);

        }

    }

}

