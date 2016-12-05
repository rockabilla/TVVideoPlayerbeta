package com.piteravto.rockabilla.tvvideoplayerbeta;

import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.piteravto.rockabilla.tvvideoplayerbeta.api.ServerApi;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private String command;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // установите свой путь к файлу на SD-карточке
        String videoSource ="/sdcard/Movies/cat.3gp";

        VideoView videoView = (VideoView) findViewById(R.id.video);

        videoView.setVideoPath(videoSource);

        videoView.setMediaController(new MediaController(this));
        videoView.requestFocus(0);
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
