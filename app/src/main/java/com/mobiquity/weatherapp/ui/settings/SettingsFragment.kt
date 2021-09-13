package com.mobiquity.weatherapp.ui.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mobiquity.weatherapp.MainActivity
import com.mobiquity.weatherapp.database.viewmodel.WeatherViewModel
import com.mobiquity.weatherapp.databinding.FragmentSettingsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    private val binding get() = _binding!!
    var unit = "metric"

    private val sharedPrefFile = "unit_preference"
    private lateinit var weatherViewModel: WeatherViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
            sharedPrefFile,
            Context.MODE_PRIVATE
        )

        loadRadioButtons(sharedPreferences)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->

            if (checkedId == binding.radioBtnImperial.id) {
                unit = "imperial"
                editor.putBoolean("imperial", binding.radioBtnImperial.isChecked);
                editor.putBoolean("metric", false);
                Toast.makeText(
                    requireContext(),
                    "Selected Imperial",
                    Toast.LENGTH_SHORT
                ).show()
                editor.putString("unit", unit)
                editor.apply()
                editor.commit()
            } else {
                unit = "metric"
                editor.putBoolean("metric", binding.radioBtnMetric.isChecked);
                editor.putBoolean("imperial", false);

                Toast.makeText(
                    requireContext(),
                    "Selected Metric",
                    Toast.LENGTH_SHORT
                ).show()
                editor.putString("unit", unit)
                editor.apply()
                editor.commit()
            }
        }

        binding.buttonClear.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                weatherViewModel.deleteData(requireContext())

                toast(requireContext(), "you have successfully deleted the data")
            }
        }

        return root
    }

    private fun toast(context: Context?, text: String?) {
        val handler = Handler(Looper.getMainLooper())
        handler.post(Runnable { Toast.makeText(context, text, Toast.LENGTH_LONG).show() })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadRadioButtons(sharedPreferences: SharedPreferences) {

        binding.radioBtnMetric.isChecked = sharedPreferences.getBoolean("metric", true)
        binding.radioBtnImperial.isChecked = sharedPreferences.getBoolean("imperial", false)
    }
}