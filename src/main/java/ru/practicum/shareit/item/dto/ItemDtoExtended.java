package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoShortOutput;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoExtended extends ItemDto {
    private List<CommentOutputDto> comments = new ArrayList<>();
    private BookingDtoShortOutput lastBooking;
    private BookingDtoShortOutput nextBooking;

    public ItemDtoExtended(ItemDto itemDto, List<CommentOutputDto> comments) {
        super(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
        this.comments = comments;
    }
}
