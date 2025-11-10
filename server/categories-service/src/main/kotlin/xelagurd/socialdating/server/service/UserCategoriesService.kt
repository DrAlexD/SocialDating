package xelagurd.socialdating.server.service

import java.lang.Long.numberOfTrailingZeros
import kotlin.math.min
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import xelagurd.socialdating.server.exception.NoDataFoundException
import xelagurd.socialdating.server.model.DefaultDataProperties.OPPOSITE_CATEGORIES_NUMBER
import xelagurd.socialdating.server.model.DefaultDataProperties.SIMILAR_CATEGORIES_NUMBER
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.model.additional.CategoryWithMaintainedLists
import xelagurd.socialdating.server.model.additional.CategoryWithSimilarityDiff
import xelagurd.socialdating.server.model.additional.SimilarCategory
import xelagurd.socialdating.server.model.additional.SimilarUser
import xelagurd.socialdating.server.model.additional.UserWithSimilarity
import xelagurd.socialdating.server.repository.UserCategoriesRepository

@Service
class UserCategoriesService(
    private val userCategoriesRepository: UserCategoriesRepository
) {

    fun getUserCategories(userId: Int): List<UserCategory> {
        return userCategoriesRepository.findAllByUserId(userId).takeIf { it.isNotEmpty() }
            ?: throw NoDataFoundException("UserCategories didn't found for userId: $userId")
    }

    fun addUserCategory(userCategory: UserCategory): UserCategory {
        return userCategoriesRepository.save(userCategory)
    }

    fun getUserCategory(userId: Int, categoryId: Int): UserCategory? {
        return userCategoriesRepository.findByUserIdAndCategoryId(userId, categoryId)
    }

    @Transactional(readOnly = true)
    fun getUsersWithSimilarity(userId: Int, categoryIds: List<Int>? = null): List<UserWithSimilarity> {
        val currentCategoriesById = userCategoriesRepository
            .findCategoriesForCurrentUser(userId, categoryIds)
            .associateBy { it.id }

        val anotherUsersCategories = userCategoriesRepository
            .findAnotherUsersCategories(userId, null, currentCategoriesById.keys.toList())

        return anotherUsersCategories
            .groupBy { it.userId }
            .mapNotNull { (anotherUserId, anotherCategories) ->
                val (similarityNumberUser, oppositeNumberUser, categoriesWithSimilarity) =
                    calculateUserSimilarity(currentCategoriesById, anotherCategories)

                if (categoriesWithSimilarity.isNotEmpty() && similarityNumberUser > oppositeNumberUser) {
                    val (similarCategories, oppositeCategories) = categoriesWithSimilarity
                        .partition { it.differenceNumber > 0 }

                    UserWithSimilarity(
                        id = anotherUserId,
                        similarityNumber = similarityNumberUser,
                        oppositeNumber = oppositeNumberUser,
                        differenceNumber = similarityNumberUser - oppositeNumberUser,
                        similarCategories = similarCategories
                            .sortedByDescending { it.differenceNumber }
                            .take(SIMILAR_CATEGORIES_NUMBER),
                        oppositeCategories = oppositeCategories
                            .sortedBy { it.differenceNumber }
                            .take(OPPOSITE_CATEGORIES_NUMBER)
                    )
                } else null
            }
            .sortedByDescending { it.differenceNumber }
    }

    @Transactional(readOnly = true)
    fun getSimilarUser(currentUserId: Int, anotherUserId: Int): SimilarUser {
        val currentUserCategories = userCategoriesRepository
            .findCategoriesForCurrentUser(currentUserId, null)
            .associateBy { it.id }

        val anotherUserCategories = userCategoriesRepository
            .findAnotherUsersCategories(currentUserId, anotherUserId, currentUserCategories.keys.toList())

        val (similarityNumberUser, oppositeNumberUser, categoriesWithSimilarity) =
            calculateDetailedUserSimilarity(currentUserCategories, anotherUserCategories)

        val (similarCategories, oppositeCategories) = categoriesWithSimilarity
            .partition { it.differenceNumber > 0 }

        return SimilarUser(
            similarityNumber = similarityNumberUser,
            oppositeNumber = oppositeNumberUser,
            similarCategories = similarCategories.sortedByDescending { it.differenceNumber },
            oppositeCategories = oppositeCategories.sortedBy { it.differenceNumber }
        )
    }

    private fun calculateUserSimilarity(
        currentCategoriesById: Map<Int, CategoryWithMaintainedLists>,
        anotherCategories: List<UserCategory>
    ): Triple<Int, Int, List<CategoryWithSimilarityDiff>> {
        val categoriesWithSimilarity = mutableListOf<CategoryWithSimilarityDiff>()
        var similarityNumberUser = 0
        var oppositeNumberUser = 0

        for (anotherCategory in anotherCategories) {
            val currentCategory = currentCategoriesById[anotherCategory.categoryId]!!

            val (similarityNumberCategory, oppositeNumberCategory) =
                calculateCategorySimilarity(currentCategory, anotherCategory)

            if (similarityNumberCategory != 0 || oppositeNumberCategory != 0) {
                similarityNumberUser += similarityNumberCategory
                oppositeNumberUser += oppositeNumberCategory

                categoriesWithSimilarity += CategoryWithSimilarityDiff(
                    name = currentCategory.name,
                    differenceNumber = similarityNumberCategory - oppositeNumberCategory
                )
            }
        }

        return Triple(similarityNumberUser, oppositeNumberUser, categoriesWithSimilarity)
    }

    private fun calculateDetailedUserSimilarity(
        currentCategoriesById: Map<Int, CategoryWithMaintainedLists>,
        anotherCategories: List<UserCategory>
    ): Triple<Int, Int, List<SimilarCategory>> {
        val categoriesWithSimilarity = ArrayList<SimilarCategory>()
        var similarityNumberUser = 0
        var oppositeNumberUser = 0

        for (anotherCategory in anotherCategories) {
            val currentCategory = currentCategoriesById[anotherCategory.categoryId]!!
            val similarDefiningThemesIds = mutableListOf<Int>()
            val oppositeDefiningThemesIds = mutableListOf<Int>()

            val (similarityNumberCategory, oppositeNumberCategory) =
                calculateCategorySimilarity(
                    currentCategory,
                    anotherCategory,
                    similarDefiningThemesIds,
                    oppositeDefiningThemesIds
                )

            if (similarityNumberCategory != 0 || oppositeNumberCategory != 0) {
                similarityNumberUser += similarityNumberCategory
                oppositeNumberUser += oppositeNumberCategory

                categoriesWithSimilarity += SimilarCategory(
                    id = currentCategory.id,
                    similarityNumber = similarityNumberCategory,
                    oppositeNumber = oppositeNumberCategory,
                    differenceNumber = similarityNumberCategory - oppositeNumberCategory,
                    similarDefiningThemesIds = similarDefiningThemesIds,
                    oppositeDefiningThemesIds = oppositeDefiningThemesIds
                )
            }
        }

        return Triple(similarityNumberUser, oppositeNumberUser, categoriesWithSimilarity)
    }

    private fun calculateCategorySimilarity(
        currentCategory: CategoryWithMaintainedLists,
        anotherCategory: UserCategory,
        similarDefiningThemesIds: MutableList<Int>? = null,
        oppositeDefiningThemesIds: MutableList<Int>? = null
    ): Pair<Int, Int> {
        var similarityNumberCategory = 0
        var oppositeNumberCategory = 0

        fun compareLists(
            list1: Array<Long>?,
            list2: Array<Long>?,
            isSimilarity: Boolean
        ) {
            if (list1 == null || list2 == null) return

            val limit = min(list1.size, list2.size)

            for (i in 0 until limit) {
                val bitMask = list1[i] and list2[i]

                if (bitMask != 0L) {
                    val bitsNumber = bitMask.countOneBits()

                    if (isSimilarity) {
                        similarityNumberCategory += bitsNumber
                        similarDefiningThemesIds?.addAll(extractDefiningThemesIds(bitMask, bitsNumber, i))
                    } else {
                        oppositeNumberCategory += bitsNumber
                        oppositeDefiningThemesIds?.addAll(extractDefiningThemesIds(bitMask, bitsNumber, i))
                    }
                }
            }
        }

        compareLists(currentCategory.maintained, anotherCategory.maintained, isSimilarity = true)
        compareLists(currentCategory.notMaintained, anotherCategory.notMaintained, isSimilarity = true)

        compareLists(currentCategory.maintained, anotherCategory.notMaintained, isSimilarity = false)
        compareLists(currentCategory.notMaintained, anotherCategory.maintained, isSimilarity = false)

        return similarityNumberCategory to oppositeNumberCategory
    }

    private fun extractDefiningThemesIds(
        bitMask: Long,
        bitsNumber: Int,
        listIndex: Int
    ): List<Int> {
        var value = bitMask
        val result = ArrayList<Int>(bitsNumber)
        val base = listIndex * Long.SIZE_BITS

        while (value != 0L) {
            val lowBit = value and -value
            result.add(base + numberOfTrailingZeros(lowBit))
            value -= lowBit
        }

        return result
    }
}