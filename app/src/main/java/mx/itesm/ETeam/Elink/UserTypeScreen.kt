package mx.itesm.ETeam.Elink

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_type_screen.*
import mx.itesm.ETeam.Elink.databinding.ActivityUserTypeScreenBinding
import java.util.jar.Manifest

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
    private val PICK_IMAGE:Int = 100;
    private var profilePic:Uri = Uri.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserTypeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSignup.setOnClickListener{
            crearCuenta()
        }
        configurarBotones()
    }

    private fun configurarBotones() {
        binding.btnSelectShark.setOnClickListener {
            userType = "shark"
            habilitarBoton()
        }

        binding.btnSelectSheep.setOnClickListener {
            userType = "sheep"
            habilitarBoton()
        }

        binding.etNombreUsuario.setOnClickListener {
            habilitarBoton()
        }

        binding.imgProfilePicture.setOnClickListener {
            abrirGaleria()
        }
    }

    private fun abrirGaleria() {
        val intGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(intGallery, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== RESULT_OK && requestCode== PICK_IMAGE){
            profilePic = data?.data!!
            binding.imgProfilePicture.setImageURI(profilePic)

        }else{
            Toast.makeText(baseContext, "No ha sido posible cargar la imagen.", Toast.LENGTH_SHORT).show()
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
            intSharkType.putExtra("profilePic",profilePic.toString())
            startActivity(intSharkType)
        }else{
            val intSheepType = Intent(this, SheepTypeScreen::class.java)
            intSheepType.putExtra("username", binding.etNombreUsuario.text.toString())
            intSheepType.putExtra("profilePic", profilePic.toString())
            startActivity(intSheepType)
        }
    }

}