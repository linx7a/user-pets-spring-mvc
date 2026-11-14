package linx7a.user_pets_spring_mvc.service;

import linx7a.user_pets_spring_mvc.model.PetDto;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class PetService {
    private Long idCounter;
    private final Map<Long, PetDto> petsMap;

    private final UserService userService;

    public PetService(UserService userService) {
        this.idCounter = 0L;
        this.petsMap = new HashMap<>();
        this.userService = userService;
    }

    public PetDto createPet(PetDto petToCreate) {
        userService.getById(petToCreate.userId());
        var pet = new PetDto(
                ++idCounter,
                petToCreate.name(),
                petToCreate.userId()
        );
        petsMap.put(pet.id(), pet);
        userService.addPet(pet);
        return pet;
    }

    public void deletePet(Long id) {
        var pet = Optional.ofNullable(petsMap.get(id))
                .orElseThrow(() -> new NoSuchElementException("No found pet by id=%s".formatted(id)));
        userService.removePet(pet);
        petsMap.remove(id);
    }
}
