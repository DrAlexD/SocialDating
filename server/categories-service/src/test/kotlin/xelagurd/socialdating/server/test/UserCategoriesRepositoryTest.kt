package xelagurd.socialdating.server.test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.ActiveProfiles
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import xelagurd.socialdating.server.model.Category
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.repository.CategoriesRepository
import xelagurd.socialdating.server.repository.UserCategoriesRepository

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserCategoriesRepositoryTest(
    @param:Autowired private val categoriesRepository: CategoriesRepository,
    @param:Autowired private val userCategoriesRepository: UserCategoriesRepository
) {

    private lateinit var category1: Category
    private lateinit var category2: Category

    @BeforeEach
    fun seedData() {
        val savedCategories = categoriesRepository.saveAllAndFlush(
            listOf(
                Category(name = "Category1"),
                Category(name = "Category2")
            )
        )
        category1 = savedCategories[0]
        category2 = savedCategories[1]

        userCategoriesRepository.saveAllAndFlush(
            listOf(
                UserCategory(interest = 50, userId = 1, categoryId = category1.id!!, maintained = arrayOf(3L)),
                UserCategory(interest = 25, userId = 1, categoryId = category2.id!!, notMaintained = arrayOf(4L)),
                UserCategory(interest = 50, userId = 2, categoryId = category1.id!!, maintained = arrayOf(1L)),
                UserCategory(interest = 25, userId = 2, categoryId = category2.id!!),
                UserCategory(interest = 25, userId = 3, categoryId = category1.id!!, notMaintained = arrayOf(2L))
            )
        )
    }

    @Test
    fun findAllByUserId_existData_returnsOnlyUsersCategories() {
        val result = userCategoriesRepository.findAllByUserId(1)

        assertEquals(2, result.size)
        assertTrue(result.all { it.userId == 1 })
        assertEquals(setOf(category1.id, category2.id), result.map { it.categoryId }.toSet())
    }

    @Test
    fun findAllByUserId_noCategories_returnsEmptyList() {
        assertTrue(userCategoriesRepository.findAllByUserId(999).isEmpty())
    }

    @Test
    fun findByUserIdAndCategoryId_existing_returnsUserCategory() {
        val result = userCategoriesRepository.findByUserIdAndCategoryId(1, category1.id!!)

        assertEquals(1, result?.userId)
        assertEquals(category1.id, result?.categoryId)
    }

    @Test
    fun findByUserIdAndCategoryId_notExisting_returnsNull() {
        assertNull(userCategoriesRepository.findByUserIdAndCategoryId(1, 99999))
    }

    @Test
    fun findCurrentUserCategories_nullCategoryIds_returnsAllWithDataJoinedWithName() {
        val result = userCategoriesRepository.findCurrentUserCategories(1, null)

        assertEquals(2, result.size)
        assertEquals(setOf(category1.id, category2.id), result.map { it.id }.toSet())
        assertEquals(setOf("Category1", "Category2"), result.map { it.name }.toSet())
    }

    @Test
    fun findCurrentUserCategories_withCategoryIds_returnsOnlyMatching() {
        val result = userCategoriesRepository.findCurrentUserCategories(1, listOf(category1.id!!))

        assertEquals(1, result.size)
        assertEquals(category1.id, result.single().id)
        assertArrayEquals(arrayOf(3L), result.single().maintained)
    }

    @Test
    fun findCurrentUserCategories_categoryWithoutData_filtersItOut() {
        // user 2 has category1 with maintained data and category2 without any data
        val result = userCategoriesRepository.findCurrentUserCategories(2, null)

        assertEquals(1, result.size)
        assertEquals(category1.id, result.single().id)
    }

    @Test
    fun findAnotherUsersCategories_allUsers_excludesCurrentUserAndEmptyData() {
        val result = userCategoriesRepository.findAnotherUsersCategories(
            currentUserId = 1,
            anotherUserId = null,
            categoryIds = listOf(category1.id!!, category2.id!!)
        )

        // users 2 (category1, maintained) and 3 (category1, notMaintained);
        // user 2 category2 (no data) and current user 1 are excluded
        assertEquals(2, result.size)
        assertTrue(result.none { it.userId == 1 })
        assertEquals(setOf(2, 3), result.map { it.userId }.toSet())
    }

    @Test
    fun findAnotherUsersCategories_specificUser_returnsOnlyThatUser() {
        val result = userCategoriesRepository.findAnotherUsersCategories(
            currentUserId = 1,
            anotherUserId = 2,
            categoryIds = listOf(category1.id!!, category2.id!!)
        )

        assertEquals(1, result.size)
        assertEquals(2, result.single().userId)
        assertEquals(category1.id, result.single().categoryId)
    }

    @Test
    fun findAnotherUsersCategories_categoryWithoutAnotherUsersData_returnsEmptyList() {
        val result = userCategoriesRepository.findAnotherUsersCategories(
            currentUserId = 1,
            anotherUserId = null,
            categoryIds = listOf(category2.id!!)
        )

        assertTrue(result.isEmpty())
    }

    companion object {
        @ServiceConnection
        val postgresContainer = PostgreSQLContainer("postgres:18")
            .apply {
                withDatabaseName("test_db")
                withUsername("test_user")
                withPassword("test_password")
                withInitScript("init-schema.sql")
            }
    }
}
