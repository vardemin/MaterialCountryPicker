package com.github.vardemin.materialcountrypicker

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

val AZ_STRING = "abcdefghijklmnopqrstuvwxyz"

/**
 * Loads a list of Country
 *
 * @param context The given context
 */
fun loadDataFromJson(context: Context): List<Country> {

    val inputStream = context.resources.openRawResource(R.raw.english)
    val jsonString = readJsonFile(inputStream)

    //create gson
    val gsonBuilder = GsonBuilder()
    gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
    val gson = gsonBuilder.create()

    val countries = gson.fromJson(jsonString, Array<Country>::class.java)
    return listOf(*countries)
}


fun mapList(countries: List<Country>): Map<String, List<Country>> {
    val groups = HashMap<String, List<Country>>()
    val chars = AZ_STRING.toCharArray()
    for (aChar in chars) {
        val group = ArrayList<Country>()
        for (country in countries) {
            if (country.name!!.toLowerCase().startsWith(aChar.toString())) {
                group.add(country)
            }
        }

        groups[aChar.toString()] = group
    }

    return groups
}

/**
 * Reads json file given an inputStream and return a json string
 *
 * @param inputStream The input stream to read from
 */
private fun readJsonFile(inputStream: InputStream): String {
    val outputStream = ByteArrayOutputStream()

    val bufferByte = ByteArray(1024)
    var length: Int = 0
    try {
        while ({length = inputStream.read(bufferByte); length}() != -1) {
            outputStream.write(bufferByte, 0, length)
        }
        outputStream.close()
        inputStream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return outputStream.toString()
}

/**
 * Util method used to hide the keyboard
 *
 * @param context The UI context
 */
private fun hideKeyboard(context: Context) {
    if (context is Activity) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = context.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(context)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

/**
 * Returns image res based on country name code
 *
 * @param country
 * @return
 */
@DrawableRes
internal fun getFlagResID(country: Country): Int {
    when (country.code!!.toLowerCase()) {
        //this should be sorted based on country name code.
        "ad" //andorra
        -> return R.drawable.ad
        "ae" //united arab emirates
        -> return R.drawable.ae
        "af" //afghanistan
        -> return R.drawable.ae
        "ag" //antigua & barbuda
        -> return R.drawable.ag
        "ai" //anguilla // Caribbean Islands
        -> return R.drawable.ai//
        "al" //albania
        -> return R.drawable.al
        "am" //armenia
        -> return R.drawable.am
        "ao" //angola
        -> return R.drawable.ao
        "aq" //antarctica // custom
        -> return R.drawable.aq
        "ar" //argentina
        -> return R.drawable.ar
        "at" //austria
        -> return R.drawable.at
        "au" //australia
        -> return R.drawable.au
        "aw" //aruba
        -> return R.drawable.aw
        "az" //azerbaijan
        -> return R.drawable.az
        "ba" //bosnia and herzegovina
        -> return R.drawable.ba
        "bb" //barbados
        -> return R.drawable.bb
        "bd" //bangladesh
        -> return R.drawable.bd
        "be" //belgium
        -> return R.drawable.be
        "bf" //burkina faso
        -> return R.drawable.bf
        "bg" //bulgaria
        -> return R.drawable.bg
        "bh" //bahrain
        -> return R.drawable.bh
        "bi" //burundi
        -> return R.drawable.bi
        "bj" //benin
        -> return R.drawable.bj
        "bl" //saint barthélemy
        -> return R.drawable.bl// custom
        "bm" //bermuda
        -> return R.drawable.bm//
        "bn" //brunei darussalam // custom
        -> return R.drawable.bn
        "bo" //bolivia, plurinational state of
        -> return R.drawable.bo
        "br" //brazil
        -> return R.drawable.br
        "bs" //bahamas
        -> return R.drawable.bs
        "bt" //bhutan
        -> return R.drawable.bt
        "bw" //botswana
        -> return R.drawable.bw
        "by" //belarus
        -> return R.drawable.by
        "bz" //belize
        -> return R.drawable.bz
        "ca" //canada
        -> return R.drawable.ca
        "cc" //cocos (keeling) islands
        -> return R.drawable.cc// custom
        "cd" //congo, the democratic republic of the
        -> return R.drawable.cd
        "cf" //central african republic
        -> return R.drawable.cf
        "cg" //congo
        -> return R.drawable.cg
        "ch" //switzerland
        -> return R.drawable.ch
        "ci" //côte d\'ivoire
        -> return R.drawable.ci
        "ck" //cook islands
        -> return R.drawable.ck
        "cl" //chile
        -> return R.drawable.cl
        "cm" //cameroon
        -> return R.drawable.cm
        "cn" //china
        -> return R.drawable.cn
        "co" //colombia
        -> return R.drawable.co
        "cr" //costa rica
        -> return R.drawable.cr
        "cu" //cuba
        -> return R.drawable.cu
        "cv" //cape verde
        -> return R.drawable.cv
        "cx" //christmas island
        -> return R.drawable.cx
        "cy" //cyprus
        -> return R.drawable.cy
        "cz" //czech republic
        -> return R.drawable.cz
        "de" //germany
        -> return R.drawable.de
        "dj" //djibouti
        -> return R.drawable.dj
        "dk" //denmark
        -> return R.drawable.dk
        "dm" //dominica
        -> return R.drawable.dm
        "do" //dominican republic
        -> return R.drawable._do
        "dz" //algeria
        -> return R.drawable.dz
        "ec" //ecuador
        -> return R.drawable.ec
        "ee" //estonia
        -> return R.drawable.ee
        "eg" //egypt
        -> return R.drawable.eg
        "er" //eritrea
        -> return R.drawable.er
        "es" //spain
        -> return R.drawable.es
        "et" //ethiopia
        -> return R.drawable.et
        "fi" //finland
        -> return R.drawable.fi
        "fj" //fiji
        -> return R.drawable.fj
        "fk" //falkland islands (malvinas)
        -> return R.drawable.fk //
        "fm" //micronesia, federated states of
        -> return R.drawable.fm
        "fo" //faroe islands
        -> return R.drawable.fo
        "fr" //france
        -> return R.drawable.fr
        "ga" //gabom
        -> return R.drawable.ga
        "gb" //united kingdom
        -> return R.drawable.gb
        "gd" //grenada
        -> return R.drawable.gd
        "ge" //georgia
        -> return R.drawable.ge
        "gf" //guyane
        -> return R.drawable.gf
        "gh" //ghana
        -> return R.drawable.gh
        "gi" //gibraltar
        -> return R.drawable.gi
        "gl" //greenland
        -> return R.drawable.gl
        "gm" //gambia
        -> return R.drawable.gm
        "gn" //guinea
        -> return R.drawable.gn
        "gq" //equatorial guinea
        -> return R.drawable.gq
        "gr" //greece
        -> return R.drawable.gr
        "gt" //guatemala
        -> return R.drawable.gt
        "gw" //guinea-bissau
        -> return R.drawable.gw
        "gy" //guyana
        -> return R.drawable.gy
        "hk" //hong kong
        -> return R.drawable.hk
        "hn" //honduras
        -> return R.drawable.hn
        "hr" //croatia
        -> return R.drawable.hr
        "ht" //haiti
        -> return R.drawable.ht
        "hu" //hungary
        -> return R.drawable.hu
        "id" //indonesia
        -> return R.drawable.id
        "ie" //ireland
        -> return R.drawable.ie
        "il" //israel
        -> return R.drawable.il
        "im" //isle of man
        -> return R.drawable.im
        "is" //Iceland
        -> return R.drawable.`is`
        "in" //india
        -> return R.drawable.`in`
        "iq" //iraq
        -> return R.drawable.iq
        "ir" //iran, islamic republic of
        -> return R.drawable.ir
        "it" //italy
        -> return R.drawable.it
        "jm" //jamaica
        -> return R.drawable.jm
        "jo" //jordan
        -> return R.drawable.jo
        "jp" //japan
        -> return R.drawable.jp
        "ke" //kenya
        -> return R.drawable.ke
        "kg" //kyrgyzstan
        -> return R.drawable.kg
        "kh" //cambodia
        -> return R.drawable.kh
        "ki" //kiribati
        -> return R.drawable.ki
        "km" //comoros
        -> return R.drawable.km
        "kn" //st kitts & nevis
        -> return R.drawable.kn
        "kp" //north korea
        -> return R.drawable.kp
        "kr" //south korea
        -> return R.drawable.kr
        "kw" //kuwait
        -> return R.drawable.kw
        "ky" //Cayman_Islands
        -> return R.drawable.ky
        "kz" //kazakhstan
        -> return R.drawable.kz
        "la" //lao people\'s democratic republic
        -> return R.drawable.la
        "lb" //lebanon
        -> return R.drawable.lb
        "lc" //st lucia
        -> return R.drawable.lc
        "li" //liechtenstein
        -> return R.drawable.li
        "lk" //sri lanka
        -> return R.drawable.lk
        "lr" //liberia
        -> return R.drawable.lr
        "ls" //lesotho
        -> return R.drawable.ls
        "lt" //lithuania
        -> return R.drawable.lt
        "lu" //luxembourg
        -> return R.drawable.lu
        "lv" //latvia
        -> return R.drawable.lv
        "ly" //libya
        -> return R.drawable.ly
        "ma" //morocco
        -> return R.drawable.ma
        "mc" //monaco
        -> return R.drawable.mc
        "md" //moldova, republic of
        -> return R.drawable.md
        "me" //montenegro
        -> return R.drawable.me// custom
        "mg" //madagascar
        -> return R.drawable.mg
        "mh" //marshall islands
        -> return R.drawable.mh
        "mk" //macedonia, the former yugoslav republic of
        -> return R.drawable.mk
        "ml" //mali
        -> return R.drawable.ml
        "mm" //myanmar
        -> return R.drawable.mm
        "mn" //mongolia
        -> return R.drawable.mn
        "mo" //macao
        -> return R.drawable.mo
        "mq" //martinique
        -> return R.drawable.mq
        "mr" //mauritania
        -> return R.drawable.mr
        "ms" //montserrat
        -> return R.drawable.ms
        "mt" //malta
        -> return R.drawable.mt
        "mu" //mauritius
        -> return R.drawable.mu
        "mv" //maldives
        -> return R.drawable.mv
        "mw" //malawi
        -> return R.drawable.mw
        "mx" //mexico
        -> return R.drawable.mx
        "my" //malaysia
        -> return R.drawable.my
        "mz" //mozambique
        -> return R.drawable.mz
        "na" //namibia
        -> return R.drawable.na
        "nc" //new caledonia
        -> return R.drawable.nc
        "ne" //niger
        -> return R.drawable.ne
        "ng" //nigeria
        -> return R.drawable.ng
        "ni" //nicaragua
        -> return R.drawable.ni
        "nl" //netherlands
        -> return R.drawable.nl
        "no" //norway
        -> return R.drawable.no
        "np" //nepal
        -> return R.drawable.np
        "nr" //nauru
        -> return R.drawable.nr
        "nu" //niue
        -> return R.drawable.nu
        "nz" //new zealand
        -> return R.drawable.nz
        "om" //oman
        -> return R.drawable.om
        "pa" //panama
        -> return R.drawable.pa
        "pe" //peru
        -> return R.drawable.pe
        "pf" //french polynesia
        -> return R.drawable.pf
        "pg" //papua new guinea
        -> return R.drawable.pg
        "ph" //philippines
        -> return R.drawable.ph
        "pk" //pakistan
        -> return R.drawable.pk
        "pl" //poland
        -> return R.drawable.pl
        "pm" //saint pierre and miquelon
        -> return R.drawable.pm
        "pn" //pitcairn
        -> return R.drawable.pn
        "pr" //puerto rico
        -> return R.drawable.pr
        "ps" //palestine
        -> return R.drawable.ps
        "pt" //portugal
        -> return R.drawable.pt
        "pw" //palau
        -> return R.drawable.pw
        "py" //paraguay
        -> return R.drawable.py
        "qa" //qatar
        -> return R.drawable.qa
        "re" //la reunion
        -> return R.drawable.re
        "ro" //romania
        -> return R.drawable.ro
        "rs" //serbia
        -> return R.drawable.rs // custom
        "ru" //russian federation
        -> return R.drawable.ru
        "rw" //rwanda
        -> return R.drawable.rw
        "sa" //saudi arabia
        -> return R.drawable.sa
        "sb" //solomon islands
        -> return R.drawable.sb
        "sc" //seychelles
        -> return R.drawable.sc
        "sd" //sudan
        -> return R.drawable.sd
        "se" //sweden
        -> return R.drawable.se // custom
        "si" //slovenia
        -> return R.drawable.si
        "sk" //slovakia
        -> return R.drawable.sk
        "sl" //sierra leone
        -> return R.drawable.sl
        "sm" //san marino
        -> return R.drawable.sm
        "sn" //senegal
        -> return R.drawable.sn
        "so" //somalia
        -> return R.drawable.so
        "sr" //suriname
        -> return R.drawable.sr
        "st" //sao tome and principe
        -> return R.drawable.st
        "sv" //el salvador
        -> return R.drawable.sv
        "sx" //sint maarten
        -> return R.drawable.sx
        "sy" //syrian arab republic
        -> return R.drawable.sy
        "sz" //swaziland
        -> return R.drawable.sz
        "tc" //turks & caicos islands
        -> return R.drawable.tc
        "td" //chad
        -> return R.drawable.td
        "tg" //togo
        -> return R.drawable.tg
        "th" //thailand
        -> return R.drawable.th
        "tj" //tajikistan
        -> return R.drawable.tj
        "tk" //tokelau
        -> return R.drawable.tk // custom
        "tl" //timor-leste
        -> return R.drawable.tl
        "tm" //turkmenistan
        -> return R.drawable.tm
        "tn" //tunisia
        -> return R.drawable.tn
        "to" //tonga
        -> return R.drawable.to
        "tr" //turkey
        -> return R.drawable.tr
        "tt" //trinidad & tobago
        -> return R.drawable.tt
        "tv" //tuvalu
        -> return R.drawable.tv
        "tw" //taiwan, province of china
        -> return R.drawable.tw
        "tz" //tanzania, united republic of
        -> return R.drawable.tz
        "ua" //ukraine
        -> return R.drawable.ua
        "ug" //uganda
        -> return R.drawable.ug
        "us" //united states
        -> return R.drawable.us
        "uy" //uruguay
        -> return R.drawable.uy
        "uz" //uzbekistan
        -> return R.drawable.uz
        "va" //holy see (vatican city state)
        -> return R.drawable.va
        "vc" //st vincent & the grenadines
        -> return R.drawable.vc
        "ve" //venezuela, bolivarian republic of
        -> return R.drawable.ve
        "vg" //british virgin islands
        -> return R.drawable.vg
        "vi" //us virgin islands
        -> return R.drawable.vi
        "vn" //vietnam
        -> return R.drawable.vn
        "vu" //vanuatu
        -> return R.drawable.vu
        "wf" //wallis and futuna
        -> return R.drawable.wf
        "ws" //samoa
        -> return R.drawable.ws
        "ye" //yemen
        -> return R.drawable.ye
        "yt" //mayotte
        -> return R.drawable.re//no exact flag found
        "za" //south africa
        -> return R.drawable.za
        "zm" //zambia
        -> return R.drawable.zm
        "zw" //zimbabwe
        -> return R.drawable.zw
        else -> return R.drawable.flag_tp
    }
}

/**
 * This method converts dp unit to equivalent pixels, depending on device density.
 *
 * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
 * @param context Context to get resources and device specific display metrics
 * @return A float value to represent px equivalent to dp depending on device density
 */
fun dpToPixel(dp: Float, context: Context): Float {
    val resources = context.resources
    val metrics = resources.displayMetrics
    return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

/**
 * This method converts device specific pixels to density independent pixels.
 *
 * @param px      A value in px (pixels) unit. Which we need to convert into db
 * @param context Context to get resources and device specific display metrics
 * @return A float value to represent dp equivalent to px value
 */
fun pixelsToDp(px: Float, context: Context): Float {
    val resources = context.resources
    val metrics = resources.displayMetrics
    return px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}