package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.Game
import com.example.model.EventPackage
import com.example.model.NoticeModel
import com.example.ui.NoorViewModel
import com.example.ui.components.NoorCarousel
import com.example.ui.components.SectionHeader
import com.example.ui.theme.PrimaryColor
import com.example.ui.theme.SecondaryColor
import com.example.ui.theme.GoldStar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: NoorViewModel,
    onNavigateToGames: () -> Unit,
    onNavigateToGameDetails: (String) -> Unit,
    onNavigateToBookings: () -> Unit,
    onNavigateToGallery: () -> Unit,
    onNavigateToContact: () -> Unit,
    onNavigateToNotifications: () -> Unit
) {
    val games by viewModel.games.collectAsState()
    val packages by viewModel.packages.collectAsState()
    val notices by viewModel.notifications.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    val featuredGames = remember(games) { games.filter { it.isFeatured } }
    val sliderImages = remember(games) { 
        if (games.isNotEmpty()) games.map { it.imageUrl } 
        else listOf(
            "https://images.unsplash.com/photo-1513885535751-8b9238bd345a?auto=format&fit=crop&q=80&w=800",
            "https://images.unsplash.com/photo-1545569341-9eb8b30979d9?auto=format&fit=crop&q=80&w=800"
        )
    }

    // Badge count of unread notifications
    val unreadCount = remember(notices) { notices.count { !it.isRead } }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                ),
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "مدينة ألعاب نور المقدادية",
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            color = PrimaryColor,
                            textAlign = TextAlign.End
                        )
                        Text(
                            text = "ديالى، العراق - عالم المرح والألعاب",
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            textAlign = TextAlign.End
                        )
                    }
                },
                actions = {
                    Box(modifier = Modifier.padding(start = 12.dp)) {
                        IconButton(onClick = onNavigateToNotifications) {
                            BadgedBox(
                                badge = {
                                    if (unreadCount > 0) {
                                        Badge(containerColor = ElegantRedState) {
                                            Text(unreadCount.toString(), color = Color.White)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Announcements",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Loading sync indicator
            if (isSyncing) {
                item {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().height(2.dp),
                        color = SecondaryColor
                    )
                }
            }

            // 1. Core Carousel Slider (Promiscuous Banner Ads)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(200.dp)
                ) {
                    NoorCarousel(
                        images = sliderImages,
                        onClick = { index ->
                            if (games.isNotEmpty() && index < games.size) {
                                onNavigateToGameDetails(games[index].id)
                            }
                        }
                    )
                }
            }

            // 2. Quick Navigation Shortcuts
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ShortcutButton(
                        icon = Icons.Default.Phone,
                        label = "اتصل بنا",
                        onClick = onNavigateToContact
                    )
                    ShortcutButton(
                        icon = Icons.Default.PhotoLibrary,
                        label = "المعرض",
                        onClick = onNavigateToGallery
                    )
                    ShortcutButton(
                        icon = Icons.Default.Star,
                        label = "احجز حفلة",
                        onClick = onNavigateToBookings
                    )
                    ShortcutButton(
                        icon = Icons.Default.SportsEsports,
                        label = "الألعاب",
                        onClick = onNavigateToGames
                    )
                }
            }

            // 3. Featured Games Section
            item {
                SectionHeader(
                    title = "ألعاب مميزة ومطلوبة 🔥",
                    actionText = "عرض الكل",
                    onActionClick = onNavigateToGames
                )
            }

            if (featuredGames.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryColor)
                    }
                }
            } else {
                item {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        reverseLayout = true // Keeping RTL natural orientation
                    ) {
                        items(featuredGames) { game ->
                            FeaturedGameCard(game = game, onClick = { onNavigateToGameDetails(game.id) })
                        }
                    }
                }
            }

            // 4. Booking Banners Linkages (Beautiful UI Cards)
            item {
                SectionHeader(title = "الحفلات والرحلات المدرسية ✨")
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Birthday Banner Card
                    DoubleActionPromoCard(
                        title = "حفلات أعياد الميلاد المتميزة",
                        description = "احجز أفضل يوم طفولي لأحبائك مع الكيك الخاص، التزيين، والألعاب التفاعلية والشخصيات الكرتونية المفضلة بأسعار وعروض استثنائية.",
                        btnText = "احجز عيد ميلاد الآن 🎂",
                        imageUrl = "https://images.unsplash.com/photo-1544717305-2782549b5136?auto=format&fit=crop&q=80&w=800",
                        onClick = onNavigateToBookings
                    )

                    // School Trip Banner Card
                    DoubleActionPromoCard(
                        title = "رحلات المدارس والروضات والجامعات",
                        description = "احصل على خصومات جماعية تصل لـ 50% وتذكرة ألعاب موحدة مدمجة مع وجبات غداء لذيذة للأطفال وفريق إشراف متكامل للأمان.",
                        btnText = "تنسيق رحلة مدرسية 🚌",
                        imageUrl = "https://images.unsplash.com/photo-1530103862676-de8c9debad1d?auto=format&fit=crop&q=80&w=800",
                        onClick = onNavigateToBookings,
                        isAltTheme = true
                    )
                }
            }

            // 5. General Offers / Announcements snippet
            val latestOffers = notices.take(1)
            if (latestOffers.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "آخر العروض والإعلانات 📣",
                        actionText = "جميع الإعلانات",
                        onActionClick = onNavigateToNotifications
                    )
                }

                items(latestOffers) { announcement ->
                    AnnouncementBannerCard(announcement = announcement, onClick = onNavigateToNotifications)
                }
            }
        }
    }
}

// Quick navigation button widget
@Composable
fun ShortcutButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White.copy(alpha = 0.05f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
            modifier = Modifier.size(56.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = PrimaryColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

// Special Featured Horizontal Slide Card
@Composable
fun FeaturedGameCard(
    game: Game,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(game.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = game.nameAr,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Category Tag Top Left
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = game.categoryAr,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Price Badge bottom right
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(PrimaryColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${game.price.toInt()} د.ع",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = game.nameAr,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = game.descriptionAr,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

// Booking visual promotors
@Composable
fun DoubleActionPromoCard(
    title: String,
    description: String,
    btnText: String,
    imageUrl: String,
    onClick: () -> Unit,
    isAltTheme: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                            )
                        )
                )

                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp),
                    textAlign = TextAlign.End
                )
            }

            Column(
                modifier = Modifier
                    .background( if (isAltTheme) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface )
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = description,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.End
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAltTheme) SecondaryColor else PrimaryColor
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Text(
                        text = btnText,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// Announcement Banner snippet card
@Composable
fun AnnouncementBannerCard(
    announcement: NoticeModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = announcement.titleAr,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = announcement.messageAr,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SecondaryColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Campaign,
                    contentDescription = "Offer icon",
                    tint = SecondaryColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

val ElegantRedState = Color(0xFFD32F2F)
