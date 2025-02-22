package xelagurd.socialdating.client.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import xelagurd.socialdating.client.R

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

var topLevelDestinations: List<TopLevelDestination> = listOf()

enum class AppScreen {
    Login,
    Registration,
    Settings,
    Categories,
    Statements,
    StatementAdding,
    StatementDetails,
    Profile,
    ProfileStatistics
}

object LoginDestination : NavigationDestination() {
    override val route = AppScreen.Login.name
    override val topLevelRoute = route
}

object RegistrationDestination : NavigationDestination() {
    override val route = AppScreen.Registration.name
    override val topLevelRoute = route
}

object SettingsDestination : NavigationDestination() {
    override val route = AppScreen.Settings.name
    override val topLevelRoute = route
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

object StatementAddingDestination : NavigationDestination() {
    override val route = AppScreen.StatementAdding.name
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

object ProfileDestination : NavigationDestination() {
    override val route = AppScreen.Profile.name

    var currentUserId = -1
    override val topLevelRoute
        get() = "$route/$currentUserId"

    const val userId = "userId"
    val routeWithArgs = "$route/{$userId}"
}

object ProfileStatisticsDestination : NavigationDestination() {
    override val route = AppScreen.ProfileStatistics.name

    var currentUserId = -1
    override val topLevelRoute
        get() = "${ProfileDestination.route}/$currentUserId"

    const val userId = "userId"
    val routeWithArgs = "$route/{$userId}"
}
