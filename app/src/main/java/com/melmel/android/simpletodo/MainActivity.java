package com.melmel.android.simpletodo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
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
    ArrayList<Task> tasks = new ArrayList<Task>();
    ArrayAdapter<Task> taskItemsAdapter;
    ListView lvItems;
    int priorityLevel;
    String currentSortBy;
    Boolean viewingCompletedTasks;

    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewingCompletedTasks = false;

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

        final Button viewCompletedButton = (Button) findViewById(R.id.btnViewCompleted);
        viewCompletedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewingCompletedTasks) { // we WERE viewing completed tasks.
                    System.out.println("!!! now view incomplete");
                    //now change to view incomplete
                    readItems();
                    sortTasks();
                    viewingCompletedTasks = false;
                    viewCompletedButton.setText("View Completed");

                } else {
                    System.out.println("!!! now view completed");
                    readCompletedTasks();
                    System.out.println("!!! tasks size"+ tasks.size());
                    taskItemsAdapter.notifyDataSetChanged();
                    viewingCompletedTasks = true;
                    viewCompletedButton.setText("View Incomplete");
                }
            }
        });


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

                        final int position = pos;
                        final Task t = tasks.get(position);
                        /*
                        tasks.remove(pos);
                        sortTasks();
                        Task task = SQLite.select()
                                .from(Task.class)
                                .where(Task_Table.id.eq(t.id))
                                .querySingle();
                        task.delete();

                        Toast.makeText(getApplicationContext(), R.string.item_deleted, Toast.LENGTH_SHORT).show();
                        */


                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                MainActivity.this);

                        // set title
                        alertDialogBuilder.setTitle(t.title);

                        // set dialog message
                        alertDialogBuilder
                                .setCancelable(true)
//                                    .setPositiveButton("Done", new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            t.setStatus(Task.STATUS_COMPLETE);
//                                            t.save();
//                                            readItems();
//                                            sortTasks();
//                                            dialog.dismiss();
//                                        }
//                                    })
                                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        tasks.remove(position);
                                        t.delete();
                                        sortTasks();
                                        dialog.dismiss();
                                    }
                                });

                        if(t.status == Task.STATUS_COMPLETE) {
                            alertDialogBuilder.setPositiveButton("Not done", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            t.setStatus(Task.STATUS_INCOMPLETE);
                                            t.save();
                                            readCompletedTasks();
                                            taskItemsAdapter.notifyDataSetChanged();
                                            dialog.dismiss();
                                        }
                                    });
                        } else {
                            alertDialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    t.setStatus(Task.STATUS_COMPLETE);
                                    t.save();
                                    readItems();
                                    sortTasks();
                                    dialog.dismiss();
                                }
                            });
                        }
                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();
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

                        if (t.status == Task.STATUS_INCOMPLETE) {
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
                        } else {
                            priorityLevelButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.check_icon, null));
                            priorityLevelButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.completedStatusIcon));
                        }
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
                        dialog.setCanceledOnTouchOutside(true);//doesn't work?
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
                t.setStatus(Task.STATUS_INCOMPLETE);
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
        tasks.clear();
        tasks.addAll((ArrayList<Task>)SQLite.select().
//                from(Task.class).queryList();
            from(Task.class).where(Task_Table.status.eq(Task.STATUS_INCOMPLETE)).queryList());
        System.out.println("!!! incomplete tasks: "+tasks.toString());
    }


    private void readCompletedTasks() {
        tasks.clear();
        tasks.addAll((ArrayList<Task>)SQLite.select().
            from(Task.class).where(Task_Table.status.eq(Task.STATUS_COMPLETE)).queryList());
            System.out.println("!!! completed tasks: "+tasks.toString());

    }

    private void writeItems(String taskTitle, String taskDesc, String dueDate) {
        Task newTask = new Task();
        newTask.setTitle(taskTitle);
        newTask.setDescription(taskDesc);
        newTask.setDueDate(dueDate);
        newTask.save();
    }

}
