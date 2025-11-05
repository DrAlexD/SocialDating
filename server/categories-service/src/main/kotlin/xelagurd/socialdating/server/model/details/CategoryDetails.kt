package xelagurd.socialdating.server.model.details

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import xelagurd.socialdating.server.model.Category
import xelagurd.socialdating.server.model.DefaultDataProperties.LENGTH_MAX

data class CategoryDetails(
    @field:NotBlank
    @field:Size(max = LENGTH_MAX)
    val name: String
) {
    fun toCategory() = Category(name = name)
}