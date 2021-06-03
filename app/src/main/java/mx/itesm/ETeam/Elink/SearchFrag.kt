package mx.itesm.ETeam.Elink

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sheep_type_screen.*
import kotlinx.android.synthetic.main.fragment_profile.*
import mx.itesm.ETeam.Elink.PostsRelated.PostAdapter
import mx.itesm.ETeam.Elink.PostsRelated.PostCreation
import mx.itesm.ETeam.Elink.PostsRelated.PostData
import java.util.*
import kotlin.collections.ArrayList

/*
Muestra los perfiles de otros usuarios similares, permite realizar una búsqueda por nombre
Autor: Alejandro Torices
Modificado por: Francisco Arenas
 */
class SearchFrag : Fragment() {

    // Instance variables
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterPost: PostAdapter
    private lateinit var postList: ArrayList<PostData>
    private lateinit var searchQuery: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        val layoutManager = LinearLayoutManager(activity)
        firebaseAuth = FirebaseAuth.getInstance()
        recyclerView = view.findViewById(R.id.searchView)
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true
        recyclerView.layoutManager = layoutManager
        postList = arrayListOf<PostData>()
        val button = view.findViewById<Button>(R.id.searchButton)

        buscarPost(button, view)

        return view
    }

    private fun buscarPost(button: Button?, view: View) {
        button?.setOnClickListener {
            searchQuery = view.findViewById(R.id.etBuscarPost)
            val query = searchQuery.text.toString()
            if (query == "") {
                postList.clear()
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
                            adapterPost = activity?.let { PostAdapter(it.applicationContext, postList) }!!
                            recyclerView.adapter = adapterPost
                        }

                        if(postList.isEmpty()){
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
}