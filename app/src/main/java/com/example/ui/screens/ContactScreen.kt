package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.HeaderBar
import com.example.ui.theme.PrimaryColor
import com.example.ui.theme.SecondaryColor

@Composable
fun ContactScreen() {
    val context = LocalContext.current
    val scroll = rememberScrollState()

    // Real channels coordinates from Noor Al-Moqdadya xyz
    val phoneNumber = "+9647740849400"
    val addressText = "العراق، ديالى، قضاء المقدادية، طريق بعقوبة-مقدادية العام"
    val mapsUrl = "https://maps.google.com/?q=مدينة+العاب+نور+المقدادية"
    val whatsappUrl = "https://wa.me/9647740849400"
    val facebookUrl = "https://www.facebook.com/NoorAlMoqdadya" // Custom placeholder that can be changed

    Scaffold(
        topBar = {
            HeaderBar(
                title = "تواصل معنا - العنوان والاتصال 📞"
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scroll),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Interactive Contact Channels Box
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "اتصل بفرع الإدارة الرئيسي 📞",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "يسعدنا الرد على استفساراتكم والطلبات الخاصة على الأرقام الرسمية التالية:",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.End,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Call Action Row
                    ContactActionRow(
                        icon = Icons.Default.Phone,
                        title = "الرقم الرسمي للهاتف",
                        value = "0774 084 9400",
                        actionLabel = "اتصال مباشر",
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:$phoneNumber")
                            }
                            context.startActivity(intent)
                        },
                        divider = true
                    )

                    // WhatsApp Action Row
                    ContactActionRow(
                        icon = Icons.Default.Chat,
                        title = "رقم الواتساب للاستفسار والخصومات",
                        value = "0774 084 9400",
                        actionLabel = "مراسلة فورية",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl))
                            context.startActivity(intent)
                        },
                        divider = false
                    )
                }
            }

            // 2. Map coordinates and Location Address card
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "العنوان والموقع الجغرافي 📍",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = addressText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        textAlign = TextAlign.End,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "بجانب محطة تعبئة المقدادية، على طريق بعقوبة الرئيسي. تتوفر مواقف سيارات واسعة ومجانية للعائلات.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.End,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("open_maps_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("فتح الموقع على خرائط Google 🗺️", color = Color.White)
                        }
                    }
                }
            }

            // 3. System opening hours card
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "أوقات وساعات العمل والزيارة ⏰",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = "يومياً طيلة أيام الأسبوع من الساعة 4:00 عصراً وحتى 12:00 منتصف الليل.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                textAlign = TextAlign.End,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "في الأعياد والمناسبات الرسمية وعطل نهاية الأسبوع يمتد العمل للساعة 1:00 بعد منتصف الليل.",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.End,
                                lineHeight = 16.sp
                            )
                        }
                        Icon(imageVector = Icons.Default.Schedule, contentDescription = "ساعات العمل", tint = SecondaryColor)
                    }
                }
            }

            // 4. Social Links Banners
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SocialIconCircle(
                    icon = Icons.Default.Language,
                    label = "صفحة الفيسبوك",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl))
                        context.startActivity(intent)
                    }
                )
                SocialIconCircle(
                    icon = Icons.Default.Launch,
                    label = "رابط الموقع الرسمي",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://nooralmoqdadya.xyz"))
                        context.startActivity(intent)
                    }
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun ContactActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    actionLabel: String,
    onClick: () -> Unit,
    divider: Boolean
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Button label Left Side
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Text(text = actionLabel, fontSize = 11.sp, color = PrimaryColor, fontWeight = FontWeight.Bold)
            }

            // Details Right Side
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = value,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.End
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PrimaryColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = title, tint = PrimaryColor, modifier = Modifier.size(18.dp))
                }
            }
        }
        if (divider) {
            Divider(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }
    }
}

@Composable
fun SocialIconCircle(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.size(140.dp, 48.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = icon, contentDescription = label, tint = PrimaryColor, modifier = Modifier.size(18.dp))
        }
    }
}
