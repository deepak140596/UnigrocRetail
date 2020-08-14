package com.avvnapps.unigrocretail.database.firestore

import android.content.Context
import com.avvnapps.unigrocretail.database.SharedPreferencesDB
import com.avvnapps.unigrocretail.models.*
import com.avvnapps.unigrocretail.utils.ApplicationConstants
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class FirestoreRepository {

    val TAG = "FIREBASE_REPOSITORY"
    val firestoreDB: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    var user = FirebaseAuth.getInstance().currentUser!!
    var email = user.email.toString()


    //save user info
    fun saveUserInfo(userInfo: UserInfo): Task<Void>{
        val documentReference = firestoreDB.collection("retailers").document(email)

        return documentReference.set(userInfo,SetOptions.merge())
    }


    // get availbale cart items
    fun getAvailableCartItems(): Task<QuerySnapshot> {
        val collectionReference = firestoreDB.collection("available_cart_items")
        return collectionReference.get()
    }


    // save address to firebase
    fun saveAddressItem(addressItem: AddressItem): Task<Void> {
        //var
        val documentReference = firestoreDB.collection("retailers").document(email)
            .collection("saved_addresses").document(addressItem.addressId)
        return documentReference.set(addressItem)
    }

    // get saved addresses from firebase
    fun getSavedAddress(): CollectionReference {
        return firestoreDB.collection("retailers/$email/saved_addresses")
    }

    fun deleteAddress(addressItem: AddressItem): Task<Void> {
        val documentReference = firestoreDB.collection("retailers/$email/saved_addresses")
            .document(addressItem.addressId)

        return documentReference.delete()
    }

    fun getSubmittedOrders(): Task<QuerySnapshot> {
        val collectionReference = firestoreDB.collection("orders")
            .whereEqualTo("orderStatus", ApplicationConstants.ORDER_SUBMITTED)
        return collectionReference.get()
    }

    fun addQuotation(context: Context,order: OrderItem){

        val docRef: DocumentReference = FirebaseFirestore.getInstance()
            .collection("orders").document(order.orderId.toString())

        val newQuotation = RetailerQuotationItem(
            user.email!!, user.displayName!!, user.photoUrl.toString(),
            getTotalQuotationPrice(order.cartItems),
            SharedPreferencesDB.getSavedAddress(context)!!, order.cartItems, 4.6
        )


        docRef.update("quotations", FieldValue.arrayUnion(newQuotation),
            "orderStatus", ApplicationConstants.ORDER_QUOTED)
    }

    fun getQuotedOrders(): Query {
        return firestoreDB.collection("orders")
            .whereEqualTo("orderStatus",ApplicationConstants.ORDER_QUOTED)
    }

    fun getCurrentOrders(): Query {
        return firestoreDB.collection("orders")
            .whereEqualTo("retailerId", email)
            .whereEqualTo("orderStatus",ApplicationConstants.ORDER_PREPARING)
    }

    fun getReadyOrders(): Query {
        return firestoreDB.collection("orders")
            .whereEqualTo("retailerId", email)
            .whereEqualTo("orderStatus",ApplicationConstants.ORDER_READY)
    }

    fun getCompletedOrders(): Query {
        return firestoreDB.collection("orders")
            .whereEqualTo("retailerId", email)
            .whereEqualTo("orderStatus",ApplicationConstants.ORDER_PICKED_DELIVERED)
    }

    fun makeOrderReady(orderItem: OrderItem): Task<Void> {
        val documentReference =
            firestoreDB.collection("orders").document(orderItem.orderId.toString())
                .update("orderStatus", ApplicationConstants.ORDER_READY)
        return documentReference
    }

    fun completeOrder(orderItem: OrderItem): Task<Void> {
        val documentReference =
            firestoreDB.collection("orders").document(orderItem.orderId.toString())
                .update("orderStatus", ApplicationConstants.ORDER_PICKED_DELIVERED)
        return documentReference
    }


    private fun getTotalQuotationPrice(cartList: List<CartEntity>): Double {
        var totalPrice = 0.0
        for (cartItem in cartList) {
            totalPrice += cartItem.price * cartItem.quantity
        }
        return totalPrice
    }

    // get reviews  from firebase
    fun getReviews(): CollectionReference {
        return firestoreDB.collection("retailers/${email}/reviews")
    }
}