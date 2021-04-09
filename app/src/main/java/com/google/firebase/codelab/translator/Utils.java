package com.google.firebase.codelab.translator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.vision.text.Text;

import java.util.List;

public class Utils {
    public static final String EXTRA_MESSAGE = "com.google.firebase.codelab.translator.extra.MESSAGE";
    public static void processTextRecognitionResult(Text texts, Activity activity, Context context){
        List<Text.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            showResult("Block size = 0!", activity, context);
        }
        String langID = blocks.get(0).getRecognizedLanguage();
        String langSource = cekLanguage(langID);
        if (langSource == "unknown"){
            showResult("Bahasa asal tidak dikenal \n Text asal : \n"+texts.getText(), activity, context);
        }
        final String sourceText = texts.getText();
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(langSource)
                        .setTargetLanguage(TranslateLanguage.INDONESIAN)
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
                                languageTranslator.translate(sourceText)
                                        .addOnSuccessListener(
                                                new OnSuccessListener<String>() {
                                                    @Override
                                                    public void onSuccess(@NonNull String translatedText) {
                                                        showResult(translatedText, activity, context);
                                                    }
                                                })
                                        .addOnFailureListener(
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        showResult("Translate Error!", activity, context);
                                                    }
                                                });
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showResult("Download Model Error!", activity, context);
                            }
                        });
    }

    private static String cekLanguage(String langID) {
        switch (langID){
            case "de":
                return TranslateLanguage.GERMAN;
            case "en":
                return TranslateLanguage.ENGLISH;
            case "id":
                return TranslateLanguage.INDONESIAN;
            case "es":
                return TranslateLanguage.SPANISH;
            case "pt":
                return TranslateLanguage.PORTUGUESE;
            case "tl":
                return TranslateLanguage.TAGALOG;
            default:
                return "unknown";
        }
    }

    private static void showResult(String translatedText, Activity activity, Context context) {
        Intent intent = new Intent(activity, ResultActivity.class);
        intent.putExtra(EXTRA_MESSAGE, translatedText);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
