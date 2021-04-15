package mx.itesm.ETeam.Elink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import mx.itesm.ETeam.Elink.databinding.ActivityFirstScreenBinding
import mx.itesm.ETeam.Elink.databinding.ActivityUserTypeScreenBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFirstScreenBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        val usuario = mAuth.currentUser
        if(usuario != null){
            println("Bienvenido de vuelta: ${usuario.displayName})")
            println("Correo: ${usuario.email}")
            println("ID: ${usuario.uid}")
            println("Imagen: ${usuario.photoUrl}")
            //Lanzar directamente la segunda pantalla
            val intRegistrarse = Intent(baseContext, profileScreen::class.java)
            startActivity(intRegistrarse)
        }else{
            binding = ActivityFirstScreenBinding.inflate(layoutInflater)
            setContentView(binding.root)


            configurarBotones()
        }


    }

    private fun configurarBotones() {
        binding.btnRegistrarse.setOnClickListener{
            val intRegistrarse = Intent(baseContext, SignupScreen::class.java)
            startActivity(intRegistrarse)
        }

        binding.btnIngresar.setOnClickListener{
            val intIngresar = Intent(baseContext, LoginScreen::class.java)
            startActivity(intIngresar)
        }
    }


}