package com.github.vardemin.materialcountrypicker

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.vardemin.fastscroll.FastScroller

/**
 * @author Vladimir Akopzhanian on 10/07/19.
 */
object CountryPickerDialog {
    private val INSTANCE: Dialog? = null

    fun openPickerDialog(
        context: Context,
        callback: OnCountrySelectedCallback?,
        showCountryCodeInList: Boolean,
        isSearchAllowed: Boolean,
        isDialogKeyboardAutoPopup: Boolean,
        isShowFastScroller: Boolean,
        fastScrollerBubbleColor: Int,
        fastScrollerHandleColor: Int,
        fastScrollerBubbleTextAppearance: Int
    ) {
        val dialog = getDialog(context)

        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (dialog.window != null)
            dialog.window!!.setContentView(R.layout.dialog_country_picker)

        //keyboard
        if (isSearchAllowed && isDialogKeyboardAutoPopup) {
            dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        } else {
            dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        }


        //list all the countries
        val countries = loadDataFromJson(context)

        //country Groups
        val countryGroup = mapList(countries)

        //set up dialog views
        //dialog views
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerCountryPicker)
        val tvNoResult = dialog.findViewById<TextView>(R.id.tvNoResult)
        val ivDismiss = dialog.findViewById<ImageView>(R.id.ivDismiss)
        val searchView = dialog.findViewById<SearchView>(R.id.searchView)


        //set click listeners
        ivDismiss.setOnClickListener { v -> dialog.dismiss() }

        val listener = object : OnItemClickCallback {
            override fun onItemClick(country: Country) {
                callback?.updateCountry(country)
                dialog.dismiss()
            }
        }

        val cca = CountryPickerAdapter(
            context, listener, countries, countryGroup,
            searchView, tvNoResult, showCountryCodeInList
        )
        recyclerView.setLayoutManager(LinearLayoutManager(context))
        recyclerView.adapter = cca

        //fast scroller
        val fastScroller = dialog.findViewById<FastScroller>(R.id.fastScroll)
        fastScroller.setRecyclerView(recyclerView)
        if (isShowFastScroller) {
            if (fastScrollerBubbleColor != 0) {
                fastScroller.setBubbleColor(fastScrollerBubbleColor)
            }

            if (fastScrollerHandleColor != 0) {
                fastScroller.setHandleColor(fastScrollerHandleColor)
            }

            if (fastScrollerBubbleTextAppearance != 0) {
                try {
                    fastScroller.setBubbleTextAppearance(fastScrollerBubbleTextAppearance)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

        } else {
            fastScroller.visibility = View.GONE
        }


        dialog.show()

    }

    private fun getDialog(context: Context?): Dialog? {
        return if (INSTANCE == null && context != null) {
            Dialog(context)
        } else INSTANCE
    }


    interface OnCountrySelectedCallback {
        fun updateCountry(country: Country)
    }
}
