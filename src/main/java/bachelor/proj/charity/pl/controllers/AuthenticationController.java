package bachelor.proj.charity.pl.controllers;

import bachelor.proj.charity.bl.dto.request.UserRequestDTO;
import bachelor.proj.charity.bl.dto.response.UserResponseDTO;
import bachelor.proj.charity.bl.dto.response.UserWithCredentialsResponseDTO;
import bachelor.proj.charity.bl.services.UserService;
import bachelor.proj.charity.pl.authorization.jwt.JWTokenProvider;
import bachelor.proj.charity.shared.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.function.Function;

@RestController
@RequestMapping("/charity/api/auth")
@CrossOrigin(
        origins = "http://localhost:4200",
        allowedHeaders = {"Authorization", "Content-Type"},
        methods = {RequestMethod.POST, RequestMethod.GET}
)
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final JWTokenProvider tokenProvider;

    @Autowired
    @Qualifier("userCacheManager")
    private CacheManager cacheManager;

    private final Function<String, Map<Object,Object>> createMessage = (String message) -> Map.of("message", message);

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginCredentials credentials) {

        UserResponseDTO user = userService.readByEmail(credentials.email);
        return ResponseEntity.ok(
            getBodyWithToken(credentials.email, credentials.password, user.getRole(), user.getId())
        );
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequestDTO userDTO) {

        if (userService.requestToCreate(userDTO))
            return ResponseEntity.ok(
                    createMessage.apply("Перевірте електронну пошту для підтвердження реєстрація.")
            );
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Не вдається зареєструвати користувача.");

    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmRegistration(@RequestBody ConfirmRegistrationCredentials registrationCredentials) {

        final UserWithCredentialsResponseDTO response = userService.confirmUserCreation(registrationCredentials.email, registrationCredentials.secretCode);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(getBodyWithToken(response.getEmail(), response.getPassword(), response.getRole(), response.getId()));
    }

    @GetMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> logout(Authentication authenticated) {

        //evict key and value from cache
        evictCache(authenticated.getName());

        return ResponseEntity.ok(createMessage.apply("Вихід з облікового запису пройшов успішно."));
    }

    private void evictCache(String email) {
        cacheManager.getCache("user-details_cache").evictIfPresent(email);
    }

    private Map<String, Object> getBodyWithToken(String email, String password, UserRole role, Long userId) {

        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));

        final String jwtToken = tokenProvider.createToken(email, role, userId);
        return Map.of("token",jwtToken,
                "id", userId,
                "role", role.toString());
    }

    @RequiredArgsConstructor
    private static class LoginCredentials {

        private final String email;

        private final String password;

    }

    @RequiredArgsConstructor
    private static class ConfirmRegistrationCredentials {

        private final String email;

        private final String secretCode;
    }

}
