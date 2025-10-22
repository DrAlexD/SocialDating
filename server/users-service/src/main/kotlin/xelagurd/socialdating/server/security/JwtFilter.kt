package xelagurd.socialdating.server.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.filter.OncePerRequestFilter
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import xelagurd.socialdating.server.model.User

class JwtFilter(
    private val jwtUtils: JwtUtils,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7)

            try {
                val username = jwtUtils.getClaims(token).subject
                val userDetails = userDetailsService.loadUserByUsername(username)

                if (jwtUtils.isTokenValid(token, userDetails as User)) {
                    SecurityContextHolder.getContext().authentication =
                        UsernamePasswordAuthenticationToken.authenticated(
                            userDetails,
                            null,
                            userDetails.authorities
                        )
                }
            } catch (ex: Exception) {
                logger.warn("Exception during verifying the JWT token", ex)
            }
        }

        filterChain.doFilter(request, response)
    }
}
