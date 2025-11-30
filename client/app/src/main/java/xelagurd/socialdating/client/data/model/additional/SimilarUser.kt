package xelagurd.socialdating.client.data.model.additional

import kotlinx.serialization.Serializable
import xelagurd.socialdating.client.data.model.User
import xelagurd.socialdating.client.data.model.ui.SimilarUserWithData

@Serializable
data class SimilarUser(
    val id: Int,
    val similarNumber: Int,
    val oppositeNumber: Int,
    val similarCategories: List<SimilarCategory>,
    val oppositeCategories: List<SimilarCategory>
) {
    fun toSimilarUserWithData(user: User?) =
        user?.let {
            SimilarUserWithData(
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