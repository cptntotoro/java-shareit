package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;

import java.util.List;

public interface BookingService {
    BookingDtoOutput add(Integer userId, BookingDtoInput bookingDtoInput);

    BookingDtoOutput setApprove(Integer bookingId, Integer userId, Boolean isApproved);

    BookingDtoOutput get(Integer bookingId, Integer userId);

    List<BookingDtoOutput> getAll(String bookingStatus, Integer userId, Integer from, Integer size);

    List<BookingDtoOutput> getAllByOwner(String bookingStatus, Integer userId, Integer from, Integer size);
}
