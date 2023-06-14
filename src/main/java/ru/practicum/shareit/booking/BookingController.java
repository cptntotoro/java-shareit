package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDtoOutput> add(@RequestHeader("X-Sharer-User-Id") @NotNull Integer userId,
                                                @Valid @RequestBody BookingDtoInput bookingDtoInput) {
        return ResponseEntity.ok().body(bookingService.add(userId, bookingDtoInput));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDtoOutput> setApprove(@PathVariable @NotNull Integer bookingId,
                                                   @RequestHeader("X-Sharer-User-Id") @NotNull Integer ownerId,
                                                   @RequestParam("approved") @NotNull Boolean isApproved) {
        return ResponseEntity.ok().body(bookingService.setApprove(bookingId, ownerId, isApproved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDtoOutput> get(@PathVariable @NotNull Integer bookingId,
                                       @RequestHeader("X-Sharer-User-Id") @NotNull Integer userId) {
        return ResponseEntity.ok().body(bookingService.get(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDtoOutput>> getAll(@RequestParam(name = "state", defaultValue = "ALL", required = false) String searchMode,
                                                         @RequestHeader("X-Sharer-User-Id") @NotNull Integer userId) {
        return ResponseEntity.ok().body(bookingService.getAll(searchMode, userId));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDtoOutput>> getAllByOwner(@RequestParam(name = "state", defaultValue = "ALL", required = false) String searchMode,
                                                   @RequestHeader("X-Sharer-User-Id") @NotNull Integer userId) {
        return ResponseEntity.ok().body(bookingService.getAllByOwner(searchMode, userId));
    }
}
