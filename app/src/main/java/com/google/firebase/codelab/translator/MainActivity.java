package com.google.firebase.codelab.translator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    CameraView cameraView;
    ImageView btnDetect;
    AlertDialog waitingDialog;
    String message;

    public static final String EXTRA_MESSAGE = "com.google.firebase.codelab.translator.extra.MESSAGE";
    public static Context context;

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = findViewById(R.id.camera_view);
        btnDetect =findViewById(R.id.btn_detect);
        context = getApplicationContext();

        waitingDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Please waiting . . .")
                .setCancelable(false).build();
        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                waitingDialog.show();
                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, cameraView.getWidth(), cameraView.getHeight(), false);
                cameraView.stop();

                runTextRecognition(bitmap);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

        btnDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.start();
                cameraView.captureImage();
            }
        });
    }
    private void runTextRecognition(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient();
        recognizer.process(image)
                .addOnSuccessListener(
                        new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text text) {
                                //processTextRecognitionResult(text);
                                if (waitingDialog.isShowing())
                                    waitingDialog.dismiss();
                                Utils.processTextRecognitionResult(text, MainActivity.this, context);
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

    }
/*
    private void processTextRecognitionResult(Text texts){
        List<Text.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            showToast("No text found");
            return;
        }
        String langID = blocks.get(0).getRecognizedLanguage();
        String langSource = cekLanguage(langID);
        if (langSource == "unknown"){
            showResult("Bahasa asal tidak dikenal \n Text asal : \n"+texts.getText());
        }
        final String sourceText = texts.getText();
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

    private void showResult(String translatedText) {
        if (waitingDialog.isShowing())
            waitingDialog.dismiss();
        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
        intent.putExtra(EXTRA_MESSAGE, translatedText);
        startActivity(intent);
    }

    private void showToast(String no_text_found) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

 */
}