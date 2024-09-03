package austral.ingsisAR.snippetPermission.permission.controller

import austral.ingsisAR.snippetPermission.permission.model.dto.CreatePermissionDTO
import austral.ingsisAR.snippetPermission.permission.model.dto.GetPaginatedPermissionsDTO
import austral.ingsisAR.snippetPermission.permission.service.PermissionService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/permissions")
@Validated
class PermissionController(
    @Autowired
    private val permissionService: PermissionService,
) {
    @PostMapping
    fun createPermission(
        @Valid @RequestBody permission: CreatePermissionDTO,
    ): ResponseEntity<Void> {
        permissionService.createPermission(permission)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/author/{snippetId}")
    fun getSnippetAuthor(
        @PathVariable("snippetId") snippetId: String,
    ): ResponseEntity<String> {
        return ResponseEntity.ok(permissionService.getSnippetAuthor(snippetId))
    }

    @GetMapping("/all")
    fun getAllUserPermissions(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestParam("page_number") pageNumber: Int,
        @RequestParam("page_size") pageSize: Int,
    ): ResponseEntity<GetPaginatedPermissionsDTO> {
        val truePageNumber = if (pageNumber - 1 < 0) 0 else pageNumber-1
        return ResponseEntity.ok(permissionService.getAllUserPermissions(jwt.subject, truePageNumber, pageSize))
    }

    @DeleteMapping("/all/{snippetId}")
    fun deleteSnippetPermissions(
        @PathVariable("snippetId") snippetId: String,
    ): ResponseEntity<Void> {
        permissionService.deleteSnippetPermissions(snippetId)
        return ResponseEntity.ok().build()
    }
}
