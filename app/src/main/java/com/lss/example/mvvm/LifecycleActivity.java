package com.lss.example.mvvm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.lss.example.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class LifecycleActivity extends AppCompatActivity implements LifecycleOwner, View.OnClickListener {

    MyViewModel viewModel;

    @BindView(R.id.btn_clear)
    Button btn_clear;

    @BindView(R.id.btn_publish)
    Button btn_publish;

    @BindView(R.id.et_publish)
    EditText et_publish;

    @BindView(R.id.lv_test)
    ListView lv_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifecycle);
        ButterKnife.bind(this);

        viewModel = new ViewModelProvider(this, new MyViewModelProviderFactory()).get(MyViewModel.class);
        viewModel.getStrHistory().observe(this, strings -> lv_test.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strings)));
        viewModel.getStrStream().observe(this, s -> Toast.makeText(this, s, Toast.LENGTH_SHORT).show());
    }

    @OnClick({R.id.btn_clear, R.id.btn_publish})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_clear :
                viewModel.clearHistory();
                break;

            case R.id.btn_publish:
                if (et_publish.getText().length() > 0) {
                    viewModel.publishStrStream(et_publish.getText().toString());
                }
                break;
        }
    }
}


class MyViewModel extends ViewModel {

    ArrayList<String> strHistoryArray;
    MutableLiveData<ArrayList<String>> strHistory;

    public void addHistory (String str) {
        if (strHistoryArray == null)
            strHistoryArray = new ArrayList<>();

        strHistoryArray.add(str);

        if (strHistory == null)
            strHistory = new MutableLiveData<>();

        strHistory.setValue(strHistoryArray);
    }

    public void clearHistory () {
        if (strHistoryArray == null)
            strHistoryArray = new ArrayList<>();

        strHistoryArray.clear();

        if (strHistory == null)
            strHistory = new MutableLiveData<>();

        strHistory.setValue(strHistoryArray);
    }

    public MutableLiveData<ArrayList<String>> getStrHistory() {
        if (strHistory == null)
            strHistory = new MutableLiveData<>();
        return strHistory;
    }

    MutableLiveData<String> strStream;
    public MutableLiveData<String> getStrStream() {
        if (strStream == null)
            strStream = new MutableLiveData<>();

        return strStream;
    }

    public void publishStrStream(String str) {
        strStream.setValue(str);

        // 추후에 Room Library로 옮길 예정이다
        addHistory(str);
    }
}


class MyViewModelProviderFactory implements ViewModelProvider.Factory {
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return modelClass.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("Factory Runtime Error");
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new RuntimeException("Runtime Error");
        }
    }
}