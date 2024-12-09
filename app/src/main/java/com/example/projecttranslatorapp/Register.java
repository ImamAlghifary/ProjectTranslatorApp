package com.example.projecttranslatorapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Register extends AppCompatActivity {
    private Button btnRegister, btnLogin;
    private EditText txtEmail, txtPassword, txtConfirmPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toLogin = new Intent(getBaseContext(), Login.class);
                startActivity(toLogin);
            }
        });

        txtConfirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_NULL) {
                    validate();
                    return true;
                }
                return false;
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });
    }
    private void validate() {
        txtEmail.setError(null);
        txtPassword.setError(null);
        txtConfirmPassword.setError(null);

        View focus = null;
        boolean cancel = false;

        String username = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        String confirmPassword = txtConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(username)) {
            txtEmail.setError("Username is required");
            focus = txtEmail;
            cancel = true;
        } else if (userExists(username)) {
            txtEmail.setError("Username already exists");
            focus = txtEmail;
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            txtPassword.setError("Password is required");
            focus = txtPassword;
            cancel = true;
        } else if (!passwordMatches(password, confirmPassword)) {
            txtConfirmPassword.setError("Password does not match");
            focus = txtConfirmPassword;
            cancel = true;
        }

        if (cancel) {
            focus.requestFocus();
        } else {
            Preferences.setUsername(getBaseContext(), username);
            Preferences.setPassword(getBaseContext(), password);
            // finish();
            startActivity(new Intent(getBaseContext(), Login.class));
        }
    }
    private boolean userExists(String user) {
        String spUser = Preferences.getUsername(getBaseContext());
        if (spUser.isEmpty() || spUser.isBlank()) {
            return false;
        }
        return user.equals(Preferences.getUsername(getBaseContext()));
    }

    private boolean passwordMatches(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }
}