package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.EventPackage
import com.example.model.BookingRequest
import com.example.ui.NoorViewModel
import com.example.ui.components.GradientButton
import com.example.ui.components.GlassyCard
import com.example.ui.theme.PrimaryColor
import com.example.ui.theme.SecondaryColor
import com.example.ui.theme.ElegantGreen
import com.example.ui.theme.ElegantRed

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookingScreen(
    viewModel: NoorViewModel,
    prefilledGameName: String? = null
) {
    val packages by viewModel.packages.collectAsState()
    val bookings by viewModel.bookings.collectAsState()
    val bookingSuccess by viewModel.bookingSuccess.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Packages, 1: My Bookings
    var showFormDialog by remember { mutableStateOf(false) }
    var selectedPackageForForm by remember { mutableStateOf<EventPackage?>(null) }

    // Form states
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") }
    var guestsCount by remember { mutableStateOf("10") }
    var notes by remember { mutableStateOf("") }

    // Prepopulate form if redirected
    LaunchedEffect(prefilledGameName) {
        if (!prefilledGameName.isNullOrEmpty()) {
            val parentPack = packages.find { it.type == "school_trip" } ?: packages.firstOrNull()
            selectedPackageForForm = parentPack
            notes = "حجز مخصص للعبة: $prefilledGameName"
            showFormDialog = true
        }
    }

    Scaffold(
        topBar = {
            Surface(
                tonalElevation = 3.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.statusBarsPadding()
                ) {
                    Text(
                        text = "الحفلات، الرحلات والمناسبات 🎉",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        textAlign = TextAlign.End
                    )

                    // Navigation Tabs index (RTL Support)
                    TabRow(
                        selectedTabIndex = activeTab,
                        containerColor = Color.Transparent,
                        contentColor = PrimaryColor,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                                color = PrimaryColor
                            )
                        }
                    ) {
                        Tab(
                            selected = activeTab == 1,
                            onClick = { activeTab = 1 },
                            text = { Text("طلبات حجوزاتي 📝", fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = activeTab == 0,
                            onClick = { activeTab = 0 },
                            text = { Text("باقات الترفيه والعروض 🌟", fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    (fadeIn() + slideInHorizontally { x -> if (activeTab == 0) x else -x }).togetherWith(
                        fadeOut() + slideOutHorizontally { x -> if (activeTab == 0) -x else x }
                    )
                },
                label = "booking_tab_transition"
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> PackagesList(
                        packages = packages,
                        onSelectPackage = { pack ->
                            selectedPackageForForm = pack
                            fullName = ""
                            phone = ""
                            eventDate = ""
                            guestsCount = "10"
                            if (prefilledGameName == null) notes = ""
                            showFormDialog = true
                        }
                    )
                    1 -> MyBookingsList(
                        bookings = bookings,
                        onCancelClick = { request -> viewModel.cancelBooking(request) }
                    )
                }
            }
        }

        // Inline Full-Screen Mode Booking Form Dialog
        if (showFormDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showFormDialog = false
                    viewModel.clearBookingSuccess()
                },
                confirmButton = {}, // Custom inside form
                title = {
                    Text(
                        text = "طلب حجز ترفيهي جديد 📅",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                },
                text = {
                    val scroll = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scroll),
                        horizontalAlignment = Alignment.End
                    ) {
                        selectedPackageForForm?.let { pack ->
                            Text(
                                text = "الباقة المختارة: ${pack.titleAr}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryColor,
                                textAlign = TextAlign.End
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Full Name input RTL
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            modifier = Modifier.fillMaxWidth().testTag("form_name_input"),
                            placeholder = { Text("أدخل اسمك الثلاثي بالكامل...", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryColor,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                                cursorColor = PrimaryColor
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Phone number input RTL
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            modifier = Modifier.fillMaxWidth().testTag("form_phone_input"),
                            placeholder = { Text("رقم هاتف الموبايل (مثال: 077xxx)...", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryColor,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                                cursorColor = PrimaryColor
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Event Date input RTL
                        OutlinedTextField(
                            value = eventDate,
                            onValueChange = { eventDate = it },
                            modifier = Modifier.fillMaxWidth().testTag("form_date_input"),
                            placeholder = { Text("تاريخ الفعالية لزيارتنا (يوم/شهر/سنة)...", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryColor,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                                cursorColor = PrimaryColor
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Guests Count number input RTL
                        OutlinedTextField(
                            value = guestsCount,
                            onValueChange = { guestsCount = it },
                            modifier = Modifier.fillMaxWidth().testTag("form_guests_input"),
                            placeholder = { Text("عدد الأطفال / الحضور التقريبي...", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryColor,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                                cursorColor = PrimaryColor
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Custom notes input RTL
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            modifier = Modifier.fillMaxWidth().height(80.dp).testTag("form_notes_input"),
                            placeholder = { Text("أي ملاحظات خاصة أو كعكة مخصصة أو ألعاب معينة...", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryColor,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                                cursorColor = PrimaryColor
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Checking outcomes animations
                        if (bookingSuccess == true) {
                            Surface(
                                color = ElegantGreen.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Text(
                                        text = "تم إرسال طلب الحجز بنجاح! وسنتواصل معك قريباً 💚",
                                        color = ElegantGreen,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.End
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "OK", tint = ElegantGreen)
                                }
                            }
                        }

                        // Operation Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Dismiss Button
                            OutlinedButton(
                                onClick = { 
                                    showFormDialog = false
                                    viewModel.clearBookingSuccess()
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("إلغاء")
                            }

                            // Submit Button
                            Button(
                                onClick = {
                                    if (fullName.isNotBlank() && phone.isNotBlank() && eventDate.isNotBlank()) {
                                        viewModel.makeBooking(
                                            fullName = fullName,
                                            phone = phone,
                                            packageId = selectedPackageForForm?.id ?: "unknown",
                                            packageName = selectedPackageForForm?.titleAr ?: "حجز مدينة الألعاب",
                                            date = eventDate,
                                            guestsCount = guestsCount.toIntOrNull() ?: 10,
                                            notes = notes
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1.5f).testTag("submit_form_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                                enabled = fullName.isNotBlank() && phone.isNotBlank() && eventDate.isNotBlank() && (bookingSuccess != true)
                            ) {
                                Text("إرسال الطلب", color = Color.White)
                            }
                        }
                    }
                }
            )
        }
    }
}

// Sub list of all available celebration packages
@Composable
fun PackagesList(
    packages: List<EventPackage>,
    onSelectPackage: (EventPackage) -> Unit
) {
    if (packages.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = PrimaryColor)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(packages) { item ->
                PackageRowCard(pack = item, onBookClick = { onSelectPackage(item) })
            }
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PackageRowCard(
    pack: EventPackage,
    onBookClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
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
                    .height(140.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(pack.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = pack.titleAr,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Price Tag inside container
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .background(PrimaryColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = pack.priceLabelAr,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = pack.titleAr,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = pack.descriptionAr,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.End,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))

                // Feature Checklist items parsed by newline
                Text(
                    text = "ميزات وعناصر العرض المتضمنة 👀",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SecondaryColor,
                    textAlign = TextAlign.End
                )
                Spacer(modifier = Modifier.height(8.dp))

                val features = pack.featuresAr.split("\n")
                features.forEach { feature ->
                    if (feature.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Text(
                                text = feature,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                textAlign = TextAlign.End
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Met",
                                tint = PrimaryColor,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Booking button
                GradientButton(
                    text = "حجز هذا العرض والاستعلام 🗓️",
                    onClick = onBookClick,
                    testTag = "book_premium_btn_${pack.id}"
                )
            }
        }
    }
}

// Track User's own reservations histories
@Composable
fun MyBookingsList(
    bookings: List<BookingRequest>,
    onCancelClick: (BookingRequest) -> Unit
) {
    if (bookings.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EventNote,
                    contentDescription = "No Reservations",
                    tint = SecondaryColor.copy(alpha = 0.5f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "لم تسجل أي حجوزات بعد",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ابحث عن العروض التي ترغب بها وسجل طلبك معنا.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(bookings) { item ->
                BookingHistoryRow(booking = item, onCancel = { onCancelClick(item) })
            }
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun BookingHistoryRow(
    booking: BookingRequest,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cancel Action Button
                IconButton(onClick = onCancel) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "إلغاء الطلب", tint = ElegantRed)
                }

                // Booking ID title
                Text(
                    text = "عرض: ${booking.packageName}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            // Info rows aligned to Right in RTL
            DataSnippetRow(label = "الاسم المعتمد", value = booking.fullName)
            DataSnippetRow(label = "رقم الجوال", value = booking.phone)
            DataSnippetRow(label = "تاريخ المناسبة", value = booking.date)
            DataSnippetRow(label = "عدد الضيوف الأفراد", value = "${booking.guestsCount} ضيف")
            if (booking.notes.isNotEmpty()) {
                DataSnippetRow(label = "ملاحظات إضافية", value = booking.notes)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // State Badge status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = booking.statusAr,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (booking.statusAr == "تم التأكيد") ElegantGreen else SecondaryColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = if (booking.statusAr == "تم التأكيد") Icons.Default.Verified else Icons.Default.Pending,
                    contentDescription = "حالة الحجز",
                    tint = if (booking.statusAr == "تم التأكيد") ElegantGreen else SecondaryColor,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun DataSnippetRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End
        )
        Text(
            text = " :$label",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.End,
            modifier = Modifier.width(100.dp)
        )
    }
}
