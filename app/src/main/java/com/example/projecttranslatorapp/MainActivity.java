package com.example.projecttranslatorapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {
    private CardView btnTranslator, btnRec, btnFav;
    private Button btnLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnTranslator = findViewById(R.id.btnTranslator);
        btnRec = findViewById(R.id.btnRec);
        btnFav = findViewById(R.id.btnFav);
        btnLogOut = findViewById(R.id.btnLogOut);

        btnTranslator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openTranslator = new Intent(getApplicationContext(),translatePage.class);
                startActivity(openTranslator);
            }
        });

        btnRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openRec = new Intent(getApplicationContext(),TextDetector.class);
                startActivities(new Intent[]{openRec});
            }});

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Preferences.clearLoggedUser(getBaseContext());
                startActivity(new Intent(getBaseContext(), Login.class));
                finish();
            }
        });

    }
}
