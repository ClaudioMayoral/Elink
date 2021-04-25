package mx.itesm.ETeam.Elink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import mx.itesm.ETeam.Elink.databinding.ActivitySheepTypeScreenBinding

class SheepTypeScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySheepTypeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySheepTypeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarDropDown()

        configurarBotones()
    }

    private fun configurarDropDown() {
        val category = resources.getStringArray(R.array.categorias)
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, category)
        binding.autoCategory.setAdapter(adapter)
    }

    private fun configurarBotones() {
        binding.buttonSheepy.setOnClickListener {
            val hashMap = HashMap<String, String>()
            val nombreDelProyecto = binding.projectName.text.toString()
            val descripcionProyecto = binding.projectDescription.text.toString()
            val moneyGoal = binding.moneyGoal.text.toString()
            val categoria = binding.autoCategory.text.toString()

            hashMap["nombreDeProyecto"] = nombreDelProyecto
            hashMap["descripcionDeProyecto"] = descripcionProyecto
            hashMap["metaMonetaria"] = moneyGoal
            hashMap["categoria"] = categoria

            val intSignup = Intent(this, SignupScreen::class.java)
            intSignup.putExtra("userType", "sheep")
            intSignup.putExtra("username", intent.getStringExtra("username").toString())
            intSignup.putExtra("userPreferences", hashMap)

            startActivity(intSignup)
        }
    }
}

