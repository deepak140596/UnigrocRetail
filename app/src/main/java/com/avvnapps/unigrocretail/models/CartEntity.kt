package com.avvnapps.unigrocretail.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.NonNull
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import java.io.Serializable

/**
 * Created by Deepak Prasad on 14-02-2019.
 */

@Entity(tableName = "cart_table")
class CartEntity constructor(itemId: String, name: String , category: String,
                             clubbedCategory: String , photoUrl: String , quantity: Int,
                             price: Double, metricWeight: String) : SearchSuggestion,Serializable{
    override fun writeToParcel(dest: Parcel?, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun getBody(): String {
        return "$name in $category"
    }

    constructor(parcel : Parcel) :this()

    constructor():this("","","","","",0,0.0,"")

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "item_id")
    var itemId: String = itemId

    @ColumnInfo(name = "name")
    var name: String = name

    @ColumnInfo(name = "category")
    var category: String = category

    @ColumnInfo(name = "clubbed_category")
    var clubbedCategory: String = clubbedCategory

    @ColumnInfo(name = "photo_url")
    var photoUrl: String = photoUrl

    @ColumnInfo(name = "quantity")
    var quantity: Int = quantity

    @ColumnInfo(name = "price")
    var price: Double = price

    @ColumnInfo(name = "metric_weight")
    var metricWeight: String = metricWeight

    fun incrementQuantity(){
        this.quantity = this.quantity + 1
    }

    fun decrementQuantity(){
        if(this.quantity > 0)
            this.quantity = this.quantity - 1
    }

    override fun toString(): String {
        return "CartEntity(itemId='$itemId', name='$name', category='$category', clubbedCategory='$clubbedCategory', photoUrl='$photoUrl', quantity=$quantity, price=$price, metricWeight='$metricWeight')"
    }

    companion object CREATOR : Parcelable.Creator<CartEntity> {
        override fun createFromParcel(parcel: Parcel): CartEntity {
            return CartEntity(parcel)
        }

        override fun newArray(size: Int): Array<CartEntity?> {
            return arrayOfNulls(size)
        }
    }


}