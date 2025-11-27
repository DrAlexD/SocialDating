package xelagurd.socialdating.server.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid
import xelagurd.socialdating.server.model.details.CategoryDetails
import xelagurd.socialdating.server.security.AdminAccess
import xelagurd.socialdating.server.security.BearerAuth
import xelagurd.socialdating.server.service.CategoriesService
import xelagurd.socialdating.server.utils.DataUtils.responseEntities

@RestController
@RequestMapping(path = ["/categories"], produces = ["application/json"])
class CategoriesController(
    private val categoriesService: CategoriesService
) {

    @BearerAuth
    @GetMapping
    fun getCategories() =
        responseEntities { categoriesService.getCategories() }

    @BearerAuth
    @AdminAccess
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addCategory(@RequestBody @Valid categoryDetails: CategoryDetails) =
        categoriesService.addCategory(categoryDetails)
}