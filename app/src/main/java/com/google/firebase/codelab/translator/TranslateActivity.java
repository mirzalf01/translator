package com.google.firebase.codelab.translator;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class TranslateActivity extends AppCompatActivity {
    Spinner textResourceID;
    Spinner textResultID;
    EditText textResource;
    TextView textResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        textResult = (TextView)findViewById(R.id.textResult);
        textResource = (EditText)findViewById(R.id.textResource);
        textResultID = (Spinner) findViewById(R.id.spinner2);
        textResourceID = (Spinner) findViewById(R.id.spinner1);



        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language_array, R.layout.translate_view_dropdown);
        adapter.setDropDownViewResource(R.layout.translate_dropdown);

        textResourceID.getBackground().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        textResourceID.setAdapter(adapter);

        textResultID.getBackground().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        textResultID.setAdapter(adapter);

        textResource.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0){
                    textResult.setText(textResource.getText());
                }
                else if(s.length() == 0){
                    textResult.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }
}