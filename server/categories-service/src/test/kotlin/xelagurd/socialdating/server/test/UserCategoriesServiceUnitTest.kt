package xelagurd.socialdating.server.test

import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.repository.UserCategoriesRepository
import xelagurd.socialdating.server.service.UserCategoriesService

@ExtendWith(MockKExtension::class)
class UserCategoriesServiceUnitTest {

    @MockK
    private lateinit var userCategoriesRepository: UserCategoriesRepository

    @InjectMockKs
    private lateinit var userCategoriesService: UserCategoriesService

    @Disabled
    @Test
    fun getUsersWithSimilarity() {
        // TODO
    }

    @Disabled
    @Test
    fun getSimilarUser() {
        // TODO
    }

}