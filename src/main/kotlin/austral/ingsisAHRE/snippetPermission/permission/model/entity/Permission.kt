package austral.ingsisAHRE.snippetPermission.permission.model.entity

import austral.ingsisAHRE.snippetPermission.permission.model.enum.PermissionType
import austral.ingsisAHRE.snippetPermission.shared.baseModel.BaseModel
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
