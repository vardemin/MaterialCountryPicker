package com.github.vardemin.countrypicker

import androidx.databinding.InverseMethod
import com.github.vardemin.materialcountrypicker.PhoneNumberEditText


object PhoneEditConverter {
    @InverseMethod("toNumber")
    @JvmStatic
    fun toString(
        view: PhoneNumberEditText,
        value: String?
    ): String? {
        return PhoneNumberEditText.fromTextNumber(view, value ?: "")
    }

    @JvmStatic
    fun toNumber(
        view: PhoneNumberEditText,
        value: String?
    ): String? {
        return PhoneNumberEditText.toTextNumber(view)
    }
}