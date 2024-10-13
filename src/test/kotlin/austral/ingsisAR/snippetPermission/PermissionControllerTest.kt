package austral.ingsisAR.snippetPermission

import austral.ingsisAR.snippetPermission.permission.model.dto.CreatePermissionDTO
import austral.ingsisAR.snippetPermission.permission.model.dto.GetPaginatedPermissionsDTO
import austral.ingsisAR.snippetPermission.permission.model.dto.GetPermissionDTO
import austral.ingsisAR.snippetPermission.permission.model.enum.PermissionType
import austral.ingsisAR.snippetPermission.permission.service.PermissionService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

@SpringBootTest
@ContextConfiguration
@AutoConfigureMockMvc
class PermissionControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var permissionService: PermissionService

    @MockBean
    private lateinit var jwtDecoder: JwtDecoder

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val testJwt = "test"

    @BeforeEach
    fun logIn() {

        val jwt = Jwt.withTokenValue(testJwt)
            .header("alg", "RS256")
            .claim("email", "test@test.com")
            .subject("test@test.com")
            .build()
        `when`(jwtDecoder.decode(testJwt)).thenReturn(jwt)
    }

    @Test
    @WithMockUser(username = "testUser", authorities = ["SCOPE_write:snippets"])
    fun createPermission() {
        val createPermissionDTO = CreatePermissionDTO("snippet1", "user1", PermissionType.OWNER)

        mockMvc.perform(
            post("/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPermissionDTO))
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk)
                .andReturn()

        verify(permissionService).createPermission(createPermissionDTO)
    }

    @Test
    @WithMockUser(username = "testUser", authorities = ["SCOPE_write:snippets"])
    fun getSnippetAuthor() {
        val snippetId = "snippet1"
        val authorId = "user1"

        `when`(permissionService.getSnippetAuthor(snippetId)).thenReturn(authorId)

        mockMvc.perform(
            get("/permissions/author/$snippetId")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andReturn()
    }


    @Test
    @WithMockUser(username = "testUser", authorities = ["SCOPE_write:snippets"])
    fun deleteSnippetPermissions() {
        val snippetId = "snippet1"

        mockMvc.perform(
            delete("/permissions/all/$snippetId")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andReturn()

        verify(permissionService).deleteSnippetPermissions(snippetId)
    }

////    Fix the use of jwt in the test
@Test
@WithMockUser(username = "testUser", authorities = ["SCOPE_write:snippets"])
fun getAllUserPermissions_withMultiplePermissions() {
    val pageNumber = 0
    val pageSize = 2
    val permissions = listOf(
        GetPermissionDTO("1", "snippet1", "testUser"),
        GetPermissionDTO("1", "snippet2", "testUser")
    )
    val paginatedPermissionsDTO = GetPaginatedPermissionsDTO(permissions, permissions.size)

    val jwt = Jwt.withTokenValue("token")
        .header("alg", "none")
        .claim("sub", "testUser")
        .claim("scope", "write:snippets")
        .build()

    val authorities = listOf(SimpleGrantedAuthority("SCOPE_write:snippets"))
    val authentication = JwtAuthenticationToken(jwt, authorities)
    SecurityContextHolder.getContext().authentication = authentication

    `when`(permissionService.getAllUserPermissions("testUser", pageNumber, pageSize)).thenReturn(paginatedPermissionsDTO)

    mockMvc.perform(
        get("/permissions/all")
            .param("page_number", pageNumber.toString())
            .param("page_size", pageSize.toString())
    )
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk)
        .andExpect(content().json(objectMapper.writeValueAsString(paginatedPermissionsDTO)))
        .andReturn()
}

    @Test
    fun `request fails with no authorization`() {
        mockMvc.perform(
            get("/permissions/all")
                .header("Authorization", "Bearer Bad Token")
                .param("page_number", "1")
                .param("page_size", "10")
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(username = "testUser", authorities = ["SCOPE_write:snippets"])
    fun `request fails with invalid params`() {
        mockMvc.perform(
            get("/permissions/all")
                .param("page_size", "10")
        )
            .andExpect(status().isBadRequest)
    }
}
