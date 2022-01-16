package com.lss.example.firebase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.lss.example.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FirebaseLoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirebaseLoginFragment extends Fragment {
    EditText et_email;
    EditText et_password;
    Button btn_login;
    Button btn_signup;


    public FirebaseLoginFragment() {
        // Required empty public constructor
    }

    public static FirebaseLoginFragment newInstance(String param1, String param2) {
        return new FirebaseLoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_firebase_login, container, false);

        et_email = v.findViewById(R.id.et_email);
        et_password = v.findViewById(R.id.et_password);
        btn_login = v.findViewById(R.id.btn_login);
        btn_signup = v.findViewById(R.id.btn_signup);

        btn_login.setOnClickListener(this::Login);
        btn_signup.setOnClickListener(this::SignUp);
        return v;
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.btn_login)
    void Login (View view) {
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(et_email.getText().toString(), et_password.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        getParentFragmentManager().beginTransaction()
                                .add(new FirebaseChatFragment(), "ChatFragment")
                                .commit();

                        Toast.makeText(getContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                    } else {
                        Objects.requireNonNull(task.getException()).printStackTrace();
                        Toast.makeText(getContext(), "로그인 에러", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.btn_signup)
    void SignUp (View view) {
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(et_email.getText().toString(), et_password.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        getParentFragmentManager().beginTransaction()
                                .add(new FirebaseChatFragment(), "ChatFragment")
                                .commit();
                        Toast.makeText(getContext(), "등록 성공", Toast.LENGTH_SHORT).show();
                    } else {
                        Objects.requireNonNull(task.getException()).printStackTrace();
                        Toast.makeText(getContext(), "등록 에러", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}