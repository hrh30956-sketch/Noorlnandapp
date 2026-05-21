package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.model.Game
import com.example.model.EventPackage
import com.example.model.GalleryImage
import com.example.model.BookingRequest
import com.example.model.NoticeModel
import kotlinx.coroutines.flow.Flow

@Dao
interface CacheDao {

    // --- Games ---
    @Query("SELECT * FROM games")
    fun getAllGames(): Flow<List<Game>>

    @Query("SELECT * FROM games WHERE id = :id")
    fun getGameById(id: String): Flow<Game?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<Game>)

    @Query("DELETE FROM games")
    suspend fun deleteAllGames()

    // --- Packages ---
    @Query("SELECT * FROM packages")
    fun getAllPackages(): Flow<List<EventPackage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackages(packages: List<EventPackage>)

    @Query("DELETE FROM packages")
    suspend fun deleteAllPackages()

    // --- Gallery ---
    @Query("SELECT * FROM gallery")
    fun getAllGallery(): Flow<List<GalleryImage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGallery(gallery: List<GalleryImage>)

    @Query("DELETE FROM gallery")
    suspend fun deleteAllGallery()

    // --- Bookings ---
    @Query("SELECT * FROM bookings ORDER BY timestamp DESC")
    fun getAllBookings(): Flow<List<BookingRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingRequest)

    @Delete
    suspend fun deleteBooking(booking: BookingRequest)

    // --- Notifications ---
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NoticeModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notice: NoticeModel)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: String)
}
