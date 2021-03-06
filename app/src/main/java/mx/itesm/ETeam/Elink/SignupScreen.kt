package mx.itesm.ETeam.Elink

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import mx.itesm.ETeam.Elink.databinding.ActivitySignupScreenBinding
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import mx.itesm.ETeam.Elink.DataClasses.Project
import mx.itesm.ETeam.Elink.DataClasses.SharkPreferences
import mx.itesm.ETeam.Elink.DataClasses.User
import java.lang.Exception

/*
Pantalla para hacer signup se ofrecen distintas opciones
Autor: Alejandro Torices
 */
class SignupScreen : AppCompatActivity()
{
    private lateinit var binding : ActivitySignupScreenBinding

    // Administra la información de sign-in
    private lateinit var auth: FirebaseAuth
    private lateinit var baseDatos: FirebaseDatabase

    private val storage = Firebase.storage
    private val storageRef = storage.reference

    //  ActivityForResult
    private val RC_SIGN_IN: Int = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        baseDatos = FirebaseDatabase.getInstance()

        configurarBotones()

    }

    private fun configurarBotones() {
        binding.googleSignup.setOnClickListener {
            googleSignUp()
        }

        binding.fbSignup.setOnClickListener {
            facebookSignUp()
        }

        binding.appleSignup.setOnClickListener {
            appleSignUp()
        }
        binding.buttonSignup.setOnClickListener{
            val email = binding.signupMail.text.toString().trim()
            val password = binding.signupPassword.text.toString().trim()
            val passwordConfirmation = binding.signupConfirmPassword.text.toString().trim()
            if(verificarEntradas(email,password,passwordConfirmation)){
                signUp(email, password)
            }
        }
    }

    private fun appleSignUp() {
        val proveedores = arrayListOf(AuthUI.IdpConfig.AppleBuilder().build())
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(proveedores)
                .build(),
                RC_SIGN_IN)
    }

    private fun facebookSignUp() {
        val proveedores = arrayListOf(AuthUI.IdpConfig.FacebookBuilder().build())
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(proveedores)
                .build(),
        RC_SIGN_IN)
    }

    private fun googleSignUp() {
        val proveedores = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(proveedores)
                .build(),
                RC_SIGN_IN)
    }
    private fun verificarEntradas(email: String, password: String, passwordConfirmation: String) :Boolean{
        if (email.isEmpty() || password.isEmpty()|| passwordConfirmation.isEmpty()){
            Toast.makeText(baseContext, "Rellene todos los campos para continuar.",
                Toast.LENGTH_SHORT).show()
            return false
        }else if (password != passwordConfirmation){
            Toast.makeText(baseContext, "Las contraseñas no coinciden.",
                Toast.LENGTH_SHORT).show()
            return false
        }else if(password.length < 6){
            Toast.makeText(baseContext, "La contraseña es demasiado corta. Crea una de 6 caracteres mínimo.",
                Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }


    private fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    insertUserDataInDB(user!!)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "No se ha podido crear la cuenta, inténtelo de nuevo más tarde",
                        Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun insertUserDataInDB(user: FirebaseUser) {
        val userID = user.uid
        val usermail = user.email!!
        val username = intent.getStringExtra("username").toString()
        val usertype = intent.getStringExtra("userType").toString()
        val profilePic = intent.getStringExtra("profilePic").toString()


        // Save Image
        val storagePath = "ProfilePics/"
        val filePath = storagePath + userID
        val storageReferecne = FirebaseStorage.getInstance().reference
        val dbReference = baseDatos.getReference("Users")
        val secondStorageReference = storageReferecne.child(filePath)

        secondStorageReference.putFile(Uri.parse(profilePic)).addOnSuccessListener {
            val uriTask = it.storage.downloadUrl
            while (!uriTask.isSuccessful);
            val downloadUri = uriTask.result

            if(uriTask.isSuccessful){
                val hashMap = HashMap<String, Any>()
                hashMap["username"] = username
                hashMap["userMail"] = usermail
                hashMap["usertype"] = usertype
                hashMap["dirImagen"] = downloadUri.toString()
                hashMap["userID"] = userID

                // Path to store post data
                val ref2DB = baseDatos.getReference("Users").child(userID)
                ref2DB.setValue(hashMap)

                if(usertype =="shark"){
                    val refPrefShark = baseDatos.getReference("/Users/$userID/Preferences")

                    val checkAmbiental = intent.getBooleanExtra("ambiental", false)
                    val checkTecnologia = intent.getBooleanExtra("tecnologia", false)
                    val checkSocial = intent.getBooleanExtra("social", false)
                    val checkEntretenimiento = intent.getBooleanExtra("entretenimiento", false)
                    val checkLifeStyle = intent.getBooleanExtra("lifestyle", false)

                    val sharkPrefDB = SharkPreferences(checkAmbiental,checkTecnologia,checkSocial,checkEntretenimiento,checkLifeStyle)
                    refPrefShark.setValue(sharkPrefDB)
                }else{
                    val refPresSheep = baseDatos.getReference("Users").child(userID).child("Project")
                    val nombreDelProyecto = intent.getStringExtra("nombreDeProyecto").toString()
                    val descripcionProyecto = intent.getStringExtra("descripcionDeProyecto").toString()
                    val moneyGoal = intent.getIntExtra("metaMonetaria",0).toString().toInt()
                    val categoria = intent.getStringExtra("categoria").toString()

                    val projectDB = Project(nombreDelProyecto,descripcionProyecto,moneyGoal,categoria)
                    refPresSheep.setValue(projectDB)
                }

                Toast.makeText(this, "Bienvenido!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Ocurrió un error", Toast.LENGTH_SHORT).show()
            }
        } .addOnFailureListener {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }

        val intMasterScreen = Intent(this, masterScreen::class.java)
        startActivity(intMasterScreen)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            when(resultCode){
                RESULT_OK ->{
                    val usuario = FirebaseAuth.getInstance().currentUser

                    // Siguiente actividad
                    insertUserDataInDB(usuario!!)
                }
                RESULT_CANCELED ->{
                    // signup cancelado
                }
                else -> {
                    val response =IdpResponse.fromResultIntent(data)
                    println("Error: ${response?.error?.errorCode}")
                }
            }
        }
    }
    companion object {
        private const val TAG = "EmailPassword"
    }
}