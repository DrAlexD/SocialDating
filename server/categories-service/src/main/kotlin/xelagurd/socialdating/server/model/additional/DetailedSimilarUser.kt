package xelagurd.socialdating.server.model.additional

data class DetailedSimilarUser(
    val similarNumber: Int,
    val oppositeNumber: Int,
    val categories: Map<Int, DetailedSimilarCategory>
)