package com.taskflow.backend

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper
) {

    private fun loginAsDemo(password: String = "Demo@123") =
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"email":"demo@taskflow.com","password":"$password"}""")
        )

    private fun demoToken(): String {
        val result = loginAsDemo().andExpect(status().isOk).andReturn()
        return objectMapper.readTree(result.response.contentAsString).get("token").asText()
    }

    @Test
    fun `projects endpoint rejects requests without a token`() {
        mockMvc.perform(get("/api/projects"))
            .andExpect(status().is4xxClientError)
    }

    @Test
    fun `login with wrong password is rejected`() {
        loginAsDemo(password = "wrong-password")
            .andExpect(status().is4xxClientError)
    }

    @Test
    fun `demo user can log in and list own projects`() {
        val token = demoToken()

        mockMvc.perform(
            get("/api/projects").header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }
}