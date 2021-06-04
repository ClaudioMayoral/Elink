package mx.itesm.ETeam.Elink

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import mx.itesm.ETeam.Elink.DataClasses.User
import mx.itesm.ETeam.Elink.PostsRelated.SheepPostAdapter

/*
Muestra el perfil del usuario shark
Autor: Francisco Arenas
 */
class SheepProfileFrag: Fragment()
{
    // Database & Storage
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dataBase: FirebaseDatabase
    private lateinit var user: FirebaseUser
    private lateinit var dbReference: DatabaseReference
    private var storageReferecne = FirebaseStorage.getInstance().reference
    private val storagePath = "ProfilePics/"

    // Vistas
    private lateinit var userImage: ImageView
    private lateinit var userType: TextView
    private lateinit var userName: TextView
    private lateinit var userMail: TextView
    private lateinit var process: ProgressDialog

    // Permisos para fotografias
    private val CAMERA_REQUEST_CODE = 100
    private val STORAGE_REQUEST_CODE = 200
    private val IMAGE_PICK_CAMERA_CODE = 300
    private val IMAGE_PICK_GALLERY_CODE = 400
    private lateinit var cameraPermission: Array<String>
    private lateinit var storagePermission: Array<String>
    private var image_uri: Uri? = null

    // PAra el recylcerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var idList: ArrayList<String>
    private lateinit var userList: ArrayList<User>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?
                              , savedInstanceState: Bundle?): View
    {
        val view = inflater.inflate(R.layout.fragment_sheep_profile, container, false)

        cameraPermission = arrayOf(android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        idList = arrayListOf<String>()
        userList = arrayListOf<User>()

        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true

        recyclerView = view.findViewById(R.id.followersRecyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        userAdapter = context?.let { UserAdapter(it, userList) }!!
        recyclerView.adapter = userAdapter

        val editButton = view.findViewById<FloatingActionButton>(R.id.editButton)
        val logoutButton = view.findViewById<Button>(R.id.btnLogOut)
        process = ProgressDialog(activity)

        colocarDatos(view)
        obtenerSeguidores()
        editarPerfil(editButton)
        salirApp(logoutButton)

        return view
    }

    private fun obtenerSeguidores() {
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        val dbReference = FirebaseDatabase.getInstance().getReference("Follow").child(userID!!)
            .child("followers")

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                idList.clear()
                for (ds in dataSnapshot.children) {
                    idList.add(ds.key!!)
                }
                mostrarUsuarios()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(activity, "" + databaseError.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        dbReference.addListenerForSingleValueEvent(postListener)
    }

    private fun llenarRecyclerView() {
        val currentID = firebaseAuth.currentUser?.uid
        val dbReference = FirebaseDatabase.getInstance().getReference("Follow")
            .child(currentID!!).child("followers")

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                idList.clear()
                for (ds in dataSnapshot.children) {
                    ds.key?.let { idList.add(it) }
                }
                mostrarUsuarios()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(activity, "" + databaseError.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        dbReference.addListenerForSingleValueEvent(postListener)
    }


    private fun mostrarUsuarios(){
        val dbReference = FirebaseDatabase.getInstance().getReference("Users")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userList.clear()

                for (ds in dataSnapshot.children) {
                    val userInList = ds.getValue(User::class.java)!!

                    for (id in idList) {
                        if(userInList.userID == id){
                            userList.add(userInList)
                        }
                    }
                }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(activity, "" + databaseError.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        dbReference.addValueEventListener(postListener)}

    private fun salirApp(logoutButton: Button) {
        logoutButton.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val intActivity = Intent(activity, firstScreen::class.java)
            startActivity(intActivity)
        }
    }

    private fun editarPerfil(button: FloatingActionButton?) {
        button?.setOnClickListener{
            mostrarOpciones()
        }
    }

    private fun colocarDatos(view: View){
        // Initialize database references
        dataBase = FirebaseDatabase.getInstance()
        dbReference = dataBase.getReference("Users")
        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth.currentUser!!

        // Views
        userImage = view.findViewById(R.id.ivProfImage)
        userName = view.findViewById(R.id.tvProfName)
        userType = view.findViewById(R.id.tvProftype)
        userMail = view.findViewById(R.id.tvProfMail)

        val query = dbReference.orderByChild("userMail").equalTo(user.email)
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    val name = ""+ds.child("username").value
                    val mail = ""+ds.child("userMail").value
                    val type = ""+ds.child("usertype").value
                    val image = ""+ds.child("dirImagen").value

                    val nameFormat = "Nombre de usuario: $name"
                    val mailFormat = "Tipo de usuario: $type"
                    val typeFormat = "Mail: $mail"

                    userName.text = nameFormat
                    userType.text = typeFormat
                    userMail.text = mailFormat
                    try {
                        if(image == "" || image == "null" || image.isEmpty()){
                            Picasso.get().load(R.drawable.icon_profile).into(userImage)
                        } else {
                                Picasso.get().load(image).into(userImage)
                        }
                    } catch (e: Exception){
                        Picasso.get().load(R.drawable.icon_profile).into(userImage)
                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        query.addValueEventListener(postListener)
    }

    private fun mostrarOpciones() {
        val options = arrayOf("Editar foto de perfil", "Editar nombre de usuario",
            "Editar correo electrónico")

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Elige una opción")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> {
                    process.setMessage("Cambiando foto de perfil")
                    seleccionarImagen()
                }
                1 -> {
                    process.setMessage("Cambiando nombre de usuario")
                    mostrarDialogoActualizacion("username")
                }
                2 -> {
                    process.setMessage("Cambiando contraseña")
                    mostrarDialogoActualizacion("userMail")
                }
            }
        }

        builder.create().show()
    }

    private fun mostrarDialogoActualizacion(dbKey: String) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Actualizando $dbKey")

        val linearLayout = LinearLayout(activity)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(10, 10, 10, 10)

        val newEditText = EditText(activity)
        newEditText.hint = "Introduce $dbKey"
        linearLayout.addView(newEditText)

        builder.setView(linearLayout)
        builder.setPositiveButton("Actualizar") { dialog, which ->
            val text = newEditText.text.toString().trim()
            if (!TextUtils.isEmpty(text)){
                process.show()
                val hashMap = HashMap<String, Any>()
                hashMap[dbKey] = text
                dbReference.child(user.uid).updateChildren(hashMap).addOnSuccessListener {
                    process.dismiss()
                    Toast.makeText(activity, "Actualizado...", Toast.LENGTH_SHORT).show()
                } .addOnFailureListener {
                    process.dismiss()
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "Introduce: $dbKey", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, which ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun seleccionarImagen() {
        val options = arrayOf("Camara", "Galería")

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Elige imagen desde")
        builder.setItems(options) { _, which ->
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

        builder.create().show()
    }

    private fun revisarStoragePermission(): Boolean {
        return activity?.let {
            ContextCompat.checkSelfPermission(
                it, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) } ==
                (PackageManager.PERMISSION_GRANTED)
    }

    private fun solicitarStoragePermission() {
        activity?.let { ActivityCompat.requestPermissions(it, storagePermission, STORAGE_REQUEST_CODE) }
    }

    private fun revisarCameraPermission(): Boolean {
        val cameraPermission = activity?.let {
            ContextCompat.checkSelfPermission(
                it, android.Manifest.permission.CAMERA) } == (PackageManager.PERMISSION_GRANTED)

        val storagePermission = activity?.let {
            ContextCompat.checkSelfPermission(
                it, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) } ==
                (PackageManager.PERMISSION_GRANTED)

        return cameraPermission && storagePermission
    }

    private fun solicitarCameraPermission() {
        activity?.let { ActivityCompat.requestPermissions(it, cameraPermission, CAMERA_REQUEST_CODE) }
    }

    private fun obtenerDesdeCamara(){
        val cv = ContentValues()
        cv.put(MediaStore.Images.Media.TITLE,"Tempo Pick")
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Tempo Descr")

        image_uri = activity?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE)
    }

    private fun obtenerDesdeGaleria(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE)
    }


    private fun subirFoto(imageUri: Uri?) {
        process.show()
        val filePath = storagePath + user.uid
        val secondStorageReference = storageReferecne.child(filePath)

        secondStorageReference.putFile(imageUri!!).addOnSuccessListener {
            val uriTask = it.storage.downloadUrl
            while (!uriTask.isSuccessful);
            val downloadUri = uriTask.result

            if(uriTask.isSuccessful){
                val hashMap = HashMap<String, Any>()
                hashMap["dirImagen"] = downloadUri.toString()
                dbReference.child(user.uid).updateChildren(hashMap).addOnSuccessListener {
                    process.dismiss()
                    Toast.makeText(activity, "Imagen Actualizada...", Toast.LENGTH_SHORT).show()
                } .addOnFailureListener {
                    process.dismiss()
                    Toast.makeText(activity, "Error al subir imagen...", Toast.LENGTH_SHORT).show()
                }
            } else {
                process.dismiss()
                Toast.makeText(activity, "Ocurrió un error", Toast.LENGTH_SHORT).show()
            }

        } .addOnFailureListener {
            process.dismiss()
            Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
        }

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
                        Toast.makeText(activity,
                            "Se necesita el acceso a la cámara y al almacenamiento para subir una imágen.",
                            Toast.LENGTH_LONG).show()
                    }
                }
            }

            STORAGE_REQUEST_CODE -> {
                if(grantResults.isNotEmpty()){
                    val storageAceptado = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if(storageAceptado){
                        obtenerDesdeGaleria()
                    } else {
                        Toast.makeText(activity,
                            "Se necesita al almacenamiento para subir una imágen.",
                            Toast.LENGTH_LONG).show()

                    }
                }
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if(resultCode == AppCompatActivity.RESULT_OK){

            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                image_uri = data?.data
                subirFoto(image_uri)
            }

            if(requestCode == IMAGE_PICK_CAMERA_CODE){
                subirFoto(image_uri)
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}