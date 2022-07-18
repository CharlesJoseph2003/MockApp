package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button buttonPlus;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonPlus = findViewById(R.id.buttonPlus);
        tv = findViewById(R.id.tv);


        buttonPlus.setOnClickListener(new artificialCovidDetection(tv));
    }
}