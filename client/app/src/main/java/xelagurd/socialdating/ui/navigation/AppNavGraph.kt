package xelagurd.socialdating.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import xelagurd.socialdating.R
import xelagurd.socialdating.ui.screen.CategoriesScreen
import xelagurd.socialdating.ui.screen.StatementsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    initializeTopLevelDestinations(navController)

    NavHost(
        navController = navController,
        startDestination = CategoriesDestination.route
    ) {
        composable(route = CategoriesDestination.route) {
            CategoriesScreen(
                onCategoryClick = {
                    navController.navigate("${StatementsDestination.route}/${it}")
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
                    navController.navigate("${StatementDetailsDestination.route}/${it}")
                },
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
            navigationDestination = CategoriesDestination,
            navigateTo = { navigateTo(CategoriesDestination.route) },
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            contentDescription = R.string.nav_categories
        )
    )
}