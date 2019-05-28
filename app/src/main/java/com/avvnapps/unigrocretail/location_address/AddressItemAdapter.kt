package com.avvnapps.unigrocretail.location_address

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.avvnapps.unigrocretail.R
import com.avvnapps.unigrocretail.database.SharedPreferencesDB
import com.avvnapps.unigrocretail.models.AddressItem
import com.avvnapps.unigrocretail.viewmodel.FirestoreViewModel
import kotlinx.android.synthetic.main.item_address.view.*

class AddressItemAdapter(var context: AppCompatActivity, var addressList : List<AddressItem>,
                         var firestoreViewModel: FirestoreViewModel, var isSelectableAction: Boolean)
    :RecyclerView.Adapter<AddressItemAdapter.ViewHolder>(){

    var TAG = "ADDRESS_ITEM_ADAPTER"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_address,parent,false)

        return ViewHolder(itemView, isSelectableAction)
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val addressItem = addressList[position]
        holder.bindItems(context,addressItem,firestoreViewModel)
    }


    class ViewHolder(itemView: View,var isSelectableAction: Boolean): RecyclerView.ViewHolder(itemView){

        var TAG = "ADDRESS_ITEM_ADAPTER"

        fun bindItems(context: AppCompatActivity, addressItem : AddressItem, firestoreViewModel: FirestoreViewModel){
            itemView.item_saved_add_title.text = addressItem.addressName
            itemView.item_saved_add_body.text = addressItem.getAddress()

            itemView.item_address_options_tv.setOnClickListener { view ->
                Log.i(TAG,"Options clicked!")
                var popupMenu = PopupMenu(context, view.item_address_options_tv)
                popupMenu.inflate(R.menu.menu_item_address)

                // action on menu items in each row
                popupMenu.setOnMenuItemClickListener {
                    when(it.itemId){

                        R.id.menu_item_add_edit -> {
                            Log.i(TAG, "EDIT")
                            var intent = Intent(context,CreateAddressActivity::class.java)
                            intent.putExtra("address_item",addressItem)
                            context.startActivity(intent)
                            return@setOnMenuItemClickListener true
                        }

                        R.id.menu_item_add_delete -> {
                            Log.i(TAG, "EDIT")
                            firestoreViewModel.deleteAddress(addressItem)
                            return@setOnMenuItemClickListener true
                        }
                    }
                    return@setOnMenuItemClickListener  true

                }
                popupMenu.show()
            }

            itemView.item_saved_add_body.setOnClickListener {

                var resultIntent = Intent()
                resultIntent.putExtra("latitude",addressItem.latitude)
                resultIntent.putExtra("longitude",addressItem.longitude)
                Log.i(TAG,"latitude : "+addressItem.latitude)
                Log.i(TAG,"longitude : "+addressItem.longitude)



                context.setResult(Activity.RESULT_OK,resultIntent)
                if(isSelectableAction) { // if address acitivity is opened to select address
                    // save the selected address as default
                    SharedPreferencesDB.savePreferredAddress(context,addressItem)
                    context.finish()
                }
            }




        }
    }
}