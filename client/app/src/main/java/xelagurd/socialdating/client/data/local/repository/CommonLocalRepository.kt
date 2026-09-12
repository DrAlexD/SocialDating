package xelagurd.socialdating.client.data.local.repository

import javax.inject.Inject
import javax.inject.Singleton
import androidx.room.Transaction
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.model.Category
import xelagurd.socialdating.client.data.model.DefaultDataProperties.ID_MIN
import xelagurd.socialdating.client.data.model.DefiningTheme
import xelagurd.socialdating.client.data.model.dto.StatementDto
import xelagurd.socialdating.client.data.model.UserCategory
import xelagurd.socialdating.client.data.model.UserDefiningTheme

@Singleton
class CommonLocalRepository @Inject constructor(
    private val usersRepository: LocalUsersRepository,
    private val categoriesRepository: LocalCategoriesRepository,
    private val definingThemesRepository: LocalDefiningThemesRepository,
    private val userCategoriesRepository: LocalUserCategoriesRepository,
    private val userDefiningThemesRepository: LocalUserDefiningThemesRepository,
    private val statementsRepository: LocalStatementsRepository
) {

    @Transaction
    suspend fun updateStatementsScreenData(
        definingThemes: List<DefiningTheme>,
        categoryId: Int,
        statements: List<StatementDto>,
        firstOrderNumber: Int,
        isFirstPage: Boolean
    ) {
        definingThemesRepository.insertDefiningThemes(definingThemes)

        if (isFirstPage) {
            statementsRepository.deleteStatements(categoryId)
        }
        statementsRepository.insertStatements(
            statements.mapIndexed { index, statement -> statement.toStatement(firstOrderNumber + index) }
        )
        statementsRepository.insertStatementDefiningThemes(
            statements.flatMap { it.toStatementDefiningThemes() }
        )
    }

    @Transaction
    suspend fun updateProfileStatisticsScreenData(
        categories: List<Category>?,
        definingThemes: List<DefiningTheme>?,
        userCategories: List<UserCategory>? = null,
        userDefiningThemes: List<UserDefiningTheme>? = null
    ) {
        if (categories != null) {
            categoriesRepository.insertCategories(categories)
        }
        if (definingThemes != null) {
            definingThemesRepository.insertDefiningThemes(definingThemes)
        }
        if (userCategories != null) {
            userCategoriesRepository.insertUserCategories(userCategories)
        }
        if (userDefiningThemes != null) {
            userDefiningThemesRepository.insertUserDefiningThemes(userDefiningThemes)
        }
    }

    @Transaction
    suspend fun clearData() {
        statementsRepository.deleteAll()
        userDefiningThemesRepository.deleteAll()
        userCategoriesRepository.deleteAll()
        definingThemesRepository.deleteAll() // FixMe: remove after adding server hosting
        categoriesRepository.deleteAll() // FixMe: remove after adding server hosting
        usersRepository.deleteAll()
    }

    @Transaction
    suspend fun initOfflineModeData() { // FixMe: remove after adding server hosting
        usersRepository.insertUser(FakeData.users[0])
        categoriesRepository.insertCategories(FakeData.categories)
        definingThemesRepository.insertDefiningThemes(FakeData.definingThemes)
        userCategoriesRepository.insertUserCategories(FakeData.userCategories)
        userDefiningThemesRepository.insertUserDefiningThemes(FakeData.userDefiningThemes)
        statementsRepository.insertStatements(
            FakeData.statements
                .shuffled()
                .mapIndexed { index, statement -> statement.copy(orderNumber = index + ID_MIN) }
        )
        statementsRepository.insertStatementDefiningThemes(FakeData.statementDefiningThemes)
    }
}