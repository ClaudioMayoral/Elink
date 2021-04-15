package mx.itesm.ETeam.Elink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import mx.itesm.ETeam.Elink.databinding.ActivitySignupScreenBinding

class SignupScreen : AppCompatActivity() {

    private lateinit var binding : ActivitySignupScreenBinding
    // Administra la información de sign-in
    private lateinit var mAuth: FirebaseAuth

    //  ActivityForResult
    private val RC_SIGN_IN: Int = 200
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()


    }

    private fun googleSignUp() {
        val proveedores = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
        startActivityForResult(AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(proveedores)
            .build(),
        RC_SIGN_IN)
    }




    private fun signUp() {
        val proveedores = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())

        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
            .setAvailableProviders(proveedores)
                .build(),
        RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            when(resultCode){
                RESULT_OK ->{
                    val usuario = FirebaseAuth.getInstance().currentUser

                    // Siguiente actividad
                    val intUserType = Intent(this, UserTypeScreen::class.java)
                    startActivity(intUserType)
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
}