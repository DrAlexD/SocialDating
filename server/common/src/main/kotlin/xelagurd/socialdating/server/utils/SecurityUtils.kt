package xelagurd.socialdating.server.utils

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder

object SecurityUtils {

    fun checkCurrentUserAuth(currentUserId: Int) {
        val authenticatedUserId = SecurityContextHolder.getContext().authentication.principal as Int

        if (currentUserId != authenticatedUserId)
            throw AccessDeniedException("Access denied")
    }

}
