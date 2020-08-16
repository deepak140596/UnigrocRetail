package com.avvnapps.unigrocretail.store_reviews

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avvnapps.unigrocretail.R
import com.avvnapps.unigrocretail.models.Review
import com.avvnapps.unigrocretail.viewmodel.FirestoreViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_reviews.*

class ReviewsActivity : AppCompatActivity() {

    val TAG by lazy { "RETAILER_REVIEWs" }

    private val firestoreViewModel by lazy {
        ViewModelProvider(this).get(FirestoreViewModel::class.java)
    }

    private val firestoreDB by lazy { FirebaseFirestore.getInstance() }

    val email by lazy { FirebaseAuth.getInstance().currentUser!!.email.toString() }


    var reviewList: List<Review> = emptyList()
    lateinit var reviewAdapter: ReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)

        reviews_toolbar.apply {
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.title = "Reviews"
        }

        reviews_toolbar.setNavigationOnClickListener(View.OnClickListener {
            onBackPressed()
            overridePendingTransition(0, 0)
            finish()
        })

        getRetailersDetail()

        reviewAdapter = ReviewAdapter(this, reviewList)

        review_recyclerView.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(this@ReviewsActivity, RecyclerView.VERTICAL, false)
            adapter = reviewAdapter
            recycledViewPool.setMaxRecycledViews(0, 0)

        }

    }

    private fun getRetailersDetail() {
        val docRef = firestoreDB.collection("retailers").document(email)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d(TAG, "Current data: ${snapshot.data}")
                initialiseFirestoreViewModel()
                setRetailerData(snapshot)
            } else {
                Log.d(TAG, "Current data: null")
            }
        }
    }

    private fun setRetailerData(retailerData: DocumentSnapshot?) {

        retailerData?.get("rating")?.toString()?.let {
            RetailerRatingBar.rating = it.toFloat()
            retailerRating.text = it
        }
    }


    private fun initialiseFirestoreViewModel() {
        firestoreViewModel.getReviews()
            .observe(this, Observer { orders ->
                Log.i(TAG, "OrdersSize: ${orders.size}")
                reviewList = orders
                reviewAdapter.reviewList = reviewList
                reviewAdapter.notifyDataSetChanged()

            })
    }


}