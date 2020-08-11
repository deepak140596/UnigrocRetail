package com.avvnapps.unigrocretail.order_history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.avvnapps.unigrocretail.R
import com.avvnapps.unigrocretail.models.OrderItem
import com.avvnapps.unigrocretail.viewmodel.FirestoreViewModel
import kotlinx.android.synthetic.main.fragment_order_history.*


class OrderHistoryFragment : Fragment() {

    val TAG = "ORDERS_HISTORY_FRAG"
    lateinit var activity: AppCompatActivity
    var submittedOrders: List<OrderItem> = emptyList()

    private val firestoreViewModel by lazy {
        ViewModelProvider(this).get(FirestoreViewModel::class.java)
    }

    lateinit var orderHistoryAdapter: orderHistoryAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity = getActivity() as AppCompatActivity
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // initialise Firestore VM
        initialiseFirestoreViewModel()
        orderHistoryAdapter = orderHistoryAdapter(activity, submittedOrders)
        fragment_orders_history_recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = orderHistoryAdapter
        }
    }

    private fun initialiseFirestoreViewModel() {
        firestoreViewModel.getCompletedOrders().observe(viewLifecycleOwner, Observer { orders ->
            Log.i(TAG, "OrdersSize: ${orders.size}")
            submittedOrders = orders

            if (submittedOrders.isNullOrEmpty()) {
                no_new_orders_tv_history.visibility = View.VISIBLE
                fragment_orders_history_recycler_view.visibility = View.GONE
            } else {
                no_new_orders_tv_history.visibility = View.GONE
                fragment_orders_history_recycler_view.visibility = View.VISIBLE
            }

            //updatedOrder(submittedOrders)
            orderHistoryAdapter.submittedOrderList = submittedOrders
            orderHistoryAdapter.notifyDataSetChanged()

        })


    }

}