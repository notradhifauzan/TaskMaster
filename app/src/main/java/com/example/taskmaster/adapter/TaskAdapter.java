package com.example.taskmaster.adapter;

import android.content.Context;
import android.view.ContentInfo;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmaster.R;
import com.example.taskmaster.model.Task;

import java.util.ArrayList;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {

    //setup 2 variable here
    Context context;
    ArrayList<Task> taskModels;
    private int currentPos;

    public TaskAdapter (Context context, ArrayList<Task> taskModels){
        this.context = context;
        this.taskModels = taskModels;
    }

    @NonNull
    @Override
    public TaskAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_available_task, parent, false);

        return new TaskAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.MyViewHolder holder, int position) {

        holder.tvTitle.setText(taskModels.get(position).getJob_title());
        holder.tvDomain.setText(taskModels.get(position).getJob_domain());
        holder.tvPrice.setText( "RM "+taskModels.get(position).getBudget());
        holder.tvDate.setText(taskModels.get(position).getDue_date());
        holder.tvTime.setText(taskModels.get(position).getDue_time());
    }

    @Override
    public int getItemCount() {
        return taskModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        //Act like OnCreate method
        TextView tvTitle, tvDomain, tvPrice, tvDate, tvTime ;
        CardView cv;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDomain = itemView.findViewById(R.id.tvDomain);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            cv = itemView.findViewById(R.id.task_cardView);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            currentPos = getAdapterPosition();
            return false;
        }
    }

    public Task getSelectedItem() {
        if(currentPos >= 0 && taskModels!=null && currentPos<taskModels.size()) {
            return taskModels.get(currentPos);
        }
        return null;
    }
}
