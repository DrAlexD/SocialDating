package xelagurd.socialdating.server.service

import java.lang.Long.numberOfTrailingZeros
import kotlin.math.min
import org.springframework.stereotype.Service
import xelagurd.socialdating.server.client.UsersServiceClient
import xelagurd.socialdating.server.model.DefaultDataProperties.OPPOSITE_CATEGORIES_NUMBER
import xelagurd.socialdating.server.model.DefaultDataProperties.SIMILAR_CATEGORIES_NUMBER
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.model.additional.SimilarUserData
import xelagurd.socialdating.server.model.additional.UserCategoryData
import xelagurd.socialdating.server.model.dto.DetailedSimilarCategoryDto
import xelagurd.socialdating.server.model.dto.DetailedSimilarDefiningThemeDto
import xelagurd.socialdating.server.model.dto.DetailedSimilarUserDto
import xelagurd.socialdating.server.model.dto.SimilarCategoryDto
import xelagurd.socialdating.server.model.dto.SimilarUserDto
import xelagurd.socialdating.server.model.dto.UserCategoryDto
import xelagurd.socialdating.server.model.enums.AppLanguage
import xelagurd.socialdating.server.model.enums.SimilarityType.Companion.fromSimilarityDiff
import xelagurd.socialdating.server.model.enums.SimilarityType.OPPOSITE
import xelagurd.socialdating.server.model.enums.SimilarityType.SIMILAR
import xelagurd.socialdating.server.repository.UserCategoriesRepository
import xelagurd.socialdating.server.utils.SecurityUtils.checkCurrentUserAuth

@Service
class UserCategoriesService(
    private val userCategoriesRepository: UserCategoriesRepository,
    private val usersServiceClient: UsersServiceClient
) {

    fun getUserCategories(userId: Int): List<UserCategoryDto> =
        userCategoriesRepository.findAllByUserId(userId).map { it.toUserCategoryDto() }

    fun addUserCategory(userCategory: UserCategory) =
        userCategoriesRepository.save(userCategory)

    fun getUserCategory(userId: Int, categoryId: Int) =
        userCategoriesRepository.findByUserIdAndCategoryId(userId, categoryId)

    fun getSimilarUsers(
        currentUserId: Int,
        categoryIds: List<Int>? = null
    ): List<SimilarUserDto> {
        checkCurrentUserAuth(currentUserId)

        val language = AppLanguage.current()

        val currentUserCategoriesById = userCategoriesRepository
            .findCurrentUserCategories(currentUserId, categoryIds)
            .associateBy { it.id }

        val anotherUsersCategories = userCategoriesRepository
            .findAnotherUsersCategories(currentUserId, null, currentUserCategoriesById.keys.toList())

        val similarUsers = anotherUsersCategories
            .groupBy { it.userId }
            .mapNotNull { (anotherUserId, anotherUserCategories) ->
                val (similarNumberUser, oppositeNumberUser, categoriesWithSimilarity) =
                    calculateUserSimilarity(currentUserCategoriesById, anotherUserCategories, language)

                if (categoriesWithSimilarity.isNotEmpty() && similarNumberUser > oppositeNumberUser) {
                    SimilarUserData(
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

        if (similarUsers.isEmpty()) return emptyList()

        val usersById = usersServiceClient
            .getUsers(similarUsers.map { it.id })
            .associateBy { it.id }

        return similarUsers.mapNotNull { it.toSimilarUserDto(usersById[it.id]) }
    }

    fun getDetailedSimilarUser(
        currentUserId: Int,
        anotherUserId: Int
    ): DetailedSimilarUserDto {
        checkCurrentUserAuth(currentUserId)

        val currentUserCategoriesById = userCategoriesRepository
            .findCurrentUserCategories(currentUserId, null)
            .associateBy { it.id }

        val anotherUserCategories = userCategoriesRepository
            .findAnotherUsersCategories(currentUserId, anotherUserId, currentUserCategoriesById.keys.toList())

        val (similarNumberUser, oppositeNumberUser, categoriesWithSimilarity) =
            calculateDetailedUserSimilarity(currentUserCategoriesById, anotherUserCategories)

        return DetailedSimilarUserDto(
            similarNumber = similarNumberUser,
            oppositeNumber = oppositeNumberUser,
            categories = categoriesWithSimilarity.associateBy { it.id }
        )
    }

    private fun calculateUserSimilarity(
        currentUserCategoriesById: Map<Int, UserCategoryData>,
        anotherUserCategories: List<UserCategory>,
        language: AppLanguage
    ): Triple<Int, Int, List<SimilarCategoryDto>> {
        val categoriesWithSimilarity = mutableListOf<SimilarCategoryDto>()
        var similarNumberUser = 0
        var oppositeNumberUser = 0

        for (anotherUserCategory in anotherUserCategories) {
            val currentUserCategory = currentUserCategoriesById[anotherUserCategory.categoryId]!!

            val (similarNumberCategory, oppositeNumberCategory) =
                calculateCategorySimilarity(currentUserCategory, anotherUserCategory)

            if (similarNumberCategory != 0 || oppositeNumberCategory != 0) {
                similarNumberUser += similarNumberCategory
                oppositeNumberUser += oppositeNumberCategory

                categoriesWithSimilarity += SimilarCategoryDto(
                    name = currentUserCategory.getLocalizedName(language),
                    differenceNumber = similarNumberCategory - oppositeNumberCategory
                )
            }
        }

        return Triple(similarNumberUser, oppositeNumberUser, categoriesWithSimilarity)
    }

    private fun calculateDetailedUserSimilarity(
        currentUserCategoriesById: Map<Int, UserCategoryData>,
        anotherUserCategories: List<UserCategory>
    ): Triple<Int, Int, List<DetailedSimilarCategoryDto>> {
        val categoriesWithSimilarity = ArrayList<DetailedSimilarCategoryDto>()
        var similarNumberUser = 0
        var oppositeNumberUser = 0

        for (anotherUserCategory in anotherUserCategories) {
            val currentUserCategory = currentUserCategoriesById[anotherUserCategory.categoryId]!!
            val definingThemesWithSimilarity = mutableListOf<DetailedSimilarDefiningThemeDto>()

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

                categoriesWithSimilarity += DetailedSimilarCategoryDto(
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
        currentUserCategory: UserCategoryData,
        anotherUserCategory: UserCategory,
        definingThemesWithSimilarity: MutableList<DetailedSimilarDefiningThemeDto>? = null
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

                    when {
                        isSimilar -> similarNumberCategory += bitsNumber
                        else -> oppositeNumberCategory += bitsNumber
                    }

                    definingThemesWithSimilarity?.addAll(
                        extractDefiningThemesIds(bitMask, bitsNumber, i)
                            .map {
                                DetailedSimilarDefiningThemeDto(
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