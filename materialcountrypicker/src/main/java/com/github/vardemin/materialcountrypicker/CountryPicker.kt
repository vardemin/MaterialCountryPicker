package com.github.vardemin.materialcountrypicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.annotation.StyleRes

/**
 * @author Vladimir Akopzhanian on 10/07/19.
 */

object CountryPicker {

    val PICKER_REQUEST_CODE = 101
    val EXTRA_COUNTRY = "com.github.vardemin.materialcountrypicker_EXTRA_COUNTRY"
    val EXTRA_SHOW_FAST_SCROLL = "com.github.vardemin.materialcountrypicker_EXTRA_SHOW_FAST_SCROLL"
    val EXTRA_SHOW_FAST_SCROLL_BUBBLE_COLOR = "com.github.vardemin.materialcountrypicker_EXTRA_SHOW_FAST_BUBBLE_COLOR"
    val EXTRA_SHOW_FAST_SCROLL_HANDLER_COLOR = "com.github.vardemin.materialcountrypicker_EXTRA_SHOW_FAST_HANDLE_COLOR"
    val EXTRA_SHOW_FAST_SCROLL_BUBBLE_TEXT_APPEARANCE =
        "com.github.vardemin.materialcountrypicker_EXTRA_SHOW_FAST_BUBBLE_TEXT_APPEARANCE"
    val EXTRA_SHOW_COUNTRY_CODE_IN_LIST = "com.github.vardemin.materialcountrypicker_EXTRA_SHOW_COUNTRY_CODE_IN_LIST"

    fun showFullScreenPicker(
        activity: Activity,
        showFastScroller: Boolean,
        @ColorRes fastScrollerBubbleColor: Int,
        @ColorRes fastScrollerHandleColor: Int,
        @StyleRes fastScrollerBubbleTextAppearance: Int,
        showCountryCodeInList: Boolean
    ) {
        val intent = Intent(activity, CountryPickerActivity::class.java)
        val bundle = Bundle()
        bundle.putBoolean(CountryPicker.EXTRA_SHOW_FAST_SCROLL, showFastScroller)
        bundle.putInt(CountryPicker.EXTRA_SHOW_FAST_SCROLL_BUBBLE_COLOR, fastScrollerBubbleColor)
        bundle.putInt(CountryPicker.EXTRA_SHOW_FAST_SCROLL_HANDLER_COLOR, fastScrollerHandleColor)
        bundle.putInt(CountryPicker.EXTRA_SHOW_FAST_SCROLL_BUBBLE_TEXT_APPEARANCE, fastScrollerBubbleTextAppearance)
        bundle.putBoolean(CountryPicker.EXTRA_SHOW_COUNTRY_CODE_IN_LIST, showCountryCodeInList)
        intent.putExtras(bundle)
        activity.startActivityForResult(intent, PICKER_REQUEST_CODE)
    }


    fun showDialogPicker(
        context: Context,
        callback: CountryPickerDialog.OnCountrySelectedCallback,
        showFastScroller: Boolean,
        @ColorRes fastScrollerBubbleColor: Int,
        @ColorRes fastScrollerHandleColor: Int,
        @StyleRes fastScrollerBubbleTextAppearance: Int,
        showCountryCodeInList: Boolean, isShowCountryCodeInList: Boolean,
        isDialogKeyboardAutoPopup: Boolean, isSearchAllowed: Boolean
    ) {
        CountryPickerDialog.openPickerDialog(
            context, callback, isShowCountryCodeInList,
            isSearchAllowed, isDialogKeyboardAutoPopup, showFastScroller,
            fastScrollerBubbleColor, fastScrollerHandleColor, fastScrollerBubbleTextAppearance
        )

    }
}