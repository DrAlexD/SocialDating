package xelagurd.socialdating.client.data.model.ui

import xelagurd.socialdating.client.data.model.DataEntity

data class UserCategoryWithData(
    override val id: Int,
    val interest: Int,
    val userId: Int,
    val categoryId: Int,
    val categoryName: String
) : DataEntity