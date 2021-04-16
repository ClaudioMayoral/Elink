package mx.itesm.ETeam.Elink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import mx.itesm.ETeam.Elink.databinding.ActivitySignupScreenBinding
import android.widget.Toast

/*
Pantalla para hacer signup se ofrecen distintas opciones
Autor: Alejandro Torices
 */
class SignupScreen : AppCompatActivity() {

    private lateinit var binding : ActivitySignupScreenBinding
    // Administra la información de sign-in
    private lateinit var mAuth: FirebaseAuth
    private lateinit var auth: FirebaseAuth

    //  ActivityForResult
    private val RC_SIGN_IN: Int = 200
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        auth = Firebase.auth

        configurarBotones()

    }

    private fun configurarBotones() {
        binding.googleSignup.setOnClickListener {
            googleSignUp()
        }
        binding.buttonSignup.setOnClickListener{
            val email = binding.signupMail.text.toString()
            val password = binding.signupPassword.text.toString()
            val passwordConfirmation = binding.signupConfirmPassword.text.toString()
            if(verificarEntradas(email,password,passwordConfirmation)){
                signUp(email, password)
            }
        }
    }

    private fun verificarEntradas(email: String, password: String, passwordConfirmation: String) :Boolean{
        if (email.isEmpty() || password.isEmpty()|| passwordConfirmation.isEmpty()){
            Toast.makeText(baseContext, "Rellene todos los campos para continuar.",
                Toast.LENGTH_SHORT).show()
            return false
        }else if (password != passwordConfirmation){
            Toast.makeText(baseContext, "Las contraseñas no coinciden.",
                Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun googleSignUp() {
        val proveedores = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
        startActivityForResult(AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(proveedores)
            .build(),
        RC_SIGN_IN)
    }


    private fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        insertUserDataInDB()
    }

    private fun insertUserDataInDB() {
        val userID = mAuth.currentUser.uid

        val intMasterScreen = Intent(this, masterScreen::class.java)
        startActivity(intMasterScreen)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            when(resultCode){
                RESULT_OK ->{
                    val usuario = FirebaseAuth.getInstance().currentUser

                    // Siguiente actividad
                    insertUserDataInDB()
                }
                RESULT_CANCELED ->{
                    // signup cancelado
                }
                else -> {
                    val response =IdpResponse.fromResultIntent(data)
                    println("Error: ${response?.error?.errorCode}")
                }
            }
        }
    }
    companion object {
        private const val TAG = "EmailPassword"
    }
}