package mx.itesm.ETeam.Elink

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import mx.itesm.ETeam.Elink.PostsRelated.SharkPostAdapter
import mx.itesm.ETeam.Elink.PostsRelated.PostData
import java.util.*
import kotlin.collections.ArrayList

// Autor: Francisco Arenas
// Clase que permite ver ver los posts
class PostSharkFrag: Fragment()
{
    // Instance variables
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterPost: SharkPostAdapter
    private lateinit var idList: ArrayList<String>
    private lateinit var postList: ArrayList<PostData>
    private lateinit var searchQuery: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?
                              , savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_post_shark, container, false)
        val layoutManager = LinearLayoutManager(activity)
        firebaseAuth = FirebaseAuth.getInstance()
        recyclerView = view.findViewById(R.id.postsRecyclerView)
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true
        recyclerView.layoutManager = layoutManager
        idList = arrayListOf<String>()
        postList = arrayListOf<PostData>()
        val button = view.findViewById<Button>(R.id.searchButton)

        buscarPost(button, view)
        obtenerSeguidores()

        return view
    }

    private fun obtenerSeguidores() {
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        val dbReference = FirebaseDatabase.getInstance().getReference("Follow").child(userID!!)
                            .child("following")

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                idList.clear()
                for (ds in dataSnapshot.children) {
                    idList.add(ds.key!!)
                }
                cargarPosts()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(activity, "" + databaseError.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        dbReference.addListenerForSingleValueEvent(postListener)
    }

    private fun buscarPost(button: Button?, view: View) {
        button?.setOnClickListener {
            searchQuery = view.findViewById(R.id.etBuscarPost)
            val query = searchQuery.text.toString()
            if (query == "") {
                cargarPosts()
                Toast.makeText(activity, "Busqueda no valida", Toast.LENGTH_SHORT).show()
            } else {
                val dBreference = FirebaseDatabase.getInstance().getReference("Posts")
                val postListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        postList.clear()
                        for (ds in dataSnapshot.children) {
                            val postdata = ds.getValue(PostData::class.java)!!

                            if (postdata.postType.toLowerCase(Locale.ROOT)
                                    .contains(query.toLowerCase(Locale.ROOT))
                                || postdata.projectType.toLowerCase(Locale.ROOT)
                                    .contains(query.toLowerCase(Locale.ROOT))
                                || postdata.projectName.toLowerCase(Locale.ROOT)
                                    .contains(query.toLowerCase(Locale.ROOT))
                            ) {
                                postList.add(postdata)
                            }
                            adapterPost = activity?.let { SharkPostAdapter(it.applicationContext, postList) }!!
                            recyclerView.adapter = adapterPost
                        }

                        if(postList.isEmpty()){
                            cargarPosts()
                            Toast.makeText(activity, "Busqueda no valida", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(activity, "" + databaseError.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                dBreference.addValueEventListener(postListener)
            }
        }
    }

    private fun cargarPosts() {
        val dBreference = FirebaseDatabase.getInstance().getReference("Posts")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList.clear()
                for(ds in dataSnapshot.children){
                    val postdata = ds.getValue(PostData::class.java)!!

                    for (id in idList){
                        if(postdata.uid == id){
                            postList.add(postdata)
                        }
                    }
                    adapterPost = activity?.let { SharkPostAdapter(it.applicationContext, postList) }!!
                    recyclerView.adapter = adapterPost
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(activity, ""+databaseError.message, Toast.LENGTH_SHORT).show()
            }
        }
        dBreference.addValueEventListener(postListener)
    }

}