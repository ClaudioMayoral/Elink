package mx.itesm.ETeam.Elink

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import mx.itesm.ETeam.Elink.PostsRelated.SharkPostAdapter
import mx.itesm.ETeam.Elink.PostsRelated.PostCreation
import mx.itesm.ETeam.Elink.PostsRelated.PostData
import mx.itesm.ETeam.Elink.PostsRelated.SheepPostAdapter

/*
Muestra las publicaciones de los usuarios, es el fragmento por default al iniciar la aplicación
Autor: Alejandro Torices
Modificado por: Francisco Arenas
 */
class PostsSheepFrag : Fragment() {

    //private var adapter: RecyclerView.Adapter<PostAdapter.ViewHolder>?= null
    //private var layoutManager: RecyclerView.LayoutManager?= null
    //private lateinit var firebaseAuth: FirebaseAuth
    //lateinit var postList: List<PostData>

    // Instance variables
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterPost: SheepPostAdapter
    private lateinit var postList: ArrayList<PostData>

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?
                                                       , savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_posts_sheep, container, false)
        val layoutManager = LinearLayoutManager(activity)
        firebaseAuth = FirebaseAuth.getInstance()
        recyclerView = view.findViewById(R.id.postsRecyclerView)
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true
        recyclerView.layoutManager = layoutManager

        postList = arrayListOf<PostData>()
        val button = view.findViewById<FloatingActionButton>(R.id.addButton)
        crearPost(button)
        cargarPosts()

        return view
    }

    private fun crearPost(button: FloatingActionButton) {
        button.setOnClickListener{
            val intActivity = Intent(activity, PostCreation::class.java)
            startActivity(intActivity)
        }
    }

    private fun cargarPosts() {
        val dBreference = FirebaseDatabase.getInstance().getReference("Posts")
        val currentUser = FirebaseAuth.getInstance().currentUser!!
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList.clear()
                for(ds in dataSnapshot.children){
                    val postdata = ds.getValue(PostData::class.java)!!
                    if(postdata.uid == currentUser.uid){
                        postList.add(postdata)
                    }
                    adapterPost = activity?.let { SheepPostAdapter(it.applicationContext, postList) }!!
                    recyclerView.adapter = adapterPost
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(activity, ""+databaseError.message, Toast.LENGTH_SHORT).show()
            }
        }
        dBreference.addValueEventListener(postListener)
    }

    /*
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        buttonCall()

        postsRecyclerView.apply{
            layoutManager = LinearLayoutManager(activity)
            (layoutManager as LinearLayoutManager).stackFromEnd = true
            (layoutManager as LinearLayoutManager).reverseLayout = true

            postsRecyclerView.layoutManager = layoutManager
            Toast.makeText(activity, "Entre", Toast.LENGTH_SHORT).show()
            loadPosts()
        }
    }

    private fun buttonCall() {
        addButton.setOnClickListener{
            val intActivity = Intent(activity, PostCreation::class.java)
            startActivity(intActivity)
        }
    }

    private fun loadPosts() {
        val ref = FirebaseDatabase.getInstance().getReference("Posts")

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList = emptyList()

                for(ds in dataSnapshot.children){
                    val modelPost = ds.getValue<PostData>(PostData::class.java)!!
                    postList.toMutableList().add(modelPost)

                    //adapter = PostAdapter(postArray)
                //    postsRecyclerView.adapter = PostAdapter(postArray)

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Toast.makeText(activity, ""+databaseError.message, Toast.LENGTH_SHORT).show()
            }
        }
        ref.addValueEventListener(postListener)
    }

    */
}