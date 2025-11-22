package xelagurd.socialdating.server.service

import java.lang.Long.numberOfTrailingZeros
import kotlin.math.min
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import xelagurd.socialdating.server.exception.NoDataFoundException
import xelagurd.socialdating.server.model.DefaultDataProperties.OPPOSITE_CATEGORIES_NUMBER
import xelagurd.socialdating.server.model.DefaultDataProperties.SIMILAR_CATEGORIES_NUMBER
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.model.additional.CategoryWithData
import xelagurd.socialdating.server.model.additional.DetailedSimilarCategory
import xelagurd.socialdating.server.model.additional.DetailedSimilarDefiningTheme
import xelagurd.socialdating.server.model.additional.DetailedSimilarUser
import xelagurd.socialdating.server.model.additional.SimilarCategory
import xelagurd.socialdating.server.model.additional.SimilarUser
import xelagurd.socialdating.server.model.enums.SimilarityType.Companion.fromSimilarityDiff
import xelagurd.socialdating.server.model.enums.SimilarityType.OPPOSITE
import xelagurd.socialdating.server.model.enums.SimilarityType.SIMILAR
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
    fun getSimilarUsers(userId: Int, categoryIds: List<Int>? = null): List<SimilarUser> {
        val currentUserCategoriesById = userCategoriesRepository
            .findCurrentUserCategories(userId, categoryIds)
            .associateBy { it.id }

        val anotherUsersCategories = userCategoriesRepository
            .findAnotherUsersCategories(userId, null, currentUserCategoriesById.keys.toList())

        return anotherUsersCategories
            .groupBy { it.userId }
            .mapNotNull { (anotherUserId, anotherUserCategories) ->
                val (similarNumberUser, oppositeNumberUser, categoriesWithSimilarity) =
                    calculateUserSimilarity(currentUserCategoriesById, anotherUserCategories)

                if (categoriesWithSimilarity.isNotEmpty() && similarNumberUser > oppositeNumberUser) {
                    SimilarUser(
                        id = anotherUserId,
                        similarNumber = similarNumberUser,
                        oppositeNumber = oppositeNumberUser,
                        differenceNumber = similarNumberUser - oppositeNumberUser,
                        similarCategories = categoriesWithSimilarity
                            .filter { it.differenceNumber > 0 }
                            .sortedByDescending { it.differenceNumber }
                            .take(SIMILAR_CATEGORIES_NUMBER),
                        oppositeCategories = categoriesWithSimilarity
                            .filter { it.differenceNumber < 0 }
                            .sortedBy { it.differenceNumber }
                            .take(OPPOSITE_CATEGORIES_NUMBER)
                    )
                } else null
            }
            .sortedByDescending { it.differenceNumber }
            .takeIf { it.isNotEmpty() }
            ?: throw NoDataFoundException("SimilarUsers didn't found for userId: $userId")
    }

    @Transactional(readOnly = true)
    fun getDetailedSimilarUser(currentUserId: Int, anotherUserId: Int): DetailedSimilarUser {
        val currentUserCategoriesById = userCategoriesRepository
            .findCurrentUserCategories(currentUserId, null)
            .associateBy { it.id }

        val anotherUserCategories = userCategoriesRepository
            .findAnotherUsersCategories(currentUserId, anotherUserId, currentUserCategoriesById.keys.toList())

        val (similarNumberUser, oppositeNumberUser, categoriesWithSimilarity) =
            calculateDetailedUserSimilarity(currentUserCategoriesById, anotherUserCategories)

        return DetailedSimilarUser(
            similarNumber = similarNumberUser,
            oppositeNumber = oppositeNumberUser,
            categories = categoriesWithSimilarity.associateBy { it.id }
        )
    }

    private fun calculateUserSimilarity(
        currentUserCategoriesById: Map<Int, CategoryWithData>,
        anotherUserCategories: List<UserCategory>
    ): Triple<Int, Int, List<SimilarCategory>> {
        val categoriesWithSimilarity = mutableListOf<SimilarCategory>()
        var similarNumberUser = 0
        var oppositeNumberUser = 0

        for (anotherUserCategory in anotherUserCategories) {
            val currentUserCategory = currentUserCategoriesById[anotherUserCategory.categoryId]!!

            val (similarNumberCategory, oppositeNumberCategory) =
                calculateCategorySimilarity(currentUserCategory, anotherUserCategory)

            if (similarNumberCategory != 0 || oppositeNumberCategory != 0) {
                similarNumberUser += similarNumberCategory
                oppositeNumberUser += oppositeNumberCategory

                categoriesWithSimilarity += SimilarCategory(
                    name = currentUserCategory.name,
                    differenceNumber = similarNumberCategory - oppositeNumberCategory
                )
            }
        }

        return Triple(similarNumberUser, oppositeNumberUser, categoriesWithSimilarity)
    }

    private fun calculateDetailedUserSimilarity(
        currentUserCategoriesById: Map<Int, CategoryWithData>,
        anotherUserCategories: List<UserCategory>
    ): Triple<Int, Int, List<DetailedSimilarCategory>> {
        val categoriesWithSimilarity = ArrayList<DetailedSimilarCategory>()
        var similarNumberUser = 0
        var oppositeNumberUser = 0

        for (anotherUserCategory in anotherUserCategories) {
            val currentUserCategory = currentUserCategoriesById[anotherUserCategory.categoryId]!!
            val definingThemesWithSimilarity = mutableListOf<DetailedSimilarDefiningTheme>()

            val (similarNumberCategory, oppositeNumberCategory) =
                calculateCategorySimilarity(
                    currentUserCategory,
                    anotherUserCategory,
                    definingThemesWithSimilarity
                )

            if (similarNumberCategory != 0 || oppositeNumberCategory != 0) {
                similarNumberUser += similarNumberCategory
                oppositeNumberUser += oppositeNumberCategory
                val differenceNumberCategory = similarNumberCategory - oppositeNumberCategory

                categoriesWithSimilarity += DetailedSimilarCategory(
                    id = currentUserCategory.id,
                    similarityType = fromSimilarityDiff(differenceNumberCategory),
                    similarNumber = similarNumberCategory,
                    oppositeNumber = oppositeNumberCategory,
                    differenceNumber = differenceNumberCategory,
                    definingThemes = definingThemesWithSimilarity.associateBy { it.id }
                )
            }
        }

        return Triple(similarNumberUser, oppositeNumberUser, categoriesWithSimilarity)
    }

    private fun calculateCategorySimilarity(
        currentUserCategory: CategoryWithData,
        anotherUserCategory: UserCategory,
        definingThemesWithSimilarity: MutableList<DetailedSimilarDefiningTheme>? = null
    ): Pair<Int, Int> {
        var similarNumberCategory = 0
        var oppositeNumberCategory = 0

        fun compareLists(
            list1: Array<Long>?,
            list2: Array<Long>?,
            isSimilar: Boolean
        ) {
            if (list1 == null || list2 == null) return

            val limit = min(list1.size, list2.size)

            for (i in 0 until limit) {
                val bitMask = list1[i] and list2[i]

                if (bitMask != 0L) {
                    val bitsNumber = bitMask.countOneBits()

                    if (isSimilar) {
                        similarNumberCategory += bitsNumber
                    } else {
                        oppositeNumberCategory += bitsNumber
                    }

                    definingThemesWithSimilarity?.addAll(
                        extractDefiningThemesIds(bitMask, bitsNumber, i)
                            .map {
                                DetailedSimilarDefiningTheme(
                                    id = it,
                                    similarityType = if (isSimilar) SIMILAR else OPPOSITE
                                )
                            }
                    )
                }
            }
        }

        compareLists(currentUserCategory.maintained, anotherUserCategory.maintained, isSimilar = true)
        compareLists(currentUserCategory.notMaintained, anotherUserCategory.notMaintained, isSimilar = true)

        compareLists(currentUserCategory.maintained, anotherUserCategory.notMaintained, isSimilar = false)
        compareLists(currentUserCategory.notMaintained, anotherUserCategory.maintained, isSimilar = false)

        return similarNumberCategory to oppositeNumberCategory
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
            result.add(base + numberOfTrailingZeros(lowBit) + 1)
            value -= lowBit
        }

        return result
    }
}