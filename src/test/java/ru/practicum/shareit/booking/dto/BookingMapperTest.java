package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class BookingMapperTest {

    @Test
    void toBooking_FromBookingDtoInput() {
        BookingDtoInput bookingDtoInput = new BookingDtoInput(1, 1, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(3));
        Booking booking = BookingMapper.INSTANCE.toBooking(bookingDtoInput);

        assertNotNull(booking);

        assertNull(booking.getId());
        assertEquals(booking.getStart(), bookingDtoInput.getStart());
        assertEquals(booking.getEnd(), bookingDtoInput.getEnd());
        assertNull(booking.getItem());
        assertNull(booking.getBooker());
        assertNull(booking.getStatus());
    }

    @Test
    void toBookingDtoOutput_FromBooking() {
        Item item = new Item(1, "Balalaika", "Brand new balalaika", true);
        User user = new User(2, "Shaun", "shaun@ya.ru");
        Booking booking = new Booking(1, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, user, BookingStatus.APPROVED);

        BookingDtoOutput bookingDtoOutput = BookingMapper.INSTANCE.toBookingDtoOutput(booking);

        assertNotNull(bookingDtoOutput);

        assertEquals(booking.getId(), bookingDtoOutput.getId());
        assertEquals(booking.getStart(), bookingDtoOutput.getStart());
        assertEquals(booking.getEnd(), bookingDtoOutput.getEnd());

        assertEquals(booking.getItem().getId(), bookingDtoOutput.getItem().getId());
        assertEquals(booking.getItem().getName(), bookingDtoOutput.getItem().getName());
        assertEquals(booking.getItem().getDescription(), bookingDtoOutput.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), bookingDtoOutput.getItem().getAvailable());

        assertEquals(booking.getBooker().getId(), bookingDtoOutput.getBooker().getId());
        assertEquals(booking.getBooker().getName(), bookingDtoOutput.getBooker().getName());
        assertEquals(booking.getBooker().getEmail(), bookingDtoOutput.getBooker().getEmail());

        assertEquals(booking.getStatus(), bookingDtoOutput.getStatus());
    }

    @Test
    void toBookingDtoShortOutput_FromBooking() {
        Item item = new Item(1, "Balalaika", "Brand new balalaika", true);
        User user = new User(2, "Shaun", "shaun@ya.ru");
        Booking booking = new Booking(1, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), item, user, BookingStatus.APPROVED);

        BookingDtoShortOutput bookingDtoOutput = BookingMapper.INSTANCE.toBookingDtoShortOutput(booking);

        assertNotNull(bookingDtoOutput);

        assertEquals(booking.getId(), bookingDtoOutput.getId());
        assertEquals(booking.getBooker().getId(), bookingDtoOutput.getBookerId());
    }
}
