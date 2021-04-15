package mx.itesm.ETeam.Elink

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import mx.itesm.ETeam.Elink.databinding.ActivityUserTypeScreenBinding

class UserTypeScreen : AppCompatActivity() {
    private lateinit var binding: ActivityUserTypeScreenBinding
    lateinit var userType: String

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
        if(userType.isNotBlank() && binding.etNombreUsuario.text.toString() != "") {
            binding.buttonSignup.visibility = View.VISIBLE
        }
    }

    private fun crearCuenta() {
        TODO("Not yet implemented")
    }

    fun selectShark(v: View){
        userType = "shark"
    }

    fun selectSheep(v: View){
        userType = "sheep"
    }


}