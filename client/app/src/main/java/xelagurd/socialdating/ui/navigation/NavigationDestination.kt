package xelagurd.socialdating.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import xelagurd.socialdating.R

abstract class NavigationDestination {
    abstract val route: String
    abstract val topLevelRoute: String

    @StringRes
    val titleRes: Int = R.string.app_name
}

data class TopLevelDestination(
    val navigationDestination: NavigationDestination,
    val navigateTo: () -> Unit,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes
    val contentDescription: Int
)

val topLevelDestinations = arrayListOf<TopLevelDestination>()

enum class AppScreen {
    Categories,
    Statements,
    StatementDetails
}

object CategoriesDestination : NavigationDestination() {
    override val route = AppScreen.Categories.name
    override val topLevelRoute = route
}

object StatementsDestination : NavigationDestination() {
    override val route = AppScreen.Statements.name
    override val topLevelRoute = CategoriesDestination.route
    const val categoryId = "categoryId"
    val routeWithArgs = "$route/{$categoryId}"
}

object StatementDetailsDestination : NavigationDestination() {
    override val route = AppScreen.StatementDetails.name
    override val topLevelRoute = CategoriesDestination.route
    const val statementId = "statementId"
    val routeWithArgs = "$route/{$statementId}"
}
