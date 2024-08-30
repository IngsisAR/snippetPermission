package austral.ingsisAHRE.snippetPermission.permission.repository

import austral.ingsisAHRE.snippetPermission.permission.model.entity.Permission
import austral.ingsisAHRE.snippetPermission.permission.model.enum.PermissionType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface PermissionRepository : JpaRepository<Permission, String> {
    fun findAllBySnippetIdAndUserId(
        snippetId: String,
        userId: String,
    ): List<Permission>
    fun deleteAllBySnippetId(snippetId: String)
    fun findFirstBySnippetIdAndPermissionType(
        snippetId: String, permissionType: PermissionType,
    ): Permission?
    fun findAllByUserId(
        userId: String,
        pageable: Pageable,
    ): Page<Permission>
    fun findBySnippetIdAndPermissionType(snippetId: String, permissionType: PermissionType): Permission?
}
