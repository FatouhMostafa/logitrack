package ma.logitrack.fleet.web;

import ma.logitrack.fleet.domain.exception.BusinessRuleException;
import ma.logitrack.fleet.domain.exception.DuplicateResourceException;
import ma.logitrack.fleet.domain.exception.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        return problem(
                HttpStatus.NOT_FOUND,
                "Resource not found",
                ex.getMessage()
        );
    }

    @ExceptionHandler(DuplicateResourceException.class)
    ProblemDetail handleDuplicate(DuplicateResourceException ex) {
        return problem(
                HttpStatus.CONFLICT,
                "Resource already exists",
                ex.getMessage()
        );
    }

    @ExceptionHandler(BusinessRuleException.class)
    ProblemDetail handleBusinessRule(BusinessRuleException ex) {
        return problem(
                HttpStatus.CONFLICT,
                "Business rule violated",
                ex.getMessage()
        );
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    ProblemDetail handleOptimisticLock(
            ObjectOptimisticLockingFailureException ex
    ) {
        return problem(
                HttpStatus.CONFLICT,
                "Concurrent modification",
                "The resource has been modified in the meantime. "
                        + "Reload it and try again."
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail handleIntegrity(DataIntegrityViolationException ex) {
        return problem(
                HttpStatus.CONFLICT,
                "Data conflict",
                "The operation violates a uniqueness or integrity constraint."
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        ProblemDetail body = problem(
                status,
                "Invalid request",
                "One or more fields are invalid."
        );
        body.setProperty("errors", toFieldErrors(ex.getBindingResult()));
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    private static List<Map<String, String>> toFieldErrors(
            BindingResult result
    ) {
        return result.getFieldErrors().stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "message", Objects.requireNonNullElse(
                                error.getDefaultMessage(), "Invalid value"
                        )
                ))
                .toList();
    }

    @ExceptionHandler(PropertyReferenceException.class)
    ProblemDetail handleInvalidProperty(PropertyReferenceException ex) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "Invalid request",
                ex.getMessage()
        );
    }

    private static ProblemDetail problem(
            HttpStatusCode status,
            String title,
            String detail
    ) {
        ProblemDetail body = ProblemDetail.forStatusAndDetail(status, detail);
        body.setTitle(title);
        return body;
    }
}
