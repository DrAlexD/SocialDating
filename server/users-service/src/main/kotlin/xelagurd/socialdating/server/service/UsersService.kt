package xelagurd.socialdating.server.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.DefaultDataProperties.AGE_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.AGE_MIN
import xelagurd.socialdating.server.model.details.UsersFilterDetails
import xelagurd.socialdating.server.model.dto.UserDto
import xelagurd.socialdating.server.model.enums.AppLanguage
import xelagurd.socialdating.server.model.enums.Purpose.ALL_AT_ONCE
import xelagurd.socialdating.server.repository.UsersRepository

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

        return usersRepository.findUserIds(
            genders = usersFilterDetails.genders?.map { it.name },
            ageMin = usersFilterDetails.ageMin ?: AGE_MIN,
            ageMax = usersFilterDetails.ageMax ?: AGE_MAX,
            // the users who search for everything at once are suitable for any purpose
            purposes = usersFilterDetails.purposes?.plus(ALL_AT_ONCE)?.distinct()?.map { it.name },
            cityEn = sameCityUser?.cityEn,
            cityRu = sameCityUser?.cityRu
        )
    }
}