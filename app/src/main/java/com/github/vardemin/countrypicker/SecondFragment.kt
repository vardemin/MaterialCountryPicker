package com.github.vardemin.countrypicker


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.github.vardemin.countrypicker.databinding.FragmentSecondBinding
import kotlinx.android.synthetic.main.fragment_second.*


class SecondFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSecondBinding.inflate(inflater, container, false)
        val bindingView = binding.root
        binding.mainViewModel = viewModel
        return bindingView
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnResult.setOnClickListener {
            val result = "Phone:\t${viewModel.paramsMap["phone"]}\nSecondPhone:\t${viewModel.paramsMap["secondPhone"]}\n"
            tvResult.text = result
        }
        btnBack.setOnClickListener { findNavController().popBackStack() }
    }

}
