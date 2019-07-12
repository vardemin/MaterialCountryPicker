package com.github.vardemin.countrypicker

import androidx.databinding.ObservableArrayMap
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    val paramsMap = ObservableArrayMap<String, String>()
}