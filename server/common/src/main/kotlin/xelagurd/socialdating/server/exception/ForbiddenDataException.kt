package xelagurd.socialdating.server.exception

import org.springframework.security.access.AccessDeniedException

class ForbiddenDataException(
    val messageKey: String
) : AccessDeniedException(messageKey)
