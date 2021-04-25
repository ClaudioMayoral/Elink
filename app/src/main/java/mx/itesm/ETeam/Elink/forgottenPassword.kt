package mx.itesm.ETeam.Elink

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import mx.itesm.ETeam.Elink.databinding.ActivityForgottenPasswordBinding

/*
Actividad para recuperar la contraseña.
Autor: Alejandro Torices
 */
class forgottenPassword : AppCompatActivity() {
    private lateinit var binding: ActivityForgottenPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgottenPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnResetPass.setOnClickListener {
            sendResetEmail()
        }
    }

    private fun sendResetEmail() {
        val emailAddress = binding.etRecoverEmail.text.toString().trim()
        Firebase.auth.sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Envío exitoso",
                    Toast.LENGTH_SHORT).show()
                }
            }
    }
}