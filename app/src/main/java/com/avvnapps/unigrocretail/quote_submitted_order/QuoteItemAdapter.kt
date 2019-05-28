package com.avvnapps.unigrocretail.quote_submitted_order

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.avvnapps.unigrocretail.R
import com.avvnapps.unigrocretail.models.CartEntity
import com.avvnapps.unigrocretail.utils.PriceFormatter
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_cart_quote.view.*



class QuoteItemAdapter(var context: Context, var cartItems: List<CartEntity>)
    :RecyclerView.Adapter<QuoteItemAdapter.ViewHolder>(){
    var TAG = "QUOTE_ITEM_ADAPTER"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart_quote, parent, false)

        return  ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val cartItem = cartItems[position]
        holder.bindItems(context,cartItem)

        holder.itemView.item_cart_price_pu_et.tag = position

        if(cartItem.price != 0.0)
            holder.itemView.item_cart_price_pu_et.setText(cartItem.price.toString())
        else if(cartItem.price == 0.0)
            holder.itemView.item_cart_price_pu_et.setText("")


        holder.itemView.item_cart_price_pu_et.onTextChanged {
            var text = it
            if(it.isEmpty())
                text = "0"

            var pricePerUnit = text.toDouble()
            holder.itemView.item_cart_total_price_tv.text = PriceFormatter.getFormattedPrice(pricePerUnit * cartItem.quantity)
            cartItems[position].price = pricePerUnit
            cartItem.price = pricePerUnit

            Log.d(TAG,"PRICE: ${cartItem.price}")
        }

    }

    fun EditText.onTextChanged(onTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                onTextChanged.invoke(p0.toString())
            }

            override fun afterTextChanged(editable: Editable?) {

            }
        })
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var TAG = "QUOTE_ITEM_ADAPTER"
        lateinit var context : Context

        fun bindItems(context: Context,cartItem: CartEntity){
            this.context = context

            itemView.item_cart_name_tv.text = cartItem.name
            itemView.item_cart_qty_tv.text = cartItem.quantity.toString()
            itemView.item_cart_total_price_tv.text = PriceFormatter.getFormattedPrice(cartItem.price * cartItem.quantity )

            if(cartItem.photoUrl != null){
                Glide.with(context).load(cartItem.photoUrl).into(itemView.item_cart_iv)
            }


        }
    }

}