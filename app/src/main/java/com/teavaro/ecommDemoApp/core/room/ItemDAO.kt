package com.teavaro.ecommDemoApp.core.room

import androidx.room.*

@Dao
interface ItemDAO {

    @Insert
    fun saveItems(item: ItemEntity)

    @Query(value = "Select * from ItemEntity")
    fun getAllItems(): List<ItemEntity>

    @Query(value = "Select * from ItemEntity WHERE ac_id = :acId")
    fun getAllAcItems(acId: Int): List<ItemEntity>

    @Query("Delete from ItemEntity")
    fun removeAllItems()

    @Query("Update ItemEntity SET count_in_cart=:countInCart WHERE item_id = :itemId")
    fun addItemToCart(itemId: Int, countInCart: Int)

    @Update
    fun update(item: ItemEntity)

    @Delete
    fun delete(item: ItemEntity)


}