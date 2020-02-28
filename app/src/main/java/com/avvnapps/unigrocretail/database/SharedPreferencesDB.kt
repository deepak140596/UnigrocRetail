package com.avvnapps.unigrocretail.database

import android.content.Context
import android.preference.PreferenceManager
import com.avvnapps.unigrocretail.R
import com.avvnapps.unigrocretail.models.AddressItem
import com.google.gson.Gson

class SharedPreferencesDB {

    companion object {
        fun savePreferredAddress(context : Context, address: AddressItem){
            var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            var editor = sharedPreferences.edit()

            var json = Gson().toJson(address)
            editor.putString(context.getString(R.string.preferred_address),json)

            editor.apply()
            editor.commit()
        }

        fun getSavedAddress(context: Context): AddressItem?{
            var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            var json = sharedPreferences.getString(context.getString(R.string.preferred_address),"")
            if(json?.isEmpty()!!)
                return null
            return Gson().fromJson(json, AddressItem::class.java)
        }
    }

}