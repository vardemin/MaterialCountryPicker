package com.github.vardemin.materialcountrypicker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.vardemin.fastscroll.FastScroller
/**
 * @author Vladimir Akopzhanian on 10/07/19.
 */

class CountryPickerActivity: AppCompatActivity() {
    private var showFastScroller = true
    private var fastScrollerBubbleColor = 0
    private var fastScrollerHandleColor = 0
    private var listItemTextColor = 0
    private var fullScreenToolbarColor = 0
    private var fastScrollerBubbleTextAppearance = 0
    private var showCountryCode: Boolean = false
    private val TAG = CountryPickerActivity::class.java.simpleName
    private var pickerAdapter: CountryPickerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "OnCreate called")
        setContentView(R.layout.activity_country_picker)
        val toolbar = findViewById<Toolbar>(R.id.my_toolbar)
        toolbar.inflateMenu(R.menu.picker_dialog)
        val searchView = toolbar.menu.findItem(R.id.search).actionView as SearchView

        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_clear_black)
            supportActionBar!!.setTitle(getString(R.string.select_country))
        }

        if (intent != null) {
            val bundle = intent.extras
            if (bundle != null) {
                showFastScroller = bundle.getBoolean(CountryPicker.EXTRA_SHOW_FAST_SCROLL)
                fastScrollerBubbleColor = bundle.getInt(CountryPicker.EXTRA_SHOW_FAST_SCROLL_BUBBLE_COLOR)
                fastScrollerHandleColor = bundle.getInt(CountryPicker.EXTRA_SHOW_FAST_SCROLL_HANDLER_COLOR)
                listItemTextColor = bundle.getInt(CountryPicker.EXTRA_LIST_ITEM_TEXT_COLOR)
                fullScreenToolbarColor = bundle.getInt(CountryPicker.EXTRA_FULLSCREEN_TOOLBAR_COLOR)
                fastScrollerBubbleTextAppearance =
                    bundle.getInt(CountryPicker.EXTRA_SHOW_FAST_SCROLL_BUBBLE_TEXT_APPEARANCE)
                showCountryCode = bundle.getBoolean(CountryPicker.EXTRA_SHOW_COUNTRY_CODE_IN_LIST)
            }
        }


        //list all the countries
        val countries = loadDataFromJson(this)

        //country Groups
        val countryGroup = mapList(countries)
        val callback = object : OnItemClickCallback {
            override fun onItemClick(country: Country) {
                //set result and finish
                val intent = Intent()
                intent.putExtra(CountryPicker.EXTRA_COUNTRY, country)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }

        toolbar.setBackgroundColor(fullScreenToolbarColor)

        //recyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerCountryPicker)

        //no result tv
        val tvNoResult = findViewById<TextView>(R.id.tvNoResult)

        //create picker adapter
        pickerAdapter = CountryPickerAdapter(
            this, callback, countries, countryGroup,
            searchView, tvNoResult, showCountryCode, listItemTextColor
        )

        //fast scroller
        val fastScroller = findViewById<FastScroller>(R.id.fastScroll)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = pickerAdapter

        fastScroller.setRecyclerView(recyclerView)
        if (isShowFastScroller()) {
            if (getFastScrollerBubbleColor() != 0) {
                fastScroller.setBubbleColor(getFastScrollerBubbleColor())
            }

            if (getFastScrollerHandleColor() != 0) {
                fastScroller.setHandleColor(getFastScrollerHandleColor())
            }

            if (getFastScrollerBubbleTextAppearance() != 0) {
                try {
                    fastScroller.setBubbleTextAppearance(getFastScrollerBubbleTextAppearance())
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

        } else {
            fastScroller.setVisibility(View.GONE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(TAG, "OnCreate Options menu called")
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.picker_dialog, menu)
        // Associate searchable configuration with the SearchView
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        pickerAdapter?.setSearchView(searchView)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    fun getFastScrollerBubbleColor(): Int {
        return fastScrollerBubbleColor
    }

    fun getFastScrollerHandleColor(): Int {
        return fastScrollerHandleColor
    }

    fun getFastScrollerBubbleTextAppearance(): Int {
        return fastScrollerBubbleTextAppearance
    }


    fun isShowFastScroller(): Boolean {
        return showFastScroller
    }
}