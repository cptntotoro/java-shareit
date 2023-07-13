package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") @NotNull Integer userId,
                                      @Valid @RequestBody RequestDto requestDto) {

        log.info("Add request with userId={}, requestDto={}", userId, requestDto);

        return requestClient.add(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getByUser(@RequestHeader("X-Sharer-User-Id") @NotNull Integer userId) {

        log.info("Get requests by userId={}", userId);

        return requestClient.getByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestParam(name = "from", required = false) Integer from,
                                         @RequestParam(name = "size", required = false) Integer size,
                                         @RequestHeader("X-Sharer-User-Id") @NotNull Integer userId) {

        if (from != null && size != null) {
            if (size <= 0 || from < 0) {
                throw new RuntimeException("Incorrect 'from' and 'size' pagination parameter values.");
            }
        }

        log.info("Get all requests by userId={}, from={}, size={}", userId, from, size);

        return requestClient.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> get(@PathVariable @NotNull @Positive Integer requestId,
                                      @RequestHeader("X-Sharer-User-Id") @NotNull Integer userId) {

        log.info("Get request by requestId={}, userId={}", requestId, userId);

        return requestClient.get(userId, requestId);
    }
}
