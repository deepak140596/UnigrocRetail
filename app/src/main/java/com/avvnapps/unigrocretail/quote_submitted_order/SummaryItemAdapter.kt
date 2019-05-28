package com.avvnapps.unigrocretail.quote_submitted_order

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.avvnapps.unigrocretail.R
import com.avvnapps.unigrocretail.models.CartEntity
import com.avvnapps.unigrocretail.utils.PriceFormatter
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_quote_summary.view.*

class SummaryItemAdapter(var context: Context, var cartItems: List<CartEntity>)
    : RecyclerView.Adapter<SummaryItemAdapter.ViewHolder>(){
    var TAG = "QUOTE_ITEM_ADAPTER"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quote_summary, parent, false)

        return  ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val cartItem = cartItems[position]
        holder.bindItems(context,cartItem)

    }



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var TAG = "QUOTE_ITEM_ADAPTER"
        lateinit var context : Context

        fun bindItems(context: Context,cartItem: CartEntity){
            this.context = context

            itemView.item_quote_summary_name_tv.text = cartItem.name
            itemView.item_quote_summary_qty_tv.text = getQuantityAndWeight(cartItem)
            itemView.item_quote_summary_price_tv.text = PriceFormatter.getFormattedPrice(cartItem.price )
            itemView.item_quote_summary_total_price_tv.text = PriceFormatter.getFormattedPrice(cartItem.price * cartItem.quantity )

            if(cartItem.photoUrl != null){
                Glide.with(context).load(cartItem.photoUrl).into(itemView.item_cart_iv)
            }


        }

        fun getQuantityAndWeight(cartItem: CartEntity): String{
            var text = "${cartItem.metricWeight} x ${cartItem.quantity}"
            return text
        }
    }

}