package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoShortOutput;

import java.util.List;

@Data
public class ItemDtoExtended extends ItemDto {
    private List<CommentOutputDto> comments;
    private BookingDtoShortOutput lastBooking;
    private BookingDtoShortOutput nextBooking;

    public ItemDtoExtended(ItemDto itemDto, List<CommentOutputDto> comments) {
        super(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
        this.comments = comments;
    }
}
