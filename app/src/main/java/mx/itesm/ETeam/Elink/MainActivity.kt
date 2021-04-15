package mx.itesm.ETeam.Elink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import mx.itesm.ETeam.Elink.databinding.ActivityFirstScreenBinding
import mx.itesm.ETeam.Elink.databinding.ActivityUserTypeScreenBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserTypeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserTypeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //configurarBotones()

    }
/*
    private fun configurarBotones() {
        binding.btnRegistrarse.setOnClickListener{
            val intRegistrarse = Intent(baseContext, SignupScreen::class.java)
            startActivity(intRegistrarse)
        }

        binding.btnIngresar.setOnClickListener{
            val intIngresar = Intent(baseContext, LoginScreen::class.java)
            startActivity(intIngresar)
        }
    }*/


}