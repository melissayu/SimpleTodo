package com.melmel.android.simpletodo;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {
    ArrayList<Task> tasks;
    ArrayAdapter<Task> taskItemsAdapter;
    ListView lvItems;

    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvItems = (ListView)findViewById(R.id.lvItems);

        readItems();

        taskItemsAdapter = new TaskAdapter(this, tasks);
        lvItems.setAdapter(taskItemsAdapter);

        setupListViewListener();
    }

    private void setupListViewListener() {

        // On long click, delete item
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id) {
                        Task t = tasks.get(pos);
                        tasks.remove(pos);
                        taskItemsAdapter.notifyDataSetChanged();
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
                                t.save();
                                tasks.set(position, t);
                                taskItemsAdapter.notifyDataSetChanged();
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

    private Date parseDateString(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        sdf.setLenient(false);
        Date parsedDate = new Date();
        try {
            parsedDate = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsedDate;
    }
    private String datePickerToString(DatePicker datePicker){
        int   day  = datePicker.getDayOfMonth();
        int   month= datePicker.getMonth();
        int   year = datePicker.getYear();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        Date dateRepresentation = cal.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        String formattedDate = sdf.format(dateRepresentation);

        return formattedDate;
    }

    public void onAddItem(View v) {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_edit);
        dialog.setTitle("Add New Task");

        final EditText titleText = (EditText) dialog.findViewById(R.id.dialogEditTitle);
        final EditText descText = (EditText) dialog.findViewById(R.id.dialogEditDesc);
        final DatePicker dueDate = (DatePicker) dialog.findViewById(R.id.dialogDatePicker);

        Button dialogButton = (Button) dialog.findViewById(R.id.dialogSave);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskTitle = titleText.getText().toString();
                String taskDesc = descText.getText().toString();
                Task t = new Task();
                t.setTitle(taskTitle);
                t.setDescription(taskDesc);
                t.setDueDate(datePickerToString(dueDate));
                t.save();
                tasks.add(t);
                taskItemsAdapter.notifyDataSetChanged();
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
