package ru.practicum.shareit.exceptions;

public class UnavailableItemBookingException extends RuntimeException {
    public UnavailableItemBookingException(String message) {
        super(message);
    }
}
