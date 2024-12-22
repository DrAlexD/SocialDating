package xelagurd.socialdating.data.fake

import xelagurd.socialdating.data.model.Category
import xelagurd.socialdating.data.model.Statement

// FixMe: remove after implementing server
object FakeDataSource {
    val categories = listOf(
        Category(1, "abc1"),
        Category(2, "abc2"),
        Category(3, "abc3"),
        Category(4, "abc4"),
        Category(5, "abc5"),
        Category(6, "abc6"),
        Category(7, "abc7"),
        Category(8, "abc8"),
        Category(9, "abc9"),
        Category(10, "abc10"),
        Category(11, "abc11"),
        Category(12, "abc12"),
        Category(13, "abc13"),
        Category(14, "abc14"),
        Category(15, "abc15"),
        Category(16, "abc16"),
        Category(17, "abc17"),
        Category(18, "abc18"),
        Category(19, "abc19"),
        Category(20, "abc20"),
    )

    val statements = listOf(
        Statement(1, "abc1 dfdsds dfdsfdsf dfssdfsdf ddf.", 1),
        Statement(2, "f bc2dfsd dfsdf, dfdsf sdfdsf. s sfdsdfs dfdsfsdf", 1),
        Statement(3, "abc3ddsf! dfdsfdsfdfsfddsf", 1),
        Statement(4, "abcd fdsf gfd. dfdsfdf", 1),
        Statement(5, "abfdssdfc5 dfs dfdd dfs. dfdsfdsfdf", 1),
        Statement(6, "abc6 sdfdsf sdfdf dsf dfdfdfdsfdf", 1),
        Statement(7, "abc7 dsf dfdffd dfsf dfsdsfd fdfsdfds", 1),
        Statement(8, "abc8 sfdd sdfdsfd daadsadsad dsfdsf", 1),
        Statement(9, "abc9dfdf dsdsfds sdfdsf ss fdfds sdfdf sasdad adadsd asdadssad asdada asd", 1),
        Statement(10, "abc10asd asdasd adsd asd  asdsd", 1),
        Statement(11, "abc11 adssd ssda as sfddsfdf", 1),
        Statement(12, "abc12 asd asdsdss ssd", 1),
        Statement(13, "abc13 ads aasd sdfdf", 1),
        Statement(14, "abc14 asd assadsa adasdsd", 1),
        Statement(15, "abc15 ad adsds asdsads adssd", 1),
        Statement(16, "abc16 asd asdsads aasd", 1),
        Statement(17, "abc17 adsadf adds ssad", 2),
        Statement(18, "abc18 adas asdas asdsds adssads daaddssa asdasdsd asddsa", 2),
        Statement(19, "abc19 adsssad asdsa asdsad sadsd adsd adssd adadsdadaaa", 2),
        Statement(20, "abc20 asdsad asdsd asdss sdasd addadads adssdf!", 2),
    )
}