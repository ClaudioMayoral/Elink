package mx.itesm.ETeam.Elink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import mx.itesm.ETeam.Elink.databinding.ActivitySharkTypeScreenBinding
import mx.itesm.ETeam.Elink.databinding.ActivitySheepTypeScreenBinding

class SheepTypeScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySheepTypeScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySheepTypeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarBotones()
    }

    private fun configurarBotones() {
        binding.buttonSheepy.setOnClickListener {
            val intSignup = Intent(this, SignupScreen::class.java)
            intSignup.putExtra("userType", "sheep")
            intSignup.putExtra("username", intent.getStringExtra("username").toString())
            startActivity(intSignup)
        }
    }
}