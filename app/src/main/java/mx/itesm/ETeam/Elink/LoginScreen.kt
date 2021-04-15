package mx.itesm.ETeam.Elink

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import mx.itesm.ETeam.Elink.databinding.ActivityFirstScreenBinding
import mx.itesm.ETeam.Elink.databinding.ActivityLoginScreenBinding

/*
Pantalla de login. Se ofrecen distintas opciones para ingresar.
Autor: Alejandro Torices
 */
class LoginScreen : AppCompatActivity() {

    private lateinit var binding: ActivityLoginScreenBinding
    private val RC_SIGN_IN: Int = 200
    private lateinit var mAuth: FirebaseAuth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        auth = Firebase.auth
        configurarBotones()
    }

    private fun configurarBotones() {
        binding.buttonLogin.setOnClickListener{
            val email = binding.loginMail.text.toString()
            val password = binding.loginPassword.text.toString()
            autenticarEmail(email,password)
        }
        binding.googleLogin.setOnClickListener{
            auntenticarGoogle()
        }
    }

    private fun autenticarEmail(email:String, password:String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {

    }

    private fun auntenticarGoogle() {
        val proveedores = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
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
                    val intMasterScreen = Intent(this, masterScreen::class.java)
                    startActivity(intMasterScreen)
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

    companion object {
        private const val TAG = "EmailPassword"
    }

}