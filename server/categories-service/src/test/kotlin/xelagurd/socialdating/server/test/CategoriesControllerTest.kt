package xelagurd.socialdating.server.test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.controller.CategoriesController
import xelagurd.socialdating.server.service.CategoriesService
import xelagurd.socialdating.server.utils.TestUtils.mockkList

@WebMvcTest(CategoriesController::class)
@Import(NoSecurityConfig::class)
@ExtendWith(MockKExtension::class)
class CategoriesControllerTest(@param:Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var categoriesService: CategoriesService

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

}