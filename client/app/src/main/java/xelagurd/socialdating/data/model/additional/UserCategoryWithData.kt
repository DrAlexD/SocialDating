package xelagurd.socialdating.data.model.additional

import kotlinx.serialization.Serializable
import xelagurd.socialdating.data.model.DataEntity

@Serializable
data class UserCategoryWithData(
    override val id: Int,
    val interest: Int,
    val userId: Int,
    val categoryId: Int,
    val categoryName: String
) : DataEntity