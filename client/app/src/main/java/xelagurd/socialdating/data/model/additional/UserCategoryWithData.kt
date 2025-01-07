package xelagurd.socialdating.data.model.additional

import kotlinx.serialization.Serializable
import xelagurd.socialdating.data.model.UserCategory

@Serializable
data class UserCategoryWithData(
    val id: Int,
    val interest: Int,
    val userId: Int,
    val categoryId: Int,
    val categoryName: String
) {
    fun toUserCategory() =
        UserCategory(
            id = id,
            interest = interest,
            userId = userId,
            categoryId = categoryId
        )
}

