package com.example.practicalappdev.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.practicalappdev.models.Address

@Dao
interface LogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: Address)

    @Query("SELECT * FROM address")
    fun getAddress(): MutableList<Address>

    @Query("DELETE FROM address WHERE id = :id")
    fun deleteAddress(id: Int)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAddress(address: Address)

    @Query("UPDATE address SET distance = :newDistance WHERE placeId = :itemId")
    fun updateAddressById(itemId: String, newDistance: Double)

    @Query("SELECT * FROM address ORDER BY distance ASC")
    fun getAscAddress(): MutableList<Address>

    @Query("SELECT * FROM address ORDER BY distance DESC")
    fun getDescAddress(): MutableList<Address>

}