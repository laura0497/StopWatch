package Data;

/**
 * Created by Usuario on 5/06/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.usuario.cronometro.R;


import java.util.ArrayList;

import Entidades.ClaseVuelta;

public class CronometroAdapter extends RecyclerView.Adapter<CronometroAdapter.CronometroViewHolder> {

    Context context;
    ArrayList<ClaseVuelta> data = new ArrayList<>();
    LayoutInflater inflater;

    public CronometroAdapter(Context context, ArrayList<ClaseVuelta> vuelta) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        data = vuelta;

    }

    @Override
    public CronometroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_vuelta, parent, false);
        CronometroViewHolder viewHolder = new CronometroViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CronometroViewHolder holder, int position) {
        try {
            //animacion RV(RecyclerView)
            YoYo.with(Techniques.Swing).playOn(holder.VueltaContedor);

            // Preparo el texto a mostrar
            ClaseVuelta lap = data.get(position);
            //obtengo la informacion de la clase
            String info = (lap.getTiempo());
            String num = (lap.getNumeroVuelta().toString());
            //muestro la información en la vista
            holder.Numero.setText(num);
            holder.Info.setText(info);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Añade un nuevo item
     */
    public void addAll(ArrayList<ClaseVuelta> laps) {
        //posicion-arrayList
        data.addAll(0, laps);
        notifyDataSetChanged();

    }


    /**
     * limpia el recyclerview
     */
    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }


    /**
     * Clase View Holder
     */
    public static class CronometroViewHolder extends RecyclerView.ViewHolder {

        TextView Info, Numero;
        RelativeLayout VueltaContedor;

        public CronometroViewHolder(View itemView) {
            super(itemView);

            Info = (TextView) itemView.findViewById(R.id.tiempo);
            Numero = (TextView) itemView.findViewById(R.id.numero);
            VueltaContedor = (RelativeLayout) itemView.findViewById(R.id.vueltaCont);

        }
    }


}
