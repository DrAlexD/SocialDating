package xelagurd.socialdating.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import xelagurd.socialdating.MainViewModel
import xelagurd.socialdating.R
import xelagurd.socialdating.ui.screen.CategoriesScreen
import xelagurd.socialdating.ui.screen.LoginScreen
import xelagurd.socialdating.ui.screen.ProfileScreen
import xelagurd.socialdating.ui.screen.ProfileStatisticsScreen
import xelagurd.socialdating.ui.screen.RegistrationScreen
import xelagurd.socialdating.ui.screen.SettingsScreen
import xelagurd.socialdating.ui.screen.StatementAddingScreen
import xelagurd.socialdating.ui.screen.StatementsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val currentUserId by mainViewModel.currentUserId.collectAsState()

    ProfileDestination.currentUserId = currentUserId
    ProfileStatisticsDestination.currentUserId = currentUserId

    initializeTopLevelDestinations(navController)

    NavHost(
        navController = navController,
        startDestination = if (currentUserId == -1) LoginDestination.route else CategoriesDestination.route
    ) {
        composable(route = LoginDestination.route) {
            LoginScreen(
                onSuccessLogin = {
                    navController.navigate(CategoriesDestination.route)
                },
                onRegistrationClick = {
                    navController.navigate(RegistrationDestination.route)
                }
            )
        }

        composable(route = RegistrationDestination.route) {
            RegistrationScreen(
                onSuccessRegistration = {
                    navController.navigate(CategoriesDestination.route)
                },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(route = SettingsDestination.route) {
            SettingsScreen(
                onSuccessLogout = {
                    navController.navigate(LoginDestination.route)
                },
            )
        }

        composable(route = CategoriesDestination.route) {
            CategoriesScreen(
                onCategoryClick = {
                    navController.navigate("${StatementsDestination.route}/$it")
                }
            )
        }

        composable(
            route = StatementsDestination.routeWithArgs,
            arguments = listOf(navArgument(StatementsDestination.categoryId) {
                type = NavType.IntType
            })
        ) {
            StatementsScreen(
                onStatementClick = {
                    navController.navigate("${StatementDetailsDestination.route}/$it")
                },
                onStatementAddingClick = {
                    navController.navigate("${StatementAddingDestination.route}/$it")
                },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = StatementAddingDestination.routeWithArgs,
            arguments = listOf(navArgument(StatementAddingDestination.categoryId) {
                type = NavType.IntType
            })
        ) {
            StatementAddingScreen(
                onSuccessStatementAdding = { navController.navigateUp() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = ProfileDestination.routeWithArgs,
            arguments = listOf(navArgument(ProfileDestination.userId) {
                type = NavType.IntType
            })
        ) {
            ProfileScreen(
                onProfileStatisticsClick = {
                    navController.navigate("${ProfileStatisticsDestination.route}/$it")
                }
            )
        }

        composable(
            route = ProfileStatisticsDestination.routeWithArgs,
            arguments = listOf(navArgument(ProfileStatisticsDestination.userId) {
                type = NavType.IntType
            })
        ) {
            ProfileStatisticsScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = StatementDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(StatementDetailsDestination.statementId) {
                type = NavType.IntType
            })
        ) {
            //
        }
    }
}

fun initializeTopLevelDestinations(navController: NavHostController) {
    val navigateTo = { route: String ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    topLevelDestinations = listOf(
        TopLevelDestination(
            navigationDestination = ProfileDestination,
            navigateTo = { navigateTo(ProfileDestination.topLevelRoute) },
            selectedIcon = Icons.Default.AccountCircle,
            unselectedIcon = Icons.Outlined.AccountCircle,
            contentDescription = R.string.nav_profile
        ),
        TopLevelDestination(
            navigationDestination = CategoriesDestination,
            navigateTo = { navigateTo(CategoriesDestination.topLevelRoute) },
            selectedIcon = Icons.Default.Home,
            unselectedIcon = Icons.Outlined.Home,
            contentDescription = R.string.nav_categories
        ),
        TopLevelDestination(
            navigationDestination = SettingsDestination,
            navigateTo = { navigateTo(SettingsDestination.topLevelRoute) },
            selectedIcon = Icons.Default.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            contentDescription = R.string.nav_settings
        )
    )
}