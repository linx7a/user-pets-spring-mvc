package linx7a.user_pets_spring_mvc.service;

import linx7a.user_pets_spring_mvc.model.PetDto;
import linx7a.user_pets_spring_mvc.model.UserDto;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private Long idCounter;
    private final Map<Long, UserDto> usersMap;

    public UserService() {
        this.idCounter = 0L;
        this.usersMap = new HashMap<>();
    }

    public UserDto createUser(UserDto userToCreate) {
        var user = new UserDto(
                ++idCounter,
                userToCreate.name(),
                userToCreate.email(),
                userToCreate.age(),
                new ArrayList<>()
        );
        usersMap.put(user.id(), user);
        return user;
    }

    public UserDto updateById(Long id, UserDto userToUpdate) {
        Optional.ofNullable(usersMap.get(id))
                .orElseThrow(() -> new NoSuchElementException("No found user by id=%s".formatted(id)));
        var updatedUser = new UserDto(
                id,
                userToUpdate.name(),
                userToUpdate.email(),
                userToUpdate.age(),
                usersMap.get(id).pets()
        );
        usersMap.put(id, updatedUser);
        return updatedUser;
    }

    public void deleteById(Long id) {
        Optional.ofNullable(usersMap.remove(id))
                .orElseThrow(() -> new NoSuchElementException("No found user by id=%s".formatted(id)));
    }

    public UserDto getById(Long id) {
        Optional.ofNullable(usersMap.get(id))
                .orElseThrow(() -> new NoSuchElementException("No found user by id=%s".formatted(id)));
        return usersMap.get(id);
    }

    public void addPet(PetDto pet) {
        var user = getById(pet.userId());
        user.pets().add(pet);
    }

    public void removePet(PetDto pet) {
        var user = Optional.ofNullable(usersMap.get(pet.userId()))
                .orElseThrow(() -> new NoSuchElementException("No found user by id=%s".formatted(pet.userId())));
        user.pets().remove(pet);
    }
}
