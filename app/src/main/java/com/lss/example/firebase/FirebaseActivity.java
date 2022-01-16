package com.lss.example.firebase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.api.fallback.service.FirebaseAuthFallbackService;
import com.lss.example.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FirebaseActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.fm_container)
    FragmentContainerView fragmentContainerView;

    FirebaseLoginFragment firebaseLoginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);
        ButterKnife.bind(this);

        firebaseLoginFragment = new FirebaseLoginFragment();


        getSupportFragmentManager().beginTransaction()
                .add(firebaseLoginFragment, "LoginPage")
                .commit();
    }
}