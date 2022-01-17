package com.lss.example.firebase;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lss.example.R;

import java.util.ArrayList;
import java.util.List;

public class FirebaseChatFragment extends Fragment implements LifecycleOwner {

    private ListView lv_chat;
    private FirebaseChatViewModel mViewModel;
    private EditText et_msg;

    public static FirebaseChatFragment newInstance() {
        return new FirebaseChatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.firebase_chat_fragment, container, false);
        mViewModel = new ViewModelProvider(this).get(FirebaseChatViewModel.class);

        et_msg = v.findViewById(R.id.et_msg);
        et_msg.setOnKeyListener((view, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_ENTER) {
                mViewModel.SendMessage(et_msg.getText().toString());
                et_msg.getText().clear();
            }
            return true;
        });
        lv_chat = v.findViewById(R.id.lv_list);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel.getMutableLiveMessage().observe(getViewLifecycleOwner(), messages -> {
            lv_chat.setAdapter(new ChatAdapter(getContext(), messages, R.layout.firebase_chat_msgview, mViewModel.userId));
        });

        mViewModel.SendMessage("TEST");
    }
}

class ChatAdapter extends BaseAdapter {
    private String userId;
    int size;
    ArrayList<Message> dataList;
    ChatMessageViewHolder chatMessageViewHolder = null;
    int resource;
    Context context;

    public ChatAdapter (Context context, ArrayList<Message> arrayList, int resource , String userid) {
        this.context = context;
        size = arrayList.size();
        dataList = arrayList;
        this.resource = resource;
        this.userId = userid;
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return resource;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        if (v == null) {
            v = LayoutInflater.from(context).inflate(resource, viewGroup, false);
            chatMessageViewHolder = new ChatMessageViewHolder(v);
        }

        if (dataList.get(i).id == userId) {
            chatMessageViewHolder.my.setVisibility(View.VISIBLE);
            chatMessageViewHolder.others.setVisibility(View.GONE);
            chatMessageViewHolder.mymessage.setText(dataList.get(i).getMessage());
        } else {
            chatMessageViewHolder.others.setVisibility(View.VISIBLE);
            chatMessageViewHolder.my.setVisibility(View.GONE);
            chatMessageViewHolder.othersmessage.setText(dataList.get(i).getMessage());
        }
        return v;
    }
}

class ChatMessageViewHolder extends RecyclerView.ViewHolder {
    public RelativeLayout my, others;
    public TextView mymessage, othersmessage;

    View view;
    public ChatMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        view = itemView;

        my = itemView.findViewById(R.id.my);
        others = itemView.findViewById(R.id.others);

        mymessage = itemView.findViewById(R.id.my_message);
        othersmessage = itemView.findViewById(R.id.others_message);
    }
}