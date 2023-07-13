package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoExtended;
import ru.practicum.shareit.item.dto.ItemDtoWithRequestId;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDtoWithRequestId> add(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                    @RequestBody ItemDtoWithRequestId itemDto) {
        return ResponseEntity.ok().body(itemService.add(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@PathVariable Integer itemId,
                                          @RequestHeader("X-Sharer-User-Id") Integer userId,
                                          @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok().body(itemService.update(itemId, userId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> get(@PathVariable Integer itemId,
                                       @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return ResponseEntity.ok().body(itemService.get(itemId, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDtoExtended>> getAll(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return ResponseEntity.ok().body(itemService.getAll(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                @RequestParam("text") String text) {
        return ResponseEntity.ok().body(itemService.search(userId, text));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentOutputDto> addComment(@PathVariable Integer itemId,
                                                       @RequestHeader("X-Sharer-User-Id") Integer userId,
                                                       @RequestBody Comment comment) {
        return ResponseEntity.ok().body(itemService.addComment(itemId, userId, comment));
    }
}
