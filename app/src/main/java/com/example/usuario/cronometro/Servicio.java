package com.example.usuario.cronometro;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Usuario on 7/06/2016.
 */

public class Servicio extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        /**
         * borra la base de datos cuando se fuerza el cierre de la aplicación
         */
        MainActivity.Limpiar();
        super.onDestroy();
    }

    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
    }
}