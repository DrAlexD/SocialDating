package xelagurd.socialdating.server.service

import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.Category
import xelagurd.socialdating.server.model.details.CategoryDetails
import xelagurd.socialdating.server.repository.CategoriesRepository

@Service
class CategoriesService(
    private val categoriesRepository: CategoriesRepository
) {

    fun getCategories(): List<Category> =
        categoriesRepository.findAll()

    fun addCategory(categoryDetails: CategoryDetails) =
        categoriesRepository.save(categoryDetails.toCategory())
}