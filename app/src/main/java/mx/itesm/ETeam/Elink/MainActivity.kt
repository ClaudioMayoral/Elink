package mx.itesm.ETeam.Elink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        val usuario = mAuth.currentUser
        if(usuario != null){
            println("Bienvenido de vuelta: ${usuario.displayName})")
            println("Correo: ${usuario.email}")
            println("ID: ${usuario.uid}")
            println("Imagen: ${usuario.photoUrl}")
            //Lanzar directamente la segunda pantalla
            val intMasterScreen = Intent(baseContext, masterScreen::class.java)
            startActivity(intMasterScreen)
        }else{
            val intFirstScreen = Intent(baseContext, firstScreen::class.java)
            startActivity(intFirstScreen)
        }


    }


}