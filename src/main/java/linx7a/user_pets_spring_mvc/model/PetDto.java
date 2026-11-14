package linx7a.user_pets_spring_mvc.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public record PetDto(
        @Null
        Long id,
        @NotBlank
        String name,
        @NotNull
        Long userId

) {
}
