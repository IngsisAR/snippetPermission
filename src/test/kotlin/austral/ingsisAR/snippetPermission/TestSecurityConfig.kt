package austral.ingsisAR.snippetPermission

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.mockito.Mockito.mock

@Configuration
@Profile("test")
class TestSecurityConfig {
    @Bean
    fun jwtDecoder(): JwtDecoder {
        return mock(JwtDecoder::class.java)
    }
}
