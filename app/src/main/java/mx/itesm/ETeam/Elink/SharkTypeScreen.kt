package mx.itesm.ETeam.Elink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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

            if(checkAmbiental or checkTecnologia or checkSocial or checkEntretenimiento or checkLifeStyle) {
                val intSignup = Intent(this, SignupScreen::class.java)
                intSignup.putExtra("userType", "shark")
                intSignup.putExtra("username", intent.getStringExtra("username").toString())
                intSignup.putExtra("profilePic", intent.getStringExtra("profilePic").toString())

                intSignup.putExtra("ambiental", checkAmbiental)
                intSignup.putExtra("tecnologia", checkTecnologia)
                intSignup.putExtra("social", checkSocial)
                intSignup.putExtra("entretenimiento", checkEntretenimiento)
                intSignup.putExtra("lifestyle", checkLifeStyle)

                startActivity(intSignup)
            }else{
                Toast.makeText(baseContext, "Escoga al menos una categoría para continuar.", Toast.LENGTH_SHORT).show()
            }
        }

    }
}