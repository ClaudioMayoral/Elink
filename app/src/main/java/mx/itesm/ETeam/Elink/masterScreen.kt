package mx.itesm.ETeam.Elink

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import mx.itesm.ETeam.Elink.databinding.ActivityMasterScreenBinding

class masterScreen : AppCompatActivity() {
    private lateinit var binding: ActivityMasterScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_master_screen)

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
}