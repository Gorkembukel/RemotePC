package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    private AtomicBoolean isStreaming = new AtomicBoolean(false);
    private Button streamButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        streamButton = findViewById(R.id.streamStart);
        ImageView imageView = findViewById(R.id.imageView);

        streamButton.setOnClickListener(v -> {
            if (streamButton.getText().toString().equals("Başlat")) {
                // Yayını başlat
                String[] args = {"192.168.1.89", "6066"}; // IP'yi kendi PC'ne göre ayarla
                isStreaming.set(true);
                clieant.startStream(MainActivity.this, args, isStreaming);
                streamButton.setText("Durdur");
            }
            else if (streamButton.getText().toString().equals(  "Durdur")){
                // Yayını durdur
                isStreaming.set(false);
                streamButton.setText("Başlat");
            }
        });
    }}