package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.NoorViewModel
import com.example.ui.components.GradientButton
import com.example.ui.components.HeaderBar
import com.example.ui.theme.PrimaryColor
import com.example.ui.theme.SecondaryColor
import com.example.ui.theme.GoldStar

@Composable
fun GameDetailsScreen(
    viewModel: NoorViewModel,
    gameId: String,
    onBack: () -> Unit,
    onBookRide: (String, String) -> Unit
) {
    // Select this game
    LaunchedEffect(gameId) {
        viewModel.selectGame(gameId)
    }

    val context = LocalContext.current
    val game by viewModel.selectedGame.collectAsState()
    val allGallery by viewModel.gallery.collectAsState()

    // Filter gallery images that might represent rides or fun
    val galleryImages = remember(allGallery, game) {
        allGallery.map { it.imageUrl }
    }

    Scaffold(
        topBar = {
            HeaderBar(
                title = game?.nameAr ?: "تفاصيل اللعبة",
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = {
                        game?.let {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, it.nameAr)
                                putExtra(Intent.EXTRA_TEXT, "اكتشف لعبة \"${it.nameAr}\" الحماسية الرائعة بمدينة ألعاب نور المقدادية بـ ${it.price.toInt()} دينار عراقي فقط! \nزورونا اليوم في ديالى.")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "مشاركة اللعبة"))
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "مشاركة", tint = PrimaryColor)
                    }
                }
            )
        }
    ) { innerPadding ->
        if (game == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryColor)
            }
        } else {
            val item = game!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. Hero Image Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = item.nameAr,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Availability pill
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                            .background(
                                if (item.isAvailable) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                                else Color.Red.copy(alpha = 0.9f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (item.isAvailable) "جاهزة ومتوفرة 🟢" else "تحت الصيانة المؤقتة 🔴",
                            color = if (item.isAvailable) MaterialTheme.colorScheme.onPrimaryContainer else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Info Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    // Title and Category
                    Text(
                        text = item.nameAr,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "الفئة: ${item.categoryAr}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryColor,
                        textAlign = TextAlign.End
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Description heading inside RTL format
                    Text(
                        text = "عن اللعبة ℹ️",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = item.descriptionAr,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        lineHeight = 22.sp,
                        textAlign = TextAlign.End
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Highlight parameters (Price, height requirement, safety)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        SpecSquare(
                            icon = Icons.Default.MonetizationOn,
                            title = "التذكرة",
                            value = "${item.price.toInt()} د.ع"
                        )
                        SpecSquare(
                            icon = Icons.Default.Height,
                            title = "الحد الأدنى للطول",
                            value = "${item.minHeightCm} سم"
                        )
                        SpecSquare(
                            icon = Icons.Default.Security,
                            title = "درجة الأمان",
                            value = "قصوى 🛡️"
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Rules sheet inside RTL format
                    Text(
                        text = "تعليمات السلامة والقوانين 🚨",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = item.rulesAr,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 19.sp,
                                textAlign = TextAlign.End
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Custom Gallery Slider link
                    if (galleryImages.isNotEmpty()) {
                        Text(
                            text = "رؤية اللعبة من أرض الواقع 📸",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(bottom = 8.dp),
                            textAlign = TextAlign.End
                        )
                        LazyRow(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            reverseLayout = true
                        ) {
                            items(galleryImages) { url ->
                                Box(
                                    modifier = Modifier
                                        .size(100.dp, 80.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(url)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Real attractions",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Booking Registration Button - Double Action CTAs
                    GradientButton(
                        text = "احجز رحلة / حفلة لهذه اللعبة 🎂🎮",
                        onClick = { onBookRide(item.id, item.nameAr) },
                        testTag = "book_ride_details_btn"
                    )
                    
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
fun SpecSquare(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
        modifier = Modifier
            .size(95.dp, 90.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(4.dp)
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = PrimaryColor, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}
