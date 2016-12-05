package com.piteravto.rockabilla.tvvideoplayerbeta.api;

import android.app.Application;

import com.piteravto.rockabilla.tvvideoplayerbeta.R;

import retrofit2.Retrofit;

/**
 * Через эту штуку общаемся с серваком
 * На запуске приложухи, у нас любой класс может воспользоваться этим
 * Created by MishustinAI on 05.12.2016.
 */

public class ServerApi extends Application {

    private static ServerApiInterface serverApiInterface;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();

        retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_name)) //Базовая часть адреса
                .build();
        serverApiInterface = retrofit.create(ServerApiInterface.class); //Создаем объект, при помощи которого будем выполнять запросы
    }

    public static ServerApiInterface getApi() {
        return serverApiInterface;
    }
}
