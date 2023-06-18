package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDtoInput;
import ru.practicum.shareit.request.dto.RequestDtoOutput;
import ru.practicum.shareit.request.dto.RequestDtoShortOutput;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<RequestDtoShortOutput> add(@RequestBody RequestDtoInput requestDtoInput,
                                                     @RequestHeader("X-Sharer-User-Id") @NotNull Integer userId) {
        return ResponseEntity.ok().body(requestService.add(requestDtoInput, userId));
    }

    @GetMapping
    public ResponseEntity<List<RequestDtoOutput>> getByUser(@RequestHeader("X-Sharer-User-Id") @NotNull Integer userId) {
        return ResponseEntity.ok().body(requestService.getByUser(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<RequestDtoOutput>> getAll(@RequestParam(name = "from", required = false) Integer from,
                                                         @RequestParam(name = "size", required = false) Integer size,
                                                         @RequestHeader("X-Sharer-User-Id") @NotNull Integer userId) {
        return ResponseEntity.ok().body(requestService.getAll(from, size, userId));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<RequestDtoOutput> get(@PathVariable @NotNull @Positive Integer requestId,
                                                @RequestHeader("X-Sharer-User-Id") @NotNull Integer userId) {
        return ResponseEntity.ok().body(requestService.get(requestId, userId));
    }
}
