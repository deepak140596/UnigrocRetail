package com.avvnapps.unigrocretail.models

import android.os.Parcel
import android.os.Parcelable
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion

class AvailableSearchItems() : SearchSuggestion {

    constructor(parcel: Parcel) : this() {
    }



    override fun writeToParcel(dest: Parcel?, flags: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun describeContents(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBody(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object CREATOR : Parcelable.Creator<AvailableSearchItems> {
        override fun createFromParcel(parcel: Parcel): AvailableSearchItems {
            return AvailableSearchItems(parcel)
        }

        override fun newArray(size: Int): Array<AvailableSearchItems?> {
            return arrayOfNulls(size)
        }
    }
}