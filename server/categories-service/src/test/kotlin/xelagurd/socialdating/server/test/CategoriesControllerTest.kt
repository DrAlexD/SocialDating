package xelagurd.socialdating.server.test

import kotlin.random.Random
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.MediaType
import org.springframework.transaction.TransactionSystemException
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.FakeCategoriesData
import xelagurd.socialdating.server.controller.CategoriesController
import xelagurd.socialdating.server.model.details.CategoryDetails
import xelagurd.socialdating.server.service.CategoriesService
import xelagurd.socialdating.server.utils.TestUtils.mockkList
import xelagurd.socialdating.server.utils.TestUtils.nextIntList
import xelagurd.socialdating.server.utils.TestUtils.toRequestParams

@WebMvcTest(CategoriesController::class)
@Import(NoSecurityConfig::class)
@ExtendWith(MockKExtension::class)
class CategoriesControllerTest(@param:Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var categoriesService: CategoriesService

    private val objectMapper = jacksonObjectMapper()

    private val categoryIds = Random.nextIntList()
    private val categoryDetails = FakeCategoriesData.categoriesDetails[0]
    private val categoryDto = FakeCategoriesData.categoryDtos[0]

    @Test
    fun getCategories_existData_ok() {
        every { categoriesService.getCategories() } returns mockkList(relaxed = true)

        mockMvc.perform(
            get("/categories")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        verify(exactly = 1) { categoriesService.getCategories() }
        confirmVerified(categoriesService)
    }

    @Test
    fun getCategories_withIds_ok() {
        every { categoriesService.getCategories(any()) } returns mockkList(relaxed = true)

        mockMvc.perform(
            get("/categories?categoryIds=${categoryIds.toRequestParams()}")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        verify(exactly = 1) { categoriesService.getCategories(categoryIds) }
        confirmVerified(categoriesService)
    }

    @Test
    fun addCategory_validData_created() {
        every { categoriesService.addCategory(any()) } returns categoryDto

        mockMvc.perform(
            post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDetails))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        verify(exactly = 1) { categoriesService.addCategory(any()) }
        confirmVerified(categoriesService)
    }

    @Test
    fun addCategory_invalidData_badRequest() {
        mockMvc.perform(
            post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(CategoryDetails(nameEn = "", nameRu = "")))
        )
            .andExpect(status().isBadRequest)

        confirmVerified(categoriesService)
    }

    @Test
    fun addCategory_invalidEntityData_badRequest() {
        every { categoriesService.addCategory(any()) } throws
                TransactionSystemException("Could not commit JPA transaction")

        mockMvc.perform(
            post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDetails))
        )
            .andExpect(status().isBadRequest)

        verify(exactly = 1) { categoriesService.addCategory(any()) }
        confirmVerified(categoriesService)
    }

    @Test
    fun addCategory_notUniqueData_conflict() {
        every { categoriesService.addCategory(any()) } throws
                DataIntegrityViolationException("Unique index or primary key violation")

        mockMvc.perform(
            post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDetails))
        )
            .andExpect(status().isConflict)

        verify(exactly = 1) { categoriesService.addCategory(any()) }
        confirmVerified(categoriesService)
    }

    @Test
    fun getCategories_unknownError_internalServerError() {
        every { categoriesService.getCategories() } throws RuntimeException("Unknown error")

        mockMvc.perform(
            get("/categories")
        )
            .andExpect(status().isInternalServerError)

        verify(exactly = 1) { categoriesService.getCategories() }
        confirmVerified(categoriesService)
    }
}
