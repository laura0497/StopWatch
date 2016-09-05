package Entidades;

import android.content.Context;
import android.database.Cursor;

import Data.ClaseVueltaDbAdapter;

/**
 * Created by Usuario on 5/06/2016.
 */

public class ClaseVuelta {
    String tiempo;
    String numeroVuelta;
    private Context context;

    public ClaseVuelta(String tiempo, String numeroVuelta) {
        super();
        this.tiempo = tiempo;
        this.numeroVuelta = numeroVuelta;

    }

    public ClaseVuelta(Context context) {
        super();
        this.context = context;
    }

    //get y set
    public String getTiempo() {
        return tiempo;
    }

    public String getNumeroVuelta() {
        return numeroVuelta;
    }

    public void setNumeroVuelta(String numeroVuelta) {
        this.numeroVuelta = numeroVuelta;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }


    public static ClaseVuelta cursorToVuelta(Context context, Cursor c) {
        ClaseVuelta claseVuelta = null;
        try {
            if (c != null) {
                claseVuelta = new ClaseVuelta(context);

                claseVuelta.setTiempo(c.getString(c.getColumnIndex(ClaseVueltaDbAdapter.C_Tiempo)));

                claseVuelta.setNumeroVuelta(c.getString(c.getColumnIndex(ClaseVueltaDbAdapter.C_NumeroVuelta)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return claseVuelta;
    }

}
