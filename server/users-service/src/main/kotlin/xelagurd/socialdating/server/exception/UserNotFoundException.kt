package xelagurd.socialdating.server.exception

import org.springframework.security.core.userdetails.UsernameNotFoundException

class UserNotFoundException(
    username: String
) : UsernameNotFoundException("User is not found by username: $username"), LocalizedAuthException {
    override val messageKey = "error.user.notFound"
}
