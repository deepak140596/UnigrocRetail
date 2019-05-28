package com.avvnapps.unigrocretail.quote_submitted_order

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avvnapps.unigrocretail.R
import com.avvnapps.unigrocretail.models.CartEntity
import com.avvnapps.unigrocretail.models.OrderItem
import kotlinx.android.synthetic.main.activity_calculate_quotation_price.*
import kotlinx.android.synthetic.main.fragment_dashboard.*

class CalculateQuotationPriceActivity : AppCompatActivity() {

    val TAG = "CALCU_QUOTAT_ACT"

    var cartItems : List<CartEntity> = emptyList()
    lateinit var adapter : QuoteItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate_quotation_price)

        var orderItem : OrderItem = intent.getSerializableExtra(getString(R.string.selected_order_item)) as OrderItem
        cartItems = orderItem.cartItems

        Log.i(TAG,"OrderItem: $orderItem")

        // set up divider in recycler view
        activity_calculate_quotation_price_rv.layoutManager = LinearLayoutManager(this)
        activity_calculate_quotation_price_rv.addItemDecoration(
            DividerItemDecoration(
                activity_calculate_quotation_price_rv.context, DividerItemDecoration.VERTICAL
            )
        )

        adapter = QuoteItemAdapter(this,cartItems)
        activity_calculate_quotation_price_rv.adapter = adapter

        activity_calculate_quotation_price_ll.setOnClickListener {

            var intent = Intent(this@CalculateQuotationPriceActivity,SummaryQuotationActivity::class.java)
            intent.putExtra(getString(R.string.selected_order_item),orderItem)

            Log.d("TAG","Order Item: $orderItem")
            startActivity(intent)
        }
    }
}
