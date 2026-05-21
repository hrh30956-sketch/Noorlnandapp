package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.NoorViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Edge to edge immersive mode
        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                val viewModel: NoorViewModel = viewModel()
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Detect screen space configurations for tablet adaptation
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val isTablet = maxWidth > 600.dp
                    
                    Scaffold(
                        bottomBar = {
                            if (!isTablet && shouldShowNavigationBars(currentRoute)) {
                                MobileBottomBar(
                                    currentRoute = currentRoute,
                                    onTabSelected = { route ->
                                        navController.navigate(route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    bottom = if (isTablet) 0.dp else innerPadding.calculateBottomPadding()
                                )
                        ) {
                            if (isTablet && shouldShowNavigationBars(currentRoute)) {
                                TabletNavigationRail(
                                    currentRoute = currentRoute,
                                    onTabSelected = { route ->
                                        navController.navigate(route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(1.dp)
                                        .background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.08f))
                                )
                            }
                            
                            // Navigation Core Host
                            NavHost(
                                navController = navController,
                                startDestination = "home",
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            ) {
                                // 1. Home tab
                                composable("home") {
                                    HomeScreen(
                                        viewModel = viewModel,
                                        onNavigateToGames = { navController.navigate("games") },
                                        onNavigateToGameDetails = { id -> navController.navigate("game_details/$id") },
                                        onNavigateToBookings = { navController.navigate("booking") },
                                        onNavigateToGallery = { navController.navigate("gallery") },
                                        onNavigateToContact = { navController.navigate("contact") },
                                        onNavigateToNotifications = { navController.navigate("notifications") }
                                    )
                                }

                                // 2. Games list tab
                                composable("games") {
                                    GamesScreen(
                                        viewModel = viewModel,
                                        onNavigateToGameDetails = { id -> navController.navigate("game_details/$id") }
                                    )
                                }

                                // 3. Game details route
                                composable(
                                    route = "game_details/{gameId}",
                                    arguments = listOf(navArgument("gameId") { type = NavType.StringType })
                                ) { backStackEntry ->
                                    val gameId = backStackEntry.arguments?.getString("gameId") ?: ""
                                    GameDetailsScreen(
                                        viewModel = viewModel,
                                        gameId = gameId,
                                        onBack = { navController.popBackStack() },
                                        onBookRide = { _, name -> 
                                            navController.navigate("booking?predefined=$name") {
                                                popUpTo("home") { saveState = true }
                                            } 
                                        }
                                    )
                                }

                                // 4. Bookings and celebration packages tab
                                composable(
                                    route = "booking?predefined={predefined}",
                                    arguments = listOf(navArgument("predefined") { 
                                        type = NavType.StringType
                                        nullable = true
                                        defaultValue = null
                                    })
                                ) { backStackEntry ->
                                    val predefined = backStackEntry.arguments?.getString("predefined")
                                    BookingScreen(
                                        viewModel = viewModel,
                                        prefilledGameName = predefined
                                    )
                                }

                                // 5. Photo gallery tab
                                composable("gallery") {
                                    GalleryScreen(viewModel = viewModel)
                                }

                                // 6. Contact channel tab
                                composable("contact") {
                                    ContactScreen()
                                }

                                // 7. Announcements / Notifications screen
                                composable("notifications") {
                                    NotificationsScreen(
                                        viewModel = viewModel,
                                        onBack = { navController.popBackStack() }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Map endpoints for showing/hiding bars (e.g., hide in full details or zoom viewers)
private fun shouldShowNavigationBars(route: String?): Boolean {
    if (route == null) return true
    return !route.startsWith("game_details/") && route != "notifications"
}

// Beautiful Compact Mobile Bottom Navigation Bar (Arabic Aligned)
@Composable
fun MobileBottomBar(
    currentRoute: String?,
    onTabSelected: (String) -> Unit
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.08f))
        )
        NavigationBar(
            tonalElevation = 8.dp,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            // Items in Right to Left direction for Arabic
            NavigationBarItem(
                selected = currentRoute == "contact",
                onClick = { onTabSelected("contact") },
                icon = { Icon(Icons.Default.Phone, contentDescription = "اتصل بنا") },
                label = { Text("اتصل بنا", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryColor),
                modifier = Modifier.testTag("tab_contact")
            )
            NavigationBarItem(
                selected = currentRoute?.startsWith("gallery") == true,
                onClick = { onTabSelected("gallery") },
                icon = { Icon(Icons.Default.PhotoLibrary, contentDescription = "المعرض") },
                label = { Text("المعرض", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryColor),
                modifier = Modifier.testTag("tab_gallery")
            )
            NavigationBarItem(
                selected = currentRoute?.startsWith("booking") == true,
                onClick = { onTabSelected("booking") },
                icon = { Icon(Icons.Default.EventNote, contentDescription = "حفلات ورحلات") },
                label = { Text("حجوزاتي", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryColor),
                modifier = Modifier.testTag("tab_booking")
            )
            NavigationBarItem(
                selected = currentRoute == "games",
                onClick = { onTabSelected("games") },
                icon = { Icon(Icons.Default.SportsEsports, contentDescription = "الألعاب") },
                label = { Text("الألعاب", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryColor),
                modifier = Modifier.testTag("tab_games")
            )
            NavigationBarItem(
                selected = currentRoute == "home",
                onClick = { onTabSelected("home") },
                icon = { Icon(Icons.Default.Home, contentDescription = "الرئيسية") },
                label = { Text("الرئيسية", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryColor),
                modifier = Modifier.testTag("tab_home")
            )
        }
    }
}

// Beautiful Side Rail Navigation (for tablets & DeX)
@Composable
fun TabletNavigationRail(
    currentRoute: String?,
    onTabSelected: (String) -> Unit
) {
    NavigationRail(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxHeight(),
        header = {
            Icon(
                imageVector = Icons.Default.SportsEsports,
                contentDescription = "Logo",
                tint = PrimaryColor,
                modifier = Modifier.size(36.dp).padding(vertical = 12.dp)
            )
        }
    ) {
        NavigationRailItem(
            selected = currentRoute == "home",
            onClick = { onTabSelected("home") },
            icon = { Icon(Icons.Default.Home, contentDescription = "الرئيسية") },
            label = { Text("الرئيسية", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationRailItemDefaults.colors(selectedIconColor = PrimaryColor)
        )
        NavigationRailItem(
            selected = currentRoute == "games",
            onClick = { onTabSelected("games") },
            icon = { Icon(Icons.Default.SportsEsports, contentDescription = "الألعاب") },
            label = { Text("الألعاب", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationRailItemDefaults.colors(selectedIconColor = PrimaryColor)
        )
        NavigationRailItem(
            selected = currentRoute?.startsWith("booking") == true,
            onClick = { onTabSelected("booking") },
            icon = { Icon(Icons.Default.EventNote, contentDescription = "حجوزاتي") },
            label = { Text("حجوزاتي", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationRailItemDefaults.colors(selectedIconColor = PrimaryColor)
        )
        NavigationRailItem(
            selected = currentRoute?.startsWith("gallery") == true,
            onClick = { onTabSelected("gallery") },
            icon = { Icon(Icons.Default.PhotoLibrary, contentDescription = "المعرض") },
            label = { Text("المعرض", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationRailItemDefaults.colors(selectedIconColor = PrimaryColor)
        )
        NavigationRailItem(
            selected = currentRoute == "contact",
            onClick = { onTabSelected("contact") },
            icon = { Icon(Icons.Default.Phone, contentDescription = "اتصل بنا") },
            label = { Text("اتصل بنا", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationRailItemDefaults.colors(selectedIconColor = PrimaryColor)
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

