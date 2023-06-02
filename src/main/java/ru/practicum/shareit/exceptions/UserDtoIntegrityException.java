package ru.practicum.shareit.exceptions;

public class UserDtoIntegrityException extends RuntimeException {
    public UserDtoIntegrityException(String message) {
        super(message);
    }
}

