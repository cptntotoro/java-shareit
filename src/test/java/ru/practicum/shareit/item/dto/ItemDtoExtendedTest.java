package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoShortOutput;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoExtendedTest {

    @Autowired
    private JacksonTester<ItemDtoExtended> jacksonTester;

    @Test
    void testSerialize() throws Exception {

        ItemDto itemDto = new ItemDto(1, "Balalaika", "Brand new balalaika", true);

        CommentOutputDto comment = new CommentOutputDto(1,"Great balalaika! Thanks!", "Shaun", LocalDateTime.now());
        List<CommentOutputDto> comments = new ArrayList<>(List.of(comment));

        BookingDtoShortOutput lastBooking = new BookingDtoShortOutput(1, 2);
        BookingDtoShortOutput nextBooking = new BookingDtoShortOutput(2, 3);

        ItemDtoExtended itemDtoExtended = new ItemDtoExtended(itemDto, comments);
        itemDtoExtended.setLastBooking(lastBooking);
        itemDtoExtended.setNextBooking(nextBooking);

        JsonContent<ItemDtoExtended> itemDtoExtendedSaved = jacksonTester.write(itemDtoExtended);

        assertThat(itemDtoExtendedSaved).hasJsonPath("$.id");
        assertThat(itemDtoExtendedSaved).hasJsonPath("$.name");
        assertThat(itemDtoExtendedSaved).hasJsonPath("$.description");
        assertThat(itemDtoExtendedSaved).hasJsonPath("$.available");
        assertThat(itemDtoExtendedSaved).hasJsonPath("$.comments");
        assertThat(itemDtoExtendedSaved).hasJsonPath("$.lastBooking");
        assertThat(itemDtoExtendedSaved).hasJsonPath("$.nextBooking");

        assertThat(itemDtoExtendedSaved).extractingJsonPathNumberValue("$.id").isEqualTo(itemDto.getId());
        assertThat(itemDtoExtendedSaved).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(itemDtoExtendedSaved).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(itemDtoExtendedSaved).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());

        assertThat(itemDtoExtendedSaved).extractingJsonPathArrayValue("$.comments").hasSize(1);

        assertThat(itemDtoExtendedSaved).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(lastBooking.getId());
        assertThat(itemDtoExtendedSaved).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(lastBooking.getBookerId());

        assertThat(itemDtoExtendedSaved).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(nextBooking.getId());
        assertThat(itemDtoExtendedSaved).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(nextBooking.getBookerId());
    }

}
