package com.github.vardemin.materialcountrypicker

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * @author Vladimir Akopzhanian on 10/07/19.
 */

@Parcelize
data class Country(
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("dial_code")
    var dialCode: String? = null,
    @SerializedName("code")
    var code: String? = null) : Parcelable {

    val flagResID = R.drawable.flag_transparent
}