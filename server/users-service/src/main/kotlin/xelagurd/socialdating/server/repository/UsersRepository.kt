package xelagurd.socialdating.server.repository

import java.time.LocalDate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import xelagurd.socialdating.server.model.User

@Repository
interface UsersRepository : JpaRepository<User, Int> {

    fun findByUsername(username: String): User?

    // the nicknames are compared with their case, unlike Telegram, where they are case-insensitive,
    // so the nicknames which differ only in the case are considered as the different ones
    fun findByTelegram(telegram: String): User?

    fun findAllByIdIn(userIds: List<Int>): List<User>

    @Query(
        """
        select id
        from users
        where (coalesce(:genders) is null or gender in (:genders))
          and (cast(:birthDateMin as date) is null or birth_date >= cast(:birthDateMin as date))
          and (cast(:birthDateMax as date) is null or birth_date <= cast(:birthDateMax as date))
          and (coalesce(:purposes) is null or purpose in (:purposes))
          and ((:cityEn is null and :cityRu is null)
            or lower(trim(city_en)) = lower(trim(:cityEn))
            or lower(trim(city_ru)) = lower(trim(:cityRu)))
        """,
        nativeQuery = true
    )
    fun findUserIds(
        genders: List<String>?,
        birthDateMin: LocalDate?,
        birthDateMax: LocalDate?,
        purposes: List<String>?,
        cityEn: String?,
        cityRu: String?
    ): List<Int>
}
