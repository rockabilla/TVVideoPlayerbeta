package com.piteravto.rockabilla.tvvideoplayerbeta;


import android.content.pm.ActivityInfo;

import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.Toast;
import android.widget.VideoView;

import com.piteravto.rockabilla.tvvideoplayerbeta.api.ServerApi;


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

        /*
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
        */
        //Toast.makeText(MainActivity.this, getString(R.string.server_name) + "tv-service/uploads/d9e28907f1a7d949c7d973cdcc2e363c.mp4", Toast.LENGTH_LONG).show();

        downloadVideo("tv-service/uploads/d9e28907f1a7d949c7d973cdcc2e363c.mp4");
        //getCommand();

        //http://tabus.piteravto.ru/
    }

    private void downloadVideo1 ()
    {
        ServerApi.getApi().downloadVideo().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    //Log.d(TAG, "server contacted and has file");
                    Toast.makeText(MainActivity.this, "server contacted and has file", Toast.LENGTH_SHORT).show();
                    boolean writtenToDisk = writeVideoToDisk(response.body());

                    //Log.d(TAG, "file download was a success? " + writtenToDisk);
                } else {
                    //Log.d(TAG, "server contact failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //Log.e(TAG, "error");
            }
        });
    }

    private void downloadVideo (final String url)
    {
        new AsyncTask<Void, Long, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ServerApi.getApi().downloadVideo().enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Toast.makeText(MainActivity.this, "begin", Toast.LENGTH_SHORT).show();
                        if (response.isSuccessful()) {

                            //Log.d(TAG, "server contacted and has file");
                            Toast.makeText(MainActivity.this, "server contacted and has file", Toast.LENGTH_SHORT).show();
                            boolean writtenToDisk = writeVideoToDisk(response.body());

                            //Log.d(TAG, "file download was a success? " + writtenToDisk);
                            Toast.makeText(MainActivity.this, "file download was a success? ", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //Log.d(TAG, "server contact failed");
                            Toast.makeText(MainActivity.this, "server contact failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        //Log.e(TAG, "error");
                        Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                });
                return null;
            }
        }.execute();
    }

    private boolean writeVideoToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + "video.mp4");

            Toast.makeText(MainActivity.this, Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + "video.mp4", Toast.LENGTH_LONG).show();

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    //Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                    Toast.makeText(MainActivity.this, "file download: " + fileSizeDownloaded + " of " + fileSize, Toast.LENGTH_SHORT).show();
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
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
