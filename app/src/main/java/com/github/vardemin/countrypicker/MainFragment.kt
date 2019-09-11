package com.github.vardemin.countrypicker


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.github.vardemin.countrypicker.databinding.FragmentMainBinding
import com.github.vardemin.materialcountrypicker.CountryPicker
import com.github.vardemin.materialcountrypicker.PhoneNumberEditText
import kotlinx.android.synthetic.main.fragment_main.*


class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMainBinding.inflate(inflater, container, false)
        val bindingView = binding.root
        binding.mainViewModel = viewModel
        return bindingView
        //return inflater.inflate(R.layout.fragment_main, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editPhone.setOnStartActivityCallback(object : PhoneNumberEditText.OnStartActivityCallback{
            override fun onStartActivity(intent: Intent, requestCode: Int) {
                startActivityForResult(intent, requestCode)
            }
        })

        btnResult.setOnClickListener {
            val result = "Phone:\t${viewModel.paramsMap["phone"]}\nSecondPhone:\t${viewModel.paramsMap["secondPhone"]}\n"
            tvResult.text = result
        }
        btnNext.setOnClickListener { findNavController().navigate(R.id.secondFragment) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CountryPicker.PICKER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null)
                editPhone.handleActivityResult(data)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}
