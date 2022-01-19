package com.lss.example.restful;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.lss.example.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class RestfulActivity extends AppCompatActivity {

    RestfulViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restful);



        viewModel = new ViewModelProvider(this, new RestfulViewModelFactory()).get(RestfulViewModel.class);
        viewModel.initRetrofit();
        viewModel.getResponseLiveData().observe(this, reqRes -> {
            ((TextView)findViewById(R.id.tv_request)).setText(reqRes.getRequest());
            ((TextView)findViewById(R.id.tv_response)).setText(reqRes.getResponse());
        });
        findViewById(R.id.btn_request).setOnClickListener(view -> viewModel.getGithubRepos());
    }
}

class ReqRes {
    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    String request;
    String response;

    public ReqRes (String request, String response) {
        this.request = request;
        this.response = response;
    }
}
class RestfulViewModel extends ViewModel {
    Retrofit retrofit;
    GithubServiceApi githubServiceApi;

    MutableLiveData<ReqRes> responseLiveData;
    public MutableLiveData<ReqRes> getResponseLiveData() {
        if (responseLiveData == null)
            responseLiveData = new MutableLiveData<>();

        return responseLiveData;
    }

    public void initRetrofit () {

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")          // 요청할 BaseURL ( API 제공자 ) 반드시 '/' 로 끝나야함
                .addConverterFactory(GsonConverterFactory.create())     // 응답데이터를 GSON CONVERTER를 이용하여 변환
                .build();

        githubServiceApi = retrofit.create(GithubServiceApi.class);
    }

    public void getGithubRepos () {
        Call<JsonArray> request = githubServiceApi.getUserRepositories("blone1991");
        request.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {

                responseLiveData.setValue(new ReqRes(call.request().toString(), response.body() == null? response.toString() : response.body().toString()));
            }

            @Override
            public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }
}

class RestfulViewModelFactory implements ViewModelProvider.Factory {
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

interface GithubServiceApi {
    @GET("users/{user}/repos")
    Call<JsonArray> getUserRepositories(@Path("user") String userName);
}