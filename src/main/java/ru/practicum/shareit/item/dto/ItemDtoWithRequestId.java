package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemDtoWithRequestId extends ItemDto {
    private Integer requestId;

    public ItemDtoWithRequestId(ItemDto itemDto, Integer requestId) {
        super(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
        this.requestId = requestId;
    }
}
