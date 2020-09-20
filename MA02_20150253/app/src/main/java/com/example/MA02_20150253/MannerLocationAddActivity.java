package com.example.MA02_20150253;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class MannerLocationAddActivity extends AppCompatActivity {

    private EditText etTitle;
    private EditText etAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        etTitle = (EditText) findViewById(R.id.etTitle);
        etAddress = (EditText) findViewById(R.id.etAddress);

        Intent intent = getIntent();
        if (intent.getStringExtra("Title") != null)
            etTitle.setText(intent.getStringExtra("Title"));
        if (intent.getStringExtra("Address") != null)
            etAddress.setText(intent.getStringExtra("Address"));
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOK:
                Intent intent = new Intent();
                intent.putExtra("Title", etTitle.getText().toString());
                intent.putExtra("Address", etAddress.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
                break;

            case R.id.btnCancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }
}
