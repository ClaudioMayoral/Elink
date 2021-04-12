package mx.itesm.ETeam.Elink

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
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

        binding.btnRegistrarse2.setOnClickListener{
        }
    }
}