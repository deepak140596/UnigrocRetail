package com.avvnapps.unigrocretail.store_reviews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.avvnapps.unigrocretail.R
import com.avvnapps.unigrocretail.models.Review
import com.avvnapps.unigrocretail.utils.GetTimeAgo.getTimeAgo
import com.avvnapps.unigrocretail.utils.circularProgressDrawable
import com.avvnapps.unigrocretail.utils.hide

import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.review_item_list.view.*

class ReviewAdapter(var context: Context, var reviewList: List<Review>) :
    RecyclerView.Adapter<ReviewAdapter.ListViewHolder>() {
    var TAG = "REVIEW_ITEM_ADAPTER"

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    private lateinit var recyclerView: RecyclerView


    ///////////////////////////////////////////////////////////////////////////
    // Methods
    ///////////////////////////////////////////////////////////////////////////

    override fun getItemCount(): Int = reviewList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder =
        ListViewHolder(inflater.inflate(R.layout.review_item_list, parent, false))

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val model = reviewList[position]
        holder.bindItems(context, model)

    }

    ///////////////////////////////////////////////////////////////////////////
    // ViewHolder
    ///////////////////////////////////////////////////////////////////////////

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, model: Review) {

            val circularProgressDrawable = circularProgressDrawable(context)
            val options: RequestOptions = RequestOptions()
                .centerCrop()
                .placeholder(circularProgressDrawable)
                .error(R.drawable.user_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)

            Glide.with(context)
                .applyDefaultRequestOptions(options)
                .load(model.fromUserImageUrl)
                .into(itemView.userProfile)

            itemView.userName.text = model.fromUserName
            itemView.RatingBar.rating = model.rating!!


            val dateSubmitted: Long = model.dateSubmitted

            val lastSeenTime: String = getTimeAgo(dateSubmitted, context).toString()
            itemView.getTimeAgo.text = lastSeenTime

            if (model.positiveReview.isNullOrEmpty()) {
                itemView.positive_reviewLL.hide()
                itemView.positive_review.hide()
            } else {
                itemView.positive_review.text = model.positiveReview
            }

            if (model.negativeReview.isNullOrEmpty()) {
                itemView.negative_reviewLL.hide()
                itemView.negative_review.hide()
            } else {
                itemView.negative_review.text = model.negativeReview
            }


            if (model.mainReview.isNullOrEmpty()) {
                itemView.main_review.hide()
            } else {
                itemView.main_review.text = model.mainReview
            }


        }

    }


}