package com.piteravto.rockabilla.tvvideoplayerbeta;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Запускаем при старте устройство наше приложение
 * Created by MishustinAI on 05.12.2016.
 */

public class ActivityRunOnStartup extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
