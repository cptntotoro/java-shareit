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

    @ExceptionHandler(IllegalSearchModeException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Map<String, String> validationException(IllegalSearchModeException exception) {
        log.error(exception.getMessage());
        return Map.of("error", exception.getMessage());
    }
}
