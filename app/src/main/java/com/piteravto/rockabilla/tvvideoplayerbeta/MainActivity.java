package com.piteravto.rockabilla.tvvideoplayerbeta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.piteravto.rockabilla.tvvideoplayerbeta.api.ServerApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private String command;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    //получаем инструкцию что нам делать
    private void getCommand ()
    {
        ServerApi.getApi().getData(getString(R.string.tv_directory), getString(R.string.get_command)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                command = response.body();
                //recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Ошибка подключения", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //просто стучимся, чтобы дать знать, что мы еще живы
    private void sendNotification()
    {
        ServerApi.getApi().getData(getString(R.string.tv_directory), getString(R.string.send_notification)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Ошибка подключения", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
