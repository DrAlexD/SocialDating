package xelagurd.socialdating.repository

import org.springframework.data.repository.CrudRepository
import xelagurd.socialdating.dto.Category

interface CategoriesRepository : CrudRepository<Category, Int>