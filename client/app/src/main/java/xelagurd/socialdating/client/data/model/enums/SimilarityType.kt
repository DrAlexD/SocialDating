package xelagurd.socialdating.client.data.model.enums

import androidx.compose.ui.graphics.Color

enum class SimilarityType(val color: Color) {
    SIMILAR(Color(0xFF4CAF50)),
    EQUAL(Color(0xFFFFC107)),
    OPPOSITE(Color(0xFFF44336))
}