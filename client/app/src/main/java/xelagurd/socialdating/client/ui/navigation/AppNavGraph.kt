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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
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
    SimilarUsersDestination.currentUserId = currentUserId

    initializeTopLevelDestinations(navController)

    // the start destination belongs to the graph, so it is calculated once per navigation
    // controller: a change of the current user navigates instead of recreating the graph,
    // which would reset the back stack and duplicate the opened screen
    val startDestination = remember(navController) {
        when (currentUserId) {
            CURRENT_USER_ID_DEFAULT -> LoginDestination.route
            else -> CategoriesDestination.route
        }
    }

    // the only place which reacts to a logout, either by the button or by a rejected refresh token
    LaunchedEffect(currentUserId) {
        val currentRoute = navController.currentDestination?.route

        if (currentUserId == CURRENT_USER_ID_DEFAULT && currentRoute != null &&
            currentRoute != LoginDestination.route
        ) {
            navController.navigateWithClearedBackStack(LoginDestination.route)
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = LoginDestination.route) {
            LoginScreen(
                onSuccessLogin = {
                    navController.navigateWithClearedBackStack(CategoriesDestination.route)
                },
                onRegistrationClick = {
                    navController.navigateIfResumed(RegistrationDestination.route)
                }
            )
        }

        composable(route = RegistrationDestination.route) {
            RegistrationScreen(
                onSuccessRegistration = {
                    navController.navigateWithClearedBackStack(CategoriesDestination.route)
                },
                onNavigateUp = { navController.navigateUpIfResumed() }
            )
        }

        composable(route = SettingsDestination.route) {
            SettingsScreen()
        }

        composable(route = CategoriesDestination.route) {
            CategoriesScreen(
                onCategoryClick = {
                    navController.navigateIfResumed("${StatementsDestination.route}/$currentUserId/$it")
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
                    navController.navigateIfResumed("${StatementDetailsDestination.route}/$it")
                },
                onStatementAddingClick = {
                    navController.navigateIfResumed("${StatementAddingDestination.route}/$currentUserId/$it")
                },
                onNavigateUp = { navController.navigateUpIfResumed() }
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
                onSuccessStatementAdding = { navController.navigateUpIfResumed() },
                onNavigateUp = { navController.navigateUpIfResumed() }
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
                    navController.navigateIfResumed("${ProfileStatisticsDestination.route}/$currentUserId/$it")
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
                onNavigateUp = { navController.navigateUpIfResumed() }
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
                    navController.navigateIfResumed("${ProfileStatisticsDestination.route}/$currentUserId/$it")
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

private fun NavHostController.navigateIfResumed(route: String) {
    if (isCurrentScreenResumed()) {
        navigate(route)
    }
}

private fun NavHostController.navigateUpIfResumed() {
    if (isCurrentScreenResumed()) {
        navigateUp()
    }
}

private fun NavHostController.navigateWithClearedBackStack(route: String) =
    navigate(route) {
        // the whole graph is popped, the start destination is not used, because it can be already
        // removed from the back stack by a previous clearing navigation
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }

private fun NavHostController.isCurrentScreenResumed() =
    currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED

fun initializeTopLevelDestinations(navController: NavHostController) {
    // the categories screen is the home of the logged in area, the start destination of the graph
    // is not used here, because it stays the login screen when the app was started logged out
    val navigateTo = { route: String ->
        navController.navigate(route) {
            popUpTo(CategoriesDestination.route) {
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
            labelRes = R.string.profile,
            contentDescription = R.string.nav_profile
        ),
        TopLevelDestination(
            navigationDestination = CategoriesDestination,
            navigateTo = { navigateTo(CategoriesDestination.topLevelRoute) },
            selectedIcon = Icons.Default.Home,
            unselectedIcon = Icons.Outlined.Home,
            labelRes = R.string.categories,
            contentDescription = R.string.nav_categories
        ),
        TopLevelDestination(
            navigationDestination = SimilarUsersDestination,
            navigateTo = { navigateTo(SimilarUsersDestination.topLevelRoute) },
            selectedIcon = Icons.Default.Face,
            unselectedIcon = Icons.Outlined.Face,
            labelRes = R.string.similar_users,
            contentDescription = R.string.nav_similar_users
        ),
        TopLevelDestination(
            navigationDestination = SettingsDestination,
            navigateTo = { navigateTo(SettingsDestination.topLevelRoute) },
            selectedIcon = Icons.Default.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            labelRes = R.string.settings,
            contentDescription = R.string.nav_settings
        )
    )
}