package com.teavaro.ecommDemoApp.core.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
data class ACEntity(
    @SerializedName("ac_id")
    @Expose
    @ColumnInfo(name = "ac_id")
    @PrimaryKey
    var acId: Int = -1,
)

@Entity(primaryKeys = ["ac_id", "item_id"])
data class ACItemCrossRef(
    val ac_id: Int,
    val item_id: Int
)