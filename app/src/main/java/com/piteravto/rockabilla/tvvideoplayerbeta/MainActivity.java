package com.piteravto.rockabilla.tvvideoplayerbeta;


import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.ActivityInfo;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.Toast;
import android.widget.VideoView;

import com.piteravto.rockabilla.tvvideoplayerbeta.api.ServerApi;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
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

    }

    private void playVideo (String Path)
    {
        // установите свой путь к файлу на SD-карточке
        //String videoSource = "/storage/6605-E526/test.mp4";
        // тут у нас путь, почти не захардкожен (не всегда правильно определяется путь)
        //String videoSource = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/test.mp4";
        //Toast.makeText(MainActivity.this, videoSource, Toast.LENGTH_SHORT).show();


        videoView = (VideoView) findViewById(R.id.videoview);
        videoView.setVideoPath(Path);
        //убрали play и прочее
        videoView.setMediaController(null);
        videoView.requestFocus();
        videoView.start(); // начинаем воспроизведение автоматически
    }

    private void downloadVideo(String url, String fileName, String description, String title)
    {
        //String url = getString(R.string.server_name) + "tv-service/uploads/d9e28907f1a7d949c7d973cdcc2e363c.mp4";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription(description);
        request.setTitle(title);
// in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

// get download service and enqueue file
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }




    //получаем инструкцию что нам делать
    private void getCommand()
    {
        ServerApi.getApi().getData(getString(R.string.tv_directory), getString(R.string.get_command)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(MainActivity.this, "Успешно подключился", Toast.LENGTH_LONG).show();

                try {
                    command = response.body().string();
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "Ошибка string()", Toast.LENGTH_SHORT).show();
                }

                //recyclerView.getAdapter().notifyDataSetChanged();

                Toast.makeText(MainActivity.this, command, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Ошибка подключения", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //просто стучимся, чтобы дать знать, что мы еще живы
    private void sendNotification()
    {
        ServerApi.getApi().getData(getString(R.string.tv_directory), getString(R.string.send_notification)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Ошибка подключения", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
