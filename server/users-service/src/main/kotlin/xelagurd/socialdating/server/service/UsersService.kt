package xelagurd.socialdating.server.service

import java.time.LocalDate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.details.UsersFilterDetails
import xelagurd.socialdating.server.model.dto.UserDto
import xelagurd.socialdating.server.model.enums.AppLanguage
import xelagurd.socialdating.server.model.enums.Purpose.ALL_AT_ONCE
import xelagurd.socialdating.server.repository.UsersRepository
import xelagurd.socialdating.server.utils.DateUtils.toEarliestBirthDate
import xelagurd.socialdating.server.utils.DateUtils.toLatestBirthDate

@Service
class UsersService(
    private val usersRepository: UsersRepository
) {

    fun getUser(userId: Int) =
        usersRepository.findByIdOrNull(userId)?.toUserDto()

    fun getUsers(userIds: List<Int>): List<UserDto> {
        val language = AppLanguage.current()

        return usersRepository.findAllByIdIn(userIds).map { it.toUserDto(language) }
    }

    fun getUserIds(usersFilterDetails: UsersFilterDetails): List<Int> {
        val sameCityUser = usersFilterDetails.sameCityUserId?.let {
            usersRepository.findByIdOrNull(it) ?: return listOf()
        }

        val today = LocalDate.now()

        return usersRepository.findUserIds(
            genders = usersFilterDetails.genders?.map { it.name },
            // the older users have the earlier birth dates, the absent borders don't limit the users at all
            birthDateMin = usersFilterDetails.ageMax?.toEarliestBirthDate(today),
            birthDateMax = usersFilterDetails.ageMin?.toLatestBirthDate(today),
            // the users who search for everything at once are suitable for any purpose
            purposes = usersFilterDetails.purposes?.plus(ALL_AT_ONCE)?.distinct()?.map { it.name },
            cityEn = sameCityUser?.cityEn,
            cityRu = sameCityUser?.cityRu
        )
    }
}