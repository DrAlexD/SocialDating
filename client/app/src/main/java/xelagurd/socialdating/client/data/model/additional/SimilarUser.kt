package xelagurd.socialdating.client.data.model.additional

import kotlinx.serialization.Serializable
import xelagurd.socialdating.client.data.model.User

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
                name = user.name,
                gender = user.gender,
                age = user.age,
                city = user.city,
                purpose = user.purpose,
                similarNumber = similarNumber,
                oppositeNumber = oppositeNumber,
                similarCategories = similarCategories,
                oppositeCategories = oppositeCategories
            )
        }
}