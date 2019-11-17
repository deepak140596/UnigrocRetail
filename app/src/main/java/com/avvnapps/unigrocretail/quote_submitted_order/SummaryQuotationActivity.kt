package com.avvnapps.unigrocretail.quote_submitted_order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.avvnapps.unigrocretail.R
import com.avvnapps.unigrocretail.database.SharedPreferencesDB
import com.avvnapps.unigrocretail.models.CartEntity
import com.avvnapps.unigrocretail.models.OrderItem
import com.avvnapps.unigrocretail.models.RetailerQuotationItem
import com.avvnapps.unigrocretail.utils.ApplicationConstants
import com.avvnapps.unigrocretail.viewmodel.FirestoreViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_summary_quotation.*

class SummaryQuotationActivity : AppCompatActivity() {

    val TAG = "SUMMARY_QUOTATION_ACTIVITY"

    var cartItems : List<CartEntity> = emptyList()
    lateinit var adapter : SummaryItemAdapter
    lateinit var firestoreViewModel: FirestoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary_quotation)

        var orderItem : OrderItem = intent.getSerializableExtra(getString(R.string.selected_order_item)) as OrderItem
        cartItems = orderItem.cartItems

        firestoreViewModel = ViewModelProviders.of(this).get(FirestoreViewModel::class.java)

        // set up divider in recycler view
        activity_summary_quotation_price_rv.layoutManager = LinearLayoutManager(this)
        activity_summary_quotation_price_rv.addItemDecoration(
            DividerItemDecoration(
                activity_summary_quotation_price_rv.context, DividerItemDecoration.VERTICAL
            )
        )

        adapter = SummaryItemAdapter(this,cartItems)
        activity_summary_quotation_price_rv.adapter = adapter

        // quote the order
        activity_summary_quotation_price_ll.setOnClickListener {

            var alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Quote Order?")
                .setMessage("Go ahead and quote order?")
                .setPositiveButton("Yes") { dialog, which ->
                    firestoreViewModel.addQuotation(this,orderItem)
                    Toasty.success(this,"Quotation Successful",Toast.LENGTH_LONG).show()
                    finish()
                }.setNegativeButton("Cancel"){ _,_ ->

                }

            var alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }

    private fun addQuotations(order: OrderItem){
        var user = FirebaseAuth.getInstance().currentUser!!
        var docRef : DocumentReference = FirebaseFirestore.getInstance()
            .collection("orders").document(order.orderId.toString())

        var newQuotation = RetailerQuotationItem(user.email!!,user.displayName!!,user.photoUrl.toString(),
            getTotalQuotationPrice(order.cartItems),
            SharedPreferencesDB.getSavedAddress(this)!!,cartItems,4.6)


        docRef.update("quotations", FieldValue.arrayUnion(newQuotation),
            "orderStatus", ApplicationConstants.ORDER_QUOTED)
    }

    private fun getTotalQuotationPrice(cartList: List<CartEntity>) : Double{
        var totalPrice = 0.0
        for(cartItem in cartList){
            totalPrice += cartItem.price * cartItem.quantity
        }
        return totalPrice
    }
}
