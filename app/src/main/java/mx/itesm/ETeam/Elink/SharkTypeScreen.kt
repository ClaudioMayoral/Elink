package mx.itesm.ETeam.Elink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import mx.itesm.ETeam.Elink.databinding.ActivitySharkTypeScreenBinding
import java.util.*
import kotlin.collections.HashMap

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
            val checkAmbiental = binding.categoriaAmbiental.isChecked
            val checkTecnologia = binding.categoriaTec.isChecked
            val checkSocial = binding.categoriaSocial.isChecked
            val checkEntretenimiento = binding.categoriaEntertainment.isChecked
            val checkLifeStyle = binding.categoriaLifestyle.isChecked

            val hashMap = HashMap<String, Boolean>()
            hashMap["ambiental"] = checkAmbiental
            hashMap["tecnologia"] = checkTecnologia
            hashMap["social"] = checkSocial
            hashMap["entretenimiento"] = checkEntretenimiento
            hashMap["lifestyle"] = checkLifeStyle

            val intSignup = Intent(this, SignupScreen::class.java)
            intSignup.putExtra("userType", "shark")
            intSignup.putExtra("username", intent.getStringExtra("username").toString())
            intSignup.putExtra("userPreferences", hashMap)
            startActivity(intSignup)
        }

    }
}