package bachelor.proj.charity.shared.enums;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
public enum CharityCategory {
    ECOLOGY(new CharityCategoryResponseDTO("Екологія", "bi bi-flower3")),
    ARMY(new CharityCategoryResponseDTO("Армія", "bi bi-shield-shaded")),
    HEALTH_CARE(new CharityCategoryResponseDTO("Охорона здоров'я", "bi bi-heart-pulse")),
    ANIMALS_HELP(new CharityCategoryResponseDTO("Допомога тваринам", "bi bi-bug")),
    EDUCATION(new CharityCategoryResponseDTO("Освіта", "bi bi-book")),
    CULTURE(new CharityCategoryResponseDTO("Культура", "bi bi-palette")),
    SPORT(new CharityCategoryResponseDTO("Спорт", "bi bi-trophy")),
    HUMAN_RIGHTS(new CharityCategoryResponseDTO("Права людини", "bi bi-mortarboard"));


    private final CharityCategoryResponseDTO response;

    CharityCategory(CharityCategoryResponseDTO response) {
        this.response = response;
    }

    public static CharityCategory getByName(String name) {
        return Arrays.stream(CharityCategory.values())
                .filter(arg -> arg.getResponse().getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Невірна назва категорії."));
    }

    @Getter
    @RequiredArgsConstructor
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class CharityCategoryResponseDTO {
        private final String name;
        private final String iconClass;
    }

}
