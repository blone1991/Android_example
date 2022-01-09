package com.lss.example;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lss.example.mvvm.LifecycleActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_mvvm)
    Button btn_mvvm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    @SuppressLint("NonConstantResourceId")
    @OnClick ({R.id.btn_mvvm})
    void onClick(View view) {
        if (view.getId() == R.id.btn_mvvm) {
            startActivity(new Intent(this, LifecycleActivity.class));
        }
    }

}