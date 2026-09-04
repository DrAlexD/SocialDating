package xelagurd.socialdating.client.data.model.additional

import xelagurd.socialdating.client.data.model.DataEntity

data class UserCategoryData(
    override val id: Int,
    val interest: Int,
    val userId: Int,
    val categoryId: Int,
    val categoryName: String,
    val categoryOrderNumber: Int
) : DataEntity