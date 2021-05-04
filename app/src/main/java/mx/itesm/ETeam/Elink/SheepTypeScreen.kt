package mx.itesm.ETeam.Elink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
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
            val nombreDelProyecto = binding.projectName.text.toString()
            val descripcionProyecto = binding.projectDescription.text.toString()
            val moneyGoal = binding.moneyGoal.text.toString()
            val categoria = binding.autoCategory.text.toString()

            if((nombreDelProyecto == "") or (descripcionProyecto =="") or (categoria == "") or (moneyGoal == "" )){
                Toast.makeText(baseContext,"Rellene todos los campos para continuar", Toast.LENGTH_SHORT).show()
            }else {

                val intSignup = Intent(this, SignupScreen::class.java)
                intSignup.putExtra("userType", "sheep")
                intSignup.putExtra("username", intent.getStringExtra("username").toString())
                intSignup.putExtra("profilePic", intent.getStringExtra("profilePic").toString())

                intSignup.putExtra("nombreDeProyecto", nombreDelProyecto)
                intSignup.putExtra("descripcionDeProyecto", descripcionProyecto)
                intSignup.putExtra("metaMonetaria", moneyGoal.toDouble())
                intSignup.putExtra("categoria", categoria)

                try {
                    startActivity(intSignup)
                }catch (e:NumberFormatException){
                    Toast.makeText(baseContext,"Introduzca una cantidad válida.", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }
}

