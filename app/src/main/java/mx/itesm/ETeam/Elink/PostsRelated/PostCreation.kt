package mx.itesm.ETeam.Elink.PostsRelated

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
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
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import mx.itesm.ETeam.Elink.R
import mx.itesm.ETeam.Elink.databinding.ActivityPostCreationBinding
import java.lang.Exception

// Autor: Francisco Arenas
// Clase que permite la creación de un post en la red
class PostCreation : AppCompatActivity() {

    // Creacion de variables importantes
    private lateinit var binding: ActivityPostCreationBinding
    private lateinit var baseDatos: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    // Datos de usuario
    private var userName: String? = null
    private var userMail: String? = null
    private var userImage: String? = null
    private var uid: String? = null
    private var projectName: String? = null
    private var projectType: String? = null

    // Permisos para fotografias
    private val CAMERA_REQUEST_CODE = 100
    private val STORAGE_REQUEST_CODE = 200
    private val IMAGE_CAMERA_CODE = 300
    private val IMAGE_GALLERY_CODE = 400
    private lateinit var cameraPermission: Array<String>
    private lateinit var storagePermission: Array<String>
    private var image_uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        baseDatos = FirebaseDatabase.getInstance()
        auth = Firebase.auth

        cameraPermission = arrayOf(android.Manifest.permission.CAMERA,
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        obtenerImagen()
        configurarDropDown()
        obtenerDatos()
        configurarBotones()
    }

    private fun configurarDropDown() {
        val type = resources.getStringArray(R.array.tipoPost)
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, type)
        binding.autoType.setAdapter(adapter)
    }

    private fun configurarBotones(){
        //obtenerImagen()
        binding.postButton.setOnClickListener{
            val postText = binding.post.text.toString().trim()
            val postType = binding.autoType.text.toString()
            if(revisarDatos(postText, postType)){
                if(image_uri == null){
                    subirDatos(postText, postType, "noImage")
                    //finish()
                } else {
                    subirDatos(postText, postType, image_uri.toString())
                    //finish()
                }
            }
        }
    }

    private fun obtenerDatos() {
        val user = auth.currentUser!!
        uid = user.uid
        userMail = user.email!!
        val dbPath = baseDatos.getReference("Users")
        val query = dbPath.orderByChild("userMail").equalTo(userMail)
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    //TEST
                    projectName = "" + ds.child("Project/nombreDelProyecto").value
                    projectType = "" + ds.child("Project/categoria").value

                    userName = "" + ds.child("username").value
                    userMail = "" + ds.child("userMail").value
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

    private fun subirDatos(postText: String, postType: String, image: String) {
        val timeStamp = System.currentTimeMillis().toString()
        val path = "Posts/post_$timeStamp"
        val pd = ProgressDialog(this)
        pd.setMessage("Publicando...")
        pd.show()

        if(image != "noImage"){
            val ref = FirebaseStorage.getInstance().reference.child(path)
            ref.putFile(Uri.parse(image)).addOnSuccessListener{ takeSnapshot ->

                val uriTask = takeSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val downloadUri = uriTask.result.toString()

                if(uriTask.isSuccessful){
                    val hashMap = HashMap<Any, String?>()
                    hashMap["uid"] = uid
                    hashMap["username"] = userName
                    hashMap["userMail"] = userMail
                    hashMap["dirImagen"] = userImage
                    hashMap["postID"] = timeStamp
                    hashMap["postText"] = postText
                    hashMap["postType"] = postType
                    hashMap["postImage"] = downloadUri
                    hashMap["postTime"] = timeStamp

                    //TEST
                    hashMap["projectName"] = projectName
                    hashMap["projectType"] = projectType

                    // Path to store post data
                    val ref2DB = FirebaseDatabase.getInstance().getReference("Posts")
                    ref2DB.child(timeStamp).setValue(hashMap).addOnSuccessListener {
                        pd.dismiss()
                        Toast.makeText(this, "Post publicado", Toast.LENGTH_SHORT).show()
                        finish()
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
            val hashMap = HashMap<Any, String?>()
            hashMap["uid"] = uid
            hashMap["username"] = userName
            hashMap["userMail"] = userMail
            hashMap["dirImagen"] = userImage
            hashMap["postID"] = timeStamp
            hashMap["postText"] = postText
            hashMap["postType"] = postType
            hashMap["postImage"] = "noImage"
            hashMap["postTime"] = timeStamp

            //TEST
            hashMap["projectName"] = projectName
            hashMap["projectType"] = projectType


            // Path to store post data
            val ref2DB = FirebaseDatabase.getInstance().getReference("Posts")
            ref2DB.child(timeStamp).setValue(hashMap).addOnSuccessListener {
                pd.dismiss()
                Toast.makeText(this, "Post publicado", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener{ exception: Exception ->
                pd.dismiss()
                Toast.makeText(this, ""+exception.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun revisarDatos(postText: String, postType: String): Boolean {
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
                if(which == 0){
                    if(!revisarCameraPermission()){
                        println("permiso a camara: ${revisarCameraPermission()}")
                        solicitarCameraPermission()
                    } else {
                        obtenerDesdeCamara()
                    }
                }

                if(which == 1){
                    if(!revisarStoragePermission()){
                        println("permiso a galeria: ${revisarStoragePermission()}")
                        solicitarStoragePermission()
                    } else {
                        obtenerDesdeGaleria()
                    }
                }
            }
        }
        builder.create().show()
    }

    private fun revisarStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED)
    }

    private fun solicitarStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE)
    }

    private fun revisarCameraPermission(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED)

        val storagePermission = ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED)

        return cameraPermission && storagePermission
    }

    private fun solicitarCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE)
    }

    private fun obtenerDesdeCamara(){
        val cv = ContentValues()
        cv.put(MediaStore.Images.Media.TITLE,"Tempo Pick")
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Tempo Descr")

        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(intent, IMAGE_CAMERA_CODE)
    }

    private fun obtenerDesdeGaleria(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_GALLERY_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){

            CAMERA_REQUEST_CODE -> {
                if(grantResults.isNotEmpty()){
                    val cameraAceptada = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAceptado = grantResults[1] == PackageManager.PERMISSION_GRANTED

                    if(cameraAceptada && storageAceptado){
                        obtenerDesdeCamara()
                    } else {
                        Toast.makeText(this,
                        "Se necesita el acceso a la cámara y al almacenamiento para subir una imágen.",
                        Toast.LENGTH_LONG).show()
                    }
                }
            }

            STORAGE_REQUEST_CODE -> {
                if(grantResults.isNotEmpty()){
                    val storageAceptado = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if(storageAceptado){
                        obtenerDesdeGaleria()
                    } else {
                        Toast.makeText(this,
                        "Se necesita al almacenamiento para subir una imágen.",
                        Toast.LENGTH_LONG).show()

                    }
                }
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if(resultCode == RESULT_OK){

            if(requestCode == IMAGE_GALLERY_CODE){
                if (data != null) {
                    image_uri = data.data
                }
                binding.image.setImageURI(image_uri)
            } else if(requestCode == IMAGE_CAMERA_CODE){
                binding.image.setImageURI(image_uri)
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}