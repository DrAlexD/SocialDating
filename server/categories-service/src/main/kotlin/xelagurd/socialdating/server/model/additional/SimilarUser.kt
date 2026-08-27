package xelagurd.socialdating.server.model.additional

data class SimilarUser(
    val id: Int,
    val similarNumber: Int,
    val oppositeNumber: Int,
    val differenceNumber: Int,
    val similarCategories: List<SimilarCategory>,
    val oppositeCategories: List<SimilarCategory>
) {
    fun toSimilarUserWithData(user: UserData?) =
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