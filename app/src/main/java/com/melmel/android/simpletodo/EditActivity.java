package com.melmel.android.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class EditActivity extends AppCompatActivity {
    EditText editItemText;
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        String item = getIntent().getStringExtra("item");
        pos = getIntent().getIntExtra("pos", 0);


        editItemText = (EditText) findViewById(R.id.editItemText);
        editItemText.setText(item);
    }

    public void saveEdit(View v) {

        Intent data = new Intent();
        data.putExtra("item", editItemText.getText().toString());
        data.putExtra("pos", pos);
        setResult(RESULT_OK, data);

        this.finish();
    }
}
