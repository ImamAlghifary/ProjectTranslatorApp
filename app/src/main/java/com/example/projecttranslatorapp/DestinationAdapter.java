package com.example.projectfinal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class DestinationAdapter extends ArrayAdapter<Destination> {

    private FirebaseFirestore db;

    public DestinationAdapter(Context context, List<Destination> destinations, FirebaseFirestore db) {
        super(context, 0, destinations);
        this.db = db;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Destination destination = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_destination, parent, false);
        }

        // Lookup view for data population
        TextView titleTextView = convertView.findViewById(R.id.title);
        TextView descTextView = convertView.findViewById(R.id.desc);
        ImageView imageView = convertView.findViewById(R.id.image);
        Button favoriteButton = convertView.findViewById(R.id.BtnFav);

        // Populate the data into the template view
        titleTextView.setText(destination.getTitle());
        descTextView.setText(destination.getDescription());
        imageView.setImageResource(destination.getImageResource());

        // Handle "Add to Favorites" button click
        favoriteButton.setOnClickListener(v -> {
            if (db != null) {
                db.collection("favorites").add(destination)
                        .addOnSuccessListener(documentReference -> {
                            favoriteButton.setText("Added!");
                            favoriteButton.setEnabled(false);
                        })
                        .addOnFailureListener(e -> {
                            favoriteButton.setText("Retry");
                        });
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}

