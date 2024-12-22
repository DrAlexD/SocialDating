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
    Statements,
    StatementDetails
}

object CategoriesDestination : NavigationDestination() {
    override val route = AppScreen.Categories.name
}

object StatementsDestination : NavigationDestination() {
    override val route = AppScreen.Statements.name
    const val categoryId = "categoryId"
    val routeWithArgs = "$route/{$categoryId}"
}

object StatementDetailsDestination : NavigationDestination() {
    override val route = AppScreen.StatementDetails.name
    const val statementId = "statementId"
    val routeWithArgs = "$route/{$statementId}"
}
