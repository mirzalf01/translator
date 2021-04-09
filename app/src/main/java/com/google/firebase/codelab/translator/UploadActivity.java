package com.google.firebase.codelab.translator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.wonderkiln.camerakit.CameraKitVideo;

import java.io.IOException;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class UploadActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    Button button;
    InputImage image;
    String message;
    AlertDialog waitingDialog;
    public static final String EXTRA_MESSAGE = "com.google.firebase.codelab.translator.extra.MESSAGE";
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        context = getApplicationContext();
        button = (Button)findViewById(R.id.upload);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        waitingDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Please waiting . . .")
                .setCancelable(false).build();
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            try{
                image = InputImage.fromFilePath(this, imageUri);
                TextRecognizer recognizer = TextRecognition.getClient();
                waitingDialog.show();
                recognizer.process(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<Text>() {
                                    @Override
                                    public void onSuccess(Text text) {
                                        //processTextRecognitionResult(text);
                                        if (waitingDialog.isShowing())
                                            waitingDialog.dismiss();
                                        Utils.processTextRecognitionResult(text, UploadActivity.this, context);
                                    }
                                }
                        )
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        e.printStackTrace();
                                    }
                                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
/*
    private void processTextRecognitionResult(Text text) {
        List<Text.TextBlock> blocks = text.getTextBlocks();
        if (blocks.size() == 0) {
            showToast("No text found");
            return;
        }
        String langID = blocks.get(0).getRecognizedLanguage();
        String langSource = cekLanguage(langID);
        if (langSource == "unknown"){
            showResult("Bahasa asal tidak dikenal \n Text asal : \n"+text.getText());
        }
        final String sourceText = text.getText();
        //showToast(str);
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
                                                        showResult(translatedText);
                                                    }
                                                })
                                        .addOnFailureListener(
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        showToast("Translate Error");
                                                    }
                                                });
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showToast("Download Model Error");
                            }
                        });
    }

    private String cekLanguage(String langID) {
        switch (langID){
            case "de":
                return TranslateLanguage.GERMAN;
            case "en":
                return TranslateLanguage.ENGLISH;
            case "id":
                return TranslateLanguage.INDONESIAN;
            case "ko":
                return TranslateLanguage.KOREAN;
            case "ja":
                return TranslateLanguage.JAPANESE;
            default:
                return "unknown";
        }
    }

    private void showResult(String translatedText) {
        if (waitingDialog.isShowing())
            waitingDialog.dismiss();
        Intent intent = new Intent(UploadActivity.this, ResultActivity.class);
        intent.putExtra(EXTRA_MESSAGE, translatedText);
        startActivity(intent);
    }

    private void showToast(String no_text_found) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

 */

}