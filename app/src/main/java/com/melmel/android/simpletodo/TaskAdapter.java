package com.melmel.android.simpletodo;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by melmel on 2/14/2017.
 */

public class TaskAdapter extends ArrayAdapter<Task> {
    private static class ViewHolder {
        private TextView itemView;
    }

    public TaskAdapter(Context context, ArrayList<Task> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Task task = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_task, parent, false);
        }
        // Lookup view for data population
        TextView taskTitle = (TextView) convertView.findViewById(R.id.taskTitle);
        TextView taskDesc = (TextView) convertView.findViewById(R.id.taskDesc);
        TextView taskDueDate = (TextView) convertView.findViewById(R.id.due_date);
        ImageView taskPriorityIcon = (ImageView) convertView.findViewById(R.id.taskPriority);

        // Populate the data into the template view using the data object

        taskDesc.setText(task.description);
        taskTitle.setText(task.title);
        taskDueDate.setText(task.dueDate);
        updatePriorityImage(taskPriorityIcon, task.priority);
        // Return the completed view to render on screen
        return convertView;
    }

    private void updatePriorityImage(ImageView priorityIcon, int priority) {
        if (priority == Task.PRIORITY_LOW) priorityIcon.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lowPriorityIcon));
        else if (priority == Task.PRIORITY_MEDIUM) priorityIcon.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.medPriorityIcon));
        else if (priority == Task.PRIORITY_HIGH) priorityIcon.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.highPriorityIcon));
        else priorityIcon.setImageResource(android.R.drawable.btn_star);
    }


}
