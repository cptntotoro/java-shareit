package ru.practicum.shareit.exceptions;

public class IllegalItemBookingException extends RuntimeException {
    public IllegalItemBookingException(String message) {
        super(message);
    }
}
