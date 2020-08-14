package com.avvnapps.unigrocretail.models

import java.util.*

data class Review(
    var reviewId: String? = "",
    var rating: Float? = 0f,
    var fromUserName: String? = "",
    var fromUserImageUrl: String? = "",
    var positiveReview: String? = "",
    var negativeReview: String? = "",
    var mainReview: String? = "",
    var dateSubmitted: Long = Date().time
)