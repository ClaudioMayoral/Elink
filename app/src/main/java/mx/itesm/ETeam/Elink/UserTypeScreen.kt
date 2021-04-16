package mx.itesm.ETeam.Elink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import mx.itesm.ETeam.Elink.databinding.ActivityUserTypeScreenBinding
/*
Es la primer pantalla de registro.
Los datos obtenidos no se registran en la base de datos hasta que la cuenta sea creada.
Estos datos se asocian con la uid que firebase le da a cada usuario, de tal forma que se asegura que cada
asociación sea única.
Autor: Alejandro Torices
 */
class UserTypeScreen : AppCompatActivity() {
    private lateinit var binding: ActivityUserTypeScreenBinding
    var userType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserTypeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSignup.setOnClickListener{
            crearCuenta()
        }

        binding.etNombreUsuario.setOnClickListener {
            habilitarBoton()
        }
        binding.btnSelectShark.setOnClickListener{
            habilitarBoton()
        }

        binding.btnSelectSheep.setOnClickListener {
            habilitarBoton()
        }
    }

    private fun habilitarBoton() {
        if((userType != " ") && (binding.etNombreUsuario.text.toString() != "")) {
            binding.buttonSignup.visibility = View.VISIBLE
        }
    }

    private fun crearCuenta() {
        if(userType == "shark"){
            val intSharkType = Intent(this, SharkTypeScreen::class.java)
            intSharkType.putExtra("username", binding.etNombreUsuario.text.toString())
            startActivity(intSharkType)
        }else{
            val intSheepType = Intent(this, SheepTypeScreen::class.java)
            intSheepType.putExtra("username", binding.etNombreUsuario.text.toString())
            startActivity(intSheepType)
        }
    }

    fun selectShark(v: View){
        userType = "shark"
    }

    fun selectSheep(v: View){
        userType = "sheep"
    }
}