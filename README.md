# MaterialCountryPicker
Country (ISO/phone code) picker library based on EditText (TextInputEditText). [Fresh port to kotlin androidx of https://github.com/jerryOkafor/CountryPicker]
Why edittext? - Ready implementation for material input field. Just wrap this widget with TextInputLayout.

[![](https://jitpack.io/v/po10cio/TimeLineView.svg)](https://jitpack.io/#vardemin/MaterialCountryPicker)

## Quick Setup
### 1. Include library

**Using Gradle**

MaterialCountryPicker is currently available in on Jitpack so add the following line before every other thing if you have not done that already.

```gradle
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
	
Then add the following line 

``` gradle
dependencies {
  implementation 'com.github.vardemin.MaterialCountryPicker:materialcountrypicker:0.1.2'
}
```

### 2. Usage
In your XML layout include the TimelineView as follows:

```xml
<com.github.vardemin.materialcountrypicker.PhoneNumberEditText
        android:id="@+id/countryPicker"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginEnd="8dp"
        android:layout_marginStart="8dp"
        android:layout_marginTop="58dp"
        android:hint="08030720816"
        app:cp_autoDetectCountry="false"
        app:cp_fastScrollerBubbleColor="@color/colorPrimary"
        app:cp_fastScrollerBubbleTextAppearance="@style/TextAppearance.AppCompat.Medium"
        app:cp_fastScrollerHandleColor="@color/colorAccent"
        app:cp_preferredCountries="us,ru,am"
        app:cp_rememberLastSelection="true"
        app:cp_setCountryCodeBorder="true"
        app:cp_showCountryCodeInView="true"
        app:cp_showCountryDialCodeInView="true"
        app:cp_showFastScroll="true"
        app:cp_showFullScreeDialog="true" />
      
```
## XML Attributes

List of xml attribues that are available in PhoneNumberEdittext

| XML Attribute | Description   | Default |
| ------------- |:---------------:|:---------:| 
| cp_autoDetectCountry      | Enables auto detection of the country the device is currently being used | true|
|cp_searchAllowed|Enables search functionality in the CountryPicker.|true
|cp_showFastScroll|Determines whether the Fastscroller button is show or not.|true
|cp_dialogKeyboardAutoPopup|Use this to toggle Kwyboard auto popup for  CountryPicker in dialog mode. |true
|cp_showFullScreeDialog|Use this to switch between Dialog and full screen Pickers| false
|cp_showCountryCodeInView|Dtermins whether the country code is shown in the EditText|true
|cp_showCountryCodeInList|Determins whether the Country Code is shown in the picker list.|true
|cp_showCountryDialCodeInView|Determind if the Country Dial code is shown in the view.|true
|cp_showCountryDialCodeInList|Dtermins if the Country Code is show in the picker list.|true
|cp_showCountryDropdownArrow|Dtermins if the Country Dropdown Arrow is shown near the flag.|false
|cp_setCountryCodeBorder|Determins whether a fancy border is shown around the Picker view.|false
|cp_defaultCountryName|Use this to specify the dafult country you want to show in the PickerView|Empty
|cp_preferredCountries|Use this to enter comma seperated list of prefferd countries.|Empty
|cp_fastScrollerBubbleColor| Sets the color of the fast scroller bubble color| #5e64ce
|cp_fastScrollerBubbleTextAppearance|Sets the testAppearance of the fastScroller| TextAppearance.AppCompat.Medium|
|cp_fastScrollerHandleColor|Sets the fastscroller handle color| #8f93d1

## License

CountryPicker is distributed under the MIT license. [See LICENSE](https://github.com/vardemin/MaterialCountryPicker/blob/master/LICENSE.md) for details.
