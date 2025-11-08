package xelagurd.socialdating.server.model.additional

data class CategoryWithMaintainedLists(
    val id: Int,
    val name: String,
    val maintained: Array<Long>?,
    val notMaintained: Array<Long>?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CategoryWithMaintainedLists

        if (id != other.id) return false
        if (name != other.name) return false
        if (!maintained.contentEquals(other.maintained)) return false
        if (!notMaintained.contentEquals(other.notMaintained)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + maintained.contentHashCode()
        result = 31 * result + notMaintained.contentHashCode()
        return result
    }
}