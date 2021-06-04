package mx.itesm.ETeam.Elink

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
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
import mx.itesm.ETeam.Elink.PostsRelated.PostData
import mx.itesm.ETeam.Elink.PostsRelated.SharkPostAdapter

// Autor: Francisco Arenas
// Clase que permite enseñar ver el perfil de un Sheep
class ShowSheepInfo: Fragment()
{
    // Database
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var dataBase: FirebaseDatabase

    // Vistas
    private lateinit var userImage: ImageView
    private lateinit var userName: TextView
    private lateinit var projectName: TextView
    private lateinit var projectType: TextView
    private lateinit var projectDescr: TextView

    // Para el recylcerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: SharkPostAdapter
    private lateinit var postList: ArrayList<PostData>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?
                              , savedInstanceState: Bundle?): View
    {
        val view = inflater.inflate(R.layout.fragment_show_sheepprofile_info, container, false)
        val prefs = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        val userSendedID = prefs?.getString("profileiD", "none")

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        postList = arrayListOf<PostData>()

        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true

        recyclerView = view.findViewById(R.id.userPostRecyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        postAdapter = context?.let { SharkPostAdapter(it, postList) }!!
        recyclerView.adapter = postAdapter

        configureView(view, userSendedID!!)

        return view
    }

    private fun configureView(view: View, userSendedID: String) {
        colocarDatos(view, userSendedID)
        cargarPosts(userSendedID)
    }

    private fun cargarPosts(userSendedID: String) {
        val dBreference = FirebaseDatabase.getInstance().getReference("Posts")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList.clear()
                for(ds in dataSnapshot.children){
                    val postdata = ds.getValue(PostData::class.java)!!

                    if(postdata.uid == userSendedID){
                        postList.add(postdata)
                    }
                    postAdapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, ""+databaseError.message, Toast.LENGTH_SHORT).show()
            }
        }
        dBreference.addValueEventListener(postListener)
    }

    private fun colocarDatos(view: View, userSendedID: String){
        // Initialize database references
        dataBase = FirebaseDatabase.getInstance()
        val dbReference = dataBase.getReference("Users").child(userSendedID)
        val dbReference2 = dataBase.getReference("Users").child(userSendedID).child("Project")

        /*
        dbReference.child("Users").child(userSendedID).child("Project").get().addOnSuccessListener {
                    val projectNameForm = "Nombre del poryecto: ${it.child("nombreDelProyecto").value.toString()}"
                    val projectTypeForm = "Categoria: ${it.child("categoria").value.toString()}"
                    val projectDescriptionForm = it.child("descripcionProyecto").value.toString()

                    projectName.text = projectNameForm
                    projectType.text = projectTypeForm
                    projectDescr.text = projectDescriptionForm

                }.addOnFailureListener{
                    Toast.makeText(context, "" + it.toString(), Toast.LENGTH_SHORT).show()
                }
         */

        // Views
        userImage = view.findViewById(R.id.ivProfImage)
        userName = view.findViewById(R.id.nombreUsuario)
        projectName = view.findViewById(R.id.nombreProyecto)
        projectType = view.findViewById(R.id.tipoProyecto)
        projectDescr = view.findViewById(R.id.descripcion)

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (context == null){
                    null
                }

                val sendedUser = snapshot.getValue(User::class.java)
                val name = sendedUser?.username
                val image = sendedUser?.dirImagen
                val nameFormat = "Nombre de usuario: $name"
                userName.text = nameFormat

                try {
                    if (image != null) {
                        if(image == "" || image == "null" || image.isEmpty()){
                            Picasso.get().load(R.drawable.icon_profile).into(userImage)
                        } else {
                            Picasso.get().load(image).into(userImage)
                        }
                    }
                } catch (e: Exception){
                    Picasso.get().load(R.drawable.icon_profile).into(userImage)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, ""+error.message, Toast.LENGTH_SHORT).show()
            }
        }
        dbReference.addValueEventListener(postListener)

        val postListener2 = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (context == null){
                    null
                }

                var formaterName = snapshot.child("nombreDelProyecto").value.toString()
                var formaterType = snapshot.child("categoria").value.toString()
                val formaterDescr = snapshot.child("descripcionProyecto").value.toString()

                formaterName = "Nombre del proyecto $formaterName"
                formaterType = "Categoria: $formaterType"

                projectName.text = formaterName
                projectType.text = formaterType
                projectDescr.text = formaterDescr

            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, ""+error.message, Toast.LENGTH_SHORT).show()
            }
        }
        dbReference2.addValueEventListener(postListener2)
    }
}