package bachelor.proj.charity.pl.controllers;

import bachelor.proj.charity.bl.dto.request.UserRequestDTO;
import bachelor.proj.charity.bl.dto.request.documents.UserPhotoRequestDTO;
import bachelor.proj.charity.bl.services.UserService;
import bachelor.proj.charity.pl.authorization.jwt.JWTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/charity/api/user")
@CrossOrigin(
        origins = "http://localhost:4200",
        allowedHeaders = {"Authorization", "Content-Type"},
        methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE}
)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final JWTokenProvider tokenProvider;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/profile-photo")
    public ResponseEntity<?> addProfilePhoto(UserPhotoRequestDTO photoDTO,
                                             HttpServletRequest request) {

        final Long userId = tokenProvider.getUserId(request);

        photoDTO.setUserId(userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.addProfilePhoto(photoDTO));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping()
    public ResponseEntity<?> readUser(@RequestParam(value = "initializePhoto", defaultValue = "false") Boolean initalizePhoto,
                                      Authentication authenticated) {

        return ResponseEntity.ok(
                userService.readByEmail(authenticated.getName(), initalizePhoto)
        );
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote-available")
    public ResponseEntity<?> checkIfVoteAvailable(HttpServletRequest request) {

        final Long userId = tokenProvider.getUserId(request);

        return ResponseEntity.ok(
                Map.of("canVote", userService.checkIfVoteAvailable(userId))
        );
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping
    public ResponseEntity<?> updateUser(UserRequestDTO userDTO,
                                        HttpServletRequest request) {

        final Long userId = tokenProvider.getUserId(request);

        if (userId.equals(userDTO.getId()))
            return ResponseEntity.ok(userService.update(userDTO));
        else
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Ідентифікатор оновлюваного користувача не збігається з ідентифікатором авторизованого користувача.");
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam(value = "oldPassword") String oldPassword,
                                            @RequestParam(value = "newPassword") String newPassword,
                                            HttpServletRequest request) {

        final Long userId = tokenProvider.getUserId(request);

        return ResponseEntity.ok(
                Map.of("passwordChanged", userService.changePassword(userId, oldPassword, newPassword))
        );
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete")
    public ResponseEntity<?> requestToDelete(HttpServletRequest request) {

        final Long userId = tokenProvider.getUserId(request);

        userService.requestToDelete(userId);

        return ResponseEntity.ok(
                Map.of("message", "Перевірте електронну пошту для підтвердження видалення облікового запису.")
        );
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete")
    public ResponseEntity<?> confirmDelete(@RequestParam(value = "secretCode") String secretCode,
                                           Authentication authenticated) {

        return ResponseEntity.ok(
                Map.of("deleted", userService.confirmUserDelete(authenticated.getName(), secretCode))
        );
    }

    @DeleteMapping("/profile-photo")
    public ResponseEntity<?> deletePhoto(HttpServletRequest request) {

        final Long userId = tokenProvider.getUserId(request);

        userService.deleteProfilePhoto(userId);

        return ResponseEntity.ok(null);
    }

}
