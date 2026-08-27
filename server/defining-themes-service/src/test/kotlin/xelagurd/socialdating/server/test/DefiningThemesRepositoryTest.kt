package xelagurd.socialdating.server.test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.ActiveProfiles
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import xelagurd.socialdating.server.model.DefiningTheme
import xelagurd.socialdating.server.repository.DefiningThemesRepository

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DefiningThemesRepositoryTest(
    @param:Autowired private val definingThemesRepository: DefiningThemesRepository
) {

    private lateinit var category1Theme1: DefiningTheme
    private lateinit var category1Theme2: DefiningTheme
    private lateinit var category2Theme1: DefiningTheme

    private fun definingTheme(name: String, categoryId: Int, numberInCategory: Int) =
        DefiningTheme(
            name = name,
            fromOpinion = "No",
            toOpinion = "Yes",
            categoryId = categoryId,
            numberInCategory = numberInCategory
        )

    @BeforeEach
    fun seedData() {
        val saved = definingThemesRepository.saveAllAndFlush(
            listOf(
                definingTheme(name = "Theme1", categoryId = 1, numberInCategory = 1),
                definingTheme(name = "Theme2", categoryId = 1, numberInCategory = 2),
                definingTheme(name = "Theme3", categoryId = 2, numberInCategory = 1)
            )
        )
        category1Theme1 = saved[0]
        category1Theme2 = saved[1]
        category2Theme1 = saved[2]
    }

    @Test
    fun findAllByIdsAndCategoryId_bothNull_returnsAll() {
        val result = definingThemesRepository.findAllByIdsAndCategoryId(null, null)

        assertEquals(3, result.size)
        assertEquals(
            setOf(category1Theme1.id, category1Theme2.id, category2Theme1.id),
            result.map { it.id }.toSet()
        )
    }

    @Test
    fun findAllByIdsAndCategoryId_categoryIdOnly_returnsThemesOfCategory() {
        val result = definingThemesRepository.findAllByIdsAndCategoryId(null, 1)

        assertEquals(2, result.size)
        assertTrue(result.all { it.categoryId == 1 })
        assertEquals(
            setOf(category1Theme1.id, category1Theme2.id),
            result.map { it.id }.toSet()
        )
    }

    @Test
    fun findAllByIdsAndCategoryId_idsOnly_returnsThemesWithIds() {
        val ids = listOf(category1Theme1.id!!, category2Theme1.id!!)

        val result = definingThemesRepository.findAllByIdsAndCategoryId(ids, null)

        assertEquals(2, result.size)
        assertEquals(ids.toSet(), result.map { it.id }.toSet())
    }

    @Test
    fun findAllByIdsAndCategoryId_idsAndCategoryId_returnsIntersection() {
        val ids = listOf(category1Theme1.id!!, category1Theme2.id!!, category2Theme1.id!!)

        val result = definingThemesRepository.findAllByIdsAndCategoryId(ids, 2)

        assertEquals(1, result.size)
        assertEquals(category2Theme1.id, result.single().id)
    }

    @Test
    fun findMaxNumberInCategory_existingCategory_returnsMaxNumber() {
        assertEquals(2, definingThemesRepository.findMaxNumberInCategory(1))
        assertEquals(1, definingThemesRepository.findMaxNumberInCategory(2))
    }

    @Test
    fun findMaxNumberInCategory_emptyCategory_returnsNull() {
        assertNull(definingThemesRepository.findMaxNumberInCategory(999))
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
