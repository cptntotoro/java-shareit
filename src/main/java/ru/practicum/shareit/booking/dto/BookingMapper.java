package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    Booking toBooking(BookingDtoInput bookingDtoInput);

    BookingDtoOutput toBookingDtoOutput(Booking booking);

    @Mapping(target = "bookerId", source = "booking.booker.id")
    BookingDtoShortOutput toBookingDtoShortOutput(Booking booking);
}
