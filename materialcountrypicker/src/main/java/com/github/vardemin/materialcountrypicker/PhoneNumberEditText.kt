package com.github.vardemin.materialcountrypicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.InputType
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.textfield.TextInputEditText

/**
 * @author Vladimir Akopzhanian on 10/07/19.
 */

class PhoneNumberEditText : TextInputEditText, CountryPickerDialog.OnCountrySelectedCallback {

    private var defaultCountryName: String? = null
    var preferredCountries: String? = null

    var fastScrollerBubbleColor: Int = 0
        private set
    var fastScrollerHandleColor: Int = 0
        private set
    var fastScrollerBubbleTextAppearance: Int = 0
        private set

    private var isRTL: Boolean = false
    var isSearchAllowed: Boolean = false
    var isDialogKeyboardAutoPopup: Boolean = false
        private set
    var isShowFastScroller: Boolean = false
    var isAutoDetectCountryEnabled: Boolean = false
    var isShowFullscreenDialog: Boolean = false
    private var showCountryCodeInView: Boolean = false
    private var showCountryDialCodeInView: Boolean = false
    private var showCountryDropdownArrow: Boolean = false
    private var rememberLastSelection: Boolean = false
    private var setCountryCodeBorder: Boolean = false
    var isShowCountryCodeInList: Boolean = false


    private var languageToApply: Language? = Language.ENGLISH
    private var selectedAutoDetectionPref: AutoDetectionPref? = null
    private var selectedCountry: Country = defaultCountry
    private var chip: BitmapDrawable? = null

    /**
     * Used for +{code} string replacement
     * null if disabled
     */
    var plusConverterString: String? = null

    /**
     * Return the selected Country name
     *
     * @return `String` name of the selected country
     */
    val selectedCountryName: String?
        get() = this.selectedCountry.name

    /**
     * Return the selected Country code
     *
     * @return `String` code of the selected country
     */
    val selectedCountryDialCode: String?
        get() {
            return if (plusConverterString == null) {
                this.selectedCountry.dialCode ?: defaultCountry.dialCode
            } else this.selectedCountry.dialCode?.replace("+", plusConverterString!!)
        }

    /**
     * Checks if RTL is enabled in the device
     *
     * @return `true` if enabled or `false` otherwise
     */
    private// as getLayoutDirection was introduced in API 17, under 17 we default to LTR
    val isRTLLanguage: Boolean
        get() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                return false
            }
            val config = resources.configuration
            return config.layoutDirection == View.LAYOUT_DIRECTION_RTL
        }

    val fullNumber: String
        get() {
            val phoneNumber: String
            val text = text.toString()
            phoneNumber = if (text.startsWith("0")) {
                text.replaceFirst("0".toRegex(), "")
            } else {
                text
            }

            return this.selectedCountryDialCode + phoneNumber
        }

    val formattedFullNumber: String?
        get() {
            val formattedFullNumber: String
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                formattedFullNumber = PhoneNumberUtils.formatNumber(fullNumber, selectedCountryCode)
            } else {
                formattedFullNumber = PhoneNumberUtils.formatNumber(fullNumber)
            }

            return formattedFullNumber
        }

    private val selectedCountryCode: String?
        get() = this.selectedCountry.code

    var isShowCountryCodeInView: Boolean
        get() = showCountryCodeInView
        set(show) {
            this.showCountryCodeInView = show
            updateSelectedCountry(this.selectedCountry)
        }

    var isShowCountryDialCodeInView: Boolean
        get() = showCountryDialCodeInView
        set(show) {
            this.showCountryDialCodeInView = show
            updateSelectedCountry(this.selectedCountry)
        }

    var isSetCountryCodeBorder: Boolean
        get() = setCountryCodeBorder
        set(setCountryCodeBorder) {
            this.setCountryCodeBorder = setCountryCodeBorder
            updateSelectedCountry(this.selectedCountry)
        }

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr)
    }

/*    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(
            if(text?.isNotBlank() == true)
                text.replace(Regex(selectedCountryDialCode ?: ""), "")
            else text, type)
    }

    override fun getText(): Editable? {
        return if (appendCountryCode) {
            val s = super.getText()
            SpannableStringBuilder(s.toString().prependIndent(selectedCountryDialCode ?: "+1"))
        } else super.getEditableText()
    }*/


    private fun init(attrs: AttributeSet?, defStyleAttr: Int) {
        if (attrs != null) {
            val a =
                context.theme.obtainStyledAttributes(attrs, R.styleable.PhoneNumberEditText, defStyleAttr, 0)
            try {
                //replace plus sign by this
                plusConverterString = a.getString(R.styleable.PhoneNumberEditText_cp_convertPlusTo)

                //show country code in view: false by default
                showCountryCodeInView = a.getBoolean(R.styleable.PhoneNumberEditText_cp_showCountryCodeInView, true)

                //show country code in list: false by default
                isShowCountryCodeInList = a.getBoolean(R.styleable.PhoneNumberEditText_cp_showCountryCodeInList, true)

                //show country dial code : true by default
                showCountryDialCodeInView =
                    a.getBoolean(R.styleable.PhoneNumberEditText_cp_showCountryDialCodeInView, true)

                //show dropdown arrow icon near flag
                showCountryDropdownArrow =
                    a.getBoolean(R.styleable.PhoneNumberEditText_cp_showCountryDropdownArrow, false)

                //default Country : null/empty by default
                defaultCountryName = a.getString(R.styleable.PhoneNumberEditText_cp_defaultCountryName)

                //remember last selection : false by default
                rememberLastSelection = a.getBoolean(R.styleable.PhoneNumberEditText_cp_rememberLastSelection, false)

                //show fullscreen Dialog : false by default, let the user decide
                //when they want to show full screen
                isShowFullscreenDialog = a.getBoolean(R.styleable.PhoneNumberEditText_cp_showFullScreeDialog, false)

                //show fast scroller : true by default, always show fast scroll
                //let the user decide when they want to hide the fast scroll
                isShowFastScroller = a.getBoolean(R.styleable.PhoneNumberEditText_cp_showFastScroll, true)

                //bubble color
                fastScrollerBubbleColor = a.getColor(R.styleable.PhoneNumberEditText_cp_fastScrollerBubbleColor, 0)

                //scroller handle color
                fastScrollerHandleColor = a.getColor(R.styleable.PhoneNumberEditText_cp_fastScrollerHandleColor, 0)

                //scroller text appearance
                fastScrollerBubbleTextAppearance =
                    a.getResourceId(R.styleable.PhoneNumberEditText_cp_fastScrollerBubbleTextAppearance, 0)

                //allow the user to search : true by default, let the user search all the time
                //and decide when they do not want search
                //                searchAllowed = a.getBoolean(R.styleable.PhoneNumberEditText_cp_searchAllowed, true);

                //keyboard auto pop up when dialog is showing : true by default
                //always show the keyboard as soon as possible
                isDialogKeyboardAutoPopup =
                    a.getBoolean(R.styleable.PhoneNumberEditText_cp_dialogKeyboardAutoPopup, true)

                //country auto detection pref : default to SIM_NETWORK_LOCALE in that order
                val autoDetectionPrefValue = a.getInt(R.styleable.PhoneNumberEditText_cp_countryAutoDetectionPref, 123)
                selectedAutoDetectionPref = AutoDetectionPref.getPrefForValue(autoDetectionPrefValue.toString())

                //auto detect country : default to true, always try to detect the country of the user
                isAutoDetectCountryEnabled = a.getBoolean(R.styleable.PhoneNumberEditText_cp_autoDetectCountry, true)

                //set the border around country code
                setCountryCodeBorder = a.getBoolean(R.styleable.PhoneNumberEditText_cp_setCountryCodeBorder, false)

                //preferred countries
                preferredCountries = a.getString(R.styleable.PhoneNumberEditText_cp_preferredCountries)

            } finally {
                a.recycle()
            }
        }

        isRTL = isRTLLanguage
        setInputType(InputType.TYPE_CLASS_NUMBER)

        //load the default country if it was set by the user
        if (!TextUtils.isEmpty(defaultCountryName)) {
            setSelectedCountry(getCountryForName(languageToApply, defaultCountryName))
        }

        //implement auto Country detection if it is set
        if (isAutoDetectCountryEnabled && !isInEditMode) {
            startAutoCountryDetection(true)
        }

        //if remember last selection is set and default country is not set
        //        if (rememberLastSelection) {
        //            loadLastSelectedCountryCode();
        //        }

        updateSelectedCountry(selectedCountry)

    }

    /**
     * Detects and load the detect country
     *
     * @param resetDefault used to reset to the default country when loading the detected country fail
     */
    private fun startAutoCountryDetection(resetDefault: Boolean) {

        try {
            var successfullyDetected = false
            for (i in 0 until selectedAutoDetectionPref!!.representation.length) {
                when (selectedAutoDetectionPref!!.representation[i]) {
                    '1' -> {
                        Log.d(TAG, "setAutoDetectedCountry: Setting using SIM")
                        successfullyDetected = detectSIMCountry(false)
                        Log.d(
                            TAG,
                            "setAutoDetectedCountry: Result of sim country detection:$successfullyDetected current country:$selectedCountryName"
                        )
                    }
                    '2' -> {
                        Log.d(TAG, "setAutoDetectedCountry: Setting using NETWORK")
                        successfullyDetected = detectNetworkCountry(false)
                        Log.d(
                            TAG,
                            "setAutoDetectedCountry: Result of network country detection:$successfullyDetected current country:$selectedCountryName"
                        )
                    }
                    '3' -> {
                        Log.d(TAG, "setAutoDetectedCountry: Setting using LOCALE")
                        successfullyDetected = detectLocaleCountry(false)
                        Log.d(
                            TAG,
                            "setAutoDetectedCountry: Result of LOCALE country detection:$successfullyDetected current country:$selectedCountryName"
                        )
                    }
                }
                if (successfullyDetected) {
                    break
                }
            }

            if (!successfullyDetected && resetDefault) {
                resetToDefaultCountry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w(TAG, "setAutoDetectCountry: Exception" + e.message)
            if (resetDefault) {
                resetToDefaultCountry()
            }
        }

    }

    /**
     * Updates the CountryPicker with the selected Country
     *
     * @param country Selected country
     */
    private fun updateSelectedCountry(country: Country?) {
        val dialCode: CharSequence?
        val code: CharSequence?
        val drawableId: Int
        if (country == null) {
            dialCode = "+123"
            code = "NG"
            drawableId = R.drawable.ng
        } else {
            dialCode = country.dialCode
            code = country.code
            drawableId = getFlagResID(country)
        }

        chip = createClusterBitmap(dialCode, code, drawableId)
        setCompoundDrawablesWithIntrinsicBounds(chip, null, null, null)
        setCompoundDrawablePadding(10)

    }


    /**
     * Draws the Country flag, code and dial code and return it as a drawable
     *
     * @param dialCode   Country dial code
     * @param code       County code.
     * @param drawableId Drawable id for the flag
     */
    private fun createClusterBitmap(dialCode: CharSequence?, code: CharSequence?, drawableId: Int): BitmapDrawable {
        val wrapper = LayoutInflater.from(getContext()).inflate(R.layout.picker_view, null)

        val tvCode = wrapper.findViewById<TextView>(R.id.tvShortCode)
        wrapper.findViewById<AppCompatImageView>(R.id.btnPick).visibility =
            if (showCountryDropdownArrow) View.VISIBLE else View.INVISIBLE
        tvCode.typeface = typeface
        tvCode.textSize = textSize
        tvCode.setTextColor(textColors)

        if (isShowCountryCodeInView) {
            tvCode.text = context.getString(R.string.fmt_code, code)
        }

        if (isShowCountryDialCodeInView) {
            tvCode.text = context.getString(R.string.fmt_dial_code, dialCode)
        }

        if (isShowCountryDialCodeInView && isShowCountryCodeInView) {
            tvCode.text = context.getString(R.string.fmt_code_and_dial_code, code, dialCode)
        }

        if (!isShowCountryCodeInView && !isShowCountryDialCodeInView) {
            tvCode.visibility = View.GONE
        }


        val ivFlag = wrapper.findViewById<ImageView>(R.id.ivFlag)
        ivFlag.setImageResource(drawableId)


        wrapper.measure(
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        wrapper.layout(0, 0, wrapper.measuredWidth, wrapper.measuredHeight)

        if (isSetCountryCodeBorder) {
            wrapper.setBackgroundResource(R.drawable.picker_chip_bg)
        }

        wrapper.isDrawingCacheEnabled = true
        var bitmap: Bitmap? = null
        try {
            bitmap = wrapper.getDrawingCache(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return BitmapDrawable(bitmap)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val bounds = chip!!.bounds
        val x = event.x.toInt()
        val iconXRect = if (isRTL)
            right - bounds.width() - EXTRA_PADDING
        else
            left + bounds.width() + EXTRA_PADDING

        when (event.action) {
            MotionEvent.ACTION_UP -> {
                if (if (isRTL) x >= iconXRect else x <= iconXRect) {
                    startCountrySelection()
                }
            }
        }
        return super.onTouchEvent(event)

    }

    override fun updateCountry(country: Country) {
        this.selectedCountry = country
        updateSelectedCountry(this.selectedCountry)

    }

    private fun getLanguageToApply(): Language? {
        if (languageToApply == null) {
            updateLanguageToApply()
        }
        return languageToApply
    }

    private fun updateLanguageToApply() {
        languageToApply = Language.ENGLISH

    }

    internal fun setLanguageToApply(languageToApply: Language) {
        this.languageToApply = languageToApply
    }

    private fun resetToDefaultCountry() {
        setSelectedCountry(getCountryForName(getLanguageToApply(), defaultCountryName))
    }

    /**
     * This will detect country from SIM info and then load it into CCP.
     *
     * @param resetDefault true if want to reset to default country when sim country cannot be detected. if false, then it
     * will not change currently selected country
     * @return true if it successfully sets country, false otherwise
     */
    fun detectSIMCountry(resetDefault: Boolean): Boolean {
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val simCountryISO = telephonyManager.simCountryIso
            if (simCountryISO == null || simCountryISO.isEmpty()) {
                if (resetDefault) {
                    resetToDefaultCountry()
                }
                return false
            }
            setSelectedCountry(getCountryForName(getLanguageToApply(), simCountryISO))
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            if (resetDefault) {
                resetToDefaultCountry()
            }
            return false
        }

    }

    /**
     * This will detect country from NETWORK info and then load it into CCP.
     *
     * @param resetDefault true if want to reset to default country when network country cannot be detected. if false, then it
     * will not change currently selected country
     * @return true if it successfully sets country, false otherwise
     */
    fun detectNetworkCountry(resetDefault: Boolean): Boolean {
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val networkCountryISO = telephonyManager.networkCountryIso
            if (networkCountryISO == null || networkCountryISO.isEmpty()) {
                if (resetDefault) {
                    resetToDefaultCountry()
                }
                return false
            }
            setSelectedCountry(getCountryForName(getLanguageToApply(), networkCountryISO))
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            if (resetDefault) {
                resetToDefaultCountry()
            }
            return false
        }

    }

    /**
     * This will detect country from LOCALE info and then load it into CCP.
     *
     * @param resetDefault true if want to reset to default country when locale country cannot be detected. if false, then it
     * will not change currently selected country
     * @return true if it successfully sets country, false otherwise
     */
    fun detectLocaleCountry(resetDefault: Boolean): Boolean {
        try {
            val localeCountryISO = context.resources.configuration.locale.country
            if (localeCountryISO == null || localeCountryISO.isEmpty()) {
                if (resetDefault) {
                    resetToDefaultCountry()
                }
                return false
            }
            setSelectedCountry(getCountryForName(getLanguageToApply(), localeCountryISO))
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            if (resetDefault) {
                resetToDefaultCountry()
            }
            return false
        }

    }

    private fun getCountryForName(languageToApply: Language?, countryCode: String?): Country {
        val countries = loadDataFromJson(context)
        for (country in countries) {
            if (countryCode?.toUpperCase() == country.code)
                return country
        }
        return defaultCountry
    }

    private fun getCountryForDialCode(dialCode: String?): Country {
        val countries = loadDataFromJson(context)
        for (country in countries) {
            if (dialCode == country.dialCode)
                return country
        }
        return defaultCountry
    }

    /**
     * This will update the pref for country auto detection.
     * Remeber, this will not call setAutoDetectedCountry() to update country. This must be called separately.
     *
     * @param selectedAutoDetectionPref new detection pref
     */
    fun setCountryAutoDetectionPref(selectedAutoDetectionPref: AutoDetectionPref) {
        this.selectedAutoDetectionPref = selectedAutoDetectionPref
    }

    fun setSelectedCountry(selectedCountry: Country) {
        this.selectedCountry = selectedCountry
        saveLastSelectedCountryCode(this.selectedCountry.code)
        updateSelectedCountry(selectedCountry)
    }

    fun startCountrySelection() {
        if (isShowFullscreenDialog) {
            try {
                val intent = Intent(context, CountryPickerActivity::class.java)
                val bundle = Bundle()
                bundle.putBoolean(CountryPicker.EXTRA_SHOW_FAST_SCROLL, isShowFastScroller)
                bundle.putInt(CountryPicker.EXTRA_SHOW_FAST_SCROLL_BUBBLE_COLOR, fastScrollerBubbleColor)
                bundle.putInt(CountryPicker.EXTRA_SHOW_FAST_SCROLL_HANDLER_COLOR, fastScrollerHandleColor)
                bundle.putInt(
                    CountryPicker.EXTRA_SHOW_FAST_SCROLL_BUBBLE_TEXT_APPEARANCE,
                    fastScrollerBubbleTextAppearance
                )
                bundle.putBoolean(CountryPicker.EXTRA_SHOW_COUNTRY_CODE_IN_LIST, isShowCountryCodeInList)
                intent.putExtras(bundle)
                (context as Activity).startActivityForResult(intent, CountryPicker.PICKER_REQUEST_CODE)
            } catch (e: ClassCastException) {
                e.printStackTrace()
                CountryPickerDialog.openPickerDialog(
                    context, this, isShowCountryCodeInList,
                    isSearchAllowed, isDialogKeyboardAutoPopup, isShowFastScroller,
                    fastScrollerBubbleColor, fastScrollerHandleColor, fastScrollerBubbleTextAppearance
                )
            }

        } else {
            CountryPickerDialog.openPickerDialog(
                context, this, isShowCountryCodeInList,
                isSearchAllowed, isDialogKeyboardAutoPopup, isShowFastScroller,
                fastScrollerBubbleColor, fastScrollerHandleColor, fastScrollerBubbleTextAppearance
            )
        }
    }

    fun handleActivityResult(data: Intent) {
        val country = data.getParcelableExtra<Country>(CountryPicker.EXTRA_COUNTRY)
        Log.d(TAG, "Country: $country")
        setSelectedCountry(country)
        updateSelectedCountry(country)

    }

    /**
     * Saves the last selected Country code into Sharedpref
     * when remember last selection is set
     */
    private fun saveLastSelectedCountryCode(countryCode: String?) {
        //get instance of the shared pref
        val preferences = context.getSharedPreferences(CP_PREF_FILE, Context.MODE_PRIVATE)

        //get the Editor
        val editor = preferences.edit()

        //put the code into the editor
        editor.putString(LAST_SELECTION_TAG, countryCode)

        //save the code
        editor.apply()

    }

    private fun loadLastSelectedCountryCode() {
        //get instance of the shared pref
        val preferences = context.getSharedPreferences(CP_PREF_FILE, Context.MODE_PRIVATE)

        //get the last selected country code
        val lastSelectedCode = preferences.getString(LAST_SELECTION_TAG, defaultCountry.code)

        //set the country
        setSelectedCountry(getCountryForName(languageToApply, lastSelectedCode))
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        return SavedState(superState, selectedCountryCode)

    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        val countryCode = savedState.countryCode
        if (countryCode != null) {
            setSelectedCountry(getCountryForName(languageToApply, countryCode))
        }
    }

    //convenient class to save and restore the view state
    protected class SavedState : BaseSavedState {

        var countryCode: String? = null

        constructor(source: Parcelable, countryCode: String?) : super(source) {
            this.countryCode = countryCode
        }

        constructor(source: Parcel) : super(source) {}

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeString(this.countryCode)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {

                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState> {
                    return emptyArray()
                }

            }
        }

    }


    /**
     * Lis of all the supported languages
     */
    enum class Language(var code: String) {
        ENGLISH("en")
    }

    /**
     * All the supported network detection steps
     */
    enum class AutoDetectionPref(//local then network then sim

        internal var representation: String
    ) {
        SIM_ONLY("1"), //sim only
        NETWORK_ONLY("2"), //network only
        LOCALE_ONLY("3"), //local only
        SIM_NETWORK("12"), //sim  then network
        NETWORK_SIM("21"), //network
        SIM_LOCALE("13"), //sim then local
        LOCALE_SIM("31"), //local then sim
        NETWORK_LOCALE("23"), //network then local
        LOCALE_NETWORK("32"), //local then network
        SIM_NETWORK_LOCALE("123"), //sim then network then local
        SIM_LOCALE_NETWORK("132"), //sim then local then network
        NETWORK_SIM_LOCALE("213"), //network then sim then local
        NETWORK_LOCALE_SIM("231"), //network the local then sim
        LOCALE_SIM_NETWORK("312"), //local then sim then network
        LOCALE_NETWORK_SIM("321");


        companion object {

            fun getPrefForValue(value: String): AutoDetectionPref {
                for (autoDetectionPref in AutoDetectionPref.values()) {
                    if (autoDetectionPref.representation == value) {
                        return autoDetectionPref
                    }
                }
                return SIM_NETWORK_LOCALE
            }
        }
    }

    companion object {
        private val EXTRA_PADDING = 5
        private val TAG = PhoneNumberEditText::class.java.simpleName
        private val CP_PREF_FILE = "cp_pref_file"
        private val LAST_SELECTION_TAG = "last_selection_tag"
        private val defaultCountry = Country("United States", "+1", "US")

        fun fromTextNumber(phoneNumberEditText: PhoneNumberEditText, value: String): String {
            return value.removePrefix(phoneNumberEditText.selectedCountryDialCode ?: "+1")
        }

        fun toTextNumber(phoneNumberEditText: PhoneNumberEditText): String {
            return phoneNumberEditText.fullNumber
        }
    }
}
