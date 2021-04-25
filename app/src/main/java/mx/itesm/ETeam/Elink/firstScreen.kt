package mx.itesm.ETeam.Elink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import mx.itesm.ETeam.Elink.databinding.ActivityFirstScreenBinding

/*
Primer pantalla, se despliega en caso de que el usuario no esté logeado con su cuenta.
Muestra las opciones de crear una cuenta o iniciar sesión
Autor: Alejandro Torices
 */
class firstScreen : AppCompatActivity() {
    private lateinit var binding: ActivityFirstScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_screen)
        binding = ActivityFirstScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarBotones()
    }

    // Ignora el back a la pantalla anterior (pantalla en blanco)
    override fun onBackPressed() {
    }

    private fun configurarBotones() {
        binding.btnRegistrarse.setOnClickListener{
            val intRegistrarse = Intent(baseContext, UserTypeScreen::class.java)
            startActivity(intRegistrarse)
        }

        binding.btnIngresar.setOnClickListener{
            val intIngresar = Intent(baseContext, LoginScreen::class.java)
            startActivity(intIngresar)
        }
    }
}