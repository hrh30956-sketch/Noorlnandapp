package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.NoorApi
import com.example.data.local.AppDatabase
import com.example.data.repository.NoorRepository
import com.example.model.Game
import com.example.model.EventPackage
import com.example.model.GalleryImage
import com.example.model.BookingRequest
import com.example.model.NoticeModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NoorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoorRepository

    // Base flows
    val games: StateFlow<List<Game>>
    val packages: StateFlow<List<EventPackage>>
    val gallery: StateFlow<List<GalleryImage>>
    val bookings: StateFlow<List<BookingRequest>>
    val notifications: StateFlow<List<NoticeModel>>

    // UI state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>("الكل")
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    // For details navigation holding item select
    private val _selectedGameId = MutableStateFlow<String?>(null)
    val selectedGameId: StateFlow<String?> = _selectedGameId.asStateFlow()

    val selectedGame: StateFlow<Game?> = _selectedGameId
        .flatMapLatest { id ->
            if (id == null) flowOf(null) else repository.getGameById(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Interactive booking confirmation status
    private val _bookingSuccess = MutableStateFlow<Boolean?>(null)
    val bookingSuccess: StateFlow<Boolean?> = _bookingSuccess.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        val noorApi = NoorApi.create()
        repository = NoorRepository(noorApi, database.cacheDao())

        // ViewModel State Initialization
        games = repository.allGames
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        packages = repository.allPackages
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        gallery = repository.allGallery
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        bookings = repository.allBookings
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        notifications = repository.allNotifications
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

        // Initial launch synchronization
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
            _isSyncing.value = true
            repository.syncWithWebsite()
            _isSyncing.value = false
            
            // Programmatically launch a welcome offer notification after 4 seconds
            kotlinx.coroutines.delay(4000)
            generateSimulatedOffer()
        }
    }

    // Setters
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun selectGame(gameId: String?) {
        _selectedGameId.value = gameId
    }

    fun clearBookingSuccess() {
        _bookingSuccess.value = null
    }

    // Interactive search/filter logic
    val filteredGames: StateFlow<List<Game>> = combine(games, searchQuery, selectedCategory) { gamesList, query, cat ->
        gamesList.filter { game ->
            val matchQuery = game.nameAr.contains(query, ignoreCase = true) || 
                            game.descriptionAr.contains(query, ignoreCase = true) ||
                            game.nameEn.contains(query, ignoreCase = true)
            
            val matchCategory = if (cat == null || cat == "الكل") true else game.categoryAr == cat
            matchQuery && matchCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Unique game categories dynamically extracted
    val gameCategories: StateFlow<List<String>> = games.map { list ->
        val cats = mutableListOf("الكل")
        cats.addAll(list.map { it.categoryAr }.distinct())
        cats
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("الكل"))

    // Submit Booking logic
    fun makeBooking(
        fullName: String,
        phone: String,
        packageId: String,
        packageName: String,
        date: String,
        guestsCount: Int,
        notes: String
    ) {
        viewModelScope.launch {
            val booking = BookingRequest(
                fullName = fullName,
                phone = phone,
                packageId = packageId,
                packageName = packageName,
                date = date,
                guestsCount = guestsCount,
                notes = notes
            )
            val success = repository.submitBooking(booking)
            _bookingSuccess.value = success

            if (success) {
                // Generate matching confirmation system message in notifications
                repository.sendSystemPush(
                    titleAr = "تم استلام طلب الحجز بنجاح! 🎉",
                    titleEn = "Booking Request Received! 🎉",
                    messageAr = "عزيزي $fullName، تم استلام طلبك الخاص بحجز ($packageName) بتاريخ $date في مدينة ألعاب نور المقدادية. قيد المراجعة حالياً وسنتصل بك قريباً.",
                    messageEn = "Dear $fullName, your reservation for ($packageName) on $date at Noor Al-Moqdadya is received. Our team will contact you soon."
                )
            }
        }
    }

    // Delete Reservation
    fun cancelBooking(booking: BookingRequest) {
        viewModelScope.launch {
            repository.cancelBooking(booking)
        }
    }

    // Notifications operations
    fun markNoticeRead(id: String) {
        viewModelScope.launch {
            repository.markNoticeRead(id)
        }
    }

    // Fresh announcement push simulation
    private var simulatedOfferGenerated = false
    private fun generateSimulatedOffer() {
        if (simulatedOfferGenerated) return
        simulatedOfferGenerated = true
        viewModelScope.launch {
            repository.sendSystemPush(
                titleAr = "عرض الويكيند الذهبي الاستثنائي! ✨",
                titleEn = "Special Weekend Golden Offer! ✨",
                messageAr = "احصل على بطاقة دخول مجانية مع كل حجز حفلة عيد ميلاد في هذا الأسبوع! بالإضافة إلى تذاكر مجانية لـ 3 ألعاب من اختيارك يوم الخميس.",
                messageEn = "Score a free park pass with any birthday booking this week! Plus 3 free rides of your choosing on Thursday."
            )
        }
    }

    // Sync manually from UI
    fun triggerManualSync() {
        viewModelScope.launch {
            _isSyncing.value = true
            repository.syncWithWebsite()
            _isSyncing.value = false
        }
    }
}
