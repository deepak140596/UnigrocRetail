package com.avvnapps.unigrocretail.dashboard

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.avvnapps.unigrocretail.R
import com.avvnapps.unigrocretail.dashboard.adapters.OrderItemAdapter
import com.avvnapps.unigrocretail.models.OrderItem
import com.avvnapps.unigrocretail.viewmodel.FirestoreViewModel
import kotlinx.android.synthetic.main.fragment_current_orders.*

class CurrentOrdersFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener{

    val TAG = "CURRENT_ORDERS_FRAG"

    lateinit var activity: AppCompatActivity

    var currentOrders: List<OrderItem> = emptyList()
    private val firestoreViewModel by lazy {
        ViewModelProvider(this).get(FirestoreViewModel::class.java)
    }
    lateinit var orderItemAdapter: OrderItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity = getActivity() as AppCompatActivity
        return inflater.inflate(R.layout.fragment_current_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initialise Firestore VM
        initialiseFirestoreViewModel()
        orderItemAdapter =
            OrderItemAdapter(activity, currentOrders, firestoreViewModel)
        fragment_current_orders_recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = orderItemAdapter
        }

        fragment_current_orders_swipe_layout.setOnRefreshListener(this)
    }

    private fun initialiseFirestoreViewModel(){
        firestoreViewModel.getCurrentOrders().observe(viewLifecycleOwner, Observer { orders ->
            Log.i(TAG, "OrdersSize: ${orders.size}")
            currentOrders = orders
            if (currentOrders.isNullOrEmpty()) {
                no_current_orders_tv.visibility = View.VISIBLE
                fragment_current_orders_recycler_view.visibility = View.GONE
            } else {
                no_current_orders_tv.visibility = View.GONE
                fragment_current_orders_recycler_view.visibility = View.VISIBLE
            }
            orderItemAdapter.orderList = currentOrders
            orderItemAdapter.notifyDataSetChanged()

            if (fragment_current_orders_swipe_layout.isRefreshing)
                fragment_current_orders_swipe_layout.isRefreshing = false
        })


    }

    override fun onRefresh() {
        initialiseFirestoreViewModel()
    }

    override fun onResume() {
        super.onResume()
        initialiseFirestoreViewModel()
    }
}
