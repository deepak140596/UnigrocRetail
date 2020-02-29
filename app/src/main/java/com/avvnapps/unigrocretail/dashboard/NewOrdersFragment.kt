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
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.avvnapps.unigrocretail.R
import com.avvnapps.unigrocretail.dashboard.adapters.SubmittedOrderAdapter
import com.avvnapps.unigrocretail.models.OrderItem
import com.avvnapps.unigrocretail.utils.LocationUtils
import com.avvnapps.unigrocretail.utils.OrderUtils
import com.avvnapps.unigrocretail.viewmodel.FirestoreViewModel
import kotlinx.android.synthetic.main.fragment_new_orders.*

class NewOrdersFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    val TAG = "NEW_ORDERS_FRAG"

    lateinit var activity: AppCompatActivity

    var submittedOrders: List<OrderItem> = emptyList()
    lateinit var firestoreViewModel: FirestoreViewModel
    lateinit var adapter: SubmittedOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity = getActivity() as AppCompatActivity
        return inflater.inflate(R.layout.fragment_new_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initialise Firestore VM
        firestoreViewModel = ViewModelProvider(this).get(FirestoreViewModel::class.java)
        initialiseFirestoreViewModel()

        fragment_new_orders_recycler_view.layoutManager = LinearLayoutManager(activity)

        adapter = SubmittedOrderAdapter(activity, submittedOrders)
        fragment_new_orders_recycler_view.adapter = adapter

        fragment_new_orders_swipe_layout.setOnRefreshListener(this)
    }

    private fun initialiseFirestoreViewModel() {
        firestoreViewModel.getQuotedOrders().observe(this, Observer { orders ->
            Log.i(TAG, "OrdersSize: ${orders.size}")
            submittedOrders = OrderUtils.getLocalOrders(activity, orders)

            if (submittedOrders.isNotEmpty()) {
                empty_bag.visibility = View.GONE
            } else
                empty_bag.visibility = View.VISIBLE

            //updatedOrder(submittedOrders)
            adapter.submittedOrderList = submittedOrders
            adapter.notifyDataSetChanged()

            if (fragment_new_orders_swipe_layout.isRefreshing)
                fragment_new_orders_swipe_layout.isRefreshing = false
        })


    }

    private fun updatedOrder(savedCartItems: List<OrderItem>) {
        if (savedCartItems.isNotEmpty()) {
            empty_bag.visibility = View.GONE
        } else
            empty_bag.visibility = View.VISIBLE

    }

    override fun onRefresh() {
        initialiseFirestoreViewModel()
    }

    override fun onResume() {
        super.onResume()
        initialiseFirestoreViewModel()
    }
}