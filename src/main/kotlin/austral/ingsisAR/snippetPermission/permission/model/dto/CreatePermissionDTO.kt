package austral.ingsisAR.snippetPermission.permission.model.dto

import austral.ingsisAR.snippetPermission.permission.model.enum.PermissionType
import jakarta.validation.constraints.NotBlank

data class CreatePermissionDTO(
    @field:NotBlank(message = "SnippetId is required")
    val snippetId: String,
    @field:NotBlank(message = "UserId is required")
    val userId: String,

    val permissionType: PermissionType,
)
