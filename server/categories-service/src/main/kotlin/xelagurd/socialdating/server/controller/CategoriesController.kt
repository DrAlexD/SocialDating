package xelagurd.socialdating.server.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import xelagurd.socialdating.server.model.Category
import xelagurd.socialdating.server.model.details.CategoryDetails
import xelagurd.socialdating.server.service.CategoriesService

@RestController
@RequestMapping(path = ["/api/v1/categories"], produces = ["application/json"])
class CategoriesController(
    private val categoriesService: CategoriesService
) {

    @GetMapping
    fun getCategories(): List<Category> {
        return categoriesService.getCategories()
    }

    // TODO: Add admin privileges
    @PostMapping
    fun addCategory(@RequestBody categoryDetails: CategoryDetails): Category {
        return categoriesService.addCategory(categoryDetails)
    }
}