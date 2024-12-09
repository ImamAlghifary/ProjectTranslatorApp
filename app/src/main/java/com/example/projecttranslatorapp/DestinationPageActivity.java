package com.example.projectfinal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class DestinationPageActivity extends AppCompatActivity {

    private ListView destinationListView;
    private ArrayList<Destination> destinations;
    private DestinationAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination_page);

        destinationListView = findViewById(R.id.destinationListView);
        Button favoritePageButton = findViewById(R.id.favoritePageButton);

        db = FirebaseFirestore.getInstance();

        // Sample data
        destinations = new ArrayList<>();
        destinations.add(new Destination("Liberty", "Liberty is the state of being free to act, think, " +
                "or speak without undue restrictions or oppression. " +
                "It encompasses individual rights and freedoms, such as freedom of speech, religion, " +
                "and choice, and is often considered a cornerstone of " +
                "democratic societies. Liberty promotes personal autonomy while balancing the" +
                " well-being and rights of others within a community.", R.drawable.liberty));

        adapter = new DestinationAdapter(this, destinations, db);
        destinationListView.setAdapter(adapter);

        favoritePageButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, FavoritePageActivity.class);
            startActivity(intent);
        });
    }
}