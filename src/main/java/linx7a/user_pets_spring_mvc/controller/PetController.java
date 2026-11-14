package linx7a.user_pets_spring_mvc.controller;

import jakarta.validation.Valid;
import linx7a.user_pets_spring_mvc.model.PetDto;
import linx7a.user_pets_spring_mvc.service.PetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pets")
public class PetController {
    private static final Logger log = LoggerFactory.getLogger(PetController.class);
    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    public ResponseEntity<PetDto> createPet(
            @Valid @RequestBody PetDto petToCreate
    ) {
        log.info("Post request to create pet");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(petService.createPet(petToCreate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(
            @PathVariable Long id
    ) {
        log.info("Delete request to delete pet by id={}", id);
        petService.deletePet(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
