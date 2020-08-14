package com.avvnapps.unigrocretail.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.avvnapps.unigrocretail.database.firestore.FirestoreRepository
import com.avvnapps.unigrocretail.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot

class FirestoreViewModel(application: Application) : AndroidViewModel(application){

    val TAG = "FIRESTORE_VIEW_MODEL"
    var firebaseRepository = FirestoreRepository()
    var availableCartItems: MutableLiveData<List<CartEntity>> = MutableLiveData()
    var savedAddresses: MutableLiveData<List<AddressItem>> = MutableLiveData()
    var submittedOrdersList: MutableLiveData<List<OrderItem>> = MutableLiveData()
    var quotedOrdersList: MutableLiveData<List<OrderItem>> = MutableLiveData()
    var currentOrderList: MutableLiveData<List<OrderItem>> = MutableLiveData()
    var readyOrderList: MutableLiveData<List<OrderItem>> = MutableLiveData()
    var completedOrderList: MutableLiveData<List<OrderItem>> = MutableLiveData()

    var retailerReviews: MutableLiveData<List<Review>> = MutableLiveData()


    // get available cart items from firestore
    fun getAvailableCartItems(): LiveData<List<CartEntity>> {

        availableCartItems = MutableLiveData()
        firebaseRepository.getAvailableCartItems().addOnSuccessListener { documents ->
            val availableCartList: MutableList<CartEntity> = mutableListOf()
            for (doc in documents) {
                val cartItem = doc.toObject(CartEntity::class.java)
                availableCartList.add(cartItem)
            }

            availableCartItems.value = availableCartList

        }.addOnFailureListener{
            availableCartItems.value = null
        }

        return availableCartItems

    }

    // save User Data to firebase
    fun saveUserData(userInfo: UserInfo){
        firebaseRepository.saveUserInfo(userInfo).addOnFailureListener {
            Log.e(TAG,"Failed to save User Data!")
        }
    }

    // save address to firebase
    fun saveAddressToFirebase(addressItem: AddressItem){
        firebaseRepository.saveAddressItem(addressItem).addOnFailureListener {
            Log.e(TAG,"Failed to save Address!")
        }
    }

    // get realtime updates from firebase regarding saved addresses
    fun getSavedAddresses(): LiveData<List<AddressItem>>{
        firebaseRepository.getSavedAddress().addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                savedAddresses.value = null
                return@EventListener
            }

            val savedAddressList: MutableList<AddressItem> = mutableListOf()
            for (doc in value!!) {
                val addressItem = doc.toObject(AddressItem::class.java)
                savedAddressList.add(addressItem)
            }
            savedAddresses.value = savedAddressList
        })

        return savedAddresses
    }

    // delete an address from firebase
    fun deleteAddress(addressItem: AddressItem){
        firebaseRepository.deleteAddress(addressItem).addOnFailureListener {
            Log.e(TAG,"Failed to delete Address")
        }
    }

    // get all submitted orders
    fun getSubmittedOrders(): MutableLiveData<List<OrderItem>> {
        firebaseRepository.getSubmittedOrders().addOnSuccessListener { document ->
            val subOrderList: MutableList<OrderItem> = mutableListOf()
            for (doc in document) {
                val orderItem = doc.toObject(OrderItem::class.java)
                subOrderList.add(orderItem)
            }
            submittedOrdersList.value = subOrderList
        }.addOnFailureListener {
            submittedOrdersList.value = null
        }

        return submittedOrdersList
    }

    fun addQuotation(context: Context, orderItem: OrderItem){
        firebaseRepository.addQuotation(context,orderItem)
    }

    fun getQuotedOrders(): MutableLiveData<List<OrderItem>> {
        val user = FirebaseAuth.getInstance().currentUser!!

        firebaseRepository.getQuotedOrders().addSnapshotListener(EventListener<QuerySnapshot> { documents, e ->
            if(e != null){
                Log.e(TAG,"Error getting quotedOrders: $e")
                quotedOrdersList.value = null
                return@EventListener
            }

            val orderList: MutableList<OrderItem> = mutableListOf()
            for(doc in documents!!) {
                val orderItem = doc.toObject(OrderItem::class.java)
                val quotationList = orderItem.quotations
                var quotedBySelf = false
                for (quote in quotationList) {
                    if (quote.retailerId == user.email.toString()) {
                        quotedBySelf = true
                    }
                }
                if (!quotedBySelf)
                    orderList.add(orderItem)
            }

            quotedOrdersList.value = orderList
        })
        return quotedOrdersList
    }


    fun getCurrentOrders(): MutableLiveData<List<OrderItem>> {
        firebaseRepository.getCurrentOrders().addSnapshotListener(EventListener<QuerySnapshot> { documents, e ->
            if(e != null){
                Log.e(TAG,"Error getting current orders: $e")
                currentOrderList.value = null
                return@EventListener
            }

            val orderList: MutableList<OrderItem> = mutableListOf()
            for(doc in documents!!){
                val orderItem = doc.toObject(OrderItem::class.java)
                orderList.add(orderItem)
            }

            currentOrderList.value = orderList
        })
        return currentOrderList
    }


    fun getReadyOrders(): MutableLiveData<List<OrderItem>> {
        firebaseRepository.getReadyOrders().addSnapshotListener(EventListener<QuerySnapshot> { documents, e ->
            if(e != null){
                Log.e(TAG,"Error getting ready orders: $e")
                readyOrderList.value = null
                return@EventListener
            }

            val orderList: MutableList<OrderItem> = mutableListOf()
            for(doc in documents!!){
                var orderItem = doc.toObject(OrderItem::class.java)
                orderList.add(orderItem)
            }

            readyOrderList.value = orderList
        })
        return readyOrderList
    }

    fun getCompletedOrders(): MutableLiveData<List<OrderItem>> {
        firebaseRepository.getCompletedOrders().addSnapshotListener(EventListener<QuerySnapshot> { documents, e ->
            if(e != null){
                Log.e(TAG,"Error getting completed orders: $e")
                completedOrderList.value = null
                return@EventListener
            }

            val orderList: MutableList<OrderItem> = mutableListOf()
            for(doc in documents!!){
                val orderItem = doc.toObject(OrderItem::class.java)
                orderList.add(orderItem)
            }

            completedOrderList.value = orderList
        })
        return completedOrderList
    }

    fun makeOrderReady(orderItem: OrderItem){
        firebaseRepository.makeOrderReady(orderItem).addOnFailureListener {
            Log.e(TAG, "Unable to make order ready: $it")
        }
    }

    fun completeOrder(orderItem: OrderItem) {
        firebaseRepository.completeOrder(orderItem).addOnFailureListener {
            Log.e(TAG, "Unable to complete order: $it")
        }
    }

    // get realtime updates from firebase regarding reviews
    fun getReviews(): LiveData<List<Review>> {
        firebaseRepository.getReviews()
            .addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    savedAddresses.value = null
                    return@EventListener
                }

                val retailerReviewsList: MutableList<Review> = mutableListOf()
                for (doc in value!!) {
                    val addressItem = doc.toObject(Review::class.java)
                    retailerReviewsList.add(addressItem)
                }
                retailerReviews.value = retailerReviewsList
            })

        return retailerReviews
    }
}