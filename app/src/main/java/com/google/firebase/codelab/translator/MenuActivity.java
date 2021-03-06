package com.google.firebase.codelab.translator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.io.IOException;

import dmax.dialog.SpotsDialog;

public class MenuActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    Button button;
    InputImage image;
    String message;
    AlertDialog waitingDialog;
    String[] model = {TranslateLanguage.GERMAN, TranslateLanguage.ENGLISH, TranslateLanguage.INDONESIAN, TranslateLanguage.SPANISH, TranslateLanguage.PORTUGUESE, TranslateLanguage.TAGALOG};
    public static final String EXTRA_MESSAGE = "com.google.firebase.codelab.translator.extra.MESSAGE";
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        context = getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        waitingDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Downloading model . . .")
                .setCancelable(false).build();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.getBoolean("firstTime", false)) {
            // run your one time code
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
            downloadModel(0);
        }

    }

    private void downloadModel(int index) {
        RemoteModelManager modelManager = RemoteModelManager.getInstance();
        if (index < 6){
            waitingDialog.show();
            TranslateRemoteModel langModel =
                    new TranslateRemoteModel.Builder(model[index]).build();
            DownloadConditions conditions = new DownloadConditions.Builder()
                    .requireWifi()
                    .build();
            modelManager.download(langModel, conditions)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void v) {
                            if (waitingDialog.isShowing()){
                                waitingDialog.dismiss();
                            }
                            downloadModel(index+1);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Error.
                        }
                    });
        }
        else{
            if (waitingDialog.isShowing()){
                waitingDialog.dismiss();
            }
        }

    }
    public void launcScan(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void launcTranslate(View view) {
        Intent intent = new Intent(this, TranslateActivity.class);
        startActivity(intent);
    }

    public void exitApp(View view) {
        finish();
        System.exit(0);
    }

    public void uploadImage(View view) {
        /*
        Intent intent = new Intent(this, UploadActivity.class);
        startActivity(intent);
         */
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
                                        Utils.processTextRecognitionResult(text, MenuActivity.this, context);
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
}