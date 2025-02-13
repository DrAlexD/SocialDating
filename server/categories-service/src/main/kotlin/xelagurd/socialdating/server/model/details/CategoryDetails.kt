package xelagurd.socialdating.server.model.details

import jakarta.validation.constraints.NotBlank
import xelagurd.socialdating.server.model.Category

data class CategoryDetails(
    @field:NotBlank
    val name: String
) {
    fun toCategory(): Category =
        Category(name = name)
}