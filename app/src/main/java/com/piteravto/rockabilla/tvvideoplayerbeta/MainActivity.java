package com.piteravto.rockabilla.tvvideoplayerbeta;


import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;

import android.media.MediaPlayer;
import android.net.Uri;


import android.os.Build;
import android.os.Environment;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.VideoView;
import com.piteravto.rockabilla.tvvideoplayerbeta.api.ServerApi;
import com.piteravto.rockabilla.tvvideoplayerbeta.controllers.WifiController;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
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
    //private String device_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //горизонтальная ориентация

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //device_id = "?tvid=" + Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

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
            WifiController.configApState(MainActivity.this);
            getCommand();

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            sendError("Error in OnCreate\n" + sw.toString());
            getCommand();
        }

        //boolean con = WifiHostSpot.configApState(MainActivity.this);
        //Toast.makeText(MainActivity.this, "Connection - " + con, Toast.LENGTH_SHORT).show();


    }


    private void getFilesNames()
    {
        try {
            String path = Environment.getExternalStorageDirectory().toString()+"/Download";
            filesNames = new ArrayList<>();
            downloadFilesName = new ArrayList<>();
            File directory = new File(path);
            File[] files = directory.listFiles();

            for (int i = 0; i < files.length; i++)
            {
                filesNames.add(files[i].getName());
            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            sendError("Error in getFilesNames\n" + sw.toString());
            getFilesNames();
        }
    }



    private void playVideo (String Path, VideoView videoView)
    {
        try {
            //если у нас чего то нет, значит ничего на плей не рпиходило и сейчас загружается, тупо ждем
            if (Path != null && Path.length()!=0) {
                String[] tmp = Path.split("/");

                String fileName = tmp[tmp.length - 1];

                //если к нам пришло что то, чего у нас нет, тогда мы это не играем)
                if (filesNames.contains(fileName)) {
                    videoView.setVideoPath(Path);
                    videoView.start(); // начинаем воспроизведение автоматически
                } else {
                    getCommand();
                }
            }
            else
            {
                sendError("Error in playVideo, Path==null or Path.length()==0, not fatal error");
                getCommand();
            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            sendError("Error in playVideo\n" + sw.toString());
            getCommand();
        }
    }

    private void downloadVideo(String url, final String fileName, String description, String title)
    {

        //если к нам на загрузку пришло что то, что у нас уже есть, тогда мы это не качаем
        if(filesNames.contains(fileName) || downloadFilesName.contains(fileName))
        {
            return;
        }
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
        BroadcastReceiver onComplete=new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                //когда что то скачали, то добавим, что у нас это есть, и что качать это не надо
                filesNames.add(fileName);
                downloadFilesName.remove(fileName);
            }
        };
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        downloadFilesName.add(fileName);
    }



    private void sendError(String error)
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        final String errorToSend = formattedDate +"\n" + error;
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), errorToSend);
        ServerApi.getApi().sendError(getString(R.string.tv_directory), getString(R.string.send_error)/* + device_id*/, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                sendError("sendError onFailure, its not first try\n"+errorToSend);
            }
        });
    }


    //получаем инструкцию что нам делать
    private void getCommand()
    {
        ServerApi.getApi().getData(getString(R.string.tv_directory), getString(R.string.get_command)/* + device_id*/).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    command = response.body().string();
                    //смотрим какая это команда
                    String[] splitCommand = command.split(" ");
                    if (splitCommand[0].equals("UPDATE"))
                    {
                        //вытаскиваем имя файла
                        String[] tmp = splitCommand[1].split("/");
                        String fileName = tmp[tmp.length-1];
                        downloadVideo(splitCommand[1], fileName, "Download from server " + getString(R.string.server_name), fileName);
                    }
                    else
                    {
                        videoToPlay = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + command;
                    }
                    //если получили на загрузку играем тупо то, что нам пришло до этого
                    playVideo(videoToPlay, videoView);

                } catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    sendError("Error in getCommand() onResponse (it can be downloadVideo error)\n" + sw.toString());
                    playVideo(videoToPlay, videoView);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                sendError("onFailure in getCommand()");
                getCommand();
            }
        });
    }



}
