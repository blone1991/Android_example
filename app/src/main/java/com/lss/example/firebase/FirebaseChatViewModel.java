package com.lss.example.firebase;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FirebaseChatViewModel extends ViewModel {
    private static final String TAG = "Chat";
    String userId;
    private DatabaseReference mDatabase;

    ArrayList<Message> messageList;

    public MutableLiveData<ArrayList<Message>> getMutableLiveMessage() {
        return mutableLiveMessage;
    }

    MutableLiveData<ArrayList<Message>> mutableLiveMessage;

    public FirebaseChatViewModel () {
        super();
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mutableLiveMessage = new MutableLiveData<>();
        messageList = new ArrayList<>();

        mDatabase.child("messageRoom").addChildEventListener(postListener);
    }

    @SuppressLint("CheckResult")
    public void SendMessage (String msg) {
        Single<String> single = Single.just(msg);
        Disposable disposable = single.observeOn(Schedulers.newThread())
                .doOnError(Throwable::printStackTrace)
                .subscribe((s, throwable) -> {
                    Log.d(TAG, "SendMessage: userId = " + userId + " msg = " + msg);
                    mDatabase.child("messageRoom").push().setValue(new Message(userId, msg));
                });
    }

    ChildEventListener postListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            Message message = snapshot.getValue(Message.class);  // chatData를 가져오고
            if (message != null) {
                messageList.add(message);
                mutableLiveMessage.setValue(messageList);
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            messageList.clear();
        }
    };
}

class Message implements Serializable {
    String id;
    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public Message () {

    }

    public Message (String id, String message) {
        this.id = id;
        this.message = message;
    }

}




