package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByItemIdAndStartIsAfterAndStatusNot(Integer itemId, LocalDateTime currentDateTime, BookingStatus status, Sort sort);

    List<Booking> findByItemIdAndStartIsBeforeAndStatusNot(Integer itemId, LocalDateTime currentDateTime, BookingStatus status, Sort sort);

    Page<Booking> findByBookerId(Integer bookerId, Pageable pageable);

    Page<Booking> findByBookerIdAndStartIsAfter(Integer bookerId, LocalDateTime currentDateTime, Pageable pageable);

    Page<Booking> findByBookerIdAndEndIsBefore(Integer bookerId, LocalDateTime currentDateTime, Pageable pageable);

    Page<Booking> findByBookerIdAndStatus(Integer userId, BookingStatus bookingStatus, Pageable pageable);

    Page<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Integer bookerId, LocalDateTime currentDateTime, LocalDateTime currentTime, Pageable pageable);

    Page<Booking> findByItemOwnerId(Integer userId, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartIsAfter(Integer itemOwnerId, LocalDateTime currentDateTime, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndEndIsBefore(Integer itemOwnerId, LocalDateTime currentDateTime, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStatus(Integer itemOwnerId, BookingStatus bookingStatus, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Integer itemOwnerId, LocalDateTime currentDateTime, LocalDateTime currentTime, Pageable pageable);
}
