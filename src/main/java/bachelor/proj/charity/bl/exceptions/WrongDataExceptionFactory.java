package bachelor.proj.charity.bl.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class WrongDataExceptionFactory {

    private final Validator validator;

    public <T> boolean checkIfValid(T dto, Class<?> requestType, String errorMessage) {
        final Set<ConstraintViolation<T>> violations =
                validator.validate(dto, requestType);

        if (violations.isEmpty())
            return true;
        else
            throw this.getException(errorMessage, violations);
    }

    public <T> WrongDataException getException(String message, Set<ConstraintViolation<T>> violations) {
        StringBuilder sb = new StringBuilder(message)
                .append("\n")
                .append((violations.size() == 1)
                        ? "Inner exception: {\n"
                        : "Inner exceptions: {\n");

        for (ConstraintViolation<T> violation : violations) {
            sb.append("• ")
                    .append(violation.getMessage())
                    .append("\n");
        }

        sb.append("}");

        return new WrongDataException(sb.toString());
    }

    public WrongDataException getException(String message, String explanation) {
        StringBuilder sb = new StringBuilder(message)
                .append("\n")
                .append("Inner exception: {\n")
                .append("• ")
                .append(explanation)
                .append("\n}");

        return new WrongDataException(sb.toString());
    }
}