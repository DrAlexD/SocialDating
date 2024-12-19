package xelagurd.socialdating.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import xelagurd.socialdating.ui.screen.CategoriesScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
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
            //
        }
    }
}