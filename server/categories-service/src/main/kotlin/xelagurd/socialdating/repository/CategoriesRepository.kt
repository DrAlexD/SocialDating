package xelagurd.socialdating.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import xelagurd.socialdating.dto.Category

interface CategoriesRepository : CrudRepository<Category, Long> {
    fun findAll(pageable: Pageable): Iterable<Category>
}