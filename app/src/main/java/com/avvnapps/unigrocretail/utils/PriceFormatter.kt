package com.avvnapps.unigrocretail.utils

import android.content.Context
import com.avvnapps.unigrocretail.database.SharedPreferencesDB
import de.tobiasschuerg.money.Currency
import de.tobiasschuerg.money.Money

class PriceFormatter {

    // equivalent to static scope
    companion object {
        val CURRENCY_SYMBOL = "₹"

        var CURRENCY_FORMAT = " %.2f"

        val inr = Currency("INR", "INR", 1.00)

        val euro = Currency("EUR", "Euro", 0.012)
        val usd = Currency("USD", "USD", 0.013)
        val gbr = Currency("GBP", "GBP", 0.010)

        fun getCurrencySymbol(context: Context): String {
            var geoipVal = SharedPreferencesDB.getSavedGeoIp(context)
            if (geoipVal!!.currency == "EUR") {
                return "€"
            }
            if (geoipVal.currency == "USD") {
                return "$"
            }
            if (geoipVal.currency == "GBP") {
                return "£"

            }
            return CURRENCY_SYMBOL
        }

        fun getCurrencyFormat(): String {
            return CURRENCY_FORMAT
        }

        fun getFormattedPrice(context: Context, price: Double): String {
            val inrPrice = Money(price, inr)
            var geoipVal = SharedPreferencesDB.getSavedGeoIp(context)
            if (geoipVal!!.currency == "EUR") {
                return inrPrice.convertInto(euro).toString()
            }
            if (geoipVal.currency == "USD") {
                return inrPrice.convertInto(usd).toString()
            }
            if (geoipVal.currency == "GBP") {
                return inrPrice.convertInto(gbr).toString()
            }

            return CURRENCY_SYMBOL + String.format(CURRENCY_FORMAT, price)
        }

        fun getDefaultPrice(context: Context, price: Double): Double {

            val usdPrice = Money(price, usd)
            val euroPrice = Money(price, euro)
            val gbrPrice = Money(price, gbr)

            if (getCurrencySymbol(context) == "€") {
                val defPrice = euroPrice.convertInto(inr).toString()
                return defPrice.replace(("[^\\d.]").toRegex(), "").toDouble()
            }
            if (getCurrencySymbol(context) == "£") {
                val defPrice = gbrPrice.convertInto(inr).toString()
                return defPrice.replace(("[^\\d.]").toRegex(), "").toDouble()
            }
            if (getCurrencySymbol(context) == "$") {
                val defPrice = usdPrice.convertInto(inr).toString()
                return defPrice.replace(("[^\\d.]").toRegex(), "").toDouble()
            }
            return price
        }
    }

}