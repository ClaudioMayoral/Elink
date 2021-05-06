package mx.itesm.ETeam.Elink.PostsRelated

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_post_creation.*
import mx.itesm.ETeam.Elink.R
import mx.itesm.ETeam.Elink.databinding.ActivityPostCreationBinding
import java.lang.Exception

// Autor: Francisco Arenas
// Clase que permite la creación de un post en la red
class PostCreation : AppCompatActivity() {

    private lateinit var binding: ActivityPostCreationBinding
    private lateinit var baseDatos: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    // Datos de usuario
    private lateinit var userName: String
    private lateinit var userMail: String
    private lateinit var userImage: String
    private lateinit var uid: String

    // Permisos para fotografias
    private val REQUEST_CODE = 200
    lateinit var acceso: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        baseDatos = FirebaseDatabase.getInstance()
        auth = Firebase.auth
        val image = obtenerImagen()

        obtenerDatos()
        configurarDropDown()
        configurarBotones(image.toString())
    }

    private fun obtenerDatos() {
        val user = auth.currentUser!!
        uid = user.uid
        val dbPath = baseDatos.getReference("Users")
        val query = dbPath.orderByChild("usermail").equalTo(userMail)
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    userName = "" + ds.child("username").value
                    userMail = "" + ds.child("usermail").value
                    userImage = "" + ds.child("dirImagen").value
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext, "Algo salió mal al momento de publicar tu post. Intenta más tarde.",
                    Toast.LENGTH_LONG).show()
            }

        }
        query.addValueEventListener(eventListener)
    }

    private fun configurarDropDown() {
        val type = resources.getStringArray(R.array.tipoPost)
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, type)
        binding.autoType.setAdapter(adapter)
    }

    private fun configurarBotones(image: String){
        binding.postButton.setOnClickListener{
            val postText = binding.post.text.toString().trim()
            val postType = binding.autoType.text.toString()
            if(revisarContenido(postText, postType)){
                if(image == ""){
                    subirDatos(postText, postType, "noImage")
                } else {
                    subirDatos(postText, postType, image)
                }
            }
        }

    }

    private fun subirDatos(postText: String, postType: String, image: String) {
        val timeStamp = System.currentTimeMillis().toString()
        val path = "Posts/post_$timeStamp"
        val pd = ProgressDialog(this)
        pd.setMessage("Publicando...")
        pd.show()

        if(image != "noImage"){
            val ref = FirebaseStorage.getInstance().reference.child(path)
            ref.putFile(Uri.parse(image)).addOnSuccessListener{ takeSnapshot ->
                if(takeSnapshot.storage.downloadUrl.isSuccessful){
                    val hashMap = HashMap<Any, String>()
                    hashMap["uid"] = uid
                    hashMap["username"] = userName
                    hashMap["usermail"] = userMail
                    hashMap["dirImagen"] = userImage
                    hashMap["postID"] = timeStamp
                    hashMap["postText"] = postText
                    hashMap["postType"] = postType
                    hashMap["postImage"] = image
                    hashMap["postTime"] = timeStamp

                    // Path to store post data
                    val ref2DB = FirebaseDatabase.getInstance().getReference("Posts")
                    ref2DB.child(timeStamp).setValue(hashMap).addOnSuccessListener {
                        pd.dismiss()
                        Toast.makeText(this, "Post publicado", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener{ exception: Exception ->
                        pd.dismiss()
                        Toast.makeText(this, ""+exception.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener{exception: Exception ->
                pd.dismiss()
                Toast.makeText(this, ""+exception.message, Toast.LENGTH_SHORT).show()
            }
        } else {
            val hashMap = HashMap<Any, String>()
            hashMap["uid"] = uid
            hashMap["username"] = userName
            hashMap["usermail"] = userMail
            hashMap["dirImagen"] = userImage
            hashMap["postID"] = timeStamp
            hashMap["postText"] = postText
            hashMap["postType"] = postType
            hashMap["postImage"] = image
            hashMap["postTime"] = timeStamp

            // Path to store post data
            val ref2DB = FirebaseDatabase.getInstance().getReference("Posts")
            ref2DB.child(timeStamp).setValue(hashMap).addOnSuccessListener {
                pd.dismiss()
                Toast.makeText(this, "Post publicado", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{ exception: Exception ->
                pd.dismiss()
                Toast.makeText(this, ""+exception.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun revisarContenido(postText: String, postType: String): Boolean {
        if(postText.isEmpty()){
            Toast.makeText(baseContext, "No seas tímido. Comparte algo con tus seguidores",
                    Toast.LENGTH_SHORT).show()
            return false
        } else if(postType.isEmpty()){
            Toast.makeText(baseContext, "No seleccionaste ningún tipo de post.",
                    Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun obtenerImagen() {
        binding.image.setOnClickListener{
            seleccionImagen()
        }
    }

    private fun seleccionImagen() {
        val opciones = arrayOf("Camara", "Galería")
        val builder  = AlertDialog.Builder(this)
        with(builder){
            setTitle("Elige una imagen desde")
            setItems(opciones) { _, which ->
                when (opciones[which]) {
                    "Camara" -> {
                        acceso = "Camara"
                        if(obtenerPermiso()){
                            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(cameraIntent, REQUEST_CODE)
                        }
                    }
                    "Galería" -> {
                        acceso = "Galería"
                        if(obtenerPermiso()){
                            val intent = Intent(Intent.ACTION_PICK)
                            intent.type = "image/*"
                            startActivityForResult(intent, REQUEST_CODE)
                        }
                    }
                }

            }
        }
        builder.create().show()
    }

    private fun seDioPermiso(): Boolean{
        var permiso = false
        when(acceso) {
            "Camara" -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    permiso = true
                }
            }
            "Galería" -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    permiso = true
                }
            }
        }
        return permiso
    }

    private fun obtenerPermiso(): Boolean {
        println("hola")
        var permiso = true
        if(!seDioPermiso() && acceso == "Camara"){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this as Activity, android.Manifest.permission.CAMERA)) {
                mostrarDialogoNegacion()
            } else {
                ActivityCompat.requestPermissions(this as Activity, arrayOf(android.Manifest.permission.CAMERA), REQUEST_CODE)
            }
            permiso = false
        } else if(!seDioPermiso() && acceso == "Galería"){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this as Activity, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                mostrarDialogoNegacion()
            } else {
                ActivityCompat.requestPermissions(this as Activity, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE)
            }
            permiso = false
        }
        return permiso
    }

    private fun mostrarDialogoNegacion(){
        AlertDialog.Builder(this)
            .setTitle("Permiso negado")
            .setMessage("para poder subir una fotografia es necesario habilitar los permisos de camara y galeria. Puedes hacerlo desde la app de Ajustes.")
            .setPositiveButton("Ir a Ajustes"
            ) { _, _ ->
                // send to app settings if permission is denied permanently
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancelar",null)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(acceso){
            "Camara" -> {
                if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && data != null){
                    binding.image.setImageURI(data.data)
                }
            }
            "Galería" -> {
                if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
                    binding.image.setImageURI(data?.data) // handle chosen image
                }
            }
        }
    }
}