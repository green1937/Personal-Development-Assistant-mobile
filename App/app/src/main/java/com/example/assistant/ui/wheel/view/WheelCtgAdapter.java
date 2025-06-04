package com.example.assistant.ui.wheel.view;

import static java.lang.Integer.parseInt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
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

    int flag = 3;

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

        /*if (ctgData.get(position).get(3).equals("1")) {
            holder.ctgStatus.setChecked(true);
        }*/
        holder.ctgNameTextOutput.setText(ctgData.get(position).get(0));
        holder.ctgColorView.getBackground().setColorFilter(Color.parseColor(ctgData.get(position).get(1)),
                PorterDuff.Mode.DARKEN);

        /*
        // Добавляем обработчик для CheckBox
        holder.ctgStatus.setOnClickListener(v -> {
            if (listener != null) {
                boolean isChecked = holder.ctgStatus.isChecked();
                listener.onStatusChanged(position, isChecked);
            }
        });

        //  Смена статуса у категории (отображение чекбокс),
        //  отправка в активити обновление статуса отображения категории на колесе баланса

        flag = 3;  // Для статуса
        holder.ctgStatus.setOnClickListener(v -> {
            if (ctgData.get(position).get(3).equals("0") && holder.ctgStatus.isChecked()) {
                flag = 1;
            }
            if (ctgData.get(position).get(3).equals("1") && !holder.ctgStatus.isChecked()) {
                flag = 0;
            }

            //if (flag==0 || flag==1) {
                //sendUpdate(position, flag);
                //update page
                //Intent intent2 = new Intent(context, Class.forName(activityName));
                //context.startActivity(intent2);
            //}
        });
         */

    }


    @Override
    public int getItemCount() {
        return ctgData.size();
    }


    protected void sendUpdate(int position, int flag) {
        ArrayList<Integer> statusData = new ArrayList<>();
        statusData.add(position);
        statusData.add(flag);
        Intent intent = new Intent(context, WheelActivity.class);
        intent.putExtra("status", statusData);
        context.startActivity(intent);
    }


    public class WheelHolder extends RecyclerView.ViewHolder {
        View ctgColorView;
        TextView ctgNameTextOutput;

        CheckBox ctgStatus;

        public WheelHolder(@NonNull View itemView) {
            super(itemView);
            ctgNameTextOutput = itemView.findViewById(R.id.ctgNameTextOut);
            ctgColorView = itemView.findViewById(R.id.ctgColorView);
            ctgStatus = itemView.findViewById(R.id.checkBoxCtg);
        }

    }

}

