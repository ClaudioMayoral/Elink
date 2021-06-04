package mx.itesm.ETeam.Elink

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
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
Modificado por: Francisco Arenas
*/
class UserTypeScreen : AppCompatActivity()
{
    // Variables importantes
    private lateinit var binding: ActivityUserTypeScreenBinding
    var userType: String = ""
    private lateinit var process: ProgressDialog

    // Database & Storage
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dataBase: FirebaseDatabase
    private lateinit var user: FirebaseUser
    private lateinit var dbReference: DatabaseReference
    private var storageReferecne = FirebaseStorage.getInstance().reference
    private val storagePath = "ProfilePics/"

    // Permisos para fotografias
    private val CAMERA_REQUEST_CODE = 100
    private val STORAGE_REQUEST_CODE = 200
    private val IMAGE_PICK_CAMERA_CODE = 300
    private val IMAGE_PICK_GALLERY_CODE = 400
    private lateinit var cameraPermission: Array<String>
    private lateinit var storagePermission: Array<String>
    private var image_uri: Uri? = null

    private val PICK_IMAGE:Int = 100;
    private var profilePic:Uri = Uri.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cameraPermission = arrayOf(android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

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