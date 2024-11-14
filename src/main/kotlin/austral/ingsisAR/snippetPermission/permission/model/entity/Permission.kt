package austral.ingsisAR.snippetPermission.permission.model.entity

import austral.ingsisAR.snippetPermission.permission.model.enum.PermissionType
import austral.ingsisAR.snippetPermission.shared.baseModel.BaseModel
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Entity
data class Permission(
    val snippetId: String,
    val userId: String,
    @Enumerated(EnumType.STRING)
    val permissionType: PermissionType,
) : BaseModel()
