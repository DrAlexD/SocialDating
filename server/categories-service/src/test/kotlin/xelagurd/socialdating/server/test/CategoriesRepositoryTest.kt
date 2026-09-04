package xelagurd.socialdating.server.test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.ActiveProfiles
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import xelagurd.socialdating.server.model.Category
import xelagurd.socialdating.server.repository.CategoriesRepository

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoriesRepositoryTest(
    @param:Autowired private val categoriesRepository: CategoriesRepository
) {

    private lateinit var category1: Category
    private lateinit var category2: Category
    private lateinit var category3: Category

    @BeforeEach
    fun seedData() {
        val saved = categoriesRepository.saveAllAndFlush(
            listOf(
                Category(nameEn = "Category1", nameRu = "Категория1", orderNumber = 1),
                Category(nameEn = "Category2", nameRu = "Категория2", orderNumber = 2),
                Category(nameEn = "Category3", nameRu = "Категория3", orderNumber = 3)
            )
        )
        category1 = saved[0]
        category2 = saved[1]
        category3 = saved[2]
    }

    @Test
    fun findAllByIds_null_returnsAll() {
        val result = categoriesRepository.findAllByIds(null)

        assertEquals(3, result.size)
        assertEquals(
            setOf(category1.id, category2.id, category3.id),
            result.map { it.id }.toSet()
        )
    }

    @Test
    fun findAllByIds_withIds_returnsOnlyMatching() {
        val ids = listOf(category1.id!!, category3.id!!)

        val result = categoriesRepository.findAllByIds(ids)

        assertEquals(2, result.size)
        assertEquals(ids.toSet(), result.map { it.id }.toSet())
    }

    @Test
    fun findAllByIds_unknownIds_returnsEmptyList() {
        val result = categoriesRepository.findAllByIds(listOf(99999))

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
