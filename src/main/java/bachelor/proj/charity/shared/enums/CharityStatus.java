package bachelor.proj.charity.shared.enums;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
public enum CharityStatus {
    ACTIVE(new CharityStatusResponseDTO("Активний", "Активні")),
    ENDED(new CharityStatusResponseDTO("Завершений", "Завершені"));

    private final CharityStatusResponseDTO response;

    CharityStatus(CharityStatusResponseDTO response) {
        this.response = response;
    }
    public static CharityStatus getByName(String name) {
        return Arrays.stream(CharityStatus.values())
                .filter(arg -> arg.response.singleName.equals(name)
                        || arg.response.multipleName.equals(name))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Невірна назва статусу благодійного збору"));
    }

    @Getter
    @RequiredArgsConstructor
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class CharityStatusResponseDTO {
        private final String singleName;
        private final String multipleName;
    }

}
