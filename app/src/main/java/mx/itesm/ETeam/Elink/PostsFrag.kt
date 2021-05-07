package mx.itesm.ETeam.Elink

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_posts.*
import mx.itesm.ETeam.Elink.PostsRelated.PostAdapter
import mx.itesm.ETeam.Elink.PostsRelated.PostCreation
import mx.itesm.ETeam.Elink.PostsRelated.PostData

/*
Muestra las publicaciones de los usuarios, es el fragmento por default al iniciar la aplicación
Autor: Alejandro Torices
Modificado por: Francisco Arenas
 */
class PostsFrag : Fragment() {

    //private var adapter: RecyclerView.Adapter<PostAdapter.ViewHolder>?= null
    //private var layoutManager: RecyclerView.LayoutManager?= null
    private lateinit var auth: FirebaseAuth
    lateinit var postArray: List<PostData>

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                                                    savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
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
                postArray = emptyList()

                for(ds in dataSnapshot.children){
                    val modelPost = ds.getValue<PostData>(PostData::class.java)!!
                    postArray.toMutableList().add(modelPost)

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
}