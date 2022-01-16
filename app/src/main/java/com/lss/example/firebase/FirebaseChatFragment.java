package com.lss.example.firebase;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lss.example.R;

public class FirebaseChatFragment extends Fragment {

    private FirebaseChatViewModel mViewModel;

    public static FirebaseChatFragment newInstance() {
        return new FirebaseChatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.firebase_chat_fragment, container, false);
        mViewModel = new ViewModelProvider(this).get(FirebaseChatViewModel.class);
        return v;
    }
}