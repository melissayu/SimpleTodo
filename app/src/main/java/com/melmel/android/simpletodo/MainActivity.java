package com.melmel.android.simpletodo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;

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
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        final EditText input = new EditText(MainActivity.this);
                        final int position = pos;
                        Task t = tasks.get(position);
                        input.setText(t.toString());
                        builder.setTitle(R.string.edit_item)
                                .setView(input)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        String taskDesc = input.getText().toString();
                                        Task t = tasks.get(position);
                                        t.setDescription(taskDesc);
                                        t.save();
                                        tasks.set(position, t);
                                        taskItemsAdapter.notifyDataSetChanged();
                                        return;
                                    }
                                });

                        AlertDialog dialog = builder.create();
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
    public void onAddItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        Task t = new Task();
        t.setDescription(itemText);
        taskItemsAdapter.add(t);
        etNewItem.setText("");

        writeItems(itemText);
    }

    private void readItems() {
        tasks = (ArrayList<Task>)SQLite.select().
                from(Task.class).queryList();

    }

    private void writeItems(String taskDesc) {
        Task newTask = new Task();
        newTask.setDescription(taskDesc);
        newTask.save();
    }


}
