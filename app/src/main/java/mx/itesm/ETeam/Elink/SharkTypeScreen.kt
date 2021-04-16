package mx.itesm.ETeam.Elink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import mx.itesm.ETeam.Elink.databinding.ActivitySharkTypeScreenBinding

class SharkTypeScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySharkTypeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySharkTypeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarBotones()
    }

    private fun configurarBotones() {
        binding.buttonSignup.setOnClickListener {
            val intSignup = Intent(this, SignupScreen::class.java)
            intSignup.putExtra("userType", "shark")
            intSignup.putExtra("username", intent.getStringExtra("username").toString())
            startActivity(intSignup)
        }
    }
}