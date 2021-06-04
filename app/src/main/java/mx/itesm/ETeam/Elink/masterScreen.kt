package mx.itesm.ETeam.Elink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import mx.itesm.ETeam.Elink.databinding.ActivityMasterScreenBinding

class masterScreen : AppCompatActivity(), ClickListener
{
    // Instance Variables
    private lateinit var binding: ActivityMasterScreenBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dataBase: FirebaseDatabase
    private lateinit var currentUser: FirebaseUser
    private lateinit var dbReference: DatabaseReference
    private lateinit var userType: String
    private lateinit var fragPosts: Fragment

    override fun onCreate(savedInstanceState: Bundle?)
    {
        // Initialize database references
        dataBase = FirebaseDatabase.getInstance()
        dbReference = dataBase.reference
        firebaseAuth = FirebaseAuth.getInstance()
        currentUser = firebaseAuth.currentUser!!

        super.onCreate(savedInstanceState)
        binding = ActivityMasterScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbReference.child("Users").child(currentUser.uid).get().addOnSuccessListener {
            userType = it.child("usertype").value.toString()
            configurarFragmentoInicio(userType)
            configurarMenu()
        }.addOnFailureListener{
            Toast.makeText(baseContext, "" + it.toString(), Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun configurarFragmentoInicio(userType: String) {
        fragPosts = if (userType == "sheep"){
            PostSheepyFrag()
        } else {
            PostsSharkyFrag()
        }
        cambiarFragmento(fragPosts)
    }

    private fun configurarMenu() {
        binding.menuNavegacion.setOnNavigationItemSelectedListener { item ->
        when (item.itemId){
            R.id.navPosts ->{
                cambiarFragmento(fragPosts)
            }
            R.id.navSearch ->{
                val fragSearch = SearchFrag()
                cambiarFragmento(fragSearch)
            }
            R.id.navMessages -> {
                val fragMessages = MessagesFrag()
                cambiarFragmento(fragMessages)
            }
            R.id.navProfile -> {
                val fragProfile = ProfileFrag()
                cambiarFragmento(fragProfile)
                /*val intFirstScreen = Intent(this, firstScreen::class.java)
                startActivity(intFirstScreen)*/
            }
        }
            true
        }
    }

    private fun cambiarFragmento(fragmento: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.contenedorFragmentos, fragmento)
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    override fun clicked(posicion: Int) {
        TODO("Not yet implemented")
    }

    override fun itemClicked(){
        AuthUI.getInstance().signOut(this)
        val intFirstScreen = Intent(this, firstScreen::class.java)
        startActivity(intFirstScreen)
    }

}