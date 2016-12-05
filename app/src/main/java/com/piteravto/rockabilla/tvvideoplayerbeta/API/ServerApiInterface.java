package com.piteravto.rockabilla.tvvideoplayerbeta.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


/**
 * Для общения с серваком
 * Сюда добавляем описание запросов, которые мы будем делать
 * Created by MishustinAI on 05.12.2016.
 */

public interface ServerApiInterface {

    //rockabilla пока будем использовать и для получения команды и для посылания уведомления, что он все еще жив
    @GET("{directory}/{command}")
    Call<String> getData(@Path("directory") String directory, @Path("command") String command);


}
