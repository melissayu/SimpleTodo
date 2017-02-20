package com.melmel.android.simpletodo;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static com.melmel.android.simpletodo.Task.datePickerToString;
import static com.melmel.android.simpletodo.Task.parseDateString;
import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ArrayList<Task> tasks;
    ArrayAdapter<Task> taskItemsAdapter;
    ListView lvItems;
    int priorityLevel;
    String currentSortBy;

    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvItems = (ListView)findViewById(R.id.lvItems);
        lvItems.setEmptyView(findViewById(R.id.empty_list_view));

        readItems();

        taskItemsAdapter = new TaskAdapter(this, tasks);
        lvItems.setAdapter(taskItemsAdapter);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        List<String> sortBy = new ArrayList<String>();
        sortBy.add("Due Date");
        sortBy.add("Priority");
        sortBy.add("Name");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sortBy);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);
        currentSortBy = sortBy.get(0);

        setupListViewListener();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        currentSortBy = item;
        sortTasks();
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    private void sortTasks() {
        if (currentSortBy == "Due Date") {
            Collections.sort(tasks, new Comparator<Task>() {
                @Override
                public int compare(Task o1, Task o2) {
                    return Task.parseDateString(o1.dueDate).compareTo(Task.parseDateString(o2.dueDate));
                }
            });
        }
        else if (currentSortBy == "Priority") {
            Collections.sort(tasks, new Comparator<Task>() {
                @Override
                public int compare(Task o1, Task o2) {
                    return o2.priority - o1.priority;
                }
            });
        }
        else if (currentSortBy == "Name") {
            Collections.sort(tasks, new Comparator<Task>() {
                @Override
                public int compare(Task o1, Task o2) {
                    return o1.title.compareTo(o2.title);
                }
            });
        }
        taskItemsAdapter.notifyDataSetChanged();
    }

    private void setupListViewListener() {

        // On long click, delete item
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id) {
                        Task t = tasks.get(pos);
                        tasks.remove(pos);
//                        taskItemsAdapter.notifyDataSetChanged();
                        sortTasks();
                        Task task = SQLite.select()
                                .from(Task.class)
                                .where(Task_Table.description.eq(t.description))
                                .querySingle();
                        task.delete();

                        Toast.makeText(getApplicationContext(), R.string.item_deleted, Toast.LENGTH_SHORT).show();

                        return true;
                    }
                }
        );

        // On short click, open edit page
        lvItems.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View item, int pos, long id) {


                        /* This is support for edit screen to be a new activity instead of dialog.
                        Intent i = new Intent(MainActivity.this, EditActivity.class);
                        i.putExtra("pos", pos);
                        String listItem = items.get(pos);
                        i.putExtra("item", listItem);
                        startActivityForResult(i, REQUEST_CODE);
                        */

                        /* This is support for edit screen to be a dialog instead of new screen. */

                        final Dialog dialog = new Dialog(MainActivity.this);
                        dialog.setContentView(R.layout.dialog_edit);
                        dialog.setTitle("Edit Task");
                        final int position = pos;
                        Task t = tasks.get(position);

                        final EditText titleText = (EditText) dialog.findViewById(R.id.dialogEditTitle);
                        titleText.setText(t.title);
                        final EditText descText = (EditText) dialog.findViewById(R.id.dialogEditDesc);
                        descText.setText(t.description);

                        final DatePicker dueDate = (DatePicker) dialog.findViewById(R.id.dialogDatePicker);
                        String dateString = t.dueDate;
                        if (dateString != null) {
                            Date parsedDate = parseDateString(dateString);
                            Calendar calendar = new GregorianCalendar();
                            calendar.setTime(parsedDate);
                            dueDate.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                        }

                        final ImageButton priorityLevelButton = (ImageButton) dialog.findViewById(R.id.dialogPriority);

                        //If no priority had been set, default to LOW
                        priorityLevel = t.priority == 0 ? Task.PRIORITY_LOW : t.priority;
                        updatePriorityImage(priorityLevelButton);

                        priorityLevelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                changePriorityLevel();
                                updatePriorityImage(priorityLevelButton);
                            }
                        });

                        Button dialogButton = (Button) dialog.findViewById(R.id.dialogSave);
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String taskTitle = titleText.getText().toString();
                                String taskDesc = descText.getText().toString();
                                String taskDueDate = datePickerToString(dueDate);

                                Task t = tasks.get(position);
                                t.setTitle(taskTitle);
                                t.setDescription(taskDesc);
                                t.setDueDate(taskDueDate);
                                t.setPriority(priorityLevel);
                                t.save();
                                tasks.set(position, t);
//                                taskItemsAdapter.notifyDataSetChanged();
                                sortTasks();
                                dialog.dismiss();
                            }
                        });

                        dialog.show();

                        return;
                    }
                }
        );

    }

/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {

            String item = data.getExtras().getString("item");
            int pos = data.getExtras().getInt("pos", 0);

            items.set(pos, item);
            taskItemsAdapter.notifyDataSetChanged();
            writeItems(item);

//            Toast.makeText(this, item, Toast.LENGTH_SHORT).show();
        }
    }
*/

    private void changePriorityLevel() {
        if (priorityLevel == Task.PRIORITY_LOW) {
            priorityLevel =  Task.PRIORITY_MEDIUM;
        } else if (priorityLevel == Task.PRIORITY_MEDIUM) {
            priorityLevel = Task.PRIORITY_HIGH;
        } else if (priorityLevel == Task.PRIORITY_HIGH) {
            priorityLevel = Task.PRIORITY_LOW;
        } else {
            priorityLevel = Task.PRIORITY_LOW; //If no value or invalid value was set, default to LOW
        }
    }

    private void updatePriorityImage(ImageButton priorityButton) {
        if (priorityLevel == Task.PRIORITY_LOW) priorityButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lowPriorityIcon));
        else if (priorityLevel == Task.PRIORITY_MEDIUM) priorityButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.medPriorityIcon));
        else if (priorityLevel == Task.PRIORITY_HIGH) priorityButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.highPriorityIcon));
        else priorityButton.setImageResource(android.R.drawable.btn_star);
    }

    public void onAddItem(View v) {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_edit);
        dialog.setTitle("Add New Task");

        final EditText titleText = (EditText) dialog.findViewById(R.id.dialogEditTitle);
        final EditText descText = (EditText) dialog.findViewById(R.id.dialogEditDesc);
        final DatePicker dueDate = (DatePicker) dialog.findViewById(R.id.dialogDatePicker);
        final ImageButton priorityLevelButton = (ImageButton) dialog.findViewById(R.id.dialogPriority);
        priorityLevel = Task.PRIORITY_LOW;
        updatePriorityImage(priorityLevelButton);

        priorityLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePriorityLevel();
                updatePriorityImage(priorityLevelButton);
            }
        });

        Button dialogButton = (Button) dialog.findViewById(R.id.dialogSave);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskTitle = titleText.getText().toString();
                String taskDesc = descText.getText().toString();
                Task t = new Task();
                t.setTitle(taskTitle);
                t.setDescription(taskDesc);
                t.setDueDate(Task.datePickerToString(dueDate));
                t.setPriority(priorityLevel);
                t.save();
                tasks.add(t);
//                taskItemsAdapter.notifyDataSetChanged();
                sortTasks();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void readItems() {
        tasks = (ArrayList<Task>)SQLite.select().
                from(Task.class).queryList();
//        tasks = (ArrayList<Task>)SQLite.select().
//                from(Task.class).orderBy(Task_Table.priority, true).queryList();

    }

    private void writeItems(String taskTitle, String taskDesc, String dueDate) {
        Task newTask = new Task();
        newTask.setTitle(taskTitle);
        newTask.setDescription(taskDesc);
        newTask.setDueDate(dueDate);
        newTask.save();
    }


}
