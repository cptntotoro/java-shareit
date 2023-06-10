package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByBooker_Id(Integer bookerId, Sort sort);

    List<Booking> findByBooker_IdAndStartIsAfter(Integer bookerId, LocalDateTime currentDateTime, Sort sort);

    List<Booking> findByBooker_IdAndEndIsBefore(Integer bookerId, LocalDateTime currentDateTime, Sort sort);

    List<Booking> findByBooker_IdAndStatus(Integer userId, BookingStatus bookingStatus, Sort sort);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(Integer bookerId, LocalDateTime currentDateTime, LocalDateTime currentTime, Sort sort);

    List<Booking> findByItemOwnerId(Integer userId, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsAfter(Integer itemOwnerId, LocalDateTime currentDateTime, Sort sort);

    List<Booking> findByItemOwnerIdAndEndIsBefore(Integer itemOwnerId, LocalDateTime currentDateTime, Sort sort);

    List<Booking> findByItemOwnerIdAndStatus(Integer itemOwnerId, BookingStatus bookingStatus, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Integer itemOwnerId, LocalDateTime currentDateTime, LocalDateTime currentTime, Sort sort);

    List<Booking> findByItemIdAndStartIsAfterAndStatusNot(Integer itemId, LocalDateTime currentDateTime, BookingStatus status, Sort sort);

    List<Booking> findByItemIdAndStartIsBeforeAndStatusNot(Integer itemId, LocalDateTime currentDateTime, BookingStatus status, Sort sort);
}
