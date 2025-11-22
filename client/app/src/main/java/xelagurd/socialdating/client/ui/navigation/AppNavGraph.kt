package xelagurd.socialdating.client.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.PreferencesRepository.Defaults.CURRENT_USER_ID_DEFAULT
import xelagurd.socialdating.client.ui.screen.CategoriesScreen
import xelagurd.socialdating.client.ui.screen.LoginScreen
import xelagurd.socialdating.client.ui.screen.ProfileScreen
import xelagurd.socialdating.client.ui.screen.ProfileStatisticsScreen
import xelagurd.socialdating.client.ui.screen.RegistrationScreen
import xelagurd.socialdating.client.ui.screen.SettingsScreen
import xelagurd.socialdating.client.ui.screen.SimilarUsersScreen
import xelagurd.socialdating.client.ui.screen.StatementAddingScreen
import xelagurd.socialdating.client.ui.screen.StatementsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    appNavViewModel: AppNavViewModel = hiltViewModel()
) {
    val currentUserId by appNavViewModel.currentUserId.collectAsState()

    ProfileDestination.currentUserId = currentUserId
    ProfileStatisticsDestination.currentUserId = currentUserId
    SimilarUsersDestination.currentUserId = currentUserId

    initializeTopLevelDestinations(navController)

    NavHost(
        navController = navController,
        startDestination =
            if (currentUserId == CURRENT_USER_ID_DEFAULT)
                LoginDestination.route
            else
                CategoriesDestination.route
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
                    navController.navigate("${StatementsDestination.route}/$currentUserId/$it")
                }
            )
        }

        composable(
            route = StatementsDestination.routeWithArgs,
            arguments = listOf(
                navArgument(StatementsDestination.userId) {
                    type = NavType.IntType
                },
                navArgument(StatementsDestination.categoryId) {
                    type = NavType.IntType
                }
            )
        ) {
            StatementsScreen(
                onStatementClick = {
                    navController.navigate("${StatementDetailsDestination.route}/$it")
                },
                onStatementAddingClick = {
                    navController.navigate("${StatementAddingDestination.route}/$currentUserId/$it")
                },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = StatementAddingDestination.routeWithArgs,
            arguments = listOf(
                navArgument(StatementAddingDestination.userId) {
                    type = NavType.IntType
                },
                navArgument(StatementAddingDestination.categoryId) {
                    type = NavType.IntType
                }
            )
        ) {
            StatementAddingScreen(
                onSuccessStatementAdding = { navController.navigateUp() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = ProfileDestination.routeWithArgs,
            arguments = listOf(
                navArgument(ProfileDestination.userId) {
                    type = NavType.IntType
                },
                navArgument(ProfileDestination.anotherUserId) {
                    type = NavType.IntType
                }
            )
        ) {
            ProfileScreen(
                onProfileStatisticsClick = {
                    navController.navigate("${ProfileStatisticsDestination.route}/$currentUserId/$it")
                }
            )
        }

        composable(
            route = ProfileStatisticsDestination.routeWithArgs,
            arguments = listOf(
                navArgument(ProfileStatisticsDestination.userId) {
                    type = NavType.IntType
                },
                navArgument(ProfileStatisticsDestination.anotherUserId) {
                    type = NavType.IntType
                }
            )
        ) {
            ProfileStatisticsScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = SimilarUsersDestination.routeWithArgs,
            arguments = listOf(
                navArgument(SimilarUsersDestination.userId) {
                    type = NavType.IntType
                }
            )
        ) {
            SimilarUsersScreen(
                onSimilarUserClick = {
                    navController.navigate("${ProfileStatisticsDestination.route}/$currentUserId/$it")
                }
            )
        }

        composable(
            route = StatementDetailsDestination.routeWithArgs,
            arguments = listOf(
                navArgument(StatementDetailsDestination.statementId) {
                    type = NavType.IntType
                }
            )
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
            navigationDestination = SimilarUsersDestination,
            navigateTo = { navigateTo(SimilarUsersDestination.topLevelRoute) },
            selectedIcon = Icons.Default.Face,
            unselectedIcon = Icons.Outlined.Face,
            contentDescription = R.string.nav_similar_users
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