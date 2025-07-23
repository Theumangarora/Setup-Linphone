package com.example.telemomobileapp.Navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.*

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*

import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.linphoneapp.BottomNav.bottomScreens.ContactsScreen
import com.example.linphoneapp.BottomNav.bottomScreens.ProfileTabScreen
import com.example.linphoneapp.BottomNav.bottomScreens.RecentScreen
import com.example.linphoneapp.Screen.CallInProgressScreen
import com.example.linphoneapp.Screen.IncomingCallScreen
import com.example.linphoneapp.Screen.LoginScreen
import com.example.linphoneapp.Screen.OutboundCall
import com.example.linphoneapp.Screen.SplashScreen
import com.example.linphoneapp.data.BottomNavItem
import com.example.linphoneapp.viewmodel.CallViewModel
import com.example.telemomobileapp.Dialer.DialerScreen

sealed class Navigation(val route: String) {
    object Splash : Navigation("splash")
    object Login : Navigation("login")
    object ForgotPassword : Navigation("forgot")
    object Home : Navigation("home")
    object HomeTab : Navigation("home_tab")
    object CallTab : Navigation("call_tab")
    object MessageTab : Navigation("message_tab")
    object ProfileTab : Navigation("profile_tab")

}

val bottomNavItems = listOf(
    BottomNavItem(Navigation.HomeTab.route, Icons.Outlined.Home, Icons.Filled.Home, "Home"),
    BottomNavItem(Navigation.CallTab.route, Icons.Outlined.Call, Icons.Filled.Call, "Calls"),
    BottomNavItem(Navigation.MessageTab.route, Icons.Outlined.DateRange, Icons.Filled.DateRange, "Messages"),
    BottomNavItem(Navigation.ProfileTab.route, Icons.Outlined.Person, Icons.Filled.Person, "Profile")
)


@SuppressLint("NewApi")
@Composable
fun VoipApp(navController1: NavHostController, callViewModel: CallViewModel) {
    val INCOMING_CALL_ROUTE = "incoming_call/{caller}"

    val navController = navController1
    NavHost(
        navController = navController,
        startDestination = Navigation.Splash.route
    ) {
        composable(Navigation.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Navigation.Login.route) {
                        popUpTo(Navigation.Splash.route) { inclusive = true }
                    }
                },
                    onNavigateToHome = {
                        navController.navigate(Navigation.Home.route) {
                            popUpTo(Navigation.Login.route) { inclusive = true }
                        }
                    },

            )
        }
        composable(Navigation.Login.route) { navBackStackEntry ->
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Navigation.Home.route) {
                        popUpTo(Navigation.Login.route) { inclusive = true }
                    }
                },
                entry = navBackStackEntry

            )
        }

        composable(Navigation.Home.route) {
            HomeScreen(
                onLogout = {
                    navController.navigate(Navigation.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onEndCall = {
                    callViewModel.hangUp()
                    navController.popBackStack()
                    },

                navController
            )
        }

        composable(
            "incoming/{caller}",
            arguments = listOf(navArgument("caller") { type = NavType.StringType })
        ) { backStackEntry ->
            val caller = backStackEntry.arguments?.getString("caller") ?: "Unknown"
            IncomingCallScreen(
                caller = caller,
                onAccept = {
                    callViewModel.accept()
                    navController.navigate("on_call/$caller") {
                        popUpTo("incoming/$caller") { inclusive = true }
                    }
                },
                onReject = {
                    callViewModel.hangUp()
                    navController.popBackStack()
                }
            )
        }
        composable(
            "on_call/{caller}",
            arguments = listOf(navArgument("caller") { type = NavType.StringType })
        ) { backStackEntry ->
            val caller = backStackEntry.arguments?.getString("caller") ?: "Unknown"
            CallInProgressScreen(
                caller = caller,
                onEndCall = {
                    callViewModel.hangUp()
                    navController.popBackStack() // back to incoming if waiting
                }
            )
        }
        composable(
            "outgoing/{caller}",
            arguments = listOf(navArgument("caller") { type = NavType.StringType })
        ) { backStackEntry ->
            val caller = backStackEntry.arguments?.getString("caller") ?: "Unknown"
            OutboundCall(
                phoneNumber = caller,
                onEndCall = {
                    callViewModel.hangUp()
                    navController.popBackStack() // back to incoming if waiting
                }
            )
        }

    }
    }
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onEndCall: () -> Unit,
    navController: NavHostController,


    ) {
    val currentTab = remember { mutableStateOf(Navigation.HomeTab.route) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedRoute = currentTab.value,
                onTabSelected = { currentTab.value = it }
            )
        }
    ) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            when (currentTab.value) {
                Navigation.HomeTab.route -> RecentScreen(onEndCall)
                Navigation.CallTab.route -> ContactsScreen()
                Navigation.MessageTab.route -> DialerScreen()
                Navigation.ProfileTab.route -> ProfileTabScreen(
                    onNavigateToLogin = onLogout
                )
            }
        }
    }
}
// BOTTOM NAVIGATION BAR
@Composable
fun BottomNavigationBar(
    selectedRoute: String,
    onTabSelected: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF667eea), Color(0xFF764ba2))
                )
            )
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ) {
            bottomNavItems.forEach { item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            if (selectedRoute == item.route) item.selectedIcon else item.icon,
                            contentDescription = item.label
                        )
                    },
                    label = { Text(item.label) },
                    selected = selectedRoute == item.route,
                    onClick = {
                        onTabSelected(item.route)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.LightGray,
                        unselectedTextColor = Color.LightGray,
                        indicatorColor = Color.White.copy(alpha = 0.1f)
                    )
                )
            }
        }
    }
}