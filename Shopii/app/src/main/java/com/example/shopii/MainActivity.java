package com.example.shopii;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shopii.utils.DatabaseHelper;
import com.example.shopii.repos.UserRepository;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final DatabaseHelper dbHelper = new DatabaseHelper();
    private final UserRepository userRepository = new UserRepository(dbHelper);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCreate = findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(v -> {
            final String idStr = ((TextView) findViewById(R.id.editId)).getText().toString();
            final String username = ((TextView) findViewById(R.id.editUsername)).getText().toString();
            final String password = ((TextView) findViewById(R.id.editPassword)).getText().toString();

            if (idStr.isEmpty() || username.isEmpty() || password.isEmpty()) {
                updateUI("All fields required");
                return;
            }

            executor.execute(() -> {
                try {
                    int id = Integer.parseInt(idStr);
                    //boolean success = userRepository.createUser(id, username, password);
                    //updateUI(success ? "User created!" : "Creation failed");
                } catch (NumberFormatException e) {
                    updateUI("Invalid ID format");
                } catch (Exception e) {
                    updateUI("Error: " + e.getMessage());
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    private void updateUI(String message) {
        mainHandler.post(() -> {
            TextView resultText = findViewById(R.id.textResult);
            resultText.setText(message);
        });
    }
}
