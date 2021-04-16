package mx.itesm.ETeam.Elink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import mx.itesm.ETeam.Elink.databinding.ActivityMasterScreenBinding

class masterScreen : AppCompatActivity(), ClickListener{
    private lateinit var binding: ActivityMasterScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMasterScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarFragmentoInicio()
        configurarMenu()
    }

    private fun configurarFragmentoInicio() {
        val fragPosts = PostsFrag()
        cambiarFragmento(fragPosts)
    }

    private fun configurarMenu() {
        binding.menuNavegacion.setOnNavigationItemSelectedListener { item ->
        when (item.itemId){
            R.id.navPosts ->{
                val fragPosts = PostsFrag()
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