package xelagurd.socialdating.client.data.model.additional

import kotlinx.serialization.Serializable
import xelagurd.socialdating.client.data.model.DataEntity

@Serializable
data class UserCategoryWithData(
    override val id: Int,
    val interest: Int,
    val userId: Int,
    val categoryId: Int,
    val categoryName: String
) : DataEntity