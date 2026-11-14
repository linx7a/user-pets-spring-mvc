package linx7a.user_pets_spring_mvc.controller;

import linx7a.user_pets_spring_mvc.model.UserDto;
import linx7a.user_pets_spring_mvc.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private static final String USER_NAME = "user";
    private static final String USER_EMAIL = "user@email.ru";
    private static final Integer USER_AGE = 20;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    private ObjectMapper objectMapper = new ObjectMapper();

    private UserDto buildUser() {
        return new UserDto(
                null,
                USER_NAME,
                USER_EMAIL,
                USER_AGE,
                new ArrayList<>()
        );
    }

    @Test
    void shouldSuccessCreateUser() throws Exception {
        var user = buildUser();
        String userJson = objectMapper.writeValueAsString(user);
        String createUserJson = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var createdUser = objectMapper.readValue(createUserJson, UserDto.class);

        Assertions.assertThat(createdUser.id()).isNotNull();

        Assertions.assertThat(user)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(createdUser);
    }

    @Test
    void shouldNotCreateUserWithInvalidData() throws Exception {
        var user = new UserDto(
                null,
                null,
                USER_EMAIL,
                USER_AGE,
                new ArrayList<>()
        );

        String userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detailedMessage").value(containsString("name")));

    }

    @Test
    void shouldSuccessSearchUserById() throws Exception {
        var user = buildUser();
        var createdUser = userService.createUser(user);
        String foundUserJson = mockMvc.perform(get("/api/users/{id}", createdUser.id()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var foundUser = objectMapper.readValue(foundUserJson, UserDto.class);

        Assertions.assertThat(createdUser)
                .usingRecursiveComparison()
                .isEqualTo(foundUser);
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/users/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSuccessDeleteUser() throws Exception {
        var user = buildUser();
        var createdUser = userService.createUser(user);
        mockMvc.perform(delete("/api/users/{id}", createdUser.id()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/{id}", createdUser.id()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSuccessUpdateUser() throws Exception {
        var user = buildUser();
        var createdUser = userService.createUser(user);

        var newUser = new UserDto(
                null,
                "other-name",
                USER_EMAIL,
                USER_AGE,
                new ArrayList<>()
        );

        String newUserJson = objectMapper.writeValueAsString(newUser);
        String updatedUserJson = mockMvc.perform(put("/api/users/{id}", createdUser.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var updatedUser = objectMapper.readValue(updatedUserJson, UserDto.class);

        Assertions.assertThat(updatedUser.id()).isEqualTo(createdUser.id());

        Assertions.assertThat(updatedUser)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(newUser);
    }
}