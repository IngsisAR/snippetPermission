package austral.ingsisAHRE.snippetPermission

import austral.ingsisAHRE.snippetPermission.permission.model.dto.CreatePermissionDTO
import austral.ingsisAHRE.snippetPermission.permission.model.entity.Permission
import austral.ingsisAHRE.snippetPermission.permission.model.enum.PermissionType
import austral.ingsisAHRE.snippetPermission.permission.repository.PermissionRepository
import austral.ingsisAHRE.snippetPermission.permission.service.PermissionService
import austral.ingsisAHRE.snippetPermission.shared.exception.ConflictException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class PermissionServiceTest {
    @Mock
    private lateinit var permissionRepository: PermissionRepository

    @InjectMocks
    private lateinit var permissionService: PermissionService

    @Test
    fun createPermission() {
        val createPermissionDTO = CreatePermissionDTO("snippet1", "user1", PermissionType.OWNER)

        permissionService.createPermission(createPermissionDTO)

        verify(permissionRepository).save(any(Permission::class.java))
    }

    @Test
    fun deleteSnippetPermissions() {
        val snippetId = "snippet1"

        permissionService.deleteSnippetPermissions(snippetId)

        verify(permissionRepository).deleteAllBySnippetId(snippetId)
    }

    @Test
    fun createPermissionThrowsConflictExceptionWhenOwnerExists() {
        val createPermissionDTO = CreatePermissionDTO("snippet1", "user1", PermissionType.OWNER)
        val existingPermission = Permission("snippet1", "user1", PermissionType.OWNER)

        `when`(permissionRepository.findBySnippetIdAndPermissionType("snippet1", PermissionType.OWNER))
            .thenReturn(existingPermission)

        val exception = assertThrows(ConflictException::class.java) {
            permissionService.createPermission(createPermissionDTO)
        }

        assertEquals("Snippet already has an owner", exception.message)
        verify(permissionRepository, never()).save(any(Permission::class.java))
    }

    @Test
    fun createPermissionThrowsConflictExceptionWhenPermissionAlreadyExists() {
        val createPermissionDTO = CreatePermissionDTO("snippet1", "user1", PermissionType.OWNER)
        val existingPermissions = listOf(Permission("snippet1", "user1", PermissionType.SHARED))

        `when`(permissionRepository.findAllBySnippetIdAndUserId("snippet1", "user1"))
            .thenReturn(existingPermissions)

        val exception = assertThrows(ConflictException::class.java) {
            permissionService.createPermission(createPermissionDTO)
        }

        assertEquals("Permission already exists", exception.message)
        verify(permissionRepository, never()).save(any(Permission::class.java))
    }


    @Test
    fun getSnippetAuthorReturnsCorrectAuthorId() {
        val snippetId = "snippet1"
        val expectedUserId = "user1"
        val permission = Permission(snippetId, expectedUserId, PermissionType.OWNER)

        `when`(permissionRepository.findFirstBySnippetIdAndPermissionType(snippetId, PermissionType.OWNER))
            .thenReturn(permission)

        val authorId = permissionService.getSnippetAuthor(snippetId)

        assertEquals(expectedUserId, authorId)
    }

    @Test
    fun deleteSnippetPermissionsForTheGivenSnippetId() {
        val snippetId = "testSnippetId"

        permissionService.deleteSnippetPermissions(snippetId)

        verify(permissionRepository).deleteAllBySnippetId(snippetId)
    }

    @Test
    fun successfullyCreatePermission() {
        val createPermissionDTO = CreatePermissionDTO("newSnippet", "newUser", PermissionType.OWNER)
        val emptyPermissionsList = emptyList<Permission>()

        `when`(permissionRepository.findAllBySnippetIdAndUserId(createPermissionDTO.snippetId, createPermissionDTO.userId))
            .thenReturn(emptyPermissionsList)

        permissionService.createPermission(createPermissionDTO)

        verify(permissionRepository).save(any(Permission::class.java))
    }

    @Test
    fun getSnippetAuthor() {
        val snippetId = "testSnippetId"
        val expectedUserId = "testUserId"
        val permission = Permission(snippetId, expectedUserId, PermissionType.OWNER)

        `when`(permissionRepository.findFirstBySnippetIdAndPermissionType(snippetId, PermissionType.OWNER))
            .thenReturn(permission)

        val authorId = permissionService.getSnippetAuthor(snippetId)

        assertEquals(expectedUserId, authorId)
    }

    @Test
    fun getAllUserPermissions() {
        val userId = "testUserId"
        val pageNumber = 0
        val pageSize = 10
        val pagination = PageRequest.of(pageNumber, pageSize)
        val permissions = listOf(
            Permission("snippet1", "user1", PermissionType.OWNER).apply { id = "1"},
            Permission("snippet2", "user2", PermissionType.OWNER).apply { id = "1"},
        )
        val total = permissions.size

        `when`(permissionRepository.findAllByUserId(userId, pagination)).thenReturn(PageImpl(permissions))

        val response = permissionService.getAllUserPermissions(userId, pageNumber, pageSize)

        assertEquals(total, response.total)
        assertEquals(permissions.size, response.permissions.size)
        assertTrue(response.permissions.isNotEmpty())
    }

    @Test
    fun getAllUserPermissionsWithTypeSHARED() {
        val userId = "testUserId"
        val pageNumber = 0
        val pageSize = 10
        val pagination = PageRequest.of(pageNumber, pageSize)
        val permissions = listOf(
            Permission("snippet1", userId, PermissionType.SHARED).apply { id = "1"},
        )
        val total = permissions.size

        `when`(permissionRepository.findAllByUserId(userId, pagination)).thenReturn(PageImpl(permissions))
        `when`(permissionRepository.findBySnippetIdAndPermissionType("snippet1", PermissionType.OWNER)).thenReturn(Permission("snippet1", "user2", PermissionType.OWNER))

        val response = permissionService.getAllUserPermissions(userId, pageNumber, pageSize)

        assertEquals(total, response.total)
        assertEquals("user2", response.permissions.first().authorId)
    }
}
