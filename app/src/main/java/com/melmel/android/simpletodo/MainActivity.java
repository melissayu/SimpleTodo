package com.melmel.android.simpletodo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;

    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvItems = (ListView)findViewById(R.id.lvItems);

        readItems();

        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();
    }

    private void setupListViewListener() {

        // On long click, delete item
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id) {
                        items.remove(pos);
                        itemsAdapter.notifyDataSetChanged();
                        writeItems();

                        Context context = getApplicationContext();
                        CharSequence text = getString(R.string.item_deleted);
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        return true;
                    }
                }
        );

        // On short click, open edit page
        lvItems.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View item, int pos, long id) {

                        Intent i = new Intent(MainActivity.this, EditActivity.class);
                        i.putExtra("pos", pos);
                        String listItem = items.get(pos);
                        i.putExtra("item", listItem);
                        startActivityForResult(i, REQUEST_CODE);

                        /* This is support for edit screen to be a dialog instead of new screen.
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        final EditText input = new EditText(MainActivity.this);
                        final int position = pos;
                        String listItem = items.get(position);
                        input.setText(listItem);
                        builder.setTitle(R.string.edit_item)
                                .setView(input)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        items.set(position, input.getText().toString());
                                        itemsAdapter.notifyDataSetChanged();
                                        writeItems();
                                        return;
                                    }
                                });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                        */

                        return;
                    }
                }
        );

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {

            String item = data.getExtras().getString("item");
            int pos = data.getExtras().getInt("pos", 0);

            items.set(pos, item);
            itemsAdapter.notifyDataSetChanged();
            writeItems();

            Toast.makeText(this, item, Toast.LENGTH_SHORT).show();
        }
    }

    public void onAddItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        itemsAdapter.add(itemText);
        etNewItem.setText("");

        writeItems();
    }

    private void readItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            items = new ArrayList<String>(FileUtils.readLines(todoFile));
        }  catch (IOException e) {
            items = new ArrayList<String>();
        }
    }

    private void writeItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            FileUtils.writeLines(todoFile, items);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
