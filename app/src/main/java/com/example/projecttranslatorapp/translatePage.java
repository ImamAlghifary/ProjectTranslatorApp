package com.example.projecttranslatorapp;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class translatePage extends AppCompatActivity {
    private Spinner fromSpinner, toSpinner;
    private TextInputEditText sourceEdt, translatedTV;
    private ImageView micIV, btnSwitch;
    private MaterialButton translateBtn;
    private ImageView img;
    private Button snapBtn;
    private Button detectBtn;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private Bitmap imageBitmap;

    String[] fromLanguages = {"from", "Indonesia", "English", "French", "Spain"};
    String[] toLanguages = {"To", "Indonesia", "English", "French", "Spain"};

    private static final int REQUEST_PERMISSION_CODE = 1;
    String fromLanguageCode, toLanguageCode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_translate_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fromSpinner = findViewById(R.id.idFromSpinner);
        toSpinner = findViewById(R.id.idToSpinner);
        sourceEdt = findViewById(R.id.idEdtSource);
        micIV = findViewById(R.id.idIVMic);
        translateBtn = findViewById(R.id.btnTranslate);
        translatedTV = findViewById(R.id.idTVTranslatedTV);
        btnSwitch = findViewById(R.id.btnSwitch);
        img = findViewById(R.id.image);
        snapBtn = findViewById(R.id.snapbtn);
        detectBtn =  findViewById(R.id.detectbtn);

        detectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectTxt();
            }
        });
        snapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()){
                    dispatchTakePictureIntent();
                }else{
                    requestPermission();
                }
            }
        });
        btnSwitch.setOnClickListener(view -> {
            int fromPosition = fromSpinner.getSelectedItemPosition();
            int toPosition = toSpinner.getSelectedItemPosition();

            fromSpinner.setSelection(toPosition);
            toSpinner.setSelection(fromPosition);
        });
        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                fromLanguageCode = getLanguageCode(fromLanguages[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        ArrayAdapter<String> fromAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, fromLanguages);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdapter);

        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                toLanguageCode = getLanguageCode(toLanguages[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        ArrayAdapter<String> toAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, toLanguages);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);

        translateBtn.setOnClickListener(view -> {
            translatedTV.setText("");
            if (fromLanguageCode == null || fromLanguageCode.trim().isEmpty()) {
                Toast.makeText(translatePage.this, "Please select source language", Toast.LENGTH_SHORT).show();
            } else if (toLanguageCode == null || toLanguageCode.trim().isEmpty()) {
                Toast.makeText(translatePage.this, "Please select language to be translated", Toast.LENGTH_SHORT).show();
            } else {
                translateText(fromLanguageCode, toLanguageCode, sourceEdt.getText().toString());
            }
        });

        micIV.setOnClickListener(view -> {
            Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to convert into text");
            try {
                startActivityForResult(i, REQUEST_PERMISSION_CODE);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(translatePage.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handling Speech Recognition Result
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && !result.isEmpty()) {
                    sourceEdt.setText(result.get(0));  // Set the recognized speech as text
                } else {
                    Toast.makeText(this, "No speech recognized", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Speech recognition failed or canceled", Toast.LENGTH_SHORT).show();
            }
        }

        // Handling Image Capture Result
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    imageBitmap = (Bitmap) extras.get("data");  // Get the image bitmap from extras
                    if (imageBitmap != null) {
                        img.setImageBitmap(imageBitmap);  // Set the image to the ImageView
                    } else {
                        Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "No image data returned", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void translateText(String fromLanguageCode, String toLanguageCode, String source) {
        translatedTV.setText("Downloading Model...");
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(fromLanguageCode)
                .setTargetLanguage(toLanguageCode)
                .build();

        Translator translator = Translation.getClient(options);

        DownloadConditions conditions = new DownloadConditions.Builder().requireWifi().build();

        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(unused -> {
                    translatedTV.setText("Translating...");
                    translator.translate(source).addOnSuccessListener(translatedText -> translatedTV.setText(translatedText))
                            .addOnFailureListener(e -> Toast.makeText(translatePage.this, "Failed to Translate: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(translatePage.this, "Failed to Download Model: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public String getLanguageCode(String language) {
        switch (language) {
            case "Indonesia":
                return TranslateLanguage.INDONESIAN;
            case "English":
                return TranslateLanguage.ENGLISH;
            case "French":
                return TranslateLanguage.FRENCH;
            case "Spain":
                return TranslateLanguage.SPANISH;
            default:
                return " ";
        }

    }
    private boolean checkPermissions(){
        int cameraPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);

        return  cameraPermission == PackageManager.PERMISSION_GRANTED;
    }

    private  void requestPermission(){
        int PERMISSION_CODE = 200;
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_CODE);

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0){
            boolean cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (cameraPermission){
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT ).show();
                dispatchTakePictureIntent();
            }else{
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void detectTxt() {

        InputImage image = InputImage.fromBitmap(imageBitmap, 0);

        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> result = recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text text) {
                StringBuilder result = new StringBuilder();
                for (Text.TextBlock block: text.getTextBlocks()){
                    String blockText = block.getText();
                    Point[] blockCornerPoint = block.getCornerPoints();
                    Rect blockFrame = block.getBoundingBox();
                    for (Text.Line line: block.getLines()){
                        String lineText = line.getText();
                        Point[] lineCornerPoint = line.getCornerPoints();
                        Rect linRect = line.getBoundingBox();
                        for (Text.Element element: line.getElements()){
                            String elementText = element.getText();
                            result.append(elementText);
                        }
                        sourceEdt.setText(blockText);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(translatePage.this,"Fail to detect text from image"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processTxt(Text text) {
        // This method processes the recognized text from the image.

        List<Text.TextBlock> blocks = text.getTextBlocks();
        if (blocks.isEmpty()) {
            Toast.makeText(translatePage.this, "No Text Detected", Toast.LENGTH_LONG).show();
            return;
        }

        StringBuilder detectedText = new StringBuilder();
        for (Text.TextBlock block : blocks) {
            detectedText.append(block.getText()).append("\n");
        }

        sourceEdt.setText(detectedText.toString());
    }
}
