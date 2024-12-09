package com.example.projecttranslatorapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AddDestination extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView imagePreview;
    private StorageReference storageRef;
    private EditText titleInput, descriptionInput;
    private Button selectImageButton, addButton;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_destination);

        imagePreview = findViewById(R.id.imagePreview);
        selectImageButton = findViewById(R.id.selectImageButton);
        addButton = findViewById(R.id.addButton);
        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        selectImageButton = findViewById(R.id.selectImageButton);
        addButton = findViewById(R.id.addButton);
        storageRef = FirebaseStorage.getInstance().getReference("destinations");

        selectImageButton.setOnClickListener(v -> openFileChooser());

        addButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString();
            String description = descriptionInput.getText().toString();

            if (title.isEmpty() || description.isEmpty() || imageUri == null) {
                Toast.makeText(this, "Please fill out all fields and select an image", Toast.LENGTH_SHORT).show();
                return;
            }

            uploadImage(title, description);
        });
        db = FirebaseFirestore.getInstance();

        addButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString();
            String description = descriptionInput.getText().toString();

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add destination to Firestore
            Map<String, Object> destination = new HashMap<>();
            destination.put("title", title);
            destination.put("description", description);

            db.collection("destinations")
                    .add(destination)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Destination added successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Close the form activity
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to add destination: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imagePreview.setImageURI(imageUri);
            imagePreview.setVisibility(View.VISIBLE);
        }
    }

    private void uploadImage(String title, String description) {
        StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");
        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveToFirestore(title, description, uri.toString());
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveToFirestore(String title, String description, String imageUrl) {
        Map<String, Object> destination = new HashMap<>();
        destination.put("title", title);
        destination.put("description", description);
        destination.put("imageUrl", imageUrl);

        db.collection("destinations")
                .add(destination)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Destination added successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to add destination: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
