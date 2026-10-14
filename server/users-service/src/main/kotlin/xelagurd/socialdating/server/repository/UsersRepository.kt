package xelagurd.socialdating.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import xelagurd.socialdating.server.model.User

@Repository
interface UsersRepository : JpaRepository<User, Int> {

    fun findByUsername(username: String): User?

    fun findByEmail(email: String): User?

    fun findAllByIdIn(userIds: List<Int>): List<User>

    @Query(
        """
        select id
        from users
        where (coalesce(:genders) is null or gender in (:genders))
          and age between :ageMin and :ageMax
          and (coalesce(:purposes) is null or purpose in (:purposes))
          and ((:cityEn is null and :cityRu is null)
            or lower(trim(city_en)) = lower(trim(:cityEn))
            or lower(trim(city_ru)) = lower(trim(:cityRu)))
        """,
        nativeQuery = true
    )
    fun findUserIds(
        genders: List<String>?,
        ageMin: Int,
        ageMax: Int,
        purposes: List<String>?,
        cityEn: String?,
        cityRu: String?
    ): List<Int>
}
