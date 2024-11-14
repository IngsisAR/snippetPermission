package austral.ingsisAR.snippetPermission.permission.service

import austral.ingsisAR.snippetPermission.permission.model.dto.CreatePermissionDTO
import austral.ingsisAR.snippetPermission.permission.model.dto.GetPaginatedPermissionsDTO
import austral.ingsisAR.snippetPermission.permission.model.dto.GetPermissionDTO
import austral.ingsisAR.snippetPermission.permission.model.entity.Permission
import austral.ingsisAR.snippetPermission.permission.model.enum.PermissionType
import austral.ingsisAR.snippetPermission.permission.repository.PermissionRepository
import austral.ingsisAR.snippetPermission.shared.exception.ConflictException
import jakarta.transaction.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class PermissionService(
    @Autowired
    private val permissionRepository: PermissionRepository,
) {
    private val logger: Logger = LoggerFactory.getLogger(PermissionService::class.java)

    fun createPermission(permission: CreatePermissionDTO) {
        logger.info(
            "Creating permission for User(${permission.userId}): (snippetId: ${permission.snippetId}, permissionType: ${permission.permissionType})",
        )
        if (permissionRepository.findAllBySnippetIdAndUserId(permission.snippetId, permission.userId).isNotEmpty()) {
            logger.info("User(${permission.userId}) already has permission for Snippet(${permission.snippetId})")
            throw ConflictException("Permission already exists")
        }

        if (permissionRepository.findBySnippetIdAndPermissionType(permission.snippetId, PermissionType.OWNER)!=null && permission.permissionType == PermissionType.OWNER) {
            logger.info("Snippet(${permission.snippetId}) already has an owner")
            throw ConflictException("Snippet already has an owner")
        }

        permissionRepository.save(
            Permission(
                snippetId = permission.snippetId,
                userId = permission.userId,
                permissionType = permission.permissionType,
            ),
        )
        logger.info("Permission created for User(${permission.userId}) on Snippet(${permission.snippetId})")
    }

    fun getSnippetAuthor(snippetId: String): String? {
        logger.info("Getting author of Snippet($snippetId)")
        return permissionRepository.findFirstBySnippetIdAndPermissionType(snippetId, PermissionType.OWNER)?.userId
    }

    fun getAllUserPermissions(
        userId: String,
        pageNumber: Int,
        pageSize: Int,
    ): GetPaginatedPermissionsDTO {
        logger.info("Getting all permissions for User($userId) with pageNumber=$pageNumber, pageSize=$pageSize")
        val pagination = PageRequest.of(pageNumber, pageSize)
        val permissionsPage = permissionRepository.findAllByUserId(userId, pagination)
        val permissions = permissionsPage.content

        val response: List<GetPermissionDTO> =
            permissions.map {
                if (it.permissionType == PermissionType.OWNER) {
                    GetPermissionDTO(
                        id = it.id!!,
                        snippetId = it.snippetId,
                        authorId = it.userId,
                    )
                } else {
                    GetPermissionDTO(
                        id = it.id!!,
                        snippetId = it.snippetId,
                        authorId = permissionRepository.findBySnippetIdAndPermissionType(it.snippetId, PermissionType.OWNER)!!.userId,
                    )
                }
            }
        logger.info("Returning User($userId) permissions")
        return GetPaginatedPermissionsDTO(response, permissionsPage.totalElements.toInt())
    }

    @Transactional
    fun deleteSnippetPermissions(snippetId: String) {
        logger.info("Deleting all permissions for Snippet($snippetId)")
        permissionRepository.deleteAllBySnippetId(snippetId)
    }


}
