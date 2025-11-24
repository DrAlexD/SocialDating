package xelagurd.socialdating.client.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.PreferencesRepository.Defaults.CURRENT_USER_ID_DEFAULT

const val TIMEOUT_MILLIS = 5_000L

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
    @param:StringRes
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
    ProfileStatistics,
    SimilarUsers
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

    const val userId = "userId"
    const val categoryId = "categoryId"
    val routeWithArgs = "$route/{$userId}/{$categoryId}"
}

object StatementAddingDestination : NavigationDestination() {
    override val route = AppScreen.StatementAdding.name
    override val topLevelRoute = CategoriesDestination.route

    const val userId = "userId"
    const val categoryId = "categoryId"
    val routeWithArgs = "$route/{$userId}/{$categoryId}"
}

object StatementDetailsDestination : NavigationDestination() {
    override val route = AppScreen.StatementDetails.name
    override val topLevelRoute = CategoriesDestination.route

    const val statementId = "statementId"
    val routeWithArgs = "$route/{$statementId}"
}

object ProfileDestination : NavigationDestination() {
    override val route = AppScreen.Profile.name

    var currentUserId = CURRENT_USER_ID_DEFAULT
    override val topLevelRoute
        get() = "$route/$currentUserId/$currentUserId"

    const val userId = "userId"
    const val anotherUserId = "anotherUserId"
    val routeWithArgs = "$route/{$userId}/{$anotherUserId}"
}

object ProfileStatisticsDestination : NavigationDestination() {
    override val route = AppScreen.ProfileStatistics.name

    var currentUserId = CURRENT_USER_ID_DEFAULT
    override val topLevelRoute
        get() = "${ProfileDestination.route}/$currentUserId/$currentUserId"

    const val userId = "userId"
    const val anotherUserId = "anotherUserId"
    val routeWithArgs = "$route/{$userId}/{$anotherUserId}"
}

object SimilarUsersDestination : NavigationDestination() {
    override val route = AppScreen.SimilarUsers.name

    var currentUserId = CURRENT_USER_ID_DEFAULT
    override val topLevelRoute
        get() = "$route/$currentUserId"

    const val userId = "userId"
    val routeWithArgs = "$route/{$userId}"
}
