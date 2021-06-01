package mx.itesm.ETeam.Elink

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import mx.itesm.ETeam.Elink.PostsRelated.PostAdapter
import mx.itesm.ETeam.Elink.PostsRelated.PostCreation
import mx.itesm.ETeam.Elink.PostsRelated.PostData

class PostSheepyFrag: Fragment() {

    // Instance variables
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterPost: PostAdapter
    private lateinit var postList: ArrayList<PostData>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?
                              , savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_post_sheepy, container, false)
        val layoutManager = LinearLayoutManager(activity)
        firebaseAuth = FirebaseAuth.getInstance()
        recyclerView = view.findViewById(R.id.postsRecyclerView)
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true
        recyclerView.layoutManager = layoutManager

        postList = arrayListOf<PostData>()


        val button = view.findViewById<Button>(R.id.searchButton)

        buscarPost(button, view)
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
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList.clear()
                for(ds in dataSnapshot.children){
                    val postdata = ds.getValue(PostData::class.java)!!
                    postList.add(postdata)
                    adapterPost = activity?.let { PostAdapter(it.applicationContext, postList) }!!
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