package com.google.firebase.codelab.translator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import dmax.dialog.SpotsDialog;

public class ResultActivity extends AppCompatActivity {

    TextView textResult;
    TextView textSourceID;
    public static Context context;
    AlertDialog waitingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        context = getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        waitingDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Download Model")
                .setCancelable(false).build();
        Intent intent = getIntent();
        final String[] arr = intent.getStringArrayExtra(MainActivity.EXTRA_MESSAGE);
        textResult = (TextView)findViewById(R.id.textResult);
        textSourceID = (TextView)findViewById(R.id.langSource);
        Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
        dropdown.getBackground().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language_array, R.layout.dropdownlang);
        adapter.setDropDownViewResource(R.layout.dropdownlang);
        dropdown.setAdapter(adapter);
        String[] message = {TranslateLanguage.INDONESIAN,arr[1],};
        textSourceID.setText("Lang ID : "+arr[0]);
        textResult.setText(arr[1]);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                waitingDialog.show();
                dropdown.setOnItemSelectedListener(this);
                String lang = parent.getItemAtPosition(position).toString();
                String langTarget = "";
                switch(lang){
                    case "German":
                        langTarget = TranslateLanguage.GERMAN;
                        break;
                    case "English":
                        langTarget = TranslateLanguage.ENGLISH;
                        break;
                    case "Spain":
                        langTarget = TranslateLanguage.SPANISH;
                        break;
                    case "Indonesia":
                        langTarget = TranslateLanguage.INDONESIAN;
                        break;
                    case "Portuguese":
                        langTarget = TranslateLanguage.PORTUGUESE;
                        break;
                    case "Tagalog":
                        langTarget = TranslateLanguage.TAGALOG;
                        break;
                }
                translateText(message, langTarget);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void translateText(String[] arr, String langTarget) {
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(arr[0])
                        .setTargetLanguage(langTarget)
                        .build();
        final Translator languageTranslator =
                Translation.getClient(options);
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        languageTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void v) {
                                languageTranslator.translate(arr[1])
                                        .addOnSuccessListener(
                                                new OnSuccessListener<String>() {
                                                    @Override
                                                    public void onSuccess(@NonNull String translatedText) {
                                                        if (waitingDialog.isShowing())
                                                            waitingDialog.dismiss();
                                                        textResult.setText(translatedText);

                                                    }
                                                })
                                        .addOnFailureListener(
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        arr[0] = "Er";
                                                        arr[1] = "Translate Error!";
                                                        textResult.setText(arr[1]);
                                                    }
                                                });
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                arr[0] = "Er";
                                arr[1] = "Download Model Error!";
                                textResult.setText(arr[1]);
                            }
                        });


    }
}