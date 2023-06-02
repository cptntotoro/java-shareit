package ru.practicum.shareit.exceptions;

public class ItemDoesNotExistException extends RuntimeException {
    public ItemDoesNotExistException(String message) {
        super(message);
    }
}
