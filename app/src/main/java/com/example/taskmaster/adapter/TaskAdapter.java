package com.example.taskmaster.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmaster.R;
import com.example.taskmaster.model.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflate the single item layout
        View view = inflater.inflate(R.layout.rv_taskview,parent,false);

        // return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    public Task getSelectedItem() {
        if(currentPos >=0 && taskList!=null && currentPos<taskList.size()) {
            return taskList.get(currentPos);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // bind data to the view holder
        Task t = taskList.get(position);
        holder.tvTime.setText(t.getDue_time());
        holder.tvDate.setText(t.getDue_date());
        holder.tvDomain.setText(t.getJob_domain());
        holder.tvPrice.setText(String.format("RM %.2f", t.getBudget()));
        holder.tvTitle.setText(t.getJob_title());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView tvTitle;
        public TextView tvDomain;
        public TextView tvPrice;
        public TextView tvDate;
        public TextView tvTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDomain = itemView.findViewById(R.id.tvDomain);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            currentPos = getAdapterPosition();
            return false;
        }
    }

    private List<Task> taskList;
    private Context context;
    private int currentPos;

    public TaskAdapter(Context context,List<Task> listData) {
        taskList = listData;
        this.context = context;
    }

    private Context getContext() {return context;}


}
