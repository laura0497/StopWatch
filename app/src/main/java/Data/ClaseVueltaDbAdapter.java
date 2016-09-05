package Data;

/**
 * Created by Usuario on 5/06/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import Entidades.ClaseVuelta;

public class ClaseVueltaDbAdapter {

    /**
     * Constante con el nombre de la tabla
     */
    public static final String C_TABLA = "clasevuelta";

    /**
     * Constantes con el nombre de las columnas de la tabla
     */
    public static final String C_Tiempo = "tiempo";
    public static final String C_NumeroVuelta = "numerovuelta";

    private Context contexto;
    private CronometroBD dbHelper;
    private SQLiteDatabase db;

    /**
     * Lista de columnas de la tabla para utilizarla en las consultas
     * a la base de datos
     */
    private String[] columnas = new String[]{C_Tiempo, C_NumeroVuelta};

    public ClaseVueltaDbAdapter(Context context) {
        this.contexto = context;
    }


    /**
     * Abre la base de datos
     */
    public ClaseVueltaDbAdapter abrir() throws SQLException {
        dbHelper = new CronometroBD(contexto);
        db = dbHelper.getWritableDatabase();
        return this;
    }


    /**
     * Cierra la base de datos
     */
    public void cerrar() {
        dbHelper.close();
    }


    public int insert(ContentValues reg) {
        int result = 0;
        try {
            if (db == null)
                abrir();
            if (!db.isOpen()) {
                abrir();
            }


            db.execSQL("INSERT INTO clasevuelta (tiempo, numerovuelta)" +
                    "VALUES('" + reg.getAsString(ClaseVueltaDbAdapter.C_Tiempo) + "', '" + reg.getAsString(ClaseVueltaDbAdapter.C_NumeroVuelta) + "');");


        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Eliminar registros de la tabla
     **/
    public void deleteAll() {

        if (db == null)
            abrir();
        try {
            db.execSQL("delete from " + C_TABLA);
            db.close();

        } catch (Exception e) {

            e.printStackTrace();
        }

    }


    /**
     * Consultar tabla
     **/
    public ArrayList<ClaseVuelta> getAll() {
        ArrayList<ClaseVuelta> Laps = new ArrayList<ClaseVuelta>();
// Select All Query
        String selectQuery = "SELECT * FROM " + C_TABLA;
        if (db == null)
            abrir();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {


            if (cursor.moveToFirst()) {
                do {
                    ClaseVuelta claseVuelta = new ClaseVuelta(contexto);
                    claseVuelta.setTiempo(cursor.getString(0));
                    claseVuelta.setNumeroVuelta(cursor.getString(1));
                    Laps.add(0, claseVuelta);


                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Laps;
    }


    public ArrayList<ClaseVuelta> obtenerVuelta() {

        ArrayList<ClaseVuelta> Lap = new ArrayList<ClaseVuelta>();
        try {
            if (db == null) {
                abrir();
            }
            if (!db.isOpen()) {
                abrir();
            }

            Cursor c = db.query(C_TABLA, columnas, null, null, null, null, null);

            for (c.moveToLast(); !c.isAfterLast(); c.moveToNext()) {

                Lap.add(ClaseVuelta.cursorToVuelta(contexto, c));
            }

            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Lap;
    }

}
