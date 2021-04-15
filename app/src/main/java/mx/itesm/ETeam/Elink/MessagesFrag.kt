package mx.itesm.ETeam.Elink

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/*
Muestra a los usuarios a los que se les puede mandar mensaje, así como las conversaciones recientes.
Al seleccionar un mensaje se despliega un nuevo fragmento con la conversación completa y se habilita la
opción de enviar mensajes.
Autor: Alejandro Torices
 */
class MessagesFrag : Fragment() {
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

}