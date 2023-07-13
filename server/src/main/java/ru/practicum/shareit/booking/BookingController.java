package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDtoOutput> add(@RequestHeader("X-Sharer-User-Id")Integer userId,
                                                @RequestBody BookingDtoInput bookingDtoInput) {
        return ResponseEntity.ok().body(bookingService.add(userId, bookingDtoInput));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDtoOutput> setApprove(@PathVariable Integer bookingId,
                                                   @RequestHeader("X-Sharer-User-Id") Integer ownerId,
                                                   @RequestParam("approved") Boolean isApproved) {
        return ResponseEntity.ok().body(bookingService.setApprove(bookingId, ownerId, isApproved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDtoOutput> get(@PathVariable Integer bookingId,
                                                @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return ResponseEntity.ok().body(bookingService.get(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDtoOutput>> getAll(@RequestParam(name = "state", defaultValue = "ALL", required = false) String searchMode,
                                                         @RequestParam(name = "from", required = false) Integer from,
                                                         @RequestParam(name = "size", required = false) Integer size,
                                                         @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return ResponseEntity.ok().body(bookingService.getAll(searchMode, userId, from, size));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDtoOutput>> getAllByOwner(@RequestParam(name = "state", defaultValue = "ALL", required = false) String searchMode,
                                                                @RequestParam(name = "from", required = false) Integer from,
                                                                @RequestParam(name = "size", required = false) Integer size,
                                                                @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return ResponseEntity.ok().body(bookingService.getAllByOwner(searchMode, userId, from, size));
    }
}
