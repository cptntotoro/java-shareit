package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSearchMode;
import ru.practicum.shareit.exceptions.IllegalSearchModeException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                         @RequestParam(name = "state", defaultValue = "ALL") String searchMode,
                                         @RequestParam(name = "from", required = false) Integer from,
                                         @RequestParam(name = "size", required = false) Integer size) {

        if ((from != null && from <= 0) || (size != null && size <= 0)) {
            throw new IllegalArgumentException("Failed to process request. Incorrect pagination parameters.");
        }

        if (from == null) {
            from = 0;
        }

        if (size == null) {
            size = Integer.MAX_VALUE;
        }

        BookingSearchMode bookingSearchMode;

        try {
            bookingSearchMode = BookingSearchMode.valueOf(searchMode.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalSearchModeException("Unknown state: " + searchMode);
        }

        log.info("Get booking with searchMode={}, userId={}, from={}, size={}", searchMode, userId, from, size);

        return bookingClient.getAll(userId, bookingSearchMode, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                      @RequestBody @Valid BookingDto requestDto) {

        log.info("Creating booking {}, userId={}", requestDto, userId);

        return bookingClient.add(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                      @PathVariable Integer bookingId) {

        log.info("Get bookingId={}, userId={}", bookingId, userId);

        return bookingClient.get(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> setApprove(@PathVariable Integer bookingId,
                                             @RequestHeader("X-Sharer-User-Id") @NotNull Integer ownerId,
                                             @RequestParam("approved") @NotNull Boolean approved) {

        log.info("Set approve bookingId={}, userId={}, approved={}", bookingId, ownerId, approved);

        return bookingClient.setApprove(bookingId, ownerId, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestParam(name = "state", defaultValue = "ALL", required = false) String searchMode,
                                                @RequestParam(name = "from", required = false) @Positive Integer from,
                                                @RequestParam(name = "size", required = false) @Positive Integer size,
                                                @RequestHeader("X-Sharer-User-Id") @NotNull Integer userId) {

        if (from == null) {
            from = 0;
        }

        if (size == null) {
            size = Integer.MAX_VALUE;
        }

        BookingSearchMode bookingSearchMode;

        try {
            bookingSearchMode = BookingSearchMode.valueOf(searchMode.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalSearchModeException("Unknown state: " + searchMode);
        }

        log.info("Get all bookings by userId={}, searchMode={}, from={}, size={}", userId, searchMode, from, size);

        return bookingClient.getAllByOwner(userId, bookingSearchMode, from, size);
    }
}
