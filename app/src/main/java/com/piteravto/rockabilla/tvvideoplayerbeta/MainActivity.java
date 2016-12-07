package com.piteravto.rockabilla.tvvideoplayerbeta;


import android.content.pm.ActivityInfo;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.Toast;
import android.widget.VideoView;

import com.piteravto.rockabilla.tvvideoplayerbeta.api.ServerApi;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private String command;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //горизонтальная ориентация
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);



        // установите свой путь к файлу на SD-карточке
        //String videoSource = "/storage/6605-E526/test.mp4";
        // тут у нас путь, почти не захардкожен (не всегда правильно определяется путь)
        String videoSource = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/test.mp4";

        //Toast.makeText(MainActivity.this, videoSource, Toast.LENGTH_SHORT).show();

        videoView = (VideoView) findViewById(R.id.videoview);

        videoView.setVideoPath(videoSource);

        //убрали play и прочее
        videoView.setMediaController(null);
        videoView.requestFocus();
        videoView.start(); // начинаем воспроизведение автоматически
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
