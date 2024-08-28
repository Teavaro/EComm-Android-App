package com.teavaro.ecommDemoApp.core.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
class ItemEntity(

    @SerializedName("item_id")
    @Expose
    @ColumnInfo(name = "item_id")
    @PrimaryKey
    var itemId: Int = 0,

    @SerializedName("title")
    @Expose
    var title: String = "",

    @SerializedName("desc")
    @Expose
    var desc: String = "",

    @SerializedName("price")
    @Expose
    var price: Float = 0f,

    @SerializedName("picture")
    @Expose
    var picture: String = "",

    @SerializedName("data")
    @Expose
    var data: String = "",

    @SerializedName("is_offer")
    @ColumnInfo(name = "is_offer")
    @Expose
    var isOffer: Boolean = false,

    @SerializedName("is_in_stock")
    @ColumnInfo(name = "is_in_stock")
    @Expose
    var isInStock: Boolean = true,

    @SerializedName("is_in_wish")
    @ColumnInfo(name = "is_in_wish")
    @Expose
    var isInWish: Boolean = false,

    @SerializedName("count_in_cart")
    @ColumnInfo(name = "count_in_cart")
    @Expose
    var countInCart: Int = 0,

    @SerializedName("ac_id")
    @ColumnInfo(name = "ac_id")
    @Expose
    var acId: Int? = null,
)