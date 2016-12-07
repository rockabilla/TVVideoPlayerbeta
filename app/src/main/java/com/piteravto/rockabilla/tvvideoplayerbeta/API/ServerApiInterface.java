package com.piteravto.rockabilla.tvvideoplayerbeta.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;


/**
 * Для общения с серваком
 * Сюда добавляем описание запросов, которые мы будем делать
 * Created by MishustinAI on 05.12.2016.
 */

public interface ServerApiInterface {

    //rockabilla пока будем использовать и для получения команды и для посылания уведомления, что он все еще жив
    @GET("{directory}/{command}")
    Call<ResponseBody> getData(@Path("directory") String directory, @Path("command") String command);

    @Streaming
    @GET("tv-service/uploads/d9e28907f1a7d949c7d973cdcc2e363c.mp4")
    Call<ResponseBody> downloadVideo();
}
