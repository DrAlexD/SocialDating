package xelagurd.socialdating.server.utils

import org.springframework.security.core.context.SecurityContextHolder
import xelagurd.socialdating.server.exception.ForbiddenDataException

object SecurityUtils {

    fun checkCurrentUserAuth(currentUserId: Int) {
        val authenticatedUserId = SecurityContextHolder.getContext().authentication.principal as Int

        if (currentUserId != authenticatedUserId)
            throw ForbiddenDataException("error.accessDenied.anotherUser")
    }

}
