package com.avvnapps.unigrocretail.quote_submitted_order

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.avvnapps.unigrocretail.R
import com.avvnapps.unigrocretail.models.CartEntity
import com.avvnapps.unigrocretail.models.OrderItem
import kotlinx.android.synthetic.main.activity_calculate_quotation_price.*

class CalculateQuotationPriceActivity : AppCompatActivity() {

    val TAG = "CALCU_QUOTAT_ACT"

    var cartItems: List<CartEntity> = emptyList()
    lateinit var quoteItemAdapter: QuoteItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate_quotation_price)

        val orderItem: OrderItem =
            intent.getSerializableExtra(getString(R.string.selected_order_item)) as OrderItem
        cartItems = orderItem.cartItems

        Log.i(TAG, "OrderItem: $orderItem")

        // set up divider in recycler view
        quoteItemAdapter = QuoteItemAdapter(this, cartItems)

        activity_calculate_quotation_price_rv.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            addItemDecoration(
                DividerItemDecoration(
                    activity_calculate_quotation_price_rv.context,
                    DividerItemDecoration.VERTICAL
                )
            )

            adapter = quoteItemAdapter
        }

        activity_calculate_quotation_price_ll.setOnClickListener {

            val intent = Intent(
                this@CalculateQuotationPriceActivity,
                SummaryQuotationActivity::class.java
            ).apply {
                putExtra(getString(R.string.selected_order_item), orderItem)
            }

            Log.d("TAG", "Order Item: $orderItem")
            startActivity(intent)
        }
    }
}
