package com.example.usuario.cronometro;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import Data.ClaseVueltaDbAdapter;
import Data.CronometroAdapter;
import Entidades.TimerView;
import Entidades.ClaseVuelta;

public class MainActivity extends Activity {


    private TimerView mTimerView;
    private Animation anim;
    static Context context;
    Handler handler = new Handler();
    CronometroAdapter adapter;

    ImageButton Inicio, Resetear, LapButton;
    RecyclerView RView;
    TextView min, sec, milisec, tv_S, tv_M;

    String formato, formatomin, formatosec, formatomilisec, formatoView, indicador = "";

    long tiempoInicio = 0L;
    long tiempoenmilisegundos = 0L;
    long timeSwap = 0L;
    long actualizar = 0L;

    int corriendo = 1;
    int segundos = 0;
    int minutos = 0;
    int posicion = 0;
    int milisegundos = 0;
    int TIMER_LENGTH = 18;

    Boolean running = false;
    static ClaseVueltaDbAdapter dbAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RView = (RecyclerView) findViewById(R.id.RView);

        Inicio = (ImageButton) findViewById(R.id.Iniciar);
        LapButton = (ImageButton) findViewById(R.id.lapButton);
        Resetear = (ImageButton) findViewById(R.id.Reset);

        min = (TextView) findViewById(R.id.minutos);
        sec = (TextView) findViewById(R.id.segundos);
        milisec = (TextView) findViewById(R.id.milisegundos);
        tv_M = (TextView) findViewById(R.id.tv_M);
        tv_S = (TextView) findViewById(R.id.tv_S);

        mTimerView = (TimerView) findViewById(R.id.timerAnimacion);
        context = this;

        dbAdapter = new ClaseVueltaDbAdapter(context);
        dbAdapter.abrir();
        adapter = new CronometroAdapter(MainActivity.this, dbAdapter.obtenerVuelta());


        Inicio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Inicio();
            }

        });
        Resetear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Resetear();
            }

        });

        LapButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Vuelta();
            }

        });

        InicializarRv();
        startService(new Intent(getBaseContext(), Servicio.class));
    }


    public void Inicio() {
        if (corriendo == 1) {

            indicador = "ValorInicio";

            Inicio.setBackgroundResource(R.drawable.d1_pause);

            tiempoInicio = SystemClock.uptimeMillis();
            handler.postDelayed(ActualizarTiempo, 0);

            noparpadear(min);
            noparpadear(sec);
            noparpadear(milisec);
            noparpadear(tv_M);
            noparpadear(tv_S);

            running = true;
            corriendo = 0;
        } else {

            indicador = "ValorPausa";

            Inicio.setBackgroundResource(R.drawable.d1_play);

            timeSwap += tiempoenmilisegundos;
            handler.removeCallbacks(ActualizarTiempo);

            setColor(min, sec, milisec, tv_S, tv_M, indicador);

            parpadear(min);
            parpadear(sec);
            parpadear(milisec);
            parpadear(tv_M);
            parpadear(tv_S);

            running = false;
            corriendo = 1;
            onPause();
        }

    }


    public void Resetear() {
        indicador = "ValorDetener";
        corriendo = 1;

        tiempoInicio = 0L;
        tiempoenmilisegundos = 0L;
        timeSwap = 0L;
        actualizar = 0L;


        segundos = 0;
        minutos = 0;
        milisegundos = 0;

        Inicio.setBackgroundResource(R.drawable.d1_play);
        handler.removeCallbacks(ActualizarTiempo);

        min.setText("00");
        sec.setText("00");
        milisec.setText("000");

        setColor(min, sec, milisec, tv_S, tv_M, indicador);
        noparpadear(min);
        noparpadear(sec);
        noparpadear(milisec);
        noparpadear(tv_M);
        noparpadear(tv_S);

        limpiarRv();
        running = false;

        onPause();
    }


    public void Vuelta() {
        if (running) {
            mTimerView.start(TIMER_LENGTH);
            if (formatoView != null) {
                for (int j = 0; j <= posicion; j++) {
                    posicion++;
                    j = posicion;
                }
            }
            ContentValues reg = new ContentValues();
            reg.put(dbAdapter.C_Tiempo, formatoView);
            String pos = String.valueOf(posicion);
            reg.put(dbAdapter.C_NumeroVuelta, pos);
            dbAdapter.insert(reg);
            dbAdapter.cerrar();
            adapter.addAll(dbAdapter.obtenerVuelta());
        }
    }


    private void consultar() {
        adapter.clear();
        ArrayList<ClaseVuelta> lap = dbAdapter.getAll();
        adapter.addAll(lap);

    }


    public void InicializarRv() {
        try {
            adapter = new CronometroAdapter(MainActivity.this, dbAdapter.obtenerVuelta());
            RView.setAdapter(adapter);
            RView.setHasFixedSize(true);
            RView.setLayoutManager(new LinearLayoutManager(context));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void limpiarRv() {
        adapter.clear();
        posicion = 0;
        dbAdapter.deleteAll();
    }

    /**
     * metodo estatico que borra la base de datos. se usa en el servicio
     */
    public static void Limpiar() {
        dbAdapter.deleteAll();
    }


    public void parpadear(TextView textView) {

        anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(370);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        textView.startAnimation(anim);
    }


    public void noparpadear(TextView textView) {

        textView.clearAnimation();
    }


    protected void onPause() {
        //pausa la inamcion de recoorido de la vuelta
        mTimerView.stop();
        super.onPause();
    }


    public void setColor(TextView min, TextView sec, TextView milisec, TextView tv_S, TextView tv_M, String indicador) {
        if (indicador.equals("ValorInicio")) {
            min.setTextColor(getResources().getColor(R.color.Running));
            sec.setTextColor(getResources().getColor(R.color.Running));
            milisec.setTextColor(getResources().getColor(R.color.Running));
            tv_S.setTextColor(getResources().getColor(R.color.Running));
            tv_M.setTextColor(getResources().getColor(R.color.Running));
        } else if (indicador.equals("ValorPausa")) {
            min.setTextColor(getResources().getColor(R.color.Stop));
            sec.setTextColor(getResources().getColor(R.color.Stop));
            milisec.setTextColor(getResources().getColor(R.color.Stop));
            tv_S.setTextColor(getResources().getColor(R.color.Stop));
            tv_M.setTextColor(getResources().getColor(R.color.Stop));
        } else if (indicador.equals("ValorDetener")) {
            min.setTextColor(getResources().getColor(R.color.Reset));
            sec.setTextColor(getResources().getColor(R.color.Reset));
            milisec.setTextColor(getResources().getColor(R.color.Reset));
            tv_S.setTextColor(getResources().getColor(R.color.Reset));
            tv_M.setTextColor(getResources().getColor(R.color.Reset));
        }

    }


    public Runnable ActualizarTiempo = new Runnable() {
        public void run() {

            tiempoenmilisegundos = SystemClock.uptimeMillis() - tiempoInicio;
            actualizar = timeSwap + tiempoenmilisegundos;

            segundos = (int) (actualizar / 1000);
            minutos = segundos / 60;
            segundos = segundos % 60;
            milisegundos = (int) (actualizar % 1000);

            //defino los formatos de los minutos, segundos , milisegundos y el item del RV
            formato = "  " + minutos + " " + String.format("%02d", segundos) + " "
                    + String.format("%03d", milisegundos);

            formatomin = "  " + minutos;
            formatosec = String.format("%02d", segundos);
            formatomilisec = String.format("%03d", milisegundos);

            formatoView = "" + minutos + ":" + String.format("%02d", segundos) + ":"
                    + String.format("%03d", milisegundos);

            //Asigno valores
            min.setText(formatomin);
            sec.setText(formatosec);
            milisec.setText(formatomilisec);
            //asigno color
            setColor(min, sec, milisec, tv_S, tv_M, indicador);
            handler.postDelayed(this, 100);


        }

        ;
    };


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {


        savedInstanceState.putString("formato", formato);
        savedInstanceState.putString("formatomin", formatomin);
        savedInstanceState.putString("formatosec", formatosec);
        savedInstanceState.putString("formatomilisec", formatomilisec);
        savedInstanceState.putString("formatoView", formatoView);
        savedInstanceState.putString("indicador", indicador);


        savedInstanceState.putLong("tiempoInicio", tiempoInicio);
        savedInstanceState.putLong("tiempoenmilisegundos", tiempoenmilisegundos);
        savedInstanceState.putLong("timeSwap", timeSwap);
        savedInstanceState.putLong("actualizar", actualizar);


        savedInstanceState.putInt("corriendo", corriendo);
        savedInstanceState.putInt("segundos", segundos);
        savedInstanceState.putInt("minutos", minutos);
        savedInstanceState.putInt("posicion", posicion);
        savedInstanceState.putInt("milisegundos", milisegundos);
        savedInstanceState.putInt("TIMER_LENGTH", TIMER_LENGTH);


        savedInstanceState.putBoolean("running", running);


        super.onSaveInstanceState(savedInstanceState);


    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        indicador = savedInstanceState.getString("indicador");
        if (!indicador.equals("")) {

            formato = savedInstanceState.getString("formato");
            formatomin = savedInstanceState.getString("formatomin");
            formatosec = savedInstanceState.getString("formatosec");
            formatomilisec = savedInstanceState.getString("formatomilisec");
            formatoView = savedInstanceState.getString("formatoView");


            tiempoInicio = savedInstanceState.getLong("tiempoInicio");
            tiempoenmilisegundos = savedInstanceState.getLong("tiempoenmilisegundos");
            timeSwap = savedInstanceState.getLong("timeSwap");
            actualizar = savedInstanceState.getLong("actualizar");


            corriendo = savedInstanceState.getInt("corriendo");
            segundos = savedInstanceState.getInt("segundos");
            minutos = savedInstanceState.getInt("minutos");
            posicion = savedInstanceState.getInt("posicion");
            milisegundos = savedInstanceState.getInt("milisegundos");
            TIMER_LENGTH = savedInstanceState.getInt("TIMER_LENGTH");

            running = savedInstanceState.getBoolean("running");


            //Asigno valores
            min.setText(formatomin);
            sec.setText(formatosec);
            milisec.setText(formatomilisec);

            //consulto y lleno el RV
            consultar();
            //valido que valor tenia y restauro el cronometro
            if (indicador.equals("ValorInicio")) {
                indicador = "ValorInicio";

                Inicio.setBackgroundResource(R.drawable.d1_pause);

                handler.postDelayed(ActualizarTiempo, 0);

                noparpadear(min);
                noparpadear(sec);
                noparpadear(milisec);
                noparpadear(tv_M);
                noparpadear(tv_S);

                running = true;
                corriendo = 0;

            } else if (indicador.equals("ValorPausa")) {
                indicador = "ValorPausa";

                Inicio.setBackgroundResource(R.drawable.d1_play);


                handler.removeCallbacks(ActualizarTiempo);

                setColor(min, sec, milisec, tv_S, tv_M, indicador);

                parpadear(min);
                parpadear(sec);
                parpadear(milisec);
                parpadear(tv_M);
                parpadear(tv_S);

                running = false;
                corriendo = 1;
                onPause();


            } else if (indicador.equals("ValorDetener")) {
                Resetear();
            }

            super.onRestoreInstanceState(savedInstanceState);
        }
    }


    @Override
    public void onBackPressed() {
        limpiarRv();
        finish();
        super.onBackPressed();
    }


}
