package xelagurd.socialdating.data

import kotlinx.coroutines.flow.flowOf
import xelagurd.socialdating.data.model.Category

object DataSource {
    val categoriesList = listOf(
        Category(1, "abc1"),
        Category(2, "abc2"),
        Category(3, "abc3"),
        Category(4, "abc4"),
        Category(5, "abc5"),
        Category(6, "abc6"),
        Category(7, "abc7"),
        Category(8, "abc8"),
        Category(9, "abc9"),
        Category(10, "abc10")
    )

    val categories = flowOf(categoriesList)
}