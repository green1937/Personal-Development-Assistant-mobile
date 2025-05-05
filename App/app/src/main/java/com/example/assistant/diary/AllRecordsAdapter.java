package com.example.assistant.diary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assistant.R;

import java.util.ArrayList;
import java.util.List;

public class AllRecordsAdapter extends RecyclerView.Adapter<AllRecordsAdapter.MyViewHolder> {

    Context context;
    List<ArrayList<String>> allRecordsData;

    public AllRecordsAdapter(Context context, List<ArrayList<String>> allRecordsData) {
        this.context = context;
        this.allRecordsData = allRecordsData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.all_records_item_view, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textRecordOutput.setText(allRecordsData.get(position).get(0));
        holder.timeRecordOutput.setText(allRecordsData.get(position).get(1));

    }

    @Override
    public int getItemCount() {
        return allRecordsData.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textRecordOutput;
        TextView timeRecordOutput;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textRecordOutput = itemView.findViewById(R.id.textRecordOut);
            timeRecordOutput = itemView.findViewById(R.id.timeRecordOut);
        }

    }

}

