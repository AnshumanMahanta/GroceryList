package com.example.grocerylist.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface GroceryDao {
    @Query("SELECT * FROM grocery_items")
    suspend fun getAllItems(): List<GroceryItem>

    @Query("SELECT SUM(price * quantity) FROM grocery_items")
    suspend fun getTotalPrice(): Double

    @Insert
    suspend fun insertItem(item: GroceryItem)

    @Update
    suspend fun updateItem(item: GroceryItem)

    @Delete
    suspend fun deleteItem(item: GroceryItem)
}
