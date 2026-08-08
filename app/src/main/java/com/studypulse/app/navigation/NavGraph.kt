package com.studypulse.app.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.studypulse.app.presentation.dashboard.DashboardScreen
import com.studypulse.app.presentation.history.HistoryScreen
import com.studypulse.app.presentation.scanner.ScannerScreen

@Composable
fun StudyPulseNavGraph(
    navController: NavHostController,
    onTapTriggered: (locationId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(Screen.Dashboard, Screen.Scanner, Screen.History)

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            NavigationBar {
                items.forEach { screen ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick  = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        },
                        icon  = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController    = navController,
            startDestination = Screen.Dashboard.route,
            modifier         = modifier
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel      = hiltViewModel(),
                    onScannerClick = { navController.navigate(Screen.Scanner.route) },
                    onTapTriggered = onTapTriggered,
                    innerPadding   = padding
                )
            }
            composable(Screen.Scanner.route) {
                ScannerScreen(
                    viewModel     = hiltViewModel(),
                    onScanSuccess = { locationId ->
                        onTapTriggered(locationId)
                        navController.popBackStack(Screen.Dashboard.route, inclusive = false)
                    },
                    innerPadding  = padding
                )
            }
            composable(Screen.History.route) {
                HistoryScreen(
                    viewModel    = hiltViewModel(),
                    innerPadding = padding
                )
            }
        }
    }
}
