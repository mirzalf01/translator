package com.google.firebase.codelab.translator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

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

        String langSourceID = textResourceID.getSelectedItem().toString();
        String langResultID = textResultID.getSelectedItem().toString();

        textResourceID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String lang = parent.getItemAtPosition(position).toString();
                textResourceID.setOnItemSelectedListener(this);
                translateText(textResourceID.getSelectedItem().toString(), textResultID.getSelectedItem().toString(), textResource.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        textResultID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String lang = parent.getItemAtPosition(position).toString();
                textResultID.setOnItemSelectedListener(this);
                translateText(textResourceID.getSelectedItem().toString(), textResultID.getSelectedItem().toString(), textResource.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        textResource.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0){
                    translateText(textResourceID.getSelectedItem().toString(), textResultID.getSelectedItem().toString(), textResource.getText().toString());
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

    private void translateText(String source, String result, String text) {
        String sourceLang = checkLang(source);
        String resultLang = checkLang(result);
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(sourceLang)
                        .setTargetLanguage(resultLang)
                        .build();
        final Translator languageTranslator =
                Translation.getClient(options);
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        languageTranslator.translate(text)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@NonNull String translatedText) {
                                textResult.setText(translatedText);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
    }

    private String checkLang(String id) {
        String result = "";
        switch(id){
            case "German":
                result = TranslateLanguage.GERMAN;
                break;
            case "English":
                result = TranslateLanguage.ENGLISH;
                break;
            case "Spain":
                result = TranslateLanguage.SPANISH;
                break;
            case "Indonesia":
                result = TranslateLanguage.INDONESIAN;
                break;
            case "Portuguese":
                result = TranslateLanguage.PORTUGUESE;
                break;
            case "Tagalog":
                result = TranslateLanguage.TAGALOG;
                break;
        }
        return result;
    }

    public void changePosition(View view) {
        int sourceID = textResourceID.getSelectedItemPosition();
        int resultID = textResultID.getSelectedItemPosition();
        textResourceID.setSelection(resultID);
        textResultID.setSelection(sourceID);
        textResource.setText(textResult.getText().toString());
        translateText(textResourceID.getSelectedItem().toString(), textResultID.getSelectedItem().toString(), textResult.getText().toString());
    }
}