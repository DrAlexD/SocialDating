package xelagurd.socialdating.server.model.dto

data class DetailedSimilarUserDto(
    val similarNumber: Int,
    val oppositeNumber: Int,
    val categories: Map<Int, DetailedSimilarCategoryDto>
)