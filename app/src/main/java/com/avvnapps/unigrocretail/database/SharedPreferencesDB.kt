package com.avvnapps.unigrocretail.database

import android.content.Context
import androidx.preference.PreferenceManager
import com.avvnapps.unigrocretail.R
import com.avvnapps.unigrocretail.models.AddressItem
import com.avvnapps.unigrocretail.models.GeoIp
import com.avvnapps.unigrocretail.models.UserInfo
import com.google.gson.Gson

class SharedPreferencesDB {

    companion object {

        fun savePreferredUser(context: Context, user: UserInfo) {
            var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            var editor = sharedPreferences.edit()

            var json = Gson().toJson(user)
            editor.putString(context.getString(R.string.preferred_user), json)

            editor.apply()
            editor.commit()
        }

        fun getSavedUser(context: Context): UserInfo? {
            var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            var json = sharedPreferences.getString(context.getString(R.string.preferred_user), "")
            if (json?.isEmpty()!!)
                return null
            return Gson().fromJson(json, UserInfo::class.java)

        }

        fun savePreferredAddress(context: Context, address: AddressItem) {
            var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            var editor = sharedPreferences.edit()

            var json = Gson().toJson(address)
            editor.putString(context.getString(R.string.preferred_address), json)

            editor.apply()
            editor.commit()
        }

        fun getSavedAddress(context: Context): AddressItem? {
            var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            var json =
                sharedPreferences.getString(context.getString(R.string.preferred_address), "")
            if (json?.isEmpty()!!)
                return null
            return Gson().fromJson(json, AddressItem::class.java)
        }

        fun savePreferredGeoIp(context: Context, geoIp: GeoIp) {
            var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            var editor = sharedPreferences.edit()

            var json = Gson().toJson(geoIp)
            editor.putString(context.getString(R.string.preferred_geoip), json)

            editor.apply()
            editor.commit()
        }

        fun getSavedGeoIp(context: Context): GeoIp? {
            var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            var json = sharedPreferences.getString(context.getString(R.string.preferred_geoip), "")
            if (json!!.isEmpty())
                return null
            return Gson().fromJson(json, GeoIp::class.java)
        }
    }

}