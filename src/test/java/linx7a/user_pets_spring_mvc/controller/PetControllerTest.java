package linx7a.user_pets_spring_mvc.controller;

import linx7a.user_pets_spring_mvc.model.PetDto;
import linx7a.user_pets_spring_mvc.model.UserDto;
import linx7a.user_pets_spring_mvc.service.PetService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PetControllerTest {

    private static final String OWNER_NAME = "owner";
    private static final String OWNER_EMAIL = "owner@email.ru";
    private static final Integer OWNER_AGE = 30;
    private static final String PET_NAME = "pet";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private PetService petService;
    ObjectMapper objectMapper = new ObjectMapper();

    private UserDto createOwner() {
        return userService.createUser(new UserDto(
                null,
                OWNER_NAME,
                OWNER_EMAIL,
                OWNER_AGE,
                new ArrayList<>()
        ));
    }

    @Test
    void shouldSuccessCreatePet() throws Exception {
        var owner = createOwner();
        var pet = new PetDto(
                null,
                PET_NAME,
                owner.id()
        );
        String petJson = objectMapper.writeValueAsString(pet);
        String createdPetJson = mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(petJson)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var createdPet = objectMapper.readValue(createdPetJson, PetDto.class);

        Assertions.assertThat(pet)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(createdPet);

        Assertions.assertThat(userService.getById(owner.id()).pets())
                .contains(createdPet);
    }

    @Test
    void shouldNotCreatePetWithInvalidData() throws Exception {
        var owner = createOwner();
        var pet = new PetDto(
                null,
                null,
                owner.id()
        );

        String petJson = objectMapper.writeValueAsString(pet);
        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(petJson)
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldReturnNotFoundWhenOwnerDoesNotExist() throws Exception {
        var pet = new PetDto(
                null,
                PET_NAME,
                Long.MAX_VALUE
        );
        String petJson = objectMapper.writeValueAsString(pet);
        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(petJson)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSuccessDeletePet() throws Exception {
        var owner = createOwner();
        var pet = petService.createPet(new PetDto(
                null,
                PET_NAME,
                owner.id()
        ));

        mockMvc.perform(delete("/api/pets/{id}", pet.id()))
                .andExpect(status().isNoContent());

        Assertions.assertThat(userService.getById(owner.id()).pets()).isEmpty();

        mockMvc.perform(delete("/api/pets/{id}", pet.id()))
                .andExpect(status().isNotFound());
    }
}