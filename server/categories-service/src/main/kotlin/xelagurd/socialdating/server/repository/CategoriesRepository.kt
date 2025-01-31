package xelagurd.socialdating.server.repository

import org.springframework.data.repository.CrudRepository
import xelagurd.socialdating.server.model.Category

interface CategoriesRepository : CrudRepository<Category, Int>