package xelagurd.socialdating.data.model

import kotlinx.serialization.Serializable

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

