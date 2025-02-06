package xelagurd.socialdating.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import xelagurd.socialdating.server.model.Category

interface CategoriesRepository : JpaRepository<Category, Int>