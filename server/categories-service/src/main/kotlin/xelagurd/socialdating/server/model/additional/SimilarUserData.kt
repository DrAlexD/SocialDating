package xelagurd.socialdating.server.model.additional

import xelagurd.socialdating.server.model.dto.SimilarCategoryDto
import xelagurd.socialdating.server.model.dto.SimilarUserDto
import xelagurd.socialdating.server.model.dto.UserDto

data class SimilarUserData(
    val id: Int,
    val similarNumber: Int,
    val oppositeNumber: Int,
    val differenceNumber: Int,
    val similarCategories: List<SimilarCategoryDto>,
    val oppositeCategories: List<SimilarCategoryDto>
) {
    fun toSimilarUserDto(user: UserDto?) =
        user?.let {
            SimilarUserDto(
                id = id,
                name = it.name,
                gender = it.gender,
                age = it.age,
                city = it.city,
                purpose = it.purpose,
                similarNumber = similarNumber,
                oppositeNumber = oppositeNumber,
                similarCategories = similarCategories,
                oppositeCategories = oppositeCategories
            )
        }
}