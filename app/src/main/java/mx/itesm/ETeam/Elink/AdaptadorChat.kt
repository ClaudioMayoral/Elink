package mx.itesm.ETeam.Elink

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.renglon_chat.view.*

class AdaptadorChat(private val arrDatos: Array<TarjetaChat>):
        RecyclerView.Adapter<AdaptadorChat.VistaRenglon>()
{
    // Para avisar los eventos
    var listener: ClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdaptadorChat.VistaRenglon {
        val vista = LayoutInflater.from(parent.context)
                .inflate(R.layout.renglon_chat, parent, false)
        return VistaRenglon(vista)
    }

    // Pide los datos de un RENGLON (ViewHolder)
    override fun onBindViewHolder(holder: AdaptadorChat.VistaRenglon, position: Int) {
        val tarjeta =  arrDatos[position]
        holder.set(tarjeta)

        // Programar el listener
        holder.vistaRenglonTarjeta.setOnClickListener {
            listener?.clicked(position)
        }
    }

    override fun getItemCount(): Int {
        return arrDatos.size        // El número de datos a desplegar
    }

    class VistaRenglon(val vistaRenglonTarjeta: View) :
            RecyclerView.ViewHolder(vistaRenglonTarjeta)
    {
        fun set(tarjeta: TarjetaChat){
            vistaRenglonTarjeta.tvUsuario.text = tarjeta.usuario
            vistaRenglonTarjeta.tvUltimoComentario.text = tarjeta.ultimoMensaje
            vistaRenglonTarjeta.imgUsuario.setImageResource(tarjeta.idImagen)
        }

    }
}
