package xelagurd.socialdating.service

import org.springframework.stereotype.Service
import xelagurd.socialdating.dto.Category
import xelagurd.socialdating.dto.CategoryDetails
import xelagurd.socialdating.repository.CategoriesRepository

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