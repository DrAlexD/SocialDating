package xelagurd.socialdating.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import xelagurd.socialdating.dto.Category
import xelagurd.socialdating.repository.CategoriesRepository

@RestController
@RequestMapping(path = ["/api/categories"], produces = ["application/json"])
class CategoriesController(
    private val categoriesRepository: CategoriesRepository
) {
    @GetMapping
    fun getCategories(): Iterable<Category> {
        return categoriesRepository.findAll()
    }
}