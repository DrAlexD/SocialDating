package xelagurd.socialdating.controller

import org.springframework.web.bind.annotation.*
import xelagurd.socialdating.dto.Category
import xelagurd.socialdating.dto.CategoryDetails
import xelagurd.socialdating.service.CategoriesService

@RestController
@RequestMapping(path = ["/api/v1/categories"], produces = ["application/json"])
class CategoriesController(
    private val categoriesService: CategoriesService
) {

    @GetMapping
    fun getCategories(): Iterable<Category> {
        return categoriesService.getCategories()
    }

    // TODO: Add admin privileges
    @PostMapping
    fun addCategory(@RequestBody categoryDetails: CategoryDetails): Category {
        return categoriesService.addCategory(categoryDetails)
    }
}