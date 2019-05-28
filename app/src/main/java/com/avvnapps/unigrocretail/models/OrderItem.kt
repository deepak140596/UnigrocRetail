package com.avvnapps.unigrocretail.models

import com.google.firebase.auth.FirebaseAuth
import java.io.Serializable
import java.util.*

class OrderItem (var orderId: Long = Date().time,
                 var customerId: String = FirebaseAuth.getInstance().currentUser!!.email.toString(),
                 var cartItems : List<CartEntity> = emptyList(),
                 var isPickup: Boolean = true,
                 var dateSubmitted: Long = Date().time,
                 var dateQuoted: Long =0,
                 var datePlaced: Long = 0,
                 var dateReady: Long =0,
                 var dateCompleted: Long = 0,
                 var preferredDate: Long = 0,
                 var preferredTimeSlot : Int = 0,
                 var address: AddressItem = AddressItem(),
                 var orderStatus: Int = 0,
                 var retailerId: String = "",
                 var isExpanded: Boolean = false,
                 var quotations: List<RetailerQuotationItem> = emptyList())
    : Serializable{
    override fun toString(): String {
        return "OrderItem(orderId=$orderId, customerId='$customerId', cartItems=$cartItems, isPickup=$isPickup, dateSubmitted=$dateSubmitted, dateQuoted=$dateQuoted, datePlaced=$datePlaced, dateReady=$dateReady, dateCompleted=$dateCompleted, preferredDate=$preferredDate, preferredTimeSlot=$preferredTimeSlot, address=$address, orderStatus=$orderStatus, retailerId='$retailerId')"
    }
}