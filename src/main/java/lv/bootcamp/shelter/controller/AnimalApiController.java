package lv.bootcamp.shelter.controller;





import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lv.bootcamp.shelter.dto.AnimalCreateRequest;
import lv.bootcamp.shelter.dto.AnimalResponse;
import lv.bootcamp.shelter.service.AnimalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * REST controller for shelter animal endpoints.
 * Returns JSON — does not render HTML pages.
 */
@Tag(name = "AnimalsShelter", description = "Animal API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/animals")
public class AnimalApiController {

    private final AnimalService animalService;


    @Operation(summary = "List all animals", description = "Returns all animals.")
    @ApiResponse(responseCode = "200", description = "Animals returned successfully")
    @GetMapping
    public List<AnimalResponse> findAll() {
        return animalService.findAll();
    }

    @Operation(summary = "Get animal By ID", description = "Return an animal by ID")
    @ApiResponse(responseCode = "200", description = "An animals with id returned")
    @ApiResponse(responseCode = "404", description = "Animal not found")
    @GetMapping("/{id}")
    public ResponseEntity<AnimalResponse> findById(@PathVariable Long id) {
        return animalService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Lists adopted animals. Restricted to ROLE_ADMIN — see SecurityConfig.
     * Read-only, so it's a good endpoint for testing role-based JWT authorization:
     * calling it repeatedly (e.g. with/without a token, or with a ROLE_USER token)
     * has no side effects, unlike {@code POST /api/animals}.
     */
    @Operation(summary = "Get adopted animal", description = "Return a list of all adopted animals")
    @ApiResponse(responseCode = "200", description = "Adopted animals returned successfully")
    @ApiResponse(responseCode = "401", description = "Login required")
    @ApiResponse(responseCode = "403", description = "You are not an admin")
    @GetMapping("/adopted")
    public List<AnimalResponse> findAdopted() {
        return animalService.findAdopted();
    }

    /**
     * Creates a new animal. Restricted to ROLE_ADMIN — see SecurityConfig.
     */
    @Operation(summary = "Create animal", description = "Tries to create animal")
    @ApiResponse(responseCode = "201", description = "Animal created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Admin role required")

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnimalResponse create(@RequestBody @Valid AnimalCreateRequest request) {
        return animalService.create(request);
    }

    /**
     * Adopts an animal as the currently logged-in user. Restricted to ROLE_USER
     * (not ROLE_ADMIN) — see SecurityConfig.
     */
    @Operation(summary = "Adopt an Animal", description = "Tries to adopt an animal")
    @ApiResponse(responseCode = "200", description = "Animals adopted")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "User role required")
    @ApiResponse(responseCode = "404", description = "Animal not found")
    @ApiResponse(responseCode = "409", description = "Animal is already adopted") //     HttpStatus.CONFLICT 409


    @PostMapping("/{id}/adopt")
    public ResponseEntity<AnimalResponse> adopt(@PathVariable Long id, Authentication authentication) {
        return animalService.adopt(id, authentication.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleAlreadyAdopted(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
