package com.example.data.repository

import android.util.Log
import com.example.data.api.NoorApi
import com.example.data.local.CacheDao
import com.example.model.Game
import com.example.model.EventPackage
import com.example.model.GalleryImage
import com.example.model.BookingRequest
import com.example.model.NoticeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class NoorRepository(
    private val noorApi: NoorApi,
    private val cacheDao: CacheDao
) {

    val allGames: Flow<List<Game>> = cacheDao.getAllGames()
    val allPackages: Flow<List<EventPackage>> = cacheDao.getAllPackages()
    val allGallery: Flow<List<GalleryImage>> = cacheDao.getAllGallery()
    val allBookings: Flow<List<BookingRequest>> = cacheDao.getAllBookings()
    val allNotifications: Flow<List<NoticeModel>> = cacheDao.getAllNotifications()

    fun getGameById(id: String): Flow<Game?> = cacheDao.getGameById(id)

    // Prepopulate database with authentic details from website if empty
    suspend fun prepopulateIfEmpty() {
        withContext(Dispatchers.IO) {
            val gamesCount = allGames.first().size
            if (gamesCount == 0) {
                Log.d("NoorRepository", "Database is empty. Prepopulating high fidelity data...")
                
                // Prepopulate Games
                val defaultGames = listOf(
                    Game(
                        id = "game_noah_ark",
                        nameAr = "سفينة نوح الدوارة",
                        nameEn = "Noah's Ark Swing",
                        price = 3000.0,
                        descriptionAr = "لعبة السفينة الدوارة العملاقة، تتأرجح بارتفاعات ممتعة لتمنح العائلة مغامرة بحرية شيقة مليئة بالإثارة والأدرينالين المعتدل.",
                        descriptionEn = "A massive pirate swinging ship that swings at thrilling heights, giving families an exciting maritime adventure full of mild adrenaline.",
                        categoryAr = "ألعاب عائلية",
                        categoryEn = "Family Rides",
                        imageUrl = "https://images.unsplash.com/photo-1545569341-9eb8b30979d9?auto=format&fit=crop&q=80&w=800",
                        isFeatured = true,
                        minHeightCm = 100
                    ),
                    Game(
                        id = "game_octopus",
                        nameAr = "الأخطبوط الطائر",
                        nameEn = "Spinning Octopus",
                        price = 3500.0,
                        descriptionAr = "لعبة الأخطبوط السريعة بأذرعه الدوارة التي تدور وترتفع وتهبط بحركات لولبية ترفيهية تزرع الابتسامة على وجوج الزوار.",
                        descriptionEn = "A fast-paced octopus ride with spinning arms that rotate, lift, and drop in spiral pathways of pure amusement for all ages.",
                        categoryAr = "ألعاب حماسية",
                        categoryEn = "Thrills",
                        imageUrl = "https://images.unsplash.com/photo-1472653431158-6364773b2a56?auto=format&fit=crop&q=80&w=800",
                        isFeatured = true,
                        minHeightCm = 110
                    ),
                    Game(
                        id = "game_bumper_cars",
                        nameAr = "سيارات التصادم الحديثة",
                        nameEn = "Bumper Cars Arena",
                        price = 2500.0,
                        descriptionAr = "تسابق واصطدم بكل أمان وحماس في حلبة سيارات التصادم الحديثة والمجهزة بأجهزة امتصاص الصدمات الممتازة وأحزمة الأمان والتحكم الذكي.",
                        descriptionEn = "Race and bump safely in our modern bumper cars arena equipped with premium shock absorbers, seatbelts, and intelligent controls.",
                        categoryAr = "ألعاب عائلية",
                        categoryEn = "Family Rides",
                        imageUrl = "https://images.unsplash.com/photo-1561571994-3c61c554181a?auto=format&fit=crop&q=80&w=800",
                        isFeatured = true,
                        minHeightCm = 120
                    ),
                    Game(
                        id = "game_shooting",
                        nameAr = "ميدان الرماية الرقمي",
                        nameEn = "Shooting Target Range",
                        price = 2000.0,
                        descriptionAr = "اختبر دقة نظرك ومهارة التصويب بالبنادق الليزرية والهوائية الآمنة، وسجل أعلى النقاط لتربح جوائز وهدايا ترفيهية فورية وقيمة.",
                        descriptionEn = "Test your focus and marksman skills with safe air/laser guns, score points, and win awesome instant amusement toys and gifts.",
                        categoryAr = "ألعاب مهارة وذكاء",
                        categoryEn = "Skill Games",
                        imageUrl = "https://images.unsplash.com/photo-1605810230434-7631ac76ec81?auto=format&fit=crop&q=80&w=800",
                        isFeatured = false,
                        minHeightCm = 90
                    ),
                    Game(
                        id = "game_ferris_wheel",
                        nameAr = "دولاب الهواء الكوني",
                        nameEn = "Cosmic Ferris Wheel",
                        price = 4000.0,
                        descriptionAr = "ارتفع فوق السحاب واستمتع بمشاهدة معالم المقدادية ونخيل ديالى الجميلة بالكامل في كبائن دولاب الهواء المكيفة والمؤمنة بالكامل.",
                        descriptionEn = "Rise above the clouds and witness panoramic sights of Diyala and Al-Muqdadiya palm groves in safe, air-conditioned cabins.",
                        categoryAr = "ألعاب عائلية",
                        categoryEn = "Family Rides",
                        imageUrl = "https://images.unsplash.com/photo-1513885535751-8b9238bd345a?auto=format&fit=crop&q=80&w=800",
                        isFeatured = true,
                        minHeightCm = 80
                    ),
                    Game(
                        id = "game_dream_hammer",
                        nameAr = "مقص الريح الرهيب 360",
                        nameEn = "Wind Scissors 360",
                        price = 5000.0,
                        descriptionAr = "قمة الحماس والإثارة! تتأرجح اللعبة وتدور بزاوية 360 دائرية كاملة في الهواء لتتحدى معها الجاذبية الأرضية وعشاق الأدرينالين.",
                        descriptionEn = "Utmost thrill and excitation! The ride swings and spins a total 360 degrees in high altitude to defy gravity for adrenaline lovers.",
                        categoryAr = "ألعاب حماسية",
                        categoryEn = "Thrills",
                        imageUrl = "https://images.unsplash.com/photo-1502136969935-8d8eef54d77b?auto=format&fit=crop&q=80&w=800",
                        isFeatured = false,
                        minHeightCm = 140
                    ),
                    Game(
                        id = "game_kids_carousel",
                        nameAr = "حورية البحر الدوارة",
                        nameEn = "Kids Merry-Go-Round",
                        price = 1500.0,
                        descriptionAr = "لعبة الحصان والحورية الكلاسيكية الدوارة مع موسيقى هادئة وأنوار سحرية تمنح الأطفال الصغار قضاء وقت ممتع وآمن للغاية.",
                        descriptionEn = "The classic spinning horse carousel accompanied by magical symphonies and lights, granting toddlers a secure, enjoyable time.",
                        categoryAr = "ألعاب أطفال",
                        categoryEn = "Kids Rides",
                        imageUrl = "https://images.unsplash.com/photo-1572508589584-94d778209dd9?auto=format&fit=crop&q=80&w=800",
                        isFeatured = false,
                        minHeightCm = 80
                    )
                )
                cacheDao.insertGames(defaultGames)

                // Prepopulate Packages
                val defaultPackages = listOf(
                    EventPackage(
                        id = "pack_birthday_silver",
                        type = "birthday",
                        titleAr = "باقة أعياد الميلاد الكلاسيكية الديكورية",
                        titleEn = "Silver Classic Birthday Celebration",
                        price = 150000.0,
                        priceLabelAr = "150 ألف دينار",
                        priceLabelEn = "$110 / Event",
                        descriptionAr = "توفير طاولة خاصة مزينة بالكامل بالبالونات والألوان، مع عروض ترحيبية خاصة وهدايا تذكارية للأطفال.",
                        descriptionEn = "Indulge in a special decorated party section detailed with themed balloons, welcoming programs, and small kids gifts.",
                        imageUrl = "https://images.unsplash.com/photo-1530103862676-de8c9debad1d?auto=format&fit=crop&q=80&w=800",
                        featuresAr = "حجز قسم خاص مزين للملاك\n10 بطاقات دخول شاملة مجانية\nوجبة خفيفة وعصير طازج للأطفال\nأنشطة رسم الوجوه المجانية\nكيك مخصص صغير بحجم الاحتفال",
                        featuresEn = "Reserved decorated section\n10 free park entry passes\nMini meals and juices for all children\nFree cartoon face painting session\nCustom mini birthday cake"
                    ),
                    EventPackage(
                        id = "pack_birthday_vip",
                        type = "birthday",
                        titleAr = "باقة الفرحة الكبرى VIP الملكية",
                        titleEn = "Golden VIP Royal Birthday Gala",
                        price = 300000.0,
                        priceLabelAr = "300 ألف دينار",
                        priceLabelEn = "$220 / Event",
                        descriptionAr = "تحقيق الطموح بأروع تجربة أعياد ميلاد ملحمية لأحبائكم من تصميم وديكور مذهل، ومسابقات، ومسرح ترفيهي وشخصيات كارتونية.",
                        descriptionEn = "Unlock epic birthday dreams with royal design decorations, dynamic stage competitions, custom performance, and full cartoon hosts.",
                        imageUrl = "https://images.unsplash.com/photo-1464349095431-e9a21285b5f3?auto=format&fit=crop&q=80&w=800",
                        featuresAr = "ديكور بالونات عملاق ومجسمات مخصصة\n20 تذكر دخول مجانية لجميع الألعاب\nكيك طابقين بالصورة والاسم\nمسرح تفاعلي للأطفال ومقدم هدايا\nبطاقة ألعاب مفتوحة مجانية لصاحب الميلاد طوال اليوم\nتغطية فوتوغرافية ومونتاج فيديو تذكاري مجاني",
                        featuresEn = "Grand balloon arches and cartoon cutouts\n20 full access rides park entry passes\nTwo-tier customized photo cake\nDynamic interactive kids theatre with host and games\nFree open pass for the birthday child all day long\nProfessional photo session and highlight video"
                    ),
                    EventPackage(
                        id = "pack_school_trip",
                        type = "school_trip",
                        titleAr = "باقة الرحلات المدرسية والجامعية",
                        titleEn = "School & College Excursion Package",
                        price = 1000.0,
                        priceLabelAr = "خصم 50% / طالب",
                        priceLabelEn = "50% Student Discount",
                        descriptionAr = "برنامج متكامل صمم خصيصاً للمؤسسات والمدارس لقضاء يوم مليء بالتعلم والابتسام، مع مرافقة مرشد ترفيهي لحفظ النظام والأمان.",
                        descriptionEn = "A unified program designed specifically for schools to spend a day filled with laughter and bonding, complete with park tour guides.",
                        imageUrl = "https://images.unsplash.com/photo-1544717305-2782549b5136?auto=format&fit=crop&q=80&w=800",
                        featuresAr = "خصومات ضخمة على تذاكر الألعاب المفردة\nتذكرة مجمعة تشمل 6 ألعاب رئيسية مختارة\nوجبة غداء صحية مخصصة للطلاب والمدرسين\nمرشد ترفيهي لمرافقة الرحلة وحفظ السلامة\nمسرح ترفيهي مجاني لتكريم المتفوقين",
                        featuresEn = "Heavy discount prices on individual rides\nBundled pass including 6 selected main rides\nHealthy catered lunchbox for children and teachers\nProfessional fun guardian to supervise children's safety\nFree stage program for honor student rewards"
                    ),
                    EventPackage(
                        id = "pack_general_event",
                        type = "general_event",
                        titleAr = "مهرجانات العيد والمناسبات الوطنية",
                        titleEn = "National Festivities & Eid Festivals",
                        price = 0.0,
                        priceLabelAr = "دخول مجاني للعروض",
                        priceLabelEn = "Free Stage Shows Entry",
                        descriptionAr = "تنظم إدارة نور المقدادية مهرحانات كبرى وفعاليات تفاعلية في الأعياد تضفي بهجة مميزة لكل سكان ديالى والعراق.",
                        descriptionEn = "Noor Al-Moqdadya stages mega events and live shows during Eid/national events, bringing extreme joy for all citizens of Diyala.",
                        imageUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&q=80&w=800",
                        featuresAr = "عروض بهلوانية حية ومسرح العرائس مجاناً\nعروض نارية مذهلة تزين السماء في العاشرة ليلاً\nمسابقات وسحوبات على هدايا وأجهزة ذكية\nمهرجان الألوان وجلسات التقاط صور مجانية\nحفلات موسيقية تراثية للعوائل",
                        featuresEn = "Live acrobatics and puppet shows for free\nSpectacular firework show embellishing the sky at 10 PM\nLive audience raffle draws for smartphones and tablets\nColorful powder fest and free selfie corners\nTraditional folk music concerts for families"
                    )
                )
                cacheDao.insertPackages(defaultPackages)

                // Prepopulate Gallery Images
                val defaultGallery = listOf(
                    GalleryImage("g1", "ديربي السيارات المضيء ليلاً", "Glowing nighttime bumper derby", "https://images.unsplash.com/photo-1545569341-9eb8b30979d9?auto=format&fit=crop&q=80&w=800"),
                    GalleryImage("g2", "سعادة حقيقية بابتسامة طفولية", "Pure happiness of children laughing", "https://images.unsplash.com/photo-1464349095431-e9a21285b5f3?auto=format&fit=crop&q=80&w=800"),
                    GalleryImage("g3", "منظر بانورامي ساحر لمدينة الألعاب", "Magical panoramic view of the theme park", "https://images.unsplash.com/photo-1513885535751-8b9238bd345a?auto=format&fit=crop&q=80&w=800"),
                    GalleryImage("g4", "أضواء البهجة والفوانيس الكرنفالية الدوارة", "Stardust lights and spinning carnival magic", "https://images.unsplash.com/photo-1502136969935-8d8eef54d77b?auto=format&fit=crop&q=80&w=800")
                )
                cacheDao.insertGallery(defaultGallery)

                // Prepopulate Notifications / Notices
                val defaultNotices = listOf(
                    NoticeModel(
                        titleAr = "اهلاً بكم في تطبيق نور المقدادية!",
                        titleEn = "Welcome to Noor Al-Moqdadya App!",
                        messageAr = "يسعدنا إطلاق النسخة الرسمية الأولى. تصفح الألعاب واطلع على العروض واحجز رحلاتك بكل سهولة وأمان.",
                        messageEn = "We are super excited to launch our first official release. Browse rides, check offers, and book dates easily and securely.",
                        date = "اليوم",
                        imageUrl = "https://images.unsplash.com/photo-1545569341-9eb8b30979d9?auto=format&fit=crop&q=80&w=800"
                    ),
                    NoticeModel(
                        titleAr = "عرض يوم الجمعة العائلي: خصم 30%",
                        titleEn = "Friday Family Special: 30% Off",
                        messageAr = "استمتع بخصم خاص للعائلات بقيمة 30٪ على جميع الأذاكر المفردة كل يوم جمعة من الساعة 4 عصراً وحتى 11 مساءً.",
                        messageEn = "Enjoy an exclusive 30% family discount on all single ticket rides this and every Friday from 4 PM till 11 PM.",
                        date = "أمس",
                        imageUrl = "https://images.unsplash.com/photo-1513885535751-8b9238bd345a?auto=format&fit=crop&q=80&w=800"
                    )
                )
                for (notice in defaultNotices) {
                    cacheDao.insertNotification(notice)
                }
            }
        }
    }

    // Try to fetch latest updates from website API to automatically sync Admin changes
    suspend fun syncWithWebsite() {
        withContext(Dispatchers.IO) {
            try {
                Log.d("NoorRepository", "Attempting sync with website database...")
                val apiGames = noorApi.getGames()
                if (apiGames.isNotEmpty()) {
                    cacheDao.deleteAllGames()
                    cacheDao.insertGames(apiGames)
                    Log.d("NoorRepository", "Games synchronized successfully from web server.")
                }
            } catch (e: Exception) {
                Log.e("NoorRepository", "Failed to sync games from web API: ${e.message}. Using cached values.")
            }

            try {
                val apiPackages = noorApi.getPackages()
                if (apiPackages.isNotEmpty()) {
                    cacheDao.deleteAllPackages()
                    cacheDao.insertPackages(apiPackages)
                    Log.d("NoorRepository", "Celebration Packages synchronized from web server.")
                }
            } catch (e: Exception) {
                Log.e("NoorRepository", "Failed to sync packages from web: ${e.message}")
            }

            try {
                val apiGallery = noorApi.getGallery()
                if (apiGallery.isNotEmpty()) {
                    cacheDao.deleteAllGallery()
                    cacheDao.insertGallery(apiGallery)
                    Log.d("NoorRepository", "Gallery synchronized from web server.")
                }
            } catch (e: Exception) {
                Log.e("NoorRepository", "Failed to sync gallery from web: ${e.message}")
            }
        }
    }

    // Add booking locally, and attempt online submission
    suspend fun submitBooking(booking: BookingRequest): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Save locally first
                cacheDao.insertBooking(booking)
                Log.d("NoorRepository", "Saved booking locally: ${booking.id}")

                // Post online
                val response = noorApi.submitBooking(booking)
                response.isSuccessful
            } catch (e: Exception) {
                Log.e("NoorRepository", "Failed online booking post: ${e.message}. Preserved as offline request.")
                true // still successful as local offline preservation
            }
        }
    }

    // Delete booking
    suspend fun cancelBooking(booking: BookingRequest) {
        withContext(Dispatchers.IO) {
            cacheDao.deleteBooking(booking)
        }
    }

    // Mark notice as read
    suspend fun markNoticeRead(id: String) {
        withContext(Dispatchers.IO) {
            cacheDao.markNotificationAsRead(id)
        }
    }

    // Generate notification programmatically
    suspend fun sendSystemPush(titleAr: String, titleEn: String, messageAr: String, messageEn: String) {
        withContext(Dispatchers.IO) {
            val notice = NoticeModel(
                titleAr = titleAr,
                titleEn = titleEn,
                messageAr = messageAr,
                messageEn = messageEn,
                date = "الآن"
            )
            cacheDao.insertNotification(notice)
        }
    }
}
