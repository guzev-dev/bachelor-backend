package bachelor.proj.charity.pl.exceptions.handler;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;
import java.util.function.Function;

@ControllerAdvice
public class ExceptionsResponseHandler extends ResponseEntityExceptionHandler {

    private final Function<String, Map<Object,Object>> createErrorMessage = (String errorMessage) -> Map.of("message", errorMessage);


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException exception, WebRequest request) {
        return handleExceptionInternal(
                exception,
                createErrorMessage.apply(exception.getMessage()),
                new HttpHeaders(),
                HttpStatus.NOT_FOUND,
                request
        );
    }

}
