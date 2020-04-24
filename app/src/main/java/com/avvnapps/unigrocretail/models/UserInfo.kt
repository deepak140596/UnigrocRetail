package com.avvnapps.unigrocretail.models

import com.google.firebase.auth.FirebaseUser

data class UserInfo(
    var name: String? = "",
    var email: String? = "",
    var phoneNumber: String? = "",
    var profilePic: String? = "",
    var shopName: String? = "",
    var deviceToken: String? = ""
) {
    constructor(user: FirebaseUser) : this(
        user.uid,
        user.displayName,
        user.email,
        user.phoneNumber,
        user.photoUrl.toString()
    )
}