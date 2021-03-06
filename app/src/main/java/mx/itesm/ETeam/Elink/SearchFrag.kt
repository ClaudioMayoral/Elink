package mx.itesm.ETeam.Elink

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import mx.itesm.ETeam.Elink.DataClasses.User
import java.util.*
import kotlin.collections.ArrayList

/*
Muestra los perfiles de otros usuarios similares, permite realizar una búsqueda por nombre
Autor: Alejandro Torices
Modificado por: Francisco Arenas
 */
class SearchFrag : Fragment()
{
    // Firebase & layout components
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchQuery: EditText

    // User's data
    private lateinit var userAdapter: UserAdapter
    private lateinit var userList: ArrayList<User>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?
                              , savedInstanceState: Bundle?): View?
    {
        val vista = inflater.inflate(R.layout.fragment_search, container, false)

        val layoutManager = LinearLayoutManager(context)   //val layoutManager = LinearLayoutManager(activity)
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true

        recyclerView = vista.findViewById(R.id.searchView)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        searchQuery = vista.findViewById(R.id.etBuscarPost)
        userList = arrayListOf()

        userAdapter = context?.let { UserAdapter(it, userList) }!! //userAdapter = activity?.let { UserAdapter(it.applicationContext, userList) }!!
        recyclerView.adapter = userAdapter
        leerUsuarios()
        buscandoUsuarios(searchQuery)

        return vista
    }

    private fun buscandoUsuarios(searchQuery: EditText?) {
        searchQuery?.addTextChangedListener(object : TextWatcher{
            override fun onTextChanged(queryString: CharSequence?, start: Int, before: Int, count: Int) {
                buscarUsuarios(queryString.toString().toLowerCase(Locale.ROOT))
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                println("Hola")
            }
            override fun afterTextChanged(s: Editable?) {
                println("Hola")
            }
        })
    }

    private fun leerUsuarios() {
        val dbReference = FirebaseDatabase.getInstance().getReference("Users")
        val currentID = FirebaseAuth.getInstance().currentUser?.uid

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(searchQuery.text.toString() == "") {
                    userList.clear()
                    for (ds in dataSnapshot.children) {
                        val userData = ds.getValue(User::class.java)!!
                        if(userData.userID != currentID){
                            userList.add(userData)
                        }
                    }
                    userAdapter.notifyDataSetChanged()
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(activity, "" + databaseError.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        dbReference.addValueEventListener(postListener)
    }

    private fun buscarUsuarios(query: String) {
        val dbReference = FirebaseDatabase.getInstance().getReference("Users")

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userList.clear()
                for (ds in dataSnapshot.children) {
                    val userData = ds.getValue(User::class.java)!!

                    if (userData.usertype.toLowerCase(Locale.ROOT).contains(query) ||
                        userData.username.toLowerCase(Locale.ROOT).contains(query) ||
                        userData.userMail.toLowerCase(Locale.ROOT).contains(query))
                    {
                        userList.add(userData)
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
}

