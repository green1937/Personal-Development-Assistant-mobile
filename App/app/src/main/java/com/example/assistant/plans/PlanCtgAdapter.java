package com.example.assistant.plans;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assistant.R;

import java.util.ArrayList;
import java.util.List;

public class PlanCtgAdapter  extends RecyclerView.Adapter<PlanCtgAdapter.MyViewPlanCtgHolder> {

    Context context;

    List<ArrayList<String>> ctgData;

    public PlanCtgAdapter(Context context, List<ArrayList<String>> ctgData) {
        this.context = context;
        this.ctgData = ctgData;
    }

    @NonNull
    @Override
    public PlanCtgAdapter.MyViewPlanCtgHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plan_categories_item_view, parent, false);
        return new PlanCtgAdapter.MyViewPlanCtgHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull PlanCtgAdapter.MyViewPlanCtgHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.ctgNameTextOutput.setText(ctgData.get(position).get(0));
        holder.ctgNameTextOutput.getBackground().setColorFilter(Color.parseColor(ctgData.get(position).get(1)),
                            PorterDuff.Mode.DARKEN);  // Смена цвета кнопки

    }


    @Override
    public int getItemCount() {
        return ctgData.size();
    }

    public class MyViewPlanCtgHolder extends RecyclerView.ViewHolder {
        TextView ctgNameTextOutput;

        public MyViewPlanCtgHolder(@NonNull View itemView) {
            super(itemView);
            ctgNameTextOutput = itemView.findViewById(R.id.ctgNameTextOut);
        }

    }
}
