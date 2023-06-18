package ru.practicum.shareit.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Generated
@RestControllerAdvice
public class ControllerExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler(DtoIntegrityException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Map<String, String> validationException(DtoIntegrityException exception) {
        log.error(exception.getMessage());
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(UserEmailAlreadyExistsException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public Map<String, String> validationException(UserEmailAlreadyExistsException exception) {
        log.error(exception.getMessage());
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(ItemAccessException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public Map<String, String> validationException(ItemAccessException exception) {
        log.error(exception.getMessage());
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(IllegalItemBookingException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public Map<String, String> validationException(IllegalItemBookingException exception) {
        log.error(exception.getMessage());
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(UnavailableItemBookingException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Map<String, String> validationException(UnavailableItemBookingException exception) {
        log.error(exception.getMessage());
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public Map<String, String> validationException(ObjectNotFoundException exception) {
        log.error(exception.getMessage());
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(IllegalSearchModeException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Map<String, String> validationException(IllegalSearchModeException exception) {
        log.error(exception.getMessage());
        return Map.of("error", exception.getMessage());
    }
}
