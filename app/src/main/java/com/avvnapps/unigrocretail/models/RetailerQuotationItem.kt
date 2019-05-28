package com.avvnapps.unigrocretail.models

import java.io.Serializable

class RetailerQuotationItem (var retailerId : String ="",
                             var retailerName: String = "",
                             var photoUrl : String ="",
                             var quotedPrice: Double = 0.0,
                             var addressItem: AddressItem = AddressItem(),
                             var cartItems : List<CartEntity> = emptyList(),
                             //var quotedBySelf: Boolean = false,
                             var rating: Double = 0.0):Serializable{

    companion object {
        // compare by rating high to low
        var compareByRating: Comparator<RetailerQuotationItem> = Comparator { o1, o2 ->
            if(o1.rating < o2.rating) return@Comparator  1
            else return@Comparator -1
        }
        // compare by distance
        /*var compareByLocation : Comparator<AddressItem> = Comparator { o1, o2 ->
        //var distance1 = LocationUtils.getDistance()
        }*/
    }



}