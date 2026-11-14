package linx7a.user_pets_spring_mvc.model;

import jakarta.validation.constraints.*;

import java.util.List;

public record UserDto(
        @Null
        Long id,
        @NotBlank
        String name,
        @NotBlank
        @Email
        String email,
        @NotNull
        @Min(0)
        Integer age,
        List<PetDto> pets
) {
}
