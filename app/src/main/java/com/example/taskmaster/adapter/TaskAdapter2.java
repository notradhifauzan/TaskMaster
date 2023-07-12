package com.example.taskmaster.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmaster.R;
import com.example.taskmaster.adminFragments.AdminNavBarActivity;
import com.example.taskmaster.fragments.BottomNavBar;
import com.example.taskmaster.model.SharedPrefManager;
import com.example.taskmaster.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter2 extends RecyclerView.Adapter<TaskAdapter2.ViewHolder> implements Filterable {

    @Override
    public TaskAdapter2.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflate the single item layout
        View view = inflater.inflate(R.layout.rv_taskview, parent, false);

        // return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    public Task getSelectedItem() {
        if (currentPos >= 0 && taskList != null && currentPos < taskList.size()) {
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
        holder.assignedTo.setText("test test");

        // check user condition to manipulate 'assigned to' view on viewholder
        String userRole = SharedPrefManager.getInstance(getContext()).getUser().getRole();
        if (userRole.equalsIgnoreCase("user") ||
                userRole.equalsIgnoreCase("agent")) {
            holder.assignedTo.setVisibility(View.GONE);
        } else {
            // check job condition
            if (t.getStatus().equalsIgnoreCase("assigned")) {
                holder.assignedTo.setText("Assigned to - " + t.getAgent_name());
            } else if (t.getStatus().equalsIgnoreCase("completed")) {
                holder.assignedTo.setText("Completed by - " + t.getAgent_name());
            } else {
                holder.assignedTo.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                if (charSequence == null || charSequence.length() == 0) {
                    filterResults.values = taskListFilter;
                    filterResults.count = taskListFilter.size();
                } else {
                    String searchStr = charSequence.toString().toLowerCase();
                    List<Task> taskModels = new ArrayList<>();
                    for (Task taskModel : taskModels) {
                        if (taskModel.getJob_domain().toLowerCase().contains(searchStr) ||
                                taskModel.getJob_title().toLowerCase().contains(searchStr)) {
                            taskModels.add(taskModel);
                        }
                    }

                    filterResults.values = taskModels;
                    filterResults.count = taskModels.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                taskList = (List<Task>) filterResults.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView tvTitle;
        public TextView tvDomain;
        public TextView tvPrice;
        public TextView tvDate;
        public TextView tvTime;
        public TextView assignedTo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDomain = itemView.findViewById(R.id.tvDomain);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            assignedTo = itemView.findViewById(R.id.assignedToTextView);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            currentPos = getAdapterPosition();
            return false;
        }
    }

    private List<Task> taskList;
    private List<Task> taskListFilter;
    private Context context;
    private int currentPos;

    public TaskAdapter2(Context context, List<Task> listData) {
        taskList = listData;
        this.context = context;
        this.taskListFilter = listData;
    }

    private Context getContext() {
        return context;
    }


}
