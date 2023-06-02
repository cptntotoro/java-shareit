package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> add(@RequestHeader("X-Sharer-User-Id") @NotNull Integer userId, @Valid @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok().body(itemService.add(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@PathVariable @Positive Integer itemId, @RequestHeader("X-Sharer-User-Id") @NotNull Integer userId, @Valid @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok().body(itemService.update(itemId, userId, itemDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> get(@PathVariable @NotNull @Positive Integer id) {
        return ResponseEntity.ok().body(itemService.get(id));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAll(@RequestHeader("X-Sharer-User-Id") @NotNull Integer userId) {
        return ResponseEntity.ok().body(itemService.getAll(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestHeader("X-Sharer-User-Id") @NotNull Integer userId, @RequestParam("text") String text) {
        return ResponseEntity.ok().body(itemService.search(userId, text));
    }
}
