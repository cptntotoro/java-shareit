package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingSearchMode;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDtoOutput add(Integer bookerId, BookingDtoInput bookingDtoInput) {
        validateBookingDtoInput(bookingDtoInput);
        validateUser(bookerId);
        validateItem(bookingDtoInput.getItemId());

        Optional<Item> optionalItem = itemRepository.findById(bookingDtoInput.getItemId());
        if (optionalItem.isEmpty()) {
            throw new ObjectNotFoundException("Failed to receive item.");
        }
        Item item = optionalItem.get();

        if (!item.getAvailable()) {
            throw new UnavailableItemBookingException("Failed to create booking. Items with status 'unavailable' can't be booked.");
        }
        if (item.getOwner().getId().equals(bookerId)) {
            throw new IllegalItemBookingException("Failed to create booking. Item owners are not allowed to book their own items.");
        }

        Booking booking = bookingMapper.toBooking(bookingDtoInput);
        booking.setBooker(userRepository.findById(bookerId).get());
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);

        booking = bookingRepository.save(booking);
        return bookingMapper.toBookingDtoOutput(booking);
    }

    @Override
    public BookingDtoOutput setApprove(Integer bookingId, Integer userId, Boolean isApproved) {
        validateBooking(bookingId);
        validateUser(userId);

        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new ObjectNotFoundException("Booking with id = " + bookingId + " was not found.");
        }
        Booking booking = optionalBooking.get();

        Integer itemOwnerId = booking.getItem().getOwner().getId();

        if (!Objects.equals(userId, itemOwnerId)) {
            throw new IllegalItemBookingException("Failed to change booking status. Only item owners are allowed to change booking status.");
        }

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new UnavailableItemBookingException("Booking status must be 'WAITING'.");
        }

        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        booking = bookingRepository.save(booking);
        return bookingMapper.toBookingDtoOutput(booking);
    }

    @Override
    public BookingDtoOutput get(Integer bookingId, Integer userId) {
        validateBooking(bookingId);
        validateUser(userId);

        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new ObjectNotFoundException("Booking with id = " + bookingId + "was not found.");
        }
        Booking booking = optionalBooking.get();

        Integer itemOwnerId = booking.getItem().getOwner().getId();
        Integer bookerId = booking.getBooker().getId();

        if (!userId.equals(itemOwnerId) && !userId.equals(bookerId)) {
            throw new ObjectNotFoundException("Failed to get booking. Only item owners and item bookers are allowed to view bookings.");
        }

        return bookingMapper.toBookingDtoOutput(booking);
    }

    public List<BookingDtoOutput> getAll(String bookingSearchMode, Integer userId, Integer from, Integer size) {
        validateUser(userId);

        Sort sort = Sort.by("start").descending();

        Pageable pageable = PageRequest.of(from / size, size, sort);

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new ObjectNotFoundException("User with id = " + userId + "was not found.");
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime currentTime = LocalDateTime.now();
        BookingSearchMode searchMode = BookingSearchMode.valueOf(bookingSearchMode.toUpperCase());

        switch (searchMode) {
            case ALL:
                return bookingRepository.findByBookerId(userId, pageable).stream()
                        .map(bookingMapper::toBookingDtoOutput)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, currentDateTime, currentTime, pageable).stream()
                        .map(bookingMapper::toBookingDtoOutput)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findByBookerIdAndEndIsBefore(userId, currentDateTime, pageable).stream()
                        .map(bookingMapper::toBookingDtoOutput)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findByBookerIdAndStartIsAfter(userId, currentDateTime, pageable).stream()
                        .map(bookingMapper::toBookingDtoOutput)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, pageable).stream()
                        .map(bookingMapper::toBookingDtoOutput)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageable).stream()
                        .map(bookingMapper::toBookingDtoOutput)
                        .collect(Collectors.toList());
            default:
                throw new IllegalSearchModeException("Unknown state: " + bookingSearchMode);
        }
    }

    public List<BookingDtoOutput> getAllByOwner(String bookingSearchMode, Integer userId, Integer from, Integer size) {
        validateUser(userId);

        Sort sort = Sort.by("start").descending();

        Pageable pageable = PageRequest.of(from / size, size, sort);

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new ObjectNotFoundException("User with id = " + userId + " was not found.");
        }

        switch (bookingSearchMode) {
            case "ALL":
                return bookingRepository.findByItemOwnerId(userId, pageable).stream()
                        .map(bookingMapper::toBookingDtoOutput)
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), pageable).stream()
                        .map(bookingMapper::toBookingDtoOutput)
                        .collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findByItemOwnerIdAndEndIsBefore(userId, LocalDateTime.now(), pageable).stream()
                        .map(bookingMapper::toBookingDtoOutput)
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findByItemOwnerIdAndStartIsAfter(userId, LocalDateTime.now(), pageable).stream()
                        .map(bookingMapper::toBookingDtoOutput)
                        .collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, pageable).stream()
                        .map(bookingMapper::toBookingDtoOutput)
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, pageable).stream()
                        .map(bookingMapper::toBookingDtoOutput)
                        .collect(Collectors.toList());
            default:
                throw new IllegalSearchModeException("Unknown state: " + bookingSearchMode);
        }
    }

    private void validateBookingDtoInput(BookingDtoInput bookingDtoInput) {
        if (bookingDtoInput.getItemId() == null
                || bookingDtoInput.getStart() == null || bookingDtoInput.getEnd() == null
                || bookingDtoInput.getStart().isAfter(bookingDtoInput.getEnd())
                || bookingDtoInput.getEnd().isBefore(bookingDtoInput.getStart())
                || bookingDtoInput.getStart().equals(bookingDtoInput.getEnd())
        ) {
            throw new DtoIntegrityException("Failed to process request. " +
                    "Booker id, item id, start and end time must not be empty. " +
                    "Start and end time must be correct.");
        }
    }

    private void validateUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("Failed to process request. User with id = " + userId + " doesn't exist.");
        }
    }

    private void validateItem(Integer itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ObjectNotFoundException("Failed to process request. Item with id = " + itemId + " doesn't exist.");
        }
    }

    private void validateBooking(Integer bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new ObjectNotFoundException("Failed to process request. Booking with id = " + bookingId + " doesn't exist.");
        }
    }
}