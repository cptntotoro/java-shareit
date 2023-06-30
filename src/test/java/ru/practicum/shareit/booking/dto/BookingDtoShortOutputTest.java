package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoShortOutputTest {

    @Autowired
    private JacksonTester<BookingDtoShortOutput> jacksonTester;

    @Test
    void testSerialize() throws Exception {
        UserDto user = new UserDto(1, "Jason", "jason@ya.ru");
        BookingDtoShortOutput bookingDtoShortOutput = new BookingDtoShortOutput(1, 1);

        JsonContent<BookingDtoShortOutput> bookingDtoOutputSaved = jacksonTester.write(bookingDtoShortOutput);

        assertThat(bookingDtoOutputSaved).hasJsonPath("$.id");
        assertThat(bookingDtoOutputSaved).hasJsonPath("$.bookerId");
        assertThat(bookingDtoOutputSaved).extractingJsonPathNumberValue("$.id").isEqualTo(bookingDtoShortOutput.getId());
        assertThat(bookingDtoOutputSaved).extractingJsonPathNumberValue("$.bookerId").isEqualTo(user.getId());
    }
}
