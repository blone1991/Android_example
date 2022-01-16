package com.lss.example;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lss.example.firebase.FirebaseActivity;
import com.lss.example.listview.BtListViewActivity;
import com.lss.example.mvvm.LifecycleActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_mvvm)
    Button btn_mvvm;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_listview)
    Button btn_listview;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_firebase)
    Button btn_firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }


    @OnClick ({R.id.btn_mvvm, R.id.btn_listview, R.id.btn_firebase})
    void OnClick(View view) {
        if (view.getId() == R.id.btn_mvvm) {
            startActivity(new Intent(this, LifecycleActivity.class));
        } else if (view.getId() == R.id.btn_listview) {
            startActivity(new Intent ( this, BtListViewActivity.class));
        } else if (view.getId() == R.id.btn_firebase) {
            startActivity(new Intent(this, FirebaseActivity.class));
        }
    }

}