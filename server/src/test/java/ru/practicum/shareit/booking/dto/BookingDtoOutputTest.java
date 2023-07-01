package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
public class BookingDtoOutputTest {

    @Autowired
    private JacksonTester<BookingDtoOutput> jacksonTester;

    @Test
    void testSerialize() throws Exception {
        ItemDto item = new ItemDto(1, "Balalaika", "Brand new balalaika", true);
        UserDto user = new UserDto(1, "Jason", "jason@ya.ru");
        BookingDtoOutput bookingDtoOutput = new BookingDtoOutput(1, LocalDateTime.now(), LocalDateTime.now(), item, user, BookingStatus.WAITING);

        JsonContent<BookingDtoOutput> bookingDtoOutputSaved = jacksonTester.write(bookingDtoOutput);

        assertThat(bookingDtoOutputSaved).hasJsonPath("$.id");
        assertThat(bookingDtoOutputSaved).hasJsonPath("$.start");
        assertThat(bookingDtoOutputSaved).hasJsonPath("$.end");
        assertThat(bookingDtoOutputSaved).hasJsonPath("$.item");
        assertThat(bookingDtoOutputSaved).hasJsonPath("$.booker");
        assertThat(bookingDtoOutputSaved).hasJsonPath("$.status");

        assertThat(bookingDtoOutputSaved).extractingJsonPathNumberValue("$.id").isEqualTo(bookingDtoOutput.getId());

        assertThat(bookingDtoOutputSaved).hasJsonPathValue("$.start");
        assertThat(bookingDtoOutputSaved).hasJsonPathValue("$.end");

        assertThat(bookingDtoOutputSaved).extractingJsonPathNumberValue("$.booker.id").isEqualTo(user.getId());
        assertThat(bookingDtoOutputSaved).extractingJsonPathStringValue("$.booker.name").isEqualTo(user.getName());
        assertThat(bookingDtoOutputSaved).extractingJsonPathNumberValue("$.item.id").isEqualTo(item.getId());
        assertThat(bookingDtoOutputSaved).extractingJsonPathStringValue("$.item.name").isEqualTo(item.getName());
        assertThat(bookingDtoOutputSaved).extractingJsonPathStringValue("$.status").isEqualTo(bookingDtoOutput.getStatus().toString());
    }
}