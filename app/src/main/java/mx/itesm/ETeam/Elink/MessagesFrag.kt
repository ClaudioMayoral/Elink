package mx.itesm.ETeam.Elink

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager

import kotlinx.android.synthetic.main.fragment_messages.*

/*
Muestra a los usuarios a los que se les puede mandar mensaje, así como las conversaciones recientes.
Al seleccionar un mensaje se despliega un nuevo fragmento con la conversación completa y se habilita la
opción de enviar mensajes.
Autor: Alejandro Torices, Claudio Mayoral
 */
class MessagesFrag : Fragment(), ClickListener {

    private lateinit var arrChat : Array<TarjetaChat>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    private fun configurarRV() {
        val layout = LinearLayoutManager(this.context)
        rvChats.layoutManager = layout

        arrChat = crearArrTarjetas()
        val adaptador = AdaptadorChat(arrChat)
        rvChats.adapter = adaptador

        adaptador.listener = this

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configurarRV()

    }

    private fun crearArrTarjetas(): Array<TarjetaChat> {
        return arrayOf(
                TarjetaChat("Juan", 0,"Buen Día"),
                TarjetaChat("Pedro",0,"Muchas gracias")
        )
    }

    override fun clicked(posicion: Int) {
        val tarjeta = arrChat[posicion]
    }

    override fun itemClicked() {
        TODO("Not yet implemented")
    }
}
