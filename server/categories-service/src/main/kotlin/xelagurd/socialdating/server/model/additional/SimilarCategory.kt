package xelagurd.socialdating.server.model.additional

import com.fasterxml.jackson.annotation.JsonIgnore

data class SimilarCategory(
    val id: Int,
    val similarityNumber: Int,
    val oppositeNumber: Int,
    @field:JsonIgnore
    val differenceNumber: Int,
    val similarDefiningThemesIds: List<Int>,
    val oppositeDefiningThemesIds: List<Int>
)