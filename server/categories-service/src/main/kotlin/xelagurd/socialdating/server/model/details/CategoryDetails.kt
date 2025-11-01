package xelagurd.socialdating.server.model.details

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import xelagurd.socialdating.server.model.Category

data class CategoryDetails(
    @field:NotBlank
    @field:Size(max = 100)
    val name: String
) {
    fun toCategory() = Category(name = name)
}