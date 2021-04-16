package mx.itesm.ETeam.Elink

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.getInstance
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import mx.itesm.ETeam.Elink.databinding.FragmentProfileBinding

/*
Muestra el perfil del usuario actual
Autor: Alejandro Torices, Claudio Mayoral
 */
class ProfileFrag : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        binding = FragmentProfileBinding.inflate(layoutInflater)

    }

    private fun configurarBotones() {
        binding.btnLogOut.setOnClickListener{
            println("Bye Bye")
            //Firebase.auth.signOut()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)
        binding.btnLogOut.setOnClickListener{
            println("Bye Bye")
        }
        configurarBotones()
        println("reachable")
        return view
    }

}