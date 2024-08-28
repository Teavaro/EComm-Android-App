package com.teavaro.ecommDemoApp.core.room

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ACWithItems(
    @Embedded val acEntity: ACEntity,
    @Relation(
        parentColumn = "ac_id",
        entityColumn = "item_id",
        associateBy = Junction(ACItemCrossRef::class)
    )
    var items: List<ItemEntity>
)
