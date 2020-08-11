package com.avvnapps.unigrocretail.dashboard.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.avvnapps.unigrocretail.R
import com.avvnapps.unigrocretail.models.OrderItem
import com.avvnapps.unigrocretail.quote_submitted_order.CalculateQuotationPriceActivity
import com.avvnapps.unigrocretail.utils.DateTimeUtils
import com.avvnapps.unigrocretail.utils.OrderUtils
import kotlinx.android.synthetic.main.item_submitted_order.view.*

class SubmittedOrderAdapter(var context: Context, var submittedOrderList: List<OrderItem>) :
    RecyclerView.Adapter<SubmittedOrderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_submitted_order, parent, false)

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return submittedOrderList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderItem = submittedOrderList[position]
        holder.bindItems(context, orderItem)


    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var TAG = "SUBMITTED_ORDER_ITEM_ADAPTER"
        lateinit var context: Context

        fun bindItems(context: Context, orderItem: OrderItem) {
            this.context = context

            val preferredDeliveryTime = DateTimeUtils.getPreferredDeliveryDate(
                orderItem.preferredDate,
                orderItem.preferredTimeSlot
            )
            itemView.item_submitted_order_estimated_delivery_tv.text = preferredDeliveryTime
            itemView.item_submitted_order_id_tv.text = orderItem.orderId.toString()
            itemView.item_submitted_order_cart_size_tv.text = orderItem.cartItems.size.toString()

            setupOrderStatus(
                orderItem.orderStatus,
                OrderUtils.getTime(orderItem.orderStatus, orderItem),
                orderItem.isPickup
            )

            itemView.setOnClickListener {
                val intent = Intent(
                    context,
                    CalculateQuotationPriceActivity::class.java
                )
                intent.putExtra(context.getString(R.string.selected_order_item), orderItem)

                context.startActivity(intent)
            }
        }

        private fun setupOrderStatus(orderStatus: Int, time: Long, isPickup: Boolean) {
            var formattedDate = DateTimeUtils.dateTimeFormatter.format(time)
            if (time == 0L)
                formattedDate = ""
            val statusArray = context.resources.getStringArray(R.array.order_status_labels)
            var status = ""

            when (orderStatus) {
                0 -> status = statusArray[0]
                1 -> {
                    status = statusArray[1]
                    formattedDate = ""
                }
                2 -> status = statusArray[2]
                3 -> {
                    status = statusArray[3]
                    formattedDate = ""
                }
                4 -> {
                    if (isPickup)
                        status = statusArray[4]
                    else
                        status = statusArray[5]
                }
                5 -> {
                    if (isPickup)
                        status = statusArray[6]
                    else
                        status = statusArray[7]
                }

            }
            //itemView.item_order_status_tv.text = status
            itemView.item_order_submitted_date_tv.text = formattedDate
        }

    }
}