package xelagurd.socialdating.server.model.additional

data class SimilarUser(
    val similarityNumber: Int,
    val oppositeNumber: Int,
    val similarCategories: List<SimilarCategory>,
    val equalCategories: List<SimilarCategory>,
    val oppositeCategories: List<SimilarCategory>
)