package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithRequestId;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") @NotNull Integer userId,
                                      @RequestBody @Valid ItemDtoWithRequestId itemDto) {

        log.info("Add item with userId={}, itemDto={}", userId, itemDto);

        return itemClient.add(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable @Positive Integer itemId,
                                         @RequestHeader("X-Sharer-User-Id") @NotNull Integer userId,
                                         @RequestBody ItemDto itemDto) {

        log.info("Update item with itemId={}, userId={}, itemDto={}", itemId, userId, itemDto);

        return itemClient.update(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@PathVariable @NotNull @Positive Integer itemId,
                                      @RequestHeader("X-Sharer-User-Id") @NotNull Integer userId) {

        log.info("Get item with itemId={}, userId={}", itemId, userId);

        return itemClient.get(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") @NotNull Integer userId) {

        log.info("Get all items by userId={}", userId);

        return itemClient.getAll(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") @NotNull Integer userId,
                                         @RequestParam("text") String text) {

        log.info("Search items by userId={}, text={}", userId, text);

        return itemClient.search(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable @NotNull Integer itemId,
                                             @RequestHeader("X-Sharer-User-Id") @NotNull Integer userId,
                                             @RequestBody @Valid CommentDto comment) {
        return itemClient.addComment(itemId, userId, comment);
    }
}
