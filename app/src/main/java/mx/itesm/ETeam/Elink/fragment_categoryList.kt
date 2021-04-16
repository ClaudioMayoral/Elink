package mx.itesm.ETeam.Elink

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import mx.itesm.ETeam.Elink.databinding.ActivitySheepTypeScreenBinding

// Autor: Francisco Ariel Arenas Enciso
// Clase que permite mostrar en una dropdown list (generada a partir de un fragmento y un
// textinputLayout y basada en la metodologia de Stevdza-San (2021)) una lista de categorias
class fragment_categoryList : Fragment() {

    private var binding: ActivitySheepTypeScreenBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = ActivitySheepTypeScreenBinding.inflate(inflater, container, false)
        val categories = resources.getStringArray(R.array.categorias)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, categories)
        binding!!.autoCompleteTextView.setAdapter(arrayAdapter)


        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        val categories = resources.getStringArray(R.array.categorias)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, categories)
        binding!!.autoCompleteTextView.setAdapter(arrayAdapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}