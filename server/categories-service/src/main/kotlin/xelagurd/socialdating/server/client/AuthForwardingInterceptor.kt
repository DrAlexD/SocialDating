package xelagurd.socialdating.server.client

import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import feign.RequestInterceptor
import feign.RequestTemplate
import xelagurd.socialdating.server.security.AuthHeaders

@Component
class AuthForwardingInterceptor : RequestInterceptor {

    override fun apply(template: RequestTemplate) {
        val attributes = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes ?: return
        val request = attributes.request

        request.getHeader(AuthHeaders.USER_ID)?.let { template.header(AuthHeaders.USER_ID, it) }
        request.getHeader(AuthHeaders.ROLE)?.let { template.header(AuthHeaders.ROLE, it) }
        request.getHeader(HttpHeaders.ACCEPT_LANGUAGE)?.let { template.header(HttpHeaders.ACCEPT_LANGUAGE, it) }
    }
}