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
        String[] arr = new String[2];
        List<Text.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            arr[0] = "Er";
            arr[1] = "Block size = 0!";
            showResult(arr, activity, context);
        }
        String langID = blocks.get(0).getRecognizedLanguage();
        final String sourceText = texts.getText();
        arr[0] = langID;
        arr[1] = sourceText;
        String targetLang = TranslateLanguage.INDONESIAN;
        translateText(arr, targetLang, activity, context);
    }

    public static void translateText(String[] arr, String targetLang, Activity activity, Context context) {
        String langSource = cekLanguage(arr[0]);
        if (langSource == "unknown"){
            arr[0] = "Er";
            arr[1] = "Bahasa asal tidak dikenal";
            showResult(arr, activity, context);
        }
        else{
            TranslatorOptions options =
                    new TranslatorOptions.Builder()
                            .setSourceLanguage(langSource)
                            .setTargetLanguage(targetLang)
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
                                                            arr[0] = langSource;
                                                            arr[1] = translatedText;
                                                            showResult(arr, activity, context);

                                                        }
                                                    })
                                            .addOnFailureListener(
                                                    new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            arr[0] = "Er";
                                                            arr[1] = "Translate Error!";
                                                            showResult(arr, activity, context);
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
                                    showResult(arr, activity, context);
                                }
                            });
        }

    }

    public static String cekLanguage(String langID) {
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

    private static void showResult(String[] arr, Activity activity, Context context) {
        Intent intent = new Intent(activity, ResultActivity.class);
        intent.putExtra(EXTRA_MESSAGE, arr);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
