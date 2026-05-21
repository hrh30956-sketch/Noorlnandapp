package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.theme.PrimaryColor
import com.example.ui.theme.SecondaryColor
import kotlinx.coroutines.delay

// 1. Premium Gradient Button
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = ""
) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(PrimaryColor, SecondaryColor)
    )
    val disabledBrush = Brush.horizontalGradient(
        colors = listOf(Color.Gray.copy(alpha = 0.5f), Color.Gray.copy(alpha = 0.5f))
    )

    Surface(
        modifier = modifier
            .height(52.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .testTag(testTag)
            .clickable(enabled = enabled, onClick = onClick),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(if (enabled) gradient else disabledBrush)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

// 2. Glassmorphism Elevation Card
@Composable
fun GlassyCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    borderColor: Color = Color.White.copy(alpha = 0.08f),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier,
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            content = content
        )
    }
}

// 3. Premium Interactive Image Carousel (Saves compile and runtime errors)
@Composable
fun NoorCarousel(
    images: List<String>,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit = {}
) {
    if (images.isEmpty()) {
        Box(
            modifier = modifier
                .background(Color.Gray.copy(alpha = 0.2f))
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text("جاري التحميل...", color = Color.Gray)
        }
        return
    }

    var currentIndex by remember { mutableStateOf(0) }

    LaunchedEffect(images) {
        while (true) {
            delay(5000)
            if (images.isNotEmpty()) {
                currentIndex = (currentIndex + 1) % images.size
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
    ) {
        AnimatedContent(
            targetState = currentIndex,
            transitionSpec = {
                (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                    slideOutHorizontally { width -> -width } + fadeOut()
                )
            },
            label = "carousel_transition"
        ) { index ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(images[index])
                    .crossfade(true)
                    .build(),
                contentDescription = "Noor Al-Moqdadya slider image",
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onClick(index) },
                contentScale = ContentScale.Crop
            )
        }

        // Dark gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                        startY = 100f
                    )
                )
        )

        // Indicator dots
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            images.forEachIndexed { i, _ ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (i == currentIndex) 10.dp else 6.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(if (i == currentIndex) SecondaryColor else Color.White.copy(alpha = 0.6f))
                )
            }
        }

        // Swiping indicators / Chevrons
        IconButton(
            onClick = {
                currentIndex = if (currentIndex == 0) images.size - 1 else currentIndex - 1
            },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp)
                .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Previous Image",
                tint = Color.White
            )
        }

        IconButton(
            onClick = {
                currentIndex = (currentIndex + 1) % images.size
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
                .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Next Image",
                tint = Color.White
            )
        }
    }
}

// 4. Custom RTL Section Header
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (actionText != null && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(
                    text = actionText,
                    color = PrimaryColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Spacer(modifier = Modifier.width(1.dp))
        }

        // Title alignment - RTL text
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.End
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Orange glowing bullet indicator
            Box(
                modifier = Modifier
                    .size(6.dp, 18.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(PrimaryColor)
            )
        }
    }
}

// 5. Custom Arabized Header Bar
@Composable
fun HeaderBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Column {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Action side
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    content = actions
                )

                // Center / RTL Title
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.End
                    )
                }

                // Localized Right-aligned Back Button for Arabic (Right Side in RTL)
                if (onBackClick != null) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back Button",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.08f))
        )
    }
}
