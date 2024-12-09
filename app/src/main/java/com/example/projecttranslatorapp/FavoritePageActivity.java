package com.example.projecttranslatorapp;

import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FavoritePageActivity extends AppCompatActivity {

    private ListView favoriteListView;
    private ArrayList<Destination> favoriteDestinations;
    private DestinationAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_page);

        favoriteListView = findViewById(R.id.favoriteListView);
        favoriteDestinations = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        // Load favorites from Firestore
        db.collection("favorites").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                task.getResult().forEach(document -> {
                    Destination destination = document.toObject(Destination.class);
                    favoriteDestinations.add(destination);
                });
                adapter = new DestinationAdapter(this, favoriteDestinations, null);
                favoriteListView.setAdapter(adapter);
            }
        });
    }
}
