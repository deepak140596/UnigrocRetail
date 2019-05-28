package com.avvnapps.unigrocretail.models

import java.io.Serializable

class AddressItem(var addressId:String ="",var addressName:String ="",
                  var houseName:String = "",var locality:String="",var landmark:String="",
                  var latitude:Double= 0.0,var longitude:Double= 0.0) : Serializable{

   // constructor():this("","","","","",0.0,0.0)


    fun getAddress():String{
        return "$houseName, $locality. Landmark: $landmark"
    }
}