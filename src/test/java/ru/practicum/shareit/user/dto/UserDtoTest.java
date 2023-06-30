package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> jacksonTester;

    @Test
    void testSerialize() throws Exception {
        UserDto userDto = new UserDto(1, "Jason", "jason@ya.ru");

        JsonContent<UserDto> userDtoSaved = jacksonTester.write(userDto);

        assertThat(userDtoSaved).hasJsonPath("$.id");
        assertThat(userDtoSaved).hasJsonPath("$.name");
        assertThat(userDtoSaved).hasJsonPath("$.email");

        assertThat(userDtoSaved).extractingJsonPathNumberValue("$.id").isEqualTo(userDto.getId());
        assertThat(userDtoSaved).extractingJsonPathStringValue("$.name").isEqualTo(userDto.getName());
        assertThat(userDtoSaved).extractingJsonPathStringValue("$.email").isEqualTo(userDto.getEmail());
    }
}
