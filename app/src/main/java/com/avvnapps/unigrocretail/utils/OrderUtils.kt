package com.avvnapps.unigrocretail.utils

import androidx.appcompat.app.AppCompatActivity
import com.avvnapps.unigrocretail.models.OrderItem

class OrderUtils{
    companion object {
        fun getTime(orderStatus: Int,orderItem: OrderItem) : Long{
            when (orderStatus) {
                1 -> return orderItem.dateSubmitted
                2 -> return orderItem.dateQuoted
                3 -> return orderItem.datePlaced
                4 -> return 0
                5 -> return orderItem.dateReady
                6 -> return orderItem.dateCompleted
            }
            return 0
        }

        fun getOrderType(orderItem: OrderItem) : String{
            if(orderItem.isPickup)
                return "Pickup"
            else return "Delivery"
        }

        fun getOrderStatus(orderStatus: Int) : String{
            if(orderStatus == ApplicationConstants.ORDER_SUBMITTED)
                return "Submitted"
            if(orderStatus == ApplicationConstants.ORDER_QUOTED)
                return "Quoted"
            if(orderStatus == ApplicationConstants.ORDER_PLACED)
                return "Placed"
            if(orderStatus == ApplicationConstants.ORDER_PREPARING)
                return "Preparing"
            if(orderStatus == ApplicationConstants.ORDER_READY)
                return "Ready for Pickup/Delivery"
            if(orderStatus == ApplicationConstants.ORDER_PICKED_DELIVERED)
                return "Completed"
            return ""
        }

        fun getLocalOrders(activity: AppCompatActivity, orders : List<OrderItem>): MutableList<OrderItem> {
            val localOrders: MutableList<OrderItem> = mutableListOf()
            for(orderItem in orders){
                if(LocationUtils.isNear(activity,orderItem.address))
                    localOrders.add(orderItem)
            }
            return localOrders
        }
    }
}