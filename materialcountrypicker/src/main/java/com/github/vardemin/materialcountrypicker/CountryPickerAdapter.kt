package com.github.vardemin.materialcountrypicker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SearchView
import com.github.vardemin.fastscroll.SectionTitleProvider
import com.github.vardemin.sectionedrecycleradapter.SectionViewHolder
import com.github.vardemin.sectionedrecycleradapter.SectionedRecyclerViewAdapter
import java.util.ArrayList

class CountryPickerAdapter(val context: Context,
                           val clickListener: OnItemClickCallback,
                           val countries: List<Country>,
                           val countryGroup: Map<String, List<Country>>,
                           private var searchView: SearchView? = null,
                           private val tvNoResult: TextView,
                           val showCountryCode: Boolean
): SectionedRecyclerViewAdapter<CountryPickerAdapter.CountryCodeViewHolder>(), Filterable, SectionTitleProvider {
    private var filteredCountryGroup: Map<String, List<Country>> = countryGroup

    init {
        addSearchViewListener()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryCodeViewHolder {
        val layoutRes: Int = when (viewType) {
            VIEW_TYPE_ITEM -> R.layout.item
            VIEW_TYPE_HEADER -> R.layout.header
            else -> R.layout.item
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return CountryCodeViewHolder(view)
    }

    override fun getSectionCount(): Int {
        return filteredCountryGroup.size
    }

    override fun getItemCount(sectionIndex: Int): Int {
        return getItemsForSection(sectionIndex)!!.size
    }

    private fun getItemsForSection(section: Int): List<Country>? {
        return filteredCountryGroup[AZ_STRING[section].toString()]
    }

    override fun onBindHeaderViewHolder(holder: CountryCodeViewHolder, section: Int, expanded: Boolean) {
        (holder.itemView.findViewById(R.id.tvHeader) as TextView)
            .text = AZ_STRING[section].toString().toUpperCase()

    }

    override fun onBindFooterViewHolder(holder: CountryCodeViewHolder, section: Int) {
        //do nothing
    }

    override fun onBindViewHolder(holder: CountryCodeViewHolder, section: Int, relativePosition: Int, absolutePosition: Int) {
        val country = getItemsForSection(section)!![relativePosition]
        holder.setCountry(country, absolutePosition)
        holder.itemView.setOnClickListener { v -> clickListener.onItemClick(country) }

    }


    //    @Override
//    public int getItemCount() {
//        return filteredCountryGroup.size();
//    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): Filter.FilterResults {
                val charString = constraint.toString()
                if (charString.isEmpty()) {
                    filteredCountryGroup = countryGroup
                } else {
                    val filteredList = ArrayList<Country>()
                    for (country in countries) {
                        if (country.name!!.toLowerCase().startsWith(charString)) {
                            //check for country code
                            //                            if (showCountryCode && country.getCode().toLowerCase().startsWith(charString)) {
                            //                                filteredList.add(country);
                            //                                continue;
                            //                            }
                            filteredList.add(country)
                        }
                        //                        else if (showCountryCode && country.getCode().toLowerCase().startsWith(charString)) {
                        //                            filteredList.add(country);

                        //                        }
                    }
                    filteredCountryGroup = mapList(filteredList)

                }

                val results = Filter.FilterResults()
                results.values = filteredCountryGroup
                return results
            }

            override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
                filteredCountryGroup = results.values as Map<String, List<Country>>
                notifyDataSetChanged()

                if (filteredCountryGroup.isEmpty()) {
                    tvNoResult.visibility = View.VISIBLE
                } else {
                    tvNoResult.visibility = View.GONE
                }
            }
        }
    }

    override fun getSectionTitle(position: Int): String {
        val c = ArrayList<Country>()
        for (countryList in filteredCountryGroup.values) {
            c.addAll(countryList)
        }

        //wrap this is a try and catch due to the
        //section recycler view
        var country: Country? = null
        try {
            country = c[position]
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //        if (preferredCountriesCount > position) {
        //            return "★";
        //        } else
        return country?.name?.substring(0, 1) ?: "☺" //this should never be the case
    }


    fun setSearchView(searchView: SearchView) {
        this.searchView = searchView
        addSearchViewListener()
    }

    private fun addSearchViewListener() {
        if (this.searchView != null)
            searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    filter.filter(newText)
                    return true
                }
            })
    }


    inner class CountryCodeViewHolder(itemView: View) : SectionViewHolder(itemView) {
        var mainView: RelativeLayout = itemView as RelativeLayout
        var tvName: TextView?
        var tvCode: TextView?
        var ivFlag: ImageView?
        var flagWrapper: LinearLayout?
        var divider: View?

        init {
            tvName = mainView.findViewById(R.id.tvName)
            tvCode = mainView.findViewById(R.id.tvCode)
            ivFlag = mainView.findViewById(R.id.ivFlag)
            flagWrapper = mainView.findViewById(R.id.flagWrapper)
            divider = mainView.findViewById(R.id.preferenceDivider)
        }

        fun setCountry(country: Country?, position: Int) {
            if (position == 0) {
                divider?.visibility = View.GONE
            }

            if (country != null) {
                tvName?.visibility = View.VISIBLE
                tvCode?.visibility = View.VISIBLE
                flagWrapper?.visibility = View.VISIBLE
                if (showCountryCode) {
                    tvName?.text = context.getString(
                        R.string.format_country_with_code,
                        country.name,
                        country.code!!.toUpperCase()
                    )
                } else {
                    tvName?.text = context.getString(R.string.format_country, country.name)
                }

                tvCode?.text = context.getString(R.string.plus_prefix, country.dialCode)
                ivFlag?.setImageResource(getFlagResID(country))
            } else {
                divider?.visibility = View.VISIBLE
                tvName?.visibility = View.GONE
                tvCode?.visibility = View.GONE
                flagWrapper?.visibility = View.GONE
            }
        }
    }
}

interface OnItemClickCallback {
    fun onItemClick(country: Country)
}