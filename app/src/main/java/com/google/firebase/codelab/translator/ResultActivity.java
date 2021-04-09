package com.google.firebase.codelab.translator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    TextView fname;
    TextView fket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        final String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        fname = (TextView)findViewById(R.id.fName);
        fket = (TextView)findViewById(R.id.fket);

        fname.setText("Translate Result");
        fket.setText(message);
    }
}