package com.mibotiquin.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mibotiquin.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>): List<Long>

    @Update
    suspend fun update(product: ProductEntity): Int

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM products WHERE barcode = :barcode")
    suspend fun deleteByBarcode(barcode: String): Int

    @Query("SELECT * FROM products WHERE id = :id")
    fun getById(id: Long): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE barcode = :barcode")
    fun getByBarcode(barcode: String): Flow<ProductEntity?>

    @Query("SELECT * FROM products ORDER BY category ASC, name ASC")
    fun getAll(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE category = :category ORDER BY name ASC")
    fun getByCategory(category: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR barcode LIKE '%' || :query || '%' ORDER BY category ASC, name ASC")
    fun search(query: String): Flow<List<ProductEntity>>

    @Query("SELECT COUNT(*) FROM products")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM products WHERE quantity <= 0")
    suspend fun countEmpty(): Int

    @Query("SELECT COUNT(*) FROM products WHERE expiryDate < :now")
    suspend fun countExpired(now: Long): Int

    @Query("SELECT COUNT(*) FROM products WHERE expiryDate BETWEEN :now AND :soonThreshold")
    suspend fun countExpiringSoon(now: Long, soonThreshold: Long): Int
}