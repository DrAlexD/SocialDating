package xelagurd.socialdating.client.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.PreferencesRepository.Defaults.CURRENT_USER_ID_DEFAULT

abstract class NavigationDestination {
    abstract val route: String
    abstract val topLevelRoute: String

    @StringRes
    val titleRes: Int = R.string.app_name
}

data class TopLevelDestination(
    val navigationDestination: NavigationDestination,
    val navigateTo: () -> Unit,
    @param:DrawableRes
    val selectedIconRes: Int,
    @param:DrawableRes
    val unselectedIconRes: Int,
    @param:StringRes
    val labelRes: Int,
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
    override val topLevelRoute = CategoriesDestination.topLevelRoute

    const val userId = "userId"
    const val categoryId = "categoryId"
    val routeWithArgs = "$route/{$userId}/{$categoryId}"
}

object StatementAddingDestination : NavigationDestination() {
    override val route = AppScreen.StatementAdding.name
    override val topLevelRoute = CategoriesDestination.topLevelRoute

    const val userId = "userId"
    const val categoryId = "categoryId"
    val routeWithArgs = "$route/{$userId}/{$categoryId}"
}

object StatementDetailsDestination : NavigationDestination() {
    override val route = AppScreen.StatementDetails.name
    override val topLevelRoute = CategoriesDestination.topLevelRoute

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

    override val topLevelRoute
        get() = ProfileDestination.topLevelRoute

    const val userId = "userId"
    const val anotherUserId = "anotherUserId"

    // the statistics of a similar user are limited by the categories which were used to find this user
    const val categoryIds = "categoryIds"
    val routeWithArgs = "$route/{$userId}/{$anotherUserId}?$categoryIds={$categoryIds}"

    // the list argument is passed by the repeated query parameter
    fun categoryIdsQuery(ids: Set<Int>) =
        when {
            ids.isEmpty() -> ""
            else -> ids.joinToString(separator = "&", prefix = "?") { "$categoryIds=$it" }
        }
}

object SimilarUsersDestination : NavigationDestination() {
    override val route = AppScreen.SimilarUsers.name

    var currentUserId = CURRENT_USER_ID_DEFAULT
    override val topLevelRoute
        get() = "$route/$currentUserId"

    const val userId = "userId"
    val routeWithArgs = "$route/{$userId}"
}
