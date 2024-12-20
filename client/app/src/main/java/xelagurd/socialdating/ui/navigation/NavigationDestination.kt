package xelagurd.socialdating.ui.navigation

import androidx.annotation.StringRes
import xelagurd.socialdating.R

abstract class NavigationDestination {
    abstract val route: String
    @StringRes
    val titleRes: Int = R.string.app_name
}

enum class AppScreen {
    Categories,
    Statements
}

object CategoriesDestination : NavigationDestination() {
    override val route = AppScreen.Categories.name
}

object StatementsDestination : NavigationDestination() {
    override val route = AppScreen.Statements.name
    const val categoryId = "categoryId"
    val routeWithArgs = "$route/{$categoryId}"
}
