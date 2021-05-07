package mx.itesm.ETeam.Elink

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.ListFragment
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.getInstance
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_type_screen.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import mx.itesm.ETeam.Elink.databinding.FragmentProfileBinding
import org.json.JSONObject

/*
Muestra el perfil del usuario actual
Autor: Alejandro Torices, Claudio Mayoral
 */
class ProfileFrag : Fragment() {

    private var listener: ClickListener? = null
    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var baseDatos: FirebaseDatabase
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        baseDatos = FirebaseDatabase.getInstance()
        user = auth.currentUser!!
        binding = FragmentProfileBinding.inflate(layoutInflater)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is ClickListener){
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)

        view.btnLogOut.setOnClickListener { view ->
            listener?.itemClicked()
        }
        val image = baseDatos.getReference("Users/${user.uid}").child("dirImagen").toString()
        try {
            Picasso.get().load(image).into(binding.ivProfImage)
        }catch (e: Exception){
            Picasso.get().load("@drawable/icon_profile_picture").into(binding.ivProfImage)
        }

        return view
    }

    /*private fun descargarImagen() {
        val dirImagen =
        AndroidNetworking.get(dirImagen)
            .build()
            .getAsBitmap(object: BitmapRequestListener {
                override fun onResponse(response: Bitmap?) {
                    binding.tvProfImage.setImageBitmap(response)
                }
                override fun onError(anError: ANError?) {
                    binding.tvProfImage.setImageBitmap(null)
                }
            })
    }*/



}