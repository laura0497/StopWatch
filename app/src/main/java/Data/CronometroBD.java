package Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Usuario on 5/06/2016.
 */

public class CronometroBD extends SQLiteOpenHelper {

    private static int version = 1;
    private static String name = "CronometroBd";
    private static SQLiteDatabase.CursorFactory factory = null;

    public CronometroBD(Context context) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(this.getClass().toString(), "Creando base de datos");

        db.execSQL("create table clasevuelta(tiempo text, numerovuelta text)");

        Log.i(this.getClass().toString(), "Tabla clasevuelta creada");

        Log.i(this.getClass().toString(), "Base de datos creada");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists clasevuelta");
        db.execSQL("create table clasevuelta(tiempo text, numerovuelta text)");
    }


}