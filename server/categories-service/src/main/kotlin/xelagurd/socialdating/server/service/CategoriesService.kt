package xelagurd.socialdating.server.service

import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.Category
import xelagurd.socialdating.server.model.details.CategoryDetails
import xelagurd.socialdating.server.repository.CategoriesRepository

@Service
class CategoriesService(
    private val categoriesRepository: CategoriesRepository
) {

    fun getCategories(): Iterable<Category> {
        return categoriesRepository.findAll()
    }

    fun addCategory(categoryDetails: CategoryDetails): Category {
        return categoriesRepository.save(categoryDetails.toCategory())
    }
}