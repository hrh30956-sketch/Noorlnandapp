package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

// Game model
@Entity(tableName = "games")
data class Game(
    @PrimaryKey val id: String,
    val nameAr: String,
    val nameEn: String,
    val price: Double,
    val descriptionAr: String,
    val descriptionEn: String,
    val categoryAr: String,
    val categoryEn: String,
    val imageUrl: String,
    val isAvailable: Boolean = true,
    val rulesAr: String = "يجب الالتزام بتوجيهات المشرف. يرجى ربط حزام الأمان جيداً طوال اللعبة.",
    val rulesEn: String = "Must adhere to supervisor instructions. Always stay belted throughout the ride.",
    val minHeightCm: Int = 110,
    val isFeatured: Boolean = false
)

// Events, School trips, and Birthday Packages model
@Entity(tableName = "packages")
data class EventPackage(
    @PrimaryKey val id: String,
    val type: String, // "birthday", "school_trip", "general_event"
    val titleAr: String,
    val titleEn: String,
    val price: Double,
    val priceLabelAr: String,
    val priceLabelEn: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val imageUrl: String,
    val featuresAr: String, // Separated by newline
    val featuresEn: String  // Separated by newline
)

// Gallery images
@Entity(tableName = "gallery")
data class GalleryImage(
    @PrimaryKey val id: String,
    val captionAr: String,
    val captionEn: String,
    val imageUrl: String,
    val category: String = "general"
)

// Offline booking request submissions
@Entity(tableName = "bookings")
data class BookingRequest(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val fullName: String,
    val phone: String,
    val packageId: String,
    val packageName: String,
    val date: String,
    val guestsCount: Int,
    val notes: String = "",
    val statusAr: String = "قيد المراجعة",
    val statusEn: String = "Pending Review",
    val timestamp: Long = System.currentTimeMillis()
)

// Announcement / Notification system
@Entity(tableName = "notifications")
data class NoticeModel(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val titleAr: String,
    val titleEn: String,
    val messageAr: String,
    val messageEn: String,
    val date: String,
    val imageUrl: String? = null,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
