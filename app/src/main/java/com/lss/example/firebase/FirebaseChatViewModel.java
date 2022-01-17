package com.lss.example.firebase;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class FirebaseChatViewModel extends ViewModel {
    private static final String TAG = "Chat";
    String userId = "";
    private DatabaseReference mDatabase = null;

    ArrayList<Message> messageList;

    public MutableLiveData<ArrayList<Message>> getMutableLiveMessage() {
        return mutableLiveMessage;
    }

    MutableLiveData<ArrayList<Message>> mutableLiveMessage;

    public FirebaseChatViewModel () {
        super();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mutableLiveMessage = new MutableLiveData<>();
        messageList = new ArrayList<>();

        mDatabase.addValueEventListener(postListener);
    }

    public int SendMessage (String msg) {
        Single<String> single = Single.just(msg);
        single.observeOn(Schedulers.newThread())
                .doOnError(throwable -> throwable.printStackTrace())
                .subscribe((s, throwable) -> {
                    mDatabase.child("messageRoom").push().setValue(new Message(userId, msg));
                });
        return 0;
    }

    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            Log.d(TAG, "onDataChange: " + snapshot.child("messageRoom").getValue());
            Log.d(TAG, "onDataChange: " + snapshot.child("messageRoom").toString());
            Log.d(TAG, "onDataChange: " + snapshot.toString());


            Message message = snapshot.child("messageRoome").getValue(Message.class);
            if (message != null) {
                messageList.add(message);
                mutableLiveMessage.setValue(messageList);
                Log.d(TAG, "onDataChange: " + message.getMessage());
            }
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




