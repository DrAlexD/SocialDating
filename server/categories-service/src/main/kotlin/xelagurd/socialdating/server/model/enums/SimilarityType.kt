package xelagurd.socialdating.server.model.enums

enum class SimilarityType {
    SIMILAR,
    EQUAL,
    OPPOSITE;

    companion object {
        fun fromSimilarityDiff(similarityDiff: Int) =
            when {
                similarityDiff > 0 -> SIMILAR
                similarityDiff < 0 -> OPPOSITE
                else -> EQUAL
            }
    }
}