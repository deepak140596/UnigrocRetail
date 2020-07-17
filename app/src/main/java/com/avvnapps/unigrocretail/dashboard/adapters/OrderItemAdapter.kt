package com.avvnapps.unigrocretail.dashboard.adapters

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avvnapps.unigrocretail.R
import com.avvnapps.unigrocretail.models.OrderItem
import com.avvnapps.unigrocretail.quote_submitted_order.SummaryItemAdapter
import com.avvnapps.unigrocretail.utils.ApplicationConstants
import com.avvnapps.unigrocretail.utils.OrderUtils
import com.avvnapps.unigrocretail.viewmodel.FirestoreViewModel
import kotlinx.android.synthetic.main.activity_summary_quotation.view.*
import kotlinx.android.synthetic.main.item_order.view.*


class OrderItemAdapter(
    var context: Context,
    var orderList: List<OrderItem>,
    var firestoreViewModel: FirestoreViewModel
) : RecyclerView.Adapter<OrderItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderItem = orderList[position]
        holder.bindItems(context, orderItem, firestoreViewModel)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var TAG = "ORDER_ITEM_ADAPTER"
        lateinit var context: Context

        fun bindItems(
            context: Context,
            orderItem: OrderItem,
            firestoreViewModel: FirestoreViewModel
        ) {
            this.context = context

            itemView.item_order_customer_name_tv.text = orderItem.customerId
            itemView.item_order_id_tv.text = orderItem.orderId.toString()
            itemView.item_order_delivery_pickup_tv.text = OrderUtils.getOrderType(orderItem)
            itemView.item_order_status_tv.text = OrderUtils.getOrderStatus(orderItem.orderStatus)

            if (orderItem.orderStatus < ApplicationConstants.ORDER_PICKED_DELIVERED) {
                itemView.item_order_change_status_btn.text =
                    OrderUtils.getOrderStatus(orderItem.orderStatus + 1)
            } else {
                itemView.item_order_change_status_btn.visibility = View.GONE
            }

            itemView.item_order_change_status_btn.setOnClickListener {
                if (orderItem.orderStatus == ApplicationConstants.ORDER_PREPARING) {
                    firestoreViewModel.makeOrderReady(orderItem)
                }
                if (orderItem.orderStatus == ApplicationConstants.ORDER_READY) {
                    firestoreViewModel.completeOrder(orderItem)
                }
            }

            itemView.item_order_view_cart_btn.setOnClickListener {
                showDialogForCartItems(orderItem)
            }
        }

        private fun showDialogForCartItems(orderItem: OrderItem) {
            val dialog = Dialog(context)
            val layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view: View = layoutInflater.inflate(R.layout.activity_summary_quotation, null)
            val summaryItemAdapter = SummaryItemAdapter(context, orderItem.cartItems)

            view.activity_summary_quotation_price_rv.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = summaryItemAdapter
            }

            view.activity_summary_quotation_price_ll.visibility = View.GONE

            val rvParams = view.activity_summary_quotation_price_rv.layoutParams
            rvParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            view.activity_summary_quotation_price_rv.layoutParams = rvParams

            view.activity_summary_quotation_rl.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

            dialog.setContentView(view)
            dialog.show()
        }
    }
}