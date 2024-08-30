package austral.ingsisAHRE.snippetPermission.shared.exception

import org.springframework.http.HttpStatus

open class HttpException(val status: HttpStatus, message: String) : RuntimeException(message)

class ConflictException(message: String) : HttpException(HttpStatus.CONFLICT, message)
