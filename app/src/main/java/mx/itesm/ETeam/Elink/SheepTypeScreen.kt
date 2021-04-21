package mx.itesm.ETeam.Elink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_sheep_type_screen.*
import mx.itesm.ETeam.Elink.databinding.ActivitySharkTypeScreenBinding
import mx.itesm.ETeam.Elink.databinding.ActivitySheepTypeScreenBinding

class SheepTypeScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySheepTypeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySheepTypeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarDropDown()

        //configurarBotones()
    }

    private fun configurarDropDown() {
        val category = resources.getStringArray(R.array.categorias)
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, category)
        binding.autoCategory.setAdapter(adapter)
    }
/*
    private fun configurarBotones() {
        binding.backBtnSheepy.setOnClickListener {
            val intSignup = Intent(this, SignupScreen::class.java)
            intSignup.putExtra("userType", "sheep")
            intSignup.putExtra("username", intent.getStringExtra("username").toString())
            startActivity(intSignup)
        }
    }*/
}

