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

// Autor: Francisco Arenas
// Clase que permite enseñar ver el perfil de un Shark
class ShowSharkInfo: Fragment()
{
    // Database
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var dataBase: FirebaseDatabase

    // Vistas
    private var userID: String? = null
    private lateinit var userImage: ImageView
    private lateinit var userName: TextView

    // Para el recylcerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var idList: ArrayList<String>
    private lateinit var userList: ArrayList<User>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?
                              , savedInstanceState: Bundle?): View
    {
        val view = inflater.inflate(R.layout.fragment_show_sharkprofile_info, container, false)
        val prefs = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        val userSendedID = prefs?.getString("profileiD", "none")

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        idList = arrayListOf<String>()
        userList = arrayListOf<User>()

        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true

        recyclerView = view.findViewById(R.id.userFollowingRecyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        userAdapter = context?.let { UserAdapter(it, userList) }!!
        recyclerView.adapter = userAdapter

        configureView(view, userSendedID!!)

        return view
    }

    private fun configureView(view: View, userSendedID: String) {
        colocarDatos(view, userSendedID)
        obtenerSeguidores(userSendedID)
    }

    private fun obtenerSeguidores(userSendedID: String) {
        val dbReference = FirebaseDatabase.getInstance().getReference("Follow").child(userSendedID)
            .child("following")

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
        dbReference.addValueEventListener(postListener)
    }

    private fun colocarDatos(view: View, userSendedID: String){
        // Initialize database references
        dataBase = FirebaseDatabase.getInstance()
        val dbReference = dataBase.getReference("Users").child(userSendedID)

        // Views
        userImage = view.findViewById(R.id.ivProfImage)
        userName = view.findViewById(R.id.nombreUsuario)

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
                Toast.makeText(activity, ""+error.message, Toast.LENGTH_SHORT).show()
            }
        }
        dbReference.addValueEventListener(postListener)
    }
}