package com.piteravto.rockabilla.tvvideoplayerbeta;


import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;

import android.media.MediaPlayer;
import android.net.Uri;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.widget.Toast;
import android.widget.VideoView;

import com.piteravto.rockabilla.tvvideoplayerbeta.api.ServerApi;
import com.piteravto.rockabilla.tvvideoplayerbeta.controllers.WifiController;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.StringTokenizer;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private String command;
    private VideoView videoView;
    private String videoToPlay;
    private ArrayList<String> filesNames;
    private ArrayList<String> downloadFilesName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //горизонтальная ориентация
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        try {
            videoView = (VideoView) findViewById(R.id.videoview);
            videoView.setMediaController(null);
            videoView.requestFocus();
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    //Start a new activity or do whatever else you want here
                    //action(videoView);
                    getCommand();
                }
            });
            getFilesNames();

            //WifiController.configApState(MainActivity.this);

            getCommand();

        } catch (Exception e) {
            getCommand();
            Toast.makeText(MainActivity.this, "in main Error", Toast.LENGTH_LONG).show();
        }

        //boolean con = WifiHostSpot.configApState(MainActivity.this);
        //Toast.makeText(MainActivity.this, "Connection - " + con, Toast.LENGTH_SHORT).show();


    }


    private void getFilesNames()
    {
        try {
            String path = Environment.getExternalStorageDirectory().toString()+"/Download";
            filesNames = new ArrayList<String>();
            downloadFilesName = new ArrayList<String>();
            File directory = new File(path);
            File[] files = directory.listFiles();

            for (int i = 0; i < files.length; i++)
            {
                filesNames.add(files[i].getName());
            }
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "getFilesNames Error", Toast.LENGTH_LONG).show();
        }
    }



    private void playVideo (String Path, VideoView videoView)
    {
        // установите свой путь к файлу на SD-карточке
        //String videoSource = "/storage/6605-E526/test.mp4";
        // тут у нас путь, почти не захардкожен (не всегда правильно определяется путь)
        //String videoSource = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/test.mp4";
        //Toast.makeText(MainActivity.this, videoSource, Toast.LENGTH_SHORT).show();

        try {
            if (Path == null || Path.length()>0) {
                String[] tmp = Path.split("/");

                String fileName = tmp[tmp.length - 1];
                Toast.makeText(MainActivity.this, "file name to play " + fileName, Toast.LENGTH_LONG).show();
                if (filesNames.contains(fileName)) {
                    videoView.setVideoPath(Path);
                    Toast.makeText(MainActivity.this, "start to play " + fileName, Toast.LENGTH_LONG).show();
                    videoView.start(); // начинаем воспроизведение автоматически
                } else {
                    getCommand();
                }
            }
            else
            {
                getCommand();
            }
        } catch (Exception e) {

            Toast.makeText(MainActivity.this, "playVideo Error", Toast.LENGTH_LONG).show();
            getCommand();
        }
    }

    private void downloadVideo(String url, final String fileName, String description, String title)
    {
        //String url = getString(R.string.server_name) + "tv-service/uploads/d9e28907f1a7d949c7d973cdcc2e363c.mp4";
        Toast.makeText(MainActivity.this, "1 " + fileName, Toast.LENGTH_LONG).show();
        if(filesNames.contains(fileName) || downloadFilesName.contains(fileName))
                return;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        Toast.makeText(MainActivity.this, "2 " + fileName, Toast.LENGTH_LONG).show();
        request.setDescription(description);
        request.setTitle(title);
        // in order for this if to run, you must use the android 3.2 to compile your app
        Toast.makeText(MainActivity.this, "3 " + fileName, Toast.LENGTH_LONG).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        Toast.makeText(MainActivity.this, "4 " + fileName, Toast.LENGTH_LONG).show();
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        Toast.makeText(MainActivity.this, "5 " + fileName, Toast.LENGTH_LONG).show();
        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Toast.makeText(MainActivity.this, "6 " + fileName, Toast.LENGTH_LONG).show();
        manager.enqueue(request);
        Toast.makeText(MainActivity.this, "start download " + fileName, Toast.LENGTH_LONG).show();
        BroadcastReceiver onComplete=new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                Toast.makeText(MainActivity.this, "finish download " + fileName, Toast.LENGTH_LONG).show();
                filesNames.add(fileName);
                downloadFilesName.remove(fileName);
            }
        };
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        Toast.makeText(MainActivity.this, "in download " + fileName, Toast.LENGTH_LONG).show();
        downloadFilesName.add(fileName);
    }




    //получаем инструкцию что нам делать
    private void getCommand()
    {
        ServerApi.getApi().getData(getString(R.string.tv_directory), getString(R.string.get_command)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    command = response.body().string();
                    Toast.makeText(MainActivity.this, command, Toast.LENGTH_SHORT).show();
                    //смотрим какая это команда
                    String[] splitCommand = command.split(" ");
                    Toast.makeText(MainActivity.this, splitCommand[0], Toast.LENGTH_SHORT).show();
                    if (splitCommand[0].equals("UPDATE"))
                    {
                        //вытаскиваем имя файла
                        String[] tmp = splitCommand[1].split("/");
                        String fileName = tmp[tmp.length-1];
                        Toast.makeText(MainActivity.this, splitCommand[1], Toast.LENGTH_SHORT).show();
                        downloadVideo(splitCommand[1], fileName, "Download from server " + getString(R.string.server_name), fileName);
                    }
                    else
                    {
                        videoToPlay = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + command;
                    }

                    playVideo(videoToPlay, videoView);

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "getCommand Error " + command, Toast.LENGTH_LONG).show();

                    playVideo(videoToPlay, videoView);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }



}
