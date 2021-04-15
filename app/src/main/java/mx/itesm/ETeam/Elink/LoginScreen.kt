package mx.itesm.ETeam.Elink

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import mx.itesm.ETeam.Elink.databinding.ActivityFirstScreenBinding
import mx.itesm.ETeam.Elink.databinding.ActivityLoginScreenBinding

class LoginScreen : AppCompatActivity() {

    private lateinit var binding: ActivityLoginScreenBinding
    private val RC_SIGN_IN: Int = 200
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        configurarBotones()
    }

    private fun configurarBotones() {
        binding.googleLogin.setOnClickListener{
            auntenticar()
        }


    }

    private fun auntenticar() {
        val proveedores = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(proveedores)
            .build(), RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            when (resultCode) {
                Activity.RESULT_OK ->{
                    val usuario = FirebaseAuth.getInstance().currentUser
                    println("Bienvenido: ${usuario.displayName})")
                    println("Correo: ${usuario.email}")
                    println("ID: ${usuario.uid}")
                    println("Imagen: ${usuario.photoUrl}")
                    //Lanzasr siguiente actividad
                }
                RESULT_CANCELED->{
                    println("Cancelado (back)")
                }
                else -> {
                    val response = IdpResponse.fromResultIntent(data)
                    println("Error: ${response?.error?.errorCode}")
                }
            }
        }
    }



}